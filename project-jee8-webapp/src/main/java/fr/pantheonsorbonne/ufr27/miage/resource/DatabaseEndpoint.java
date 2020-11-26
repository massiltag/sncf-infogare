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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import static fr.pantheonsorbonne.ufr27.miage.util.Utils.*;

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

            Gare gare = Gare.builder().nom("Paris Montparnasse").build();
            Gare gare2 = Gare.builder().nom("Bordeaux Saint-Jean").build();
            Gare gare3 = Gare.builder().nom("Lille Flandres").build();

            Trajet trajet = Trajet.builder()
                    .desserteReelles(new ArrayList<>())
                    .desserteTheoriques(new ArrayList<>())
                    .build();

            LocalDateTime baseDate = LocalDateTime.of(2020, 1, 1, 12, 0);

            /* Populate with data :
                Bordeaux 12h
                Paris 13h
                Lille 14h
             */
            trajet.addDesserteTheorique(DesserteTheorique.builder()
                    .gare(gare)
                    .arrivee(localDateTimeToDate(baseDate))
                    .desservi(true)
                    .build());
            trajet.addDesserteTheorique(DesserteTheorique.builder()
                    .gare(gare2)
                    .arrivee(localDateTimeToDate(baseDate.plusHours(1)))
                    .desservi(true)
                    .build());
            trajet.addDesserteTheorique(DesserteTheorique.builder()
                    .gare(gare3)
                    .arrivee(localDateTimeToDate(baseDate.plusHours(2)))
                    .desservi(true)
                    .build());

            trajet.addDesserteReelle(DesserteReelle.builder()
                    .gare(gare)
                    .arrivee(localDateTimeToDate(baseDate))
                    .desservi(true)
                    .build());
            trajet.addDesserteReelle(DesserteReelle.builder()
                    .gare(gare2)
                    .arrivee(localDateTimeToDate(baseDate.plusHours(1)))
                    .desservi(true)
                    .build());
            trajet.addDesserteReelle(DesserteReelle.builder()
                    .gare(gare3)
                    .arrivee(localDateTimeToDate(baseDate.plusHours(2)))
                    .desservi(true)
                    .build());

            em.persist(trajet);

            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(201).build();
    }

}