package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.dao.DesserteReelleDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.dateToLocalDateTime;
import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.localDateTimeToDate;
import static fr.pantheonsorbonne.ufr27.miage.util.StringUtil.ANSI_BLUE;
import static fr.pantheonsorbonne.ufr27.miage.util.StringUtil.printColor;
import static fr.pantheonsorbonne.ufr27.miage.util.TimeUtil.addDurationToDate;
import static fr.pantheonsorbonne.ufr27.miage.util.TimeUtil.calculateIfDelay;

@ApplicationScoped
@ManagedBean
@Slf4j
public class TrainServiceImpl implements TrainService {

    @Inject
    TrajetDAO trajetDAO;

    @Inject
	DesserteReelleDAO desserteReelleDAO;

    @Inject
	GareDAO gareDAO;

	/**
     * - Récupère le trajet concerné depuis la base de données
     * - Détermine si un retard a survenu à l'aide de calculateIfDelay
     * - Modifie les temps d'arrivée réels en fonction du retard (éventuellement)
     */
    @Override
    public void processLiveInfo(LiveInfo liveInfo, int trajetId) {
        log.info("Received LiveInfo of train " + trajetId);
        log.info(printColor("Received " + liveInfo.toString(), ANSI_BLUE));

        Trajet trajet = trajetDAO.find(trajetId);

        log.info("Got train : " + trajet.toString());

        Duration delay = calculateIfDelay(trajet, liveInfo);
        log.info(printColor("Delay is " + delay.toMinutes() + " min", ANSI_BLUE));

        List<DesserteReelle> newDesserteInfo;
        if (Math.abs(delay.toSeconds()) > 0) {
            newDesserteInfo = trajet.getDesserteReelles().stream()
                    .map(dr -> {
                        if (dr.getSeq() >= liveInfo.getNextGareIndex() && dr.isDesservi())
                            //dr.addDuration(delay);
                        	dr.setArrivee(
                        			addDurationToDate(
                        					trajet.getDesserteTheoriqueNo(dr.getSeq()).getArrivee(),
											delay
									)
							);
                        return dr;
                    }).collect(Collectors.toList());

            trajetDAO.setDessertesReelles(trajet, newDesserteInfo);
        }

        if (delay.toMinutes() >= 120) {
			processExceptionalStop(trajet, liveInfo.getNextGareIndex());
		}

        if (delay.toMinutes() >= 60) {
        	waitForThisTrainWhenTER(trajet, liveInfo, delay);
		}

    }

	private void processExceptionalStop(Trajet trajet, int gareIndex) {
		// Récupérer les trajets ayant le même parcours géographique
		List<Trajet> sameParcours = trajetDAO.getTrajetsByParcoursId(trajet.getParcoursId());

		// Filtrer : Ne garder que les trajets partis après celui en retard
		sameParcours = sameParcours.stream()
				.filter(t -> dateToLocalDateTime(t.getDesserteReelleNo(1).getArrivee())
						.isAfter(dateToLocalDateTime(trajet.getDesserteReelleNo(1).getArrivee()))
				).collect(Collectors.toList());

		// Ne rien faire si il n'y en a pas
		if (sameParcours.isEmpty()) return;

		// Trouver le prochain train
		int nearestTrainId = 0;
		Duration tempDuration = Duration.of(1, ChronoUnit.DAYS);
		for (Trajet t : sameParcours) {
			Duration thisDuration = Duration.between(
					dateToLocalDateTime(t.getDesserteReelleNo(1).getArrivee()),
					dateToLocalDateTime(trajet.getDesserteReelleNo(1).getArrivee())
			);
			if (thisDuration.toSeconds() < tempDuration.toSeconds()) {
				tempDuration = thisDuration;
				nearestTrainId = t.getId();
			}
		}
		if (nearestTrainId == 0) return;

		Trajet nextTrajet = trajetDAO.find(nearestTrainId);
		List<DesserteReelle> dr_toUpdate = desserteReelleDAO.getAllOfTrajet(nearestTrainId).stream()
				.filter(d -> d.getSeq() > gareIndex)
				.filter(d -> !d.isDesservi())
				.collect(Collectors.toList());

		if (dr_toUpdate.isEmpty()) return;

		for (DesserteReelle dr : dr_toUpdate) {
			Duration d = Duration.between(
					dateToLocalDateTime(nextTrajet.getDesserteReelleNo(dr.getSeq() - 1).getArrivee()),
					dateToLocalDateTime(nextTrajet.getNextDesservieAfter(dr.getSeq()).getArrivee())
			);

			Date newDesserteDate = localDateTimeToDate(
					dateToLocalDateTime(
							nextTrajet.getDesserteReelleNo(dr.getSeq() - 1).getArrivee()
					).plus(d.toSeconds()/2, ChronoUnit.SECONDS)
			);

			desserteReelleDAO.setDesservi(dr, new Object[]{true, newDesserteDate});
		}
	}

