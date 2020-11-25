package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Duration;

@ApplicationScoped
@ManagedBean
public class TrainServiceImpl implements TrainService {

    @Inject
    EntityManager em;

    @Override
    public void processLiveInfo(LiveInfo liveInfo) {
        String idTrain = liveInfo.getIdTrain();
        /**
         * ICI : utiliser entityManager pour
         *      - Récupérer le train dont l'ID est idTrain
         *          Train train = em.find(Train.class, idTrain);
         *      - Mettre à jour les heures d'arrivée réelles (à la gare concernée) en base
         *      - Comparer les horaires réelles aux horaires théoriques
         *          => Déterminer si retard (fixer une marge)
         *          => Déclencher triggerDelay éventuellement
         */
    }

    @Override
    public void triggerDelay(String idTrain, Duration duration) {
        /**
         * Autres règles
         */
    }
}
