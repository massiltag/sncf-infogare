package fr.pantheonsorbonne.ufr27.miage.resource;

import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteTheorique;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.localDateTimeToDate;

/**
 * Test Endpoint used to populate DB.
 * Call GET http://localhost:8080/db/populate
 */
@Path("db")
public class DatabaseEndpoint {

    @Inject
    EntityManager em;

    @GET
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("populate")
    public Response populateDB() {
        try {
            System.out.println("TEST PERSISTENCE");
            em.getTransaction().begin();

            Gare gare = Gare.builder().nom("Bordeaux Saint-Jean").build();
            Gare gare2 = Gare.builder().nom("Paris Montparnasse").build();
            Gare gare3 = Gare.builder().nom("Gare d'Amiens").build();
            Gare gare4 = Gare.builder().nom("Lille Flandres").build();

            LocalDateTime baseDate = LocalDateTime.of(2020, 1, 1, 12, 0);

            /* Populate with data :
                Bordeaux 12h
                Paris 13h
                Amiens -
                Lille 14h
             */
            Trajet trajet = Trajet.builder()
                    .desserteTheoriques(List.of(
                                    DesserteTheorique.builder()
                                            .gare(gare)
                                            .arrivee(localDateTimeToDate(baseDate))
                                            .desservi(true)
                                            .build(),
                                    DesserteTheorique.builder()
                                            .gare(gare2)
                                            .arrivee(localDateTimeToDate(baseDate.plusHours(1)))
                                            .desservi(true)
                                            .build(),
                                    DesserteTheorique.builder()
                                            .gare(gare4)
                                            .arrivee(localDateTimeToDate(baseDate.plusHours(2)))
                                            .desservi(true)
                                            .build()
                    ))
                    .desserteReelles(List.of(
                            DesserteReelle.builder()
                                    .gare(gare)
                                    .arrivee(localDateTimeToDate(baseDate))
                                    .desservi(true)
                                    .build(),
                            DesserteReelle.builder()
                                    .gare(gare2)
                                    .arrivee(localDateTimeToDate(baseDate.plusHours(1)))
                                    .desservi(true)
                                    .build(),
                            DesserteReelle.builder()
                                    .gare(gare4)
                                    .arrivee(localDateTimeToDate(baseDate.plusHours(2)))
                                    .desservi(true)
                                    .build()
                    ))
                    .build();

            /* Populate with data :
                Bordeaux 11h
                Paris 12h
                Amiens 12h30
                Lille 13h
             */
            Trajet trajet2 = Trajet.builder()
                    .desserteTheoriques(List.of(
                            DesserteTheorique.builder()
                                    .gare(gare)
                                    .arrivee(localDateTimeToDate(baseDate.minusHours(1)))
                                    .desservi(true)
                                    .build(),
                            DesserteTheorique.builder()
                                    .gare(gare2)
                                    .arrivee(localDateTimeToDate(baseDate.plusHours(0)))
                                    .desservi(true)
                                    .build(),
                            DesserteTheorique.builder()
                                    .gare(gare3)
                                    .arrivee(localDateTimeToDate(baseDate.plusMinutes(30)))
                                    .desservi(true)
                                    .build(),
                            DesserteTheorique.builder()
                                    .gare(gare4)
                                    .arrivee(localDateTimeToDate(baseDate.plusHours(1)))
                                    .desservi(true)
                                    .build()
                    ))
                    .desserteReelles(List.of(
                            DesserteReelle.builder()
                                    .gare(gare)
                                    .arrivee(localDateTimeToDate(baseDate.minusHours(1)))
                                    .desservi(true)
                                    .build(),
                            DesserteReelle.builder()
                                    .gare(gare2)
                                    .arrivee(localDateTimeToDate(baseDate.plusHours(0)))
                                    .desservi(true)
                                    .build(),
                            DesserteReelle.builder()
                                    .gare(gare3)
                                    .arrivee(localDateTimeToDate(baseDate.plusMinutes(30)))
                                    .desservi(true)
                                    .build(),
                            DesserteReelle.builder()
                                    .gare(gare4)
                                    .arrivee(localDateTimeToDate(baseDate.plusHours(1)))
                                    .desservi(true)
                                    .build()
                    ))
                    .build();


            em.persist(trajet);

            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(201).build();
    }

}
