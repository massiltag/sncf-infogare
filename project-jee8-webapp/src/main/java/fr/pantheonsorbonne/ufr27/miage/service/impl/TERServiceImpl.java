package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.dao.DesserteReelleDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.service.TERService;

import javax.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.dateToLocalDateTime;
import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.localDateTimeToDate;

/**
 * <p>
 *     Service qui se charge de gérer les opérations concernant les TER.
 * </p>
 */
public class TERServiceImpl implements TERService {

    @Inject
    TrajetDAO trajetDAO;

    @Inject
    DesserteReelleDAO desserteReelleDAO;

    @Inject
    GareDAO gareDAO;

    /**
     * <p>
     *     Implémentation de la règle métier où <b>les TER passant par les gares desservies
     *     par un TGV en retard attendent ce TGV.</b>
     * </p>
     * @param trajet	Le trajet concerné
     * @param liveInfo	Les informations en direct
     * @param delay		Le retard calculé
     */
    public void waitForThisTrainWhenTER(Trajet trajet, LiveInfo liveInfo, Duration delay) {
        // Ne garder que les DR après la dernière gare parcourue
        List<DesserteReelle> desserteReelles = trajet.getDesserteReelles().stream()
                .filter(d -> d.getSeq() >= liveInfo.getNextGareIndex())
                .collect(Collectors.toList());

        // Pour ne pas affecter le même train plusieurs fois si plusieurs gares communes
        List<Integer> affectedTrainIds = new ArrayList<>();

        for (DesserteReelle desserteReelle : desserteReelles) {
            Gare thisGare = gareDAO.getGareBySeqNumber(trajet, desserteReelle.getSeq());
            List<Trajet> trajets;
            try {
                // Tous les trajets qui passent par cette gare
                trajets = trajetDAO.getTrajetsByGareId(thisGare.getId());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            // Ne garder que les TER passant par cette gare moins d'1h après de ce train
            trajets = trajets.stream().filter(t -> t.getType().equals("TER"))
                    .filter(t ->
                            Duration.between(
                                    dateToLocalDateTime(trajet.getArriveeTheoriqueToGare(thisGare.getId())),
                                    dateToLocalDateTime(t.getArriveeTheoriqueToGare(thisGare.getId()))
                            ).toMinutes() <= 60
                    )
                    .filter(t ->
                            dateToLocalDateTime(t.getArriveeTheoriqueToGare(thisGare.getId()))
                                    .isAfter(dateToLocalDateTime(trajet.getArriveeTheoriqueToGare(thisGare.getId())))
                                    || dateToLocalDateTime(t.getArriveeTheoriqueToGare(thisGare.getId()))
                                    .isEqual(dateToLocalDateTime(trajet.getArriveeTheoriqueToGare(thisGare.getId())))
                    )
                    .collect(Collectors.toList());

            if (trajets.isEmpty()) continue;

            // Retarder les trajets à partir de cette gare
            for (Trajet t : trajets) {
                if (affectedTrainIds.contains(t.getId())) continue;

                List<DesserteReelle> dr_toUpdate = desserteReelleDAO.getAllOfTrajet(t.getId()).stream()
                        .filter(d -> d.getSeq() >= t.getDesserteReelleOfGare(thisGare.getId()).getSeq())
                        .filter(DesserteReelle::isDesservi)
                        .collect(Collectors.toList());

                for (DesserteReelle dr : dr_toUpdate) {

                    Date newDesserteDate = localDateTimeToDate(
                            dateToLocalDateTime(dr.getArrivee())
                                    .plus(delay.toSeconds() + 600, ChronoUnit.SECONDS)
                    );

                    dr.setArrivee(newDesserteDate);
                    desserteReelleDAO.setDesservi(dr, new Object[]{true, newDesserteDate});
                }

                affectedTrainIds.add(t.getId());
            }

        }
    }

}
