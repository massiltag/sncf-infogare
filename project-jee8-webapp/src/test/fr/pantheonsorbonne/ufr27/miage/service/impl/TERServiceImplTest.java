package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.dao.DesserteReelleDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteTheorique;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static fr.pantheonsorbonne.ufr27.miage.util.DateUtil.localDateTimeToDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TERServiceImplTest {

    @InjectMocks
    TERServiceImpl TERService;

    @Mock
    TrajetDAO trajetDAO;

    @Mock
    DesserteReelleDAO desserteReelleDAO;

    @Mock
    GareDAO gareDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void should_wait_for_TGV() {
        //GIVEN
        Gare gare = Gare.builder().id(1).nom("Bordeaux Saint-Jean").code("BDX").build();
        Gare gare2 = Gare.builder().id(2).nom("Paris Montparnasse").code("PAR").build();
        Gare gare3 = Gare.builder().id(3).nom("Gare d'Amiens").code("AMS").build();
        Gare gare4 = Gare.builder().id(4).nom("Lille Flandres").code("LIL").build();

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

        Trajet trajet2 = Trajet.builder()
                .id(2)
                .desserteTheoriques(new ArrayList<>())
                .desserteReelles(new ArrayList<>())
                .name("Amiens - Lille")
                .type("TER")
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


        LiveInfo liveInfo = LiveInfo.builder()
                .lastGareIndex(1)
                .nextGareIndex(2)
                .build();


        Duration delay = Duration.ofMinutes(60);


        //WHEN
        when(gareDAO.getGareBySeqNumber(trajet1, 1)).thenReturn(gare);
        when(gareDAO.getGareBySeqNumber(trajet1, 2)).thenReturn(gare2);
        when(gareDAO.getGareBySeqNumber(trajet1, 3)).thenReturn(gare3);

        when(trajetDAO.getTrajetsByGareId(1)).thenReturn(List.of(trajet1));
        when(trajetDAO.getTrajetsByGareId(2)).thenReturn(List.of(trajet1));
        when(trajetDAO.getTrajetsByGareId(3)).thenReturn(List.of(trajet1, trajet2));
        when(trajetDAO.getTrajetsByGareId(4)).thenReturn(List.of(trajet2));

        when(desserteReelleDAO.getAllOfTrajet(1)).thenReturn(dr1);
        when(desserteReelleDAO.getAllOfTrajet(2)).thenReturn(dr2);

        TERService.waitForThisTrainWhenTER(trajet1, liveInfo, delay);

        //THEN
        assertEquals(
                localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 15, 40)),
                trajet2.getDesserteReelleNo(1).getArrivee()
        );
        assertEquals(
                localDateTimeToDate(LocalDateTime.of(2020, 01, 01, 16, 40)),
                trajet2.getDesserteReelleNo(2).getArrivee()
        );

    }


}