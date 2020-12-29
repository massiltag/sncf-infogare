package fr.pantheonsorbonne.ufr27.miage.util;

import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.*;
import static fr.pantheonsorbonne.ufr27.miage.util.StringUtil.ANSI_RED;
import static fr.pantheonsorbonne.ufr27.miage.util.StringUtil.printColor;

@Slf4j
public class TimeUtil {
    private final static int TOLERATED_PERCENTAGE = 10;

    /**
     * <p>
     *     Calcule le retard d'un train selon les informations reçues le concernant.<br>
     *     Compare le pourcentage et l'heure reçus au taux normal (linéaire) et détermine
     *     le retard au format {@link Duration}
     * </p>
     * @param trajet    Le trajet concerné
     * @param liveInfo  Les informations en direct sur le trajet, envoyées par le train
     * @return Le retard au format {@link Duration}
     */
    public static Duration calculateIfDelay(Trajet trajet, LiveInfo liveInfo) {
        double normalPercentage = shouldBeAtPercent(
                dateToLocalDateTime(trajet.getDesserteTheoriques().get(liveInfo.getLastGareIndex() - 1).getArrivee()),
                dateToLocalDateTime(trajet.getDesserteTheoriques().get(liveInfo.getNextGareIndex() - 1).getArrivee()),
                stringToLocalDateTime(liveInfo.getTimestamp())
        );

        log.info(printColor("Normal percentage is " + normalPercentage + "%", ANSI_RED));

        double diff = liveInfo.getPercentage() - normalPercentage;
        log.info(printColor("Diff is " + diff + "%", ANSI_RED));
        if (Math.abs(diff) >= TOLERATED_PERCENTAGE) {
            Duration duration = Duration.between(
                    dateToLocalDateTime(trajet.getDesserteTheoriqueNo(liveInfo.getLastGareIndex()).getArrivee()),
                    dateToLocalDateTime(trajet.getDesserteTheoriqueNo(liveInfo.getNextGareIndex()).getArrivee())
            );
            // diff% = pourcentage de la Duration entre le dep. et l'arr. théorique
            long secondsLong = (long) (duration.toSeconds()*Math.abs(diff)/100);
            if (diff < 0) {
                return Duration.ofSeconds(secondsLong);
            } else {
                return Duration.ofSeconds(0).minus(Duration.ofSeconds(secondsLong));
            }
        }
        return Duration.ofMillis(0);
    }

    /**
     * <p>
     *     Calcule le pourcentage normal (position d'une date entre deux dates)
     * </p>
     * @param departure Date de départ
     * @param arrival   Date d'arrivée
     * @param live      Date reçue
     * @return  Pourcentage entre le départ et l'arrivée
     */
    public static double shouldBeAtPercent(LocalDateTime departure, LocalDateTime arrival, LocalDateTime live) {
        Duration totalDuration = Duration.between(departure, arrival);
        Duration betweenDuration = Duration.between(departure, live);

        return ((double)betweenDuration.toSeconds()*100/(double)totalDuration.toSeconds());
    }

    /**
     * <p>
     *     Ajoute un objet {@link Duration} à une {@link Date} en convertissant à {@link LocalDateTime}
     *     car impossible sur {@link Date}
     * </p>
     * @param date
     * @param duration
     * @return Objet {@link Date} modifié
     */
    public static Date addDurationToDate(Date date, Duration duration) {
        return localDateTimeToDate(
                dateToLocalDateTime(date).plus(duration)
        );
    }
}
