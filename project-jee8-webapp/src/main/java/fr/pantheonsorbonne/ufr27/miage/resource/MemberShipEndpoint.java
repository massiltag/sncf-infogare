package fr.pantheonsorbonne.ufr27.miage.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.FreeTrialPlan;
import fr.pantheonsorbonne.ufr27.miage.service.GymService;;

@Path("membership/")
public class MemberShipEndpoint {

	@Inject
	GymService service;

	@Produces(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@POST
	public Response createUser(FreeTrialPlan plan) throws URISyntaxException {
		int customerId = service.createMembership(plan);

		return Response.created(new URI("/user/" + customerId)).build();

	}

}
