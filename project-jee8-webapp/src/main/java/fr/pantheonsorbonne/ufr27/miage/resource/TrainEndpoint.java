package fr.pantheonsorbonne.ufr27.miage.resource;

import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteTheorique;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("train")
public class TrainEndpoint {

    @Inject
    TrainService trainService;

    @POST
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("live")
    public Response postLiveInfo(LiveInfo liveInfo) {
        trainService.processLiveInfo(liveInfo);
        return Response.status(201, "Info received.").build();
    }

    /**
     * Annuler un train
     * @param id
     * @param conditions
     * @return
     */
    @POST
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("cancel/{id}/{conditions}")
    public Response postCancel(@PathParam("id") int id, @PathParam("conditions") String conditions) {
        trainService.processCancel(id, conditions);
        return Response.status(200, "Train canceled.").build();
    }
    
    /**
     * Get les infos d'un train
     * @return
     */
    @GET
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("trainInfo/{id}")
    public Trajet getTrain(@PathParam("id") int id) {
        return trainService.processGetTrain(id);
    }
    
    /**
     * Get la liste des trains 
     * Afin de les afficher sur l'infogare 
     * @return
     */
    @GET
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("trainListInfo")
    public List<Trajet> getTrainList() {
        return trainService.processGetTrainList();
    }
    
    /**
     * Recupere les infos d'arrivees des trains a la gare 
     * Pour les afficher sur les infogares
     * @param gare
     * @return
     */
    @GET
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("trainArriveeGare/{Gare}")
    public List<Trajet> getTrainArriveeGare(@PathParam("Gare") Gare gare) {
        return trainService.processGetTrainArriveeGareList(gare);
    }
    
    /**
     * Recupere les infos des departs des trains a la gare 
     * Pour les afficher sur les infogares 
     * @param gare
     * @return
     */
    @GET
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("trainDepartGare/{Gare}")
    public List<Trajet> getTrainDeparteGare(@PathParam("Gare") Gare gare) {
        return trainService.processGetTrainDepartGareList(gare);
    }

}

