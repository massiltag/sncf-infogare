package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;

public interface StopService {

    void processExceptionalStop(Trajet trajet, int gareIndex);

}
