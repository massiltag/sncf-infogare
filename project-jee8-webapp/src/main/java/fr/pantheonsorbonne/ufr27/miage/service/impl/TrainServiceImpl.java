package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Duration;
import java.time.LocalDateTime;

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

    public void calculateDelay(LiveInfo liveInfo) {
        /**
         * ICI : Récupérer les informations théoriques du train depuis la base de données,
         *  Comparer avec les informations reçues :
         *      - train = jpa.find(trainId)
         *      - List<Desserte> dessertes = train.getDesserte()
         *      - planifiedDeparture = dessertes.get(lastGareId)...
         *      - planifiedArrival = dessertes.get(nextGareId)...
         */
        LocalDateTime planifiedDeparture = null;
        LocalDateTime planifiedArrival = null;
        LocalDateTime now = liveInfo.getTimestamp();

        int diff = liveInfo.getPercentage() - shouldBeAtPercent(planifiedDeparture, planifiedArrival, now);
        if (Math.abs(diff) > 10) {
            if (diff < 0) {
                // retard() de diff% = pourcentage de la duration entre planifiedDep et planifiedArr
            } else {
                // avance()
            }
        }

    }

    public int shouldBeAtPercent(LocalDateTime departure, LocalDateTime arrival, LocalDateTime live) {
        Duration totalDuration = Duration.between(departure, arrival);
        Duration betweenDuration = Duration.between(departure, live);

        return (int)((double)betweenDuration.toSeconds()*100/(double)totalDuration.toSeconds());
    }
}
