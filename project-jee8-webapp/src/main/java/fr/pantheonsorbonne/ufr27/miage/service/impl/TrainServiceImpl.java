package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.dao.DesserteReelleDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InfoDTO;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InfoTypeEnum;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.service.*;
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
	DesserteReelleDAO desserteReelleDAO;

    @Inject
	RuptureService ruptureService;

    @Inject
	StopService stopService;

    @Inject
	TERService terService;

    @Inject
	InfogareSenderService infogareSenderService;

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

		// Mettre à jour les infogares // sendGareStates(trajet);
		updateInfogares();

    }

    public void sendGareStates(Trajet trajet) {
    	List<DesserteReelle> dessertes = desserteReelleDAO.getAllOfTrajet(trajet.getId());

		// Itérer sur les gares
    	for (DesserteReelle desserteReelle : dessertes) {
    		// Construire l'information
    		InfoDTO infoDTO = InfoDTO.builder()
					.trainId(trajet.getId())
					.trainName(trajet.getName())
					.trainType(trajet.getType())
					.timestamp(String.valueOf(desserteReelle.getArrivee()))
					.build();

    		// Première gare, envoyer un DTO JAXB Départ
			if (desserteReelle.getSeq() == 1)
				infoDTO.setInfoType(InfoTypeEnum.DEPARTURE.value());
			else
			// Dernière gare, envoyer un DTO JAXB Arrivée
			if (desserteReelle.getSeq() == dessertes.size())
				infoDTO.setInfoType(InfoTypeEnum.ARRIVAL.value());
			else
			// Gare intermédiaire, envoyer un DTO JAXB Passage
				infoDTO.setInfoType(InfoTypeEnum.TRANSIT.value());

			// Envoyer l'information
			infogareSenderService.send(desserteReelle.getGare().getCode(), infoDTO);
		}

	}

	/**
	 * <p>
	 *     Appelée au lancement de l'application, sert à envoyer les informations initiales aux infogares.
	 * </p>
	 */
	public void updateInfogares() {
		for (Trajet t : trajetDAO.findAll())
			sendGareStates(t);
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
			case "Seisme_DANGER" :
				finalDelay = null;
				trajetDAO.delete(trajet);
				log.info("Train " + id + " was canceled due to earthquake.");
				break;
			default :
				finalDelay = delay.plus(Duration.ofMinutes(5));
				log.info("Delay with Condition is " + finalDelay.toMinutes() + " min");
				break;
		}
	
		List<DesserteReelle> newDesserteInfo;
		if (finalDelay != null) {	
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
		
		sendGareStatesDelay(trajet);
	}
	
	public void sendGareStatesDelay(Trajet trajet) {
    	List<DesserteReelle> dessertes = desserteReelleDAO.getAllOfTrajet(trajet.getId());

		// Itérer sur les gares
    	for (DesserteReelle desserteReelle : dessertes) {
    		// Construire l'information
    		InfoDTO infoDTO = InfoDTO.builder()
					.trainId(trajet.getId())
					.trainName(trajet.getName())
					.trainType(trajet.getType())
					.timestamp(String.valueOf(desserteReelle.getArrivee()))
					.build();
    		
    		// Pour toutes les dessertes reelles de ce trajet, affiche DISRUPTION sur l'infogare 
		    		if (desserteReelle.getTrajet().getId() == trajet.getId())
						infoDTO.setInfoType(InfoTypeEnum.DISRUPTION.value());
		    		else		
		    		if (desserteReelle.getSeq() == 1)
						infoDTO.setInfoType(InfoTypeEnum.DEPARTURE.value());
					else
					// Dernière gare, envoyer un DTO JAXB Arrivée
					if (desserteReelle.getSeq() == dessertes.size())
						infoDTO.setInfoType(InfoTypeEnum.ARRIVAL.value());
					else
					// Gare intermédiaire, envoyer un DTO JAXB Passage
						infoDTO.setInfoType(InfoTypeEnum.TRANSIT.value());
		    		
			// Envoyer l'information			
			infogareSenderService.send(desserteReelle.getGare().getCode(), infoDTO);
		}
	}
}
