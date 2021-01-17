package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteTheorique;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.service.RuptureService;
import fr.pantheonsorbonne.ufr27.miage.service.StopService;
import fr.pantheonsorbonne.ufr27.miage.service.TERService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.localDateTimeToDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

class TrainServiceImplTest {

    @InjectMocks
    TrainServiceImpl trainService;

    @Mock
    TERService terService;

    @Mock
    RuptureService ruptureService;

    @Mock
    StopService stopService;

    @Mock
    TrajetDAO trajetDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void processLiveInfo() {
        // GIVEN
        Gare gare = Gare.builder().nom("Bordeaux Saint-Jean").code("BDX").build();
        Gare gare2 = Gare.builder().nom("Paris Montparnasse").code("PAR").build();

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
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 12, 00)))
                        .desservi(true)
                        .build(),
                DesserteTheorique.builder()
                        .gare(gare2)
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 13, 00)))
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
                        .arrivee(localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 13, 00)))
                        .desservi(true)
                        .build()
                )
        );

        LiveInfo liveInfo = LiveInfo.builder()
                .lastGareIndex(1)
                .nextGareIndex(2)
                .timestamp("2020-01-01 12:30")
                .percentage(0)
                .build();

        // WHEN
        when(trajetDAO.find(anyInt())).thenReturn(tgv1);
        trainService.processLiveInfo(liveInfo, 1);

        // THEN
        assertEquals(
                localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 13, 30)),
                tgv1.getDesserteReelleNo(2).getArrivee()
        );
    }

}