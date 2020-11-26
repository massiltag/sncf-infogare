package fr.pantheonsorbonne.ufr27.miage.resource;

import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteTheorique;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("train")
public class TrainEndpoint {

    @Inject
    TrainService trainService;

    @Inject
    EntityManager em;


    @POST
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("arrival")
    public Response postLiveInfo(LiveInfo liveInfo) {
        trainService.processLiveInfo(liveInfo);
        return Response.status(201, "Info received.").build();
    }

    @GET
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("test")
    public Response testPersist() {
    	try {
    		System.out.println("TEST PERSISTENCE");
    		em.getTransaction().begin();
    		
    		Gare gare = new Gare();
            gare.setNom("PAR");
            gare.setId(1);
            
            
            DesserteTheorique desserteTh = new DesserteTheorique();
            desserteTh.setId(1);
            desserteTh.setGare(gare);
            desserteTh.setDepart(Date.from(Instant.now()));
            desserteTh.setArrivee(Date.from(Instant.now()));
            desserteTh.setDesservi(true);

            
            DesserteReelle desserteRe = new DesserteReelle();
            desserteRe.setId(2);
            desserteRe.setGare(gare);
            desserteRe.setDepart(Date.from(Instant.now()));
            desserteRe.setArrivee(Date.from(Instant.now()));
            desserteRe.setDesservi(true);
            

            Trajet trajet = new Trajet();
            trajet.setId(1);
            trajet.setDesserteReelles(new ArrayList<>());
            trajet.setDesserteTheoriques(new ArrayList<>());
            trajet.addDesserteReelle(desserteRe);
            trajet.addDesserteTheorique(desserteTh);
            
            em.persist(trajet);

            em.getTransaction().commit();
            em.close();


    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return Response.status(201).build();
    }

}

