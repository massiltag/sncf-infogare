package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;

public interface RuptureService {

    public void processRuptureCorrespondance(Trajet trajet);

    public void delayIfEnoughRuptures();
}
