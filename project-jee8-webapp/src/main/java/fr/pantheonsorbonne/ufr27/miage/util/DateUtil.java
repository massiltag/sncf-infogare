package fr.pantheonsorbonne.ufr27.miage.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    /**
     * Format de dat choisi pour simplifier le DTO {@link fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo}
     */
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Convertit une date de type {@link Date} en {@link LocalDateTime}
     * @param dateToConvert objet {@link Date}
     * @return la date d'entrée en {@link LocalDateTime}
     */
    public static LocalDateTime dateToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Convertit une date de type {@link LocalDateTime} en {@link Date}
     * @param dateToConvert objet {@link LocalDateTime}
     * @return la date d'entrée en {@link Date}
     */
    public static Date localDateTimeToDate(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    /**
     * Convertit une chaîne de caractères en {@link LocalDateTime} selon le {@link #formatter}
     * @param str
     * @return Date au format {@link LocalDateTime}
     */
    public static LocalDateTime stringToLocalDateTime(String str) {
        return LocalDateTime.parse(str, formatter);
    }
}