	/**
	 * - UN TER POURRA ATTENDRE UN TGV MAIS PAS L'INVERSE
	 */
	public void waitForThisTrainWhenTER(Trajet trajet, LiveInfo liveInfo, Duration delay) {
    	// Ne garder que les DR après la dernière gare parcourue
		List<DesserteReelle> desserteReelles = trajet.getDesserteReelles().stream()
				.filter(d -> d.getSeq() >= liveInfo.getNextGareIndex())
				.collect(Collectors.toList());

		// Pour ne pas affecter le même train plusieurs fois si plusieurs gares communes
        List<Integer> affectedTrainIds = new ArrayList<>();

		for (DesserteReelle desserteReelle : desserteReelles) {
			Gare thisGare = gareDAO.getGareBySeqNumber(trajet, desserteReelle.getSeq());
			List<Trajet> trajets;
			try {
				// Tous les trajets qui passent par cette gare
				trajets = trajetDAO.getTrajetsByGareId(thisGare.getId());
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			// Ne garder que les TER passant par cette gare moins d'1h après de ce train
			trajets = trajets.stream().filter(t -> t.getType().equals("TER"))
					.filter(t ->
							Duration.between(
									dateToLocalDateTime(trajet.getArriveeTheoriqueToGare(thisGare.getId())),
									dateToLocalDateTime(t.getArriveeTheoriqueToGare(thisGare.getId()))
							).toMinutes() <= 60
					)
					.filter(t ->
                            dateToLocalDateTime(t.getArriveeTheoriqueToGare(thisGare.getId()))
                                    .isAfter(dateToLocalDateTime(trajet.getArriveeTheoriqueToGare(thisGare.getId())))
							|| dateToLocalDateTime(t.getArriveeTheoriqueToGare(thisGare.getId()))
                                    .isEqual(dateToLocalDateTime(trajet.getArriveeTheoriqueToGare(thisGare.getId())))
					)
					.collect(Collectors.toList());

			if (trajets.isEmpty()) continue;

			for (Trajet t : trajets) {
			    if (affectedTrainIds.contains(t.getId())) continue;

				List<DesserteReelle> dr_toUpdate = desserteReelleDAO.getAllOfTrajet(t.getId()).stream()
						.filter(d -> d.getSeq() >= t.getDesserteReelleOfGare(thisGare.getId()).getSeq())
						.filter(DesserteReelle::isDesservi)
						.collect(Collectors.toList());

				for (DesserteReelle dr : dr_toUpdate) {

					Date newDesserteDate = localDateTimeToDate(
							dateToLocalDateTime(dr.getArrivee())
									.plus(delay.toSeconds() + 600, ChronoUnit.SECONDS)
					);

					desserteReelleDAO.setDesservi(dr, new Object[]{true, newDesserteDate});
				}

				affectedTrainIds.add(t.getId());
			}

		}
	}











	/*
     * TODO : Inclure JMS pour transmettre le retard aux InfoGares
     */
	@Override
	public void processDelayWithCondition(LiveInfo liveInfo, int id, String condition) {
		log.info("Received Delay condition of train " + id);
		log.info("Delay condition " + condition);

		Trajet trajet = trajetDAO.find(id);

		log.info("Got train : " + trajet.toString());

		Duration delay = calculateIfDelay(trajet, liveInfo);
		log.info(printColor("Delay is " + delay.toMinutes() + " min", ANSI_BLUE));

		Duration finalDelay;

		switch (condition) {
			case "Pluie" :
				finalDelay = delay.plusMinutes(10);
				log.info("Delay with Condition is " + finalDelay.toMinutes() + " min");
				break;
			case "AccidentHumain" :
				finalDelay = delay.plus(Duration.ofMinutes(20));
				log.info("Delay with Condition is " + finalDelay.toMinutes() + " min");
				break;
			case "PanneElec" :
				finalDelay = delay.plus(Duration.ofMinutes(30));
				log.info("Delay with Condition is " + finalDelay.toMinutes() + " min");
				break;
			default :
				finalDelay = delay.plus(Duration.ofMinutes(5));
				log.info("Delay with Condition is " + finalDelay.toMinutes() + " min");
				break;
		}

		List<DesserteReelle> newDesserteInfo;
		if (Math.abs(delay.toSeconds()) > 0) {
			newDesserteInfo = trajet.getDesserteReelles().stream()
					.map(dr -> {
						if (dr.getSeq() >= liveInfo.getNextGareIndex() && dr.isDesservi())
							dr.addDuration(finalDelay);
						return dr;
					}).collect(Collectors.toList());
			trajetDAO.setDessertesReelles(trajet, newDesserteInfo);
		}
	}






	@Override
	public void processCancel(int id, String conditions) {
		// TODO Auto-generated method stub

	}

	@Override
	public Trajet processGetTrain(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Trajet> processGetTrainList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Trajet> processGetTrainArriveeGareList(Gare gare) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Trajet> processGetTrainDepartGareList(Gare gare) {
		// TODO Auto-generated method stub
		return null;
	}


}
