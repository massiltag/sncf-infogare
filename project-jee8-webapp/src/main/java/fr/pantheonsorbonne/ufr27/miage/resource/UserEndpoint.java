package fr.pantheonsorbonne.ufr27.miage.resource;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.exception.UserHasDebtException;
import fr.pantheonsorbonne.ufr27.miage.jms.PaymentValidationAckownledgerBean;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Address;
import fr.pantheonsorbonne.ufr27.miage.service.GymService;
import fr.pantheonsorbonne.ufr27.miage.service.UserService;

@Path("/user")
public class UserEndpoint {

	@Inject
	UserService userService;

	@Inject
	GymService gymServiceservice;



	@Inject
	PaymentValidationAckownledgerBean b2;

	@GET
	@Path("/{userId}")
	@Produces(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getUser(@PathParam("userId") int userId) {
		try {
			return Response.ok(userService.getUserFromId(userId)).build();
		} catch (NoSuchUserException e) {
			throw new WebApplicationException(404);
		}
	}

	@GET
	@Path("/{userId}/address")
	@Produces(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getUserAddress(@PathParam("userId") int userId) {
		try {
			return Response.ok(userService.getAddressForUser(userId)).build();
		} catch (NoSuchUserException e) {
			throw new WebApplicationException(404);
		}
	}

	@Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@PUT
	@Path("/{userId}/address")
	public Response updateUserAddress(@PathParam("userId") int userId, Address address) {
		try {
			userService.updateUserAddress(userId, address);
			return Response.noContent().build();
		} catch (NoSuchUserException e) {
			throw new WebApplicationException(404);
		}
	}

	@DELETE
	@Path("/{userId}")
	public Response deleteMemberShip(@PathParam("userId") int userId) throws UserHasDebtException {
		try {
			gymServiceservice.cancelMemberShip(userId);
			return Response.ok().build();
		} catch (

		NoSuchUserException e) {
			throw new WebApplicationException("no such user", 404);
		}
	}

}
