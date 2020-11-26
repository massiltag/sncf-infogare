package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static fr.pantheonsorbonne.ufr27.miage.util.Utils.*;

@ApplicationScoped
@ManagedBean
@Slf4j
public class TrainServiceImpl implements TrainService {
    @Inject
    EntityManager em;

    @Override
    public void processLiveInfo(LiveInfo liveInfo) {
        /**
         * ICI : utiliser entityManager pour
         *      - Récupérer le train dont l'ID est idTrain
         *          Train train = em.find(Train.class, idTrain);
         *      - Mettre à jour les heures d'arrivée réelles (à la gare concernée) en base
         *      - Comparer les horaires réelles aux horaires théoriques
         *          => Déterminer si retard (fixer une marge)
         *          => Déclencher triggerDelay éventuellement
         */
        log.info("Received LiveInfo of train " + liveInfo.getIdTrajet());
        int idTrajet = liveInfo.getIdTrajet();
        Trajet trajet = em.find(Trajet.class, idTrajet);
        log.info("Got train : " + trajet.toString());

        em.getTransaction().begin();

        int normalPercentage = shouldBeAtPercent(
                dateToLocalDateTime(trajet.getDesserteReelles().get(liveInfo.getLastGareIndex() - 1).getArrivee()),
                dateToLocalDateTime(trajet.getDesserteReelles().get(liveInfo.getNextGareIndex() - 1).getArrivee()),
                liveInfo.getTimestamp()
        );

        calculateIfDelay(liveInfo, normalPercentage);

        em.getTransaction().commit();
    }

    @Override
    public void triggerDelay(String idTrain, Duration duration) {
        /**
         * Autres règles
         */
    }

    public void calculateIfDelay(LiveInfo liveInfo, int normalPercentage) {
        /**
         * ICI : Récupérer les informations théoriques du train depuis la base de données,
         *  Comparer avec les informations reçues :
         *      - train = jpa.find(trainId)
         *      - List<Desserte> dessertes = train.getDesserte()
         *      - planifiedDeparture = dessertes.get(lastGareId)...
         *      - planifiedArrival = dessertes.get(nextGareId)...
         */
        int diff = liveInfo.getPercentage() - normalPercentage;
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
