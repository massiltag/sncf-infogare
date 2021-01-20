package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.dao.DesserteReelleDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.service.StopService;

import javax.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.dateToLocalDateTime;
import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.localDateTimeToDate;

/**
 * <p>
 *     Service qui se charge de gérer les opérations concernant les arrêts et la desserte des gares.
 * </p>
 */
public class StopServiceImpl implements StopService {

    @Inject
    TrajetDAO trajetDAO;

    @Inject
    DesserteReelleDAO desserteReelleDAO;

    /**
     * <p>
     *     Implémentation de la règle métier où <b>si un train a un retard de plus de 2 heures à une gare,
     *     le prochain train sur son parcours desservira cette gare s'il ne la desservait pas avant.</b>
     * </p>
     * @param trajet	Le trajet concerné
     * @param gareIndex	L'index de la gare sur le trajet
     */
    public void processExceptionalStop(Trajet trajet, int gareIndex) {
        // Récupérer les trajets ayant le même parcours géographique
        List<Trajet> sameParcours = trajetDAO.getTrajetsByParcoursId(trajet.getParcoursId());

        // Filtrer : Ne garder que les trajets partis après celui en retard
        sameParcours = sameParcours.stream()
                .filter(t -> dateToLocalDateTime(t.getDesserteReelleNo(1).getArrivee())
                        .isAfter(dateToLocalDateTime(trajet.getDesserteReelleNo(1).getArrivee()))
                ).collect(Collectors.toList());

        // Ne rien faire si il n'y en a pas
        if (sameParcours.isEmpty()) return;

        // Trouver le prochain train
        int nearestTrainId = 0;
        Duration tempDuration = Duration.of(1, ChronoUnit.DAYS);
        for (Trajet t : sameParcours) {
            Duration thisDuration = Duration.between(
                    dateToLocalDateTime(t.getDesserteReelleNo(1).getArrivee()),
                    dateToLocalDateTime(trajet.getDesserteReelleNo(1).getArrivee())
            );
            if (thisDuration.toSeconds() < tempDuration.toSeconds()) {
                tempDuration = thisDuration;
                nearestTrainId = t.getId();
            }
        }
        if (nearestTrainId == 0) return;

        Trajet nextTrajet = trajetDAO.find(nearestTrainId);

        // Filtre : ne garder que les dessertes concernées
        List<DesserteReelle> dr_toUpdate = desserteReelleDAO.getAllOfTrajet(nearestTrainId).stream()
                .filter(d -> d.getSeq() >= gareIndex)
                .filter(d -> !d.isDesservi())
                .collect(Collectors.toList());

        if (dr_toUpdate.isEmpty()) return;

        // Appliquer l'arrêt exceptionnel
        for (DesserteReelle dr : dr_toUpdate) {
            Duration d = Duration.between(
                    dateToLocalDateTime(nextTrajet.getDesserteReelleNo(dr.getSeq() - 1).getArrivee()),
                    dateToLocalDateTime(nextTrajet.getNextDesservieAfter(dr.getSeq()).getArrivee())
            );

            Date newDesserteDate = localDateTimeToDate(
                    dateToLocalDateTime(
                            nextTrajet.getDesserteReelleNo(dr.getSeq() - 1).getArrivee()
                    ).plus(d.toSeconds()/2, ChronoUnit.SECONDS)
            );

            dr.setDesservi(true);
            dr.setArrivee(newDesserteDate);
            desserteReelleDAO.setDesservi(dr, new Object[]{true, newDesserteDate});
        }
    }

}
