package fr.pantheonsorbonne.ufr27.miage.resource;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
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
    @Path("{id}/live")
    public Response postLiveInfo(LiveInfo liveInfo, @PathParam("id") int id) {
        trainService.processLiveInfo(liveInfo, id);
        return Response.status(201, "Info received.").build();
    }


    /**
     * Envoi d'un retard par le train en signalant la cause
     *
     * @param liveInfo
     * @param id
     * @param conditions La cause du retard
     * @return
     */
    @POST
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{id}/{condition}")
    public Response postDelayWithCondition(LiveInfo liveInfo, @PathParam("id") int id, @PathParam("condition") String conditions) {
        trainService.processDelayWithCondition(liveInfo, id, conditions);
        return Response.status(200, "Train delayed.").build();
    }

}

