package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.dao.CorrespondanceDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.DesserteReelleDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.PassagerDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Correspondance;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.Passager;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.service.RuptureService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.dateToLocalDateTime;
import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.localDateTimeToDate;

@ApplicationScoped
@ManagedBean
@Slf4j
public class RuptureServiceImpl implements RuptureService {

    private static int TOLERATED_NB_RUPTURES = 5;

    @Inject
    TrajetDAO trajetDAO;

    @Inject
    PassagerDAO passagerDAO;

    @Inject
    CorrespondanceDAO correspondanceDAO;

    @Inject
    DesserteReelleDAO desserteReelleDAO;

    /**
     * <p>
     *     Méthode qui détermine si les correspondances des passagers d'un train sont en rupture. <br>
     *     Persiste le résultat en base (champs rupture: boolean, newDate: Date)
     * </p>
     *
     * @param trajet    le trajet dont on a reçu l'information
     */
    public void processRuptureCorrespondance(Trajet trajet) {
        // Récupérer tous les passagers de ce trajet, qui ont une correspondance à faire
        List<Passager> passagers = passagerDAO.findByTrainId(trajet.getId())
                .stream().filter(p -> p.getCorrespondance() != null)
                .collect(Collectors.toList());

        // Récupérer la correspondance de chaque passager, et le trajet associé
        for (Passager passager : passagers) {
            Correspondance correspondance = passager.getCorrespondance();
            Trajet trajetCorresp = correspondance.getTrajet();

            // Si l'horaire du trajet de correspondance est inférieure (before) à l'arrivée de ce train (trajet)
            if (
                    (trajetCorresp.getDesserteReelleOfGare(correspondance.getGare().getId()).getArrivee()
                            .before(trajet.getDesserteReelleOfGare(correspondance.getGare().getId()).getArrivee()))
            ) {
                // Alors mettre la correspondance en rupture et mettre à jour l'horaire
                // du train de correspondance avec l'horaire du train (trajet) + 10 minutes
                correspondanceDAO.update(correspondance,
                        new Object[]{
                                true,
                                trajet.getDesserteReelleOfGare(correspondance.getGare().getId()).getArrivee()
                        }
                );
            }
        }

        delayIfEnoughRuptures();
    }


    /**
     * <p>
     *     Vérifie si il y a assez de passagers en rupture de correspondance ({@link #TOLERATED_NB_RUPTURES}) <br>
     *     Retarde le(s) train(s) concerné si oui, avec l'horaire du train le plus en retard + 10 minutes
     * </p>
     */
    @Override
    public void delayIfEnoughRuptures() {
        // Récupérer tous les trajets et itérer
        for (Trajet trajet : trajetDAO.findAll()) {
            // Récupérer les ruptures sur ce trajet
            List<Correspondance> ruptures = correspondanceDAO.findByTrainAndRupture(trajet.getId(), true);

            // Si > 50
            if (ruptures.size() >= TOLERATED_NB_RUPTURES) {
                // Trouver la correspondance la plus en retard
                LocalDateTime latestNewDate = LocalDateTime.MIN;
                Correspondance c = Correspondance.builder().build();
                for (Correspondance r : ruptures) {
                    LocalDateTime temp = dateToLocalDateTime(r.getNewDate());
                    if (temp.isAfter(latestNewDate)) {
                        latestNewDate = temp;
                        c = r;
                    }
                }

                // Retarder ce train à la nouvelle horaire de la correspondance + 10 minutes
                Duration delay = Duration.between(
                        dateToLocalDateTime(trajet.getDesserteReelleOfGare(c.getGare().getId()).getArrivee()),
                        latestNewDate.plusMinutes(10)
                );

                DesserteReelle fromThis = trajet.getDesserteReelleOfGare(c.getGare().getId());
                for (DesserteReelle desserteReelle : trajet.getDesserteReelles()) {
                    if (desserteReelle.getSeq() >= fromThis.getSeq()) {
                        LocalDateTime initialArrivee = dateToLocalDateTime(desserteReelle.getArrivee());
                        desserteReelleDAO.setDesservi(
                                desserteReelle,
                                new Object[]{true, localDateTimeToDate(initialArrivee.plus(delay))}
                        );
                    }
                }

            }

        }
    }

}
