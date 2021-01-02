package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.service.RuptureService;
import fr.pantheonsorbonne.ufr27.miage.service.StopService;
import fr.pantheonsorbonne.ufr27.miage.service.TERService;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

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
	RuptureService ruptureService;

    @Inject
	StopService stopService;

    @Inject
	TERService terService;

	/**
	 * Méthode principale, déclenchée à la réception des informations en direct depuis le train ({@link LiveInfo})
	 * <ul>
	 *     <li> Reçoit l'information en direct du train, recupère le trajet concerné depuis la base de données. </li>
	 *     <li> Détermine si un retard est survenu à l'aide de
	 *     		{@link fr.pantheonsorbonne.ufr27.miage.util.TimeUtil#calculateIfDelay(Trajet, LiveInfo)}. </li>
	 *     <li> Déclenche les règles métier en appelant les différents services selon les circonstances. </li>
	 * </ul>
	 * @param liveInfo	Les informations en direct sur le trajet, envoyées par le train
	 * @param trajetId	ID du trajet concerné
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

        // Arrêt exceptionnel
        if (delay.toMinutes() >= 120) {
			stopService.processExceptionalStop(trajet, liveInfo.getNextGareIndex());
		}

        // TER attend TGV si retard entre 1h et 3h
        if (delay.toMinutes() >= 60 && delay.toMinutes() <= 180) {
        	terService.waitForThisTrainWhenTER(trajet, liveInfo, delay);
		}

        // Comptabiliser la rupture de correspondance si retard a moins de 2h30 de retard
		if (delay.toMinutes() <= 150) {
			ruptureService.processRuptureCorrespondance(trajet);
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
