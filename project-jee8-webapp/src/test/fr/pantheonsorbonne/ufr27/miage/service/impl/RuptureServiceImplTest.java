package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.dao.CorrespondanceDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.DesserteReelleDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.PassagerDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.localDateTimeToDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RuptureServiceImplTest {

    @InjectMocks
    RuptureServiceImpl ruptureService;

    @Mock
    TrajetDAO trajetDAO;

    @Mock
    PassagerDAO passagerDAO;

    @Mock
    CorrespondanceDAO correspondanceDAO;

    @Mock
    DesserteReelleDAO desserteReelleDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void should_delay_next_train() {
        // GIVEN
        Gare gare = Gare.builder().id(1).nom("Bordeaux Saint-Jean").code("BDX").build();
        Gare gare2 = Gare.builder().id(2).nom("Paris Montparnasse").code("PAR").build();
        Gare gare3 = Gare.builder().id(3).nom("Gare d'Amiens").code("AMS").build();
        Gare gare4 = Gare.builder().id(4).nom("Lille Flandres").code("LIL").build();

        /*
            TGV 1
                - BDX 12h
                - PAR 13h devient ---> 14h (retard)
                - AMS 14h devient ---> 15h (retard)
         */
        Trajet trajet1 = Trajet.builder()
                .id(1)
                .desserteTheoriques(new ArrayList<>())
                .desserteReelles(new ArrayList<>())
                .name("Bordeaux - Amiens")
                .type("TGV")
                .build();
        trajet1.addDesserteTheoriques(List.of(
                DesserteTheorique.builder()
                        .gare(gare)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 12, 00)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare2)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 13, 00)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 00)))
                        .desservi(true)
                        .build()
                )
        );
        List<DesserteReelle> dr1 = List.of(
                DesserteReelle.builder()
                        .gare(gare)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 12, 00)))
                        .desservi(true)
                        .build(),
                DesserteReelle.builder()
                        .gare(gare2)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 00)))
                        .desservi(true)
                        .build(),
                DesserteReelle.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 15, 00)))
                        .desservi(true)
                        .build()
        );
        trajet1.addDesserteReelles(dr1);


        /*
            TGV 2
                - AMS 14h30
                - LIL 15h30
         */
        Trajet trajet2 = Trajet.builder()
                .id(2)
                .desserteTheoriques(new ArrayList<>())
                .desserteReelles(new ArrayList<>())
                .name("Amiens - Lille")
                .type("TGV")
                .build();
        trajet2.addDesserteTheoriques(List.of(
                DesserteTheorique.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 30)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare4)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 15, 30)))
                        .desservi(true)
                        .build()
                )
        );
        List<DesserteReelle> dr2 = List.of(
                DesserteReelle.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 30)))
                        .desservi(true)
                        .build(),
                DesserteReelle.builder()
                        .gare(gare4)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 15, 30)))
                        .desservi(true)
                        .build()
        );
        trajet2.addDesserteReelles(dr2);


        Correspondance c1 = Correspondance.builder()
                .id(1)
                .trajet(trajet2)
                .gare(gare3)
                .rupture(false)
                .build();
        Correspondance c2 = Correspondance.builder()
                .id(2)
                .trajet(trajet2)
                .gare(gare3)
                .rupture(false)
                .build();
        Correspondance c3 = Correspondance.builder()
                .id(3)
                .trajet(trajet2)
                .gare(gare3)
                .rupture(false)
                .build();
        Correspondance c4 = Correspondance.builder()
                .id(4)
                .trajet(trajet2)
                .gare(gare3)
                .rupture(false)
                .build();
        Correspondance c5 = Correspondance.builder()
                .id(5)
                .trajet(trajet2)
                .gare(gare3)
                .rupture(false)
                .build();

        Passager p1 = Passager.builder()
                .id(1)
                .trajet(trajet1)
                .correspondance(c1)
                .build();
        c1.setPassager(p1);
        Passager p2 = Passager.builder()
                .id(2)
                .trajet(trajet2)
                .correspondance(c2)
                .build();
        c2.setPassager(p2);
        Passager p3 = Passager.builder()
                .id(3)
                .trajet(trajet1)
                .correspondance(c3)
                .build();
        c3.setPassager(p3);
        Passager p4 = Passager.builder()
                .id(4)
                .trajet(trajet1)
                .correspondance(c4)
                .build();
        c4.setPassager(p4);
        Passager p5 = Passager.builder()
                .id(5)
                .trajet(trajet1)
                .correspondance(c5)
                .build();
        c5.setPassager(p5);


        // WHEN
        when(passagerDAO.findByTrainId(1)).thenReturn(List.of(p1, p2, p3, p4, p5));
        when(trajetDAO.findAll()).thenReturn(List.of(trajet1, trajet2));
        when(correspondanceDAO.findByTrainAndRupture(2, true)).thenReturn(List.of(c1, c2, c3, c4, c5));

        ruptureService.processRuptureCorrespondance(trajet1);

        // THEN
        assertTrue(c1.isRupture());
        assertTrue(c2.isRupture());
        assertTrue(c3.isRupture());
        assertTrue(c4.isRupture());
        assertTrue(c5.isRupture());

        assertNotNull(c1.getNewDate());
        assertEquals(
                localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 15, 00)),
                c1.getNewDate()
        );
        assertEquals(
                localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 15, 10)),
                trajet2.getDesserteReelleNo(1).getArrivee()
        );
        assertEquals(
                localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 16, 10)),
                trajet2.getDesserteReelleNo(2).getArrivee()
        );

    }

    @Test
    void should_not_delay_next_train() {
        // GIVEN
        Gare gare2 = Gare.builder().id(2).nom("Paris Montparnasse").code("PAR").build();
        Gare gare3 = Gare.builder().id(3).nom("Gare d'Amiens").code("AMS").build();
        Gare gare4 = Gare.builder().id(4).nom("Lille Flandres").code("LIL").build();

        /*
            TGV 2
                - AMS 14h30
                - LIL 15h30
         */
        Trajet trajet2 = Trajet.builder()
                .id(2)
                .desserteTheoriques(new ArrayList<>())
                .desserteReelles(new ArrayList<>())
                .name("Amiens - Lille")
                .type("TGV")
                .build();
        trajet2.addDesserteTheoriques(List.of(
                DesserteTheorique.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 30)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare4)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 15, 30)))
                        .desservi(true)
                        .build()
                )
        );
        List<DesserteReelle> dr2 = List.of(
                DesserteReelle.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 30)))
                        .desservi(true)
                        .build(),
                DesserteReelle.builder()
                        .gare(gare4)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 15, 30)))
                        .desservi(true)
                        .build()
        );
        trajet2.addDesserteReelles(dr2);

        /*
            TGV 3
                - PAR 13h15
                - AMS 14h15 ---> 14h20 (5min retard)
         */
        Trajet trajet3 = Trajet.builder()
                .id(3)
                .desserteTheoriques(new ArrayList<>())
                .desserteReelles(new ArrayList<>())
                .name("Paris - Amiens")
                .type("TGV")
                .build();
        trajet3.addDesserteTheoriques(List.of(
                DesserteTheorique.builder()
                        .gare(gare2)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 13, 15)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 15)))
                        .desservi(true)
                        .build()
                )
        );
        List<DesserteReelle> dr3 = List.of(
                DesserteReelle.builder()
                        .gare(gare2)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 13, 15)))
                        .desservi(true)
                        .build(),
                DesserteReelle.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 20)))
                        .desservi(true)
                        .build()
        );
        trajet3.addDesserteReelles(dr3);

        Correspondance c6 = Correspondance.builder()
                .id(6)
                .trajet(trajet2)
                .gare(gare3)
                .rupture(false)
                .build();

        Passager p6 = Passager.builder()
                .id(6)
                .trajet(trajet3)
                .correspondance(c6)
                .build();
        c6.setPassager(p6);


        // WHEN
        when(passagerDAO.findByTrainId(3)).thenReturn(List.of(p6));
        when(trajetDAO.findAll()).thenReturn(List.of(trajet2, trajet3));
        when(correspondanceDAO.findByTrainAndRupture(2, true)).thenReturn(List.of(c6));

        ruptureService.processRuptureCorrespondance(trajet3);

        // THEN
        assertFalse(c6.isRupture());

        assertNull(c6.getNewDate());
        assertEquals(
                localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 30)),
                trajet2.getDesserteReelleNo(1).getArrivee()
        );
    }

}