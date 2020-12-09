package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Gare;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
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
import static fr.pantheonsorbonne.ufr27.miage.util.TimeUtil.calculateIfDelay;

@ApplicationScoped
@ManagedBean
@Slf4j
public class TrainServiceImpl implements TrainService {
    @Inject
    TrajetDAO trajetDAO;

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
                            dr.addDuration(delay);
                        return dr;
                    }).collect(Collectors.toList());
            trajetDAO.setDessertesReelles(trajet, newDesserteInfo);
        }

        if (delay.toMinutes() > 120) {
			processExceptionalStop(trajet, liveInfo.getTimestamp(), liveInfo.getNextGareIndex());
		}

    }

	private void processExceptionalStop(Trajet trajet, String timestamp, int atGare) {

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
