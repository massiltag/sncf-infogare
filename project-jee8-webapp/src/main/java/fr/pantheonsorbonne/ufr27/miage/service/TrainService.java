package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;

import java.time.Duration;

public interface TrainService {

    public void processLiveInfo(LiveInfo liveInfo);

    public void triggerDelay(String idTrain, Duration duration);
}
