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

    public static Duration calculateIfDelay(Trajet trajet, LiveInfo liveInfo) {
        /**
         * ICI : Récupérer les informations théoriques du train depuis la base de données,
         *  Comparer avec les informations reçues
         */
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

    public static double shouldBeAtPercent(LocalDateTime departure, LocalDateTime arrival, LocalDateTime live) {
        Duration totalDuration = Duration.between(departure, arrival);
        Duration betweenDuration = Duration.between(departure, live);

        return ((double)betweenDuration.toSeconds()*100/(double)totalDuration.toSeconds());
    }


    public static Date addDurationToDate(Date date, Duration duration) {
        return localDateTimeToDate(
                dateToLocalDateTime(date).plus(duration)
        );
    }
}
