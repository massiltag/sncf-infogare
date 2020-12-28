package fr.pantheonsorbonne.ufr27.miage.resource;

import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
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
import java.util.ArrayList;
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

    @Inject
    TrajetDAO trajetDAO;

    @GET
    @Path("test")
    public Response testDB() {
        List<Trajet> trajets = trajetDAO.getTrajetsByGareId(4);
        for (Trajet trajet : trajets) {
            System.out.println(trajet.toString());
        }
        return Response.status(201).build();
    }

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


            /* TGV 1
                Bordeaux 8h
                Paris 12h
                Amiens 12h30
                Lille 13h
             */
            Trajet trajet = Trajet.builder()
                    .id(1)
                    .desserteTheoriques(new ArrayList<>())
                    .desserteReelles(new ArrayList<>())
                    .parcoursId(1)
                    .type("TGV")
                    .build();
            trajet.addDesserteTheoriques(List.of(
                    DesserteTheorique.builder()
                            .gare(gare)
                            .arrivee(localDateTimeToDate(baseDate.minusHours(4)))
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
            ));
            trajet.addDesserteReelles(List.of(
                    DesserteReelle.builder()
                            .gare(gare)
                            .arrivee(localDateTimeToDate(baseDate.minusHours(4)))
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
            ));

            /* TGV 2
                Bordeaux 9h
                Paris 13h
                Amiens -
                Lille 14h
             */
            Trajet trajet2 = Trajet.builder()
                    .id(2)
                    .desserteTheoriques(new ArrayList<>())
                    .desserteReelles(new ArrayList<>())
                    .parcoursId(1)
                    .type("TGV")
                    .build();
            trajet2.addDesserteTheoriques(List.of(
                    DesserteTheorique.builder()
                            .gare(gare)
                            .arrivee(localDateTimeToDate(baseDate.minusHours(3)))
                            .desservi(true)
                            .build(),
                    DesserteTheorique.builder()
                            .gare(gare2)
                            .arrivee(localDateTimeToDate(baseDate.plusHours(1)))
                            .desservi(true)
                            .build(),
                    DesserteTheorique.builder()
                            .gare(gare3)
                            .desservi(false)
                            .build(),
                    DesserteTheorique.builder()
                            .gare(gare4)
                            .arrivee(localDateTimeToDate(baseDate.plusHours(2)))
                            .desservi(true)
                            .build()
            ));
            trajet2.addDesserteReelles(List.of(
                    DesserteReelle.builder()
                            .gare(gare)
                            .arrivee(localDateTimeToDate(baseDate.minusHours(3)))
                            .desservi(true)
                            .build(),
                    DesserteReelle.builder()
                            .gare(gare2)
                            .arrivee(localDateTimeToDate(baseDate.plusHours(1)))
                            .desservi(true)
                            .build(),
                    DesserteReelle.builder()
                            .gare(gare3)
                            .desservi(false)
                            .build(),
                    DesserteReelle.builder()
                            .gare(gare4)
                            .arrivee(localDateTimeToDate(baseDate.plusHours(2)))
                            .desservi(true)
                            .build()
            ));

            /* TER
                Amiens 12h30
                Lille 13h
             */
            Trajet ter = Trajet.builder()
                    .id(3)
                    .desserteTheoriques(new ArrayList<>())
                    .desserteReelles(new ArrayList<>())
                    .parcoursId(2)
                    .type("TER")
                    .build();
            ter.addDesserteTheoriques(List.of(
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
                    )
            );
            ter.addDesserteReelles(List.of(
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
                    )
            );


            em.persist(trajet);
            Thread.sleep(1000);
            em.persist(trajet2);
            em.persist(ter);

            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(201).build();
    }

}
