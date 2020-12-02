package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Gare;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.dateToLocalDateTime;
import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.stringToLocalDateTime;
import static fr.pantheonsorbonne.ufr27.miage.util.StringUtil.*;

@ApplicationScoped
@ManagedBean
@Slf4j
public class TrainServiceImpl implements TrainService {
    @Inject
    EntityManager em;

    private static int TOLERATED_PERCENTAGE = 10;

    /**
     * - Récupère le trajet concerné depuis la base de données
     * - Détermine si un retard a survenu à l'aide de calculateIfDelay
     * - Modifie les temps d'arrivée réels en fonction du retard (éventuellement)
     */
    @Override
    public void processLiveInfo(LiveInfo liveInfo, int trajetId) {
        log.info("Received LiveInfo of train " + trajetId);
        log.info(printColor("Received " + liveInfo.toString(), ANSI_BLUE));
        Trajet trajet = em.find(Trajet.class, trajetId);
        log.info("Got train : " + trajet.toString());

        em.getTransaction().begin();

        Duration delay = calculateIfDelay(trajet, liveInfo);
        log.info(printColor("Delay is " + delay.toMinutes() + " min", ANSI_BLUE));

        List<DesserteReelle> newDesserteInfo;
        if (delay.toSeconds() > 0) {
            newDesserteInfo = trajet.getDesserteReelles().stream()
                    .map(dr -> {
                        if (dr.getSeq() >= liveInfo.getNextGareIndex() && dr.isDesservi())
                            dr.addDuration(delay);
                        return dr;
                    }).collect(Collectors.toList());
            trajet.setDesserteReelles(newDesserteInfo);
        }

        em.getTransaction().commit();
    }

    @Override
    public void triggerDelay(String idTrain, Duration duration) {
        /**
         * Autres règles
         */
    }

    public Duration calculateIfDelay(Trajet trajet, LiveInfo liveInfo) {
        /**
         * ICI : Récupérer les informations théoriques du train depuis la base de données,
         *  Comparer avec les informations reçues :
         *      - train = jpa.find(trainId)
         *      - List<Desserte> dessertes = train.getDesserte()
         *      - planifiedDeparture = dessertes.get(lastGareId)...
         *      - planifiedArrival = dessertes.get(nextGareId)...
         */
        int normalPercentage = shouldBeAtPercent(
                dateToLocalDateTime(trajet.getDesserteReelles().get(liveInfo.getLastGareIndex() - 1).getArrivee()),
                dateToLocalDateTime(trajet.getDesserteReelles().get(liveInfo.getNextGareIndex() - 1).getArrivee()),
                stringToLocalDateTime(liveInfo.getTimestamp())
        );

        log.info(printColor("Normal percentage is " + normalPercentage + "%", ANSI_RED));

        int diff = liveInfo.getPercentage() - normalPercentage;
        log.info(printColor("Diff is " + diff + "%", ANSI_RED));
        if (Math.abs(diff) >= TOLERATED_PERCENTAGE) {
            if (diff < 0) {
                Duration duration = Duration.between(
                        dateToLocalDateTime(trajet.getDesserteTheoriqueNo(liveInfo.getLastGareIndex()).getArrivee()),
                        dateToLocalDateTime(trajet.getDesserteTheoriqueNo(liveInfo.getNextGareIndex()).getArrivee())
                );
                // Retard de diff% = pourcentage de la Duration entre le dep. et l'arr. théorique
                long secondsLong = duration.toSeconds()*Math.abs(diff)/100;
                Duration delay = Duration.ofSeconds(secondsLong);
                return delay;
            } else {
                // TODO avance()
            }
        }
        return Duration.ofMillis(0);
    }

    public int shouldBeAtPercent(LocalDateTime departure, LocalDateTime arrival, LocalDateTime live) {
        Duration totalDuration = Duration.between(departure, arrival);
        Duration betweenDuration = Duration.between(departure, live);

        return (int)((double)betweenDuration.toSeconds()*100/(double)totalDuration.toSeconds());
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
