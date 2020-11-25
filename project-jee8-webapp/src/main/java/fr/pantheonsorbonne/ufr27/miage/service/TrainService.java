package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArrivalInfo;

import java.time.Duration;

public interface TrainService {

    public void processArrivalInfo(ArrivalInfo arrivalInfo);

    public void triggerDelay(String idTrain, Duration duration);
}
