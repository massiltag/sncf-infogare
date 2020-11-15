package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.DelayInfo;

public interface TrainService {

    public void processDelay(DelayInfo infoRetard);
}
