package fr.pantheonsorbonne.ufr27.miage.resource;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArrivalInfo;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("train")
public class TrainEndpoint {

    @Inject
    TrainService trainService;

    @POST
    @Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("arrival")
    public Response postDelayInfo(ArrivalInfo arrivalInfo) {
        trainService.processArrivalInfo(arrivalInfo);
        return Response.status(201, "Info received.").build();
    }

}

