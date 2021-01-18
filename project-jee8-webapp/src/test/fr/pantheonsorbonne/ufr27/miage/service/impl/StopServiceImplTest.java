package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.dao.DesserteReelleDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteTheorique;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

class StopServiceImplTest {

    @InjectMocks
    StopServiceImpl stopService;

    @Mock
    TrajetDAO trajetDAO;

    @Mock
    DesserteReelleDAO desserteReelleDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void should_add_exceptional_stop() {
        Gare gare = Gare.builder().nom("Bordeaux Saint-Jean").code("BDX").build();
        Gare gare2 = Gare.builder().nom("Paris Montparnasse").code("PAR").build();
        Gare gare3 = Gare.builder().nom("Lille Flandres").code("LIL").build();

        /*
            TGV 1
                - BDX 12h00
                - PAR 14h00
                - LIL 15h00
         */
        Trajet tgv1 = Trajet.builder()
                .id(1)
                .desserteTheoriques(new ArrayList<>())
                .desserteReelles(new ArrayList<>())
                .parcoursId(1)
                .name("Bordeaux - Lille par Paris")
                .type("TGV")
                .build();
        tgv1.addDesserteTheoriques(List.of(
                DesserteTheorique.builder()
                        .gare(gare)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 12, 00)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare2)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 00)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 15, 00)))
                        .desservi(true)
                        .build()
                )
        );
        tgv1.addDesserteReelles(List.of(
                DesserteReelle.builder()
                        .gare(gare)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 12, 00)))
                        .desservi(true)
                        .build(),
                DesserteReelle.builder()
                        .gare(gare2)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 15, 30)))
                        .desservi(true)
                        .build(),
                DesserteReelle.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 16, 30)))
                        .desservi(true)
                        .build()
                )
        );

        /*
            TGV 2
                - BDX 13h00
                - PAR /////
                - LIL 16h00
         */
        Trajet tgv2 = Trajet.builder()
                .id(2)
                .desserteTheoriques(new ArrayList<>())
                .desserteReelles(new ArrayList<>())
                .parcoursId(1)
                .name("Bordeaux - Lille Direct")
                .type("TGV")
                .build();
        tgv2.addDesserteTheoriques(List.of(
                DesserteTheorique.builder()
                        .gare(gare)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 13, 00)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare2)
                        .desservi(false)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 16, 00)))
                        .desservi(true)
                        .build()
                )
        );
        List<DesserteReelle> dr_keep = List.of(
                DesserteReelle.builder()
                        .gare(gare)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 13, 00)))
                        .desservi(true)
                        .build(),
                DesserteReelle.builder()
                        .gare(gare2)
                        .desservi(false)
                        .build(),
                DesserteReelle.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 16, 00)))
                        .desservi(true)
                        .build()
        );
        tgv2.addDesserteReelles(dr_keep);

        // WHEN
        when(trajetDAO.getTrajetsByParcoursId(anyInt())).thenReturn(List.of(tgv1, tgv2));
        when(trajetDAO.find(anyInt())).thenReturn(tgv2);
        when(desserteReelleDAO.getAllOfTrajet(anyInt())).thenReturn(dr_keep);

        stopService.processExceptionalStop(tgv1, 2);

        // THEN
        assertTrue(tgv2.getDesserteReelleNo(2).isDesservi());
        assertEquals(
                localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 30)),
                tgv2.getDesserteReelleNo(2).getArrivee()
        );
    }



    @Test
    void should_not_add_exceptional_stop() {
        Gare gare = Gare.builder().nom("Bordeaux Saint-Jean").code("BDX").build();
        Gare gare2 = Gare.builder().nom("Paris Montparnasse").code("PAR").build();
        Gare gare3 = Gare.builder().nom("Lille Flandres").code("LIL").build();

        /*
            TGV 1
                - BDX 12h00
                - PAR 14h00
                - LIL 15h00
         */
        Trajet tgv1 = Trajet.builder()
                .id(1)
                .desserteTheoriques(new ArrayList<>())
                .desserteReelles(new ArrayList<>())
                .parcoursId(1)
                .name("Bordeaux - Lille par Paris")
                .type("TGV")
                .build();
        tgv1.addDesserteTheoriques(List.of(
                DesserteTheorique.builder()
                        .gare(gare)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 12, 00)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare2)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 00)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 15, 00)))
                        .desservi(true)
                        .build()
                )
        );
        tgv1.addDesserteReelles(List.of(
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
                )
        );

        /*
            TGV 2
                - BDX 11h00
                - PAR /////
                - LIL 14h00
         */
        Trajet tgv2 = Trajet.builder()
                .id(2)
                .desserteTheoriques(new ArrayList<>())
                .desserteReelles(new ArrayList<>())
                .parcoursId(1)
                .name("Bordeaux - Lille Direct")
                .type("TGV")
                .build();
        tgv2.addDesserteTheoriques(List.of(
                DesserteTheorique.builder()
                        .gare(gare)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 11, 00)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare2)
                        .desservi(false)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 00)))
                        .desservi(true)
                        .build()
                )
        );
        List<DesserteReelle> dr_keep = List.of(
                DesserteReelle.builder()
                        .gare(gare)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 11, 00)))
                        .desservi(true)
                        .build(),
                DesserteReelle.builder()
                        .gare(gare2)
                        .desservi(false)
                        .build(),
                DesserteReelle.builder()
                        .gare(gare3)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 14, 00)))
                        .desservi(true)
                        .build()
        );
        tgv2.addDesserteReelles(dr_keep);

        // WHEN
        when(trajetDAO.getTrajetsByParcoursId(anyInt())).thenReturn(List.of(tgv1, tgv2));
        when(trajetDAO.find(anyInt())).thenReturn(tgv2);
        when(desserteReelleDAO.getAllOfTrajet(anyInt())).thenReturn(dr_keep);

        stopService.processExceptionalStop(tgv1, 2);

        // THEN
        assertFalse(tgv2.getDesserteReelleNo(2).isDesservi());
        assertNull(tgv2.getDesserteReelleNo(2).getArrivee());
    }
}