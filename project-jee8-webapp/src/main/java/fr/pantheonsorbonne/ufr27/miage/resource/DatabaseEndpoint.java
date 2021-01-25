package fr.pantheonsorbonne.ufr27.miage.resource;

import fr.pantheonsorbonne.ufr27.miage.jpa.*;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InfoDTO;
import fr.pantheonsorbonne.ufr27.miage.service.InfogareSenderService;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;

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
    TrainService trainService;

    @Inject
    InfogareSenderService infogareSenderService;

    @GET
    @Path("test")
    public Response testDB() {
        try {
            infogareSenderService.send("PAR", InfoDTO.builder().infoType("BONSOIR PARIS").timestamp("12").build());
            infogareSenderService.send("LIL", InfoDTO.builder().infoType("YO LILLE").timestamp("74").build());
            return Response.status(200).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("init")
    public Response initInfogare() {
        try {
            trainService.updateInfogares();
            return Response.status(200).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("populate")
    public Response populateDB() {
        try {
            System.out.println("TEST PERSISTENCE");
            em.getTransaction().begin();

            Gare gare = Gare.builder().nom("Bordeaux Saint-Jean").code("BDX").build();
            Gare gare2 = Gare.builder().nom("Paris Montparnasse").code("PAR").build();
            Gare gare3 = Gare.builder().nom("Gare d'Amiens").code("AMS").build();
            Gare gare4 = Gare.builder().nom("Lille Flandres").code("LIL").build();
            Gare gare5 = Gare.builder().nom("Lyon Perrache").code("LYN").build();

            LocalDateTime baseDate = LocalDateTime.of(2020, 1, 1, 12, 0);


            /* TGV 1
                Bordeaux 8h
                Paris 12h
                Amiens 12h30
                Lille 13h
             */
            Trajet tgv1 = Trajet.builder()
                    .id(1)
                    .desserteTheoriques(new ArrayList<>())
                    .desserteReelles(new ArrayList<>())
                    .parcoursId(1)
                    .name("Bordeaux - Lille par Amiens")
                    .type("TGV")
                    .build();
            tgv1.addDesserteTheoriques(List.of(
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
            tgv1.addDesserteReelles(List.of(
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
            Trajet tgv2 = Trajet.builder()
                    .id(2)
                    .desserteTheoriques(new ArrayList<>())
                    .desserteReelles(new ArrayList<>())
                    .parcoursId(1)
                    .name("Bordeaux - Lille")
                    .type("TGV")
                    .build();
            tgv2.addDesserteTheoriques(List.of(
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
            tgv2.addDesserteReelles(List.of(
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
                    .id(4)
                    .desserteTheoriques(new ArrayList<>())
                    .desserteReelles(new ArrayList<>())
                    .parcoursId(4)
                    .name("Amiens - Lille")
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

            /*
                - Paris 12h15
                - Lyon 15h15
             */
            Trajet tgv3 = Trajet.builder()
                    .id(3)
                    .desserteTheoriques(new ArrayList<>())
                    .desserteReelles(new ArrayList<>())
                    .parcoursId(3)
                    .name("Paris - Lyon")
                    .type("TGV")
                    .build();
            tgv3.addDesserteTheoriques(List.of(
                    DesserteTheorique.builder()
                            .gare(gare2)
                            .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 1,1,12, 15)))
                            .desservi(true)
                            .build(),
                    DesserteTheorique.builder()
                            .gare(gare5)
                            .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 1,1,15, 15)))
                            .desservi(true)
                            .build()
                    )
            );
            tgv3.addDesserteReelles(List.of(
                    DesserteReelle.builder()
                            .gare(gare2)
                            .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 1,1,12, 15)))
                            .desservi(true)
                            .build(),
                    DesserteReelle.builder()
                            .gare(gare5)
                            .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 1,1,15, 15)))
                            .desservi(true)
                            .build()
                    )
            );

            // PASSAGERS
            Passager p1 = Passager.builder().build();
            p1.setCorrespondance(Correspondance.builder().id(1).gare(gare2).trajet(tgv3).rupture(false).build());

            Passager p2 = Passager.builder().build();
            p2.setCorrespondance(Correspondance.builder().id(2).gare(gare2).trajet(tgv3).rupture(false).build());

            Passager p3 = Passager.builder().build();
            p3.setCorrespondance(Correspondance.builder().id(3).gare(gare2).trajet(tgv3).rupture(false).build());

            Passager p4 = Passager.builder().build();
            p4.setCorrespondance(Correspondance.builder().id(4).gare(gare2).trajet(tgv3).rupture(false).build());

            Passager p5 = Passager.builder().build();
            p5.setCorrespondance(Correspondance.builder().id(5).gare(gare2).trajet(tgv3).rupture(false).build());

            tgv1.setPassagers(List.of(p1, p2, p3, p4, p5));


            em.persist(tgv1);
            em.persist(tgv2);
            em.persist(tgv3);
            em.persist(ter);

            em.getTransaction().commit();
            em.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(201).build();
    }

}
