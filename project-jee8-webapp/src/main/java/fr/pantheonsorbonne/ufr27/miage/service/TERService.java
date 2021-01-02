package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;

import java.time.Duration;

public interface TERService {

    void waitForThisTrainWhenTER(Trajet trajet, LiveInfo liveInfo, Duration delay);

}
