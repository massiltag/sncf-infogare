package fr.pantheonsorbonne.ufr27.miage.resource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.pantheonsorbonne.ufr27.miage.dao.UserDAO;
import fr.pantheonsorbonne.ufr27.miage.ejb.GymService;
import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.exception.UserHasDebtException;
import fr.pantheonsorbonne.ufr27.miage.jms.PaymentValidationAckownledgerBean;
import fr.pantheonsorbonne.ufr27.miage.jms.payment.PaymentProcessorBean;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Address;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.User;

@Path("/user")
public class UserEndpoint {

	@Inject
	UserDAO dao;

	@Inject
	GymService service;

	@Inject
	PaymentValidationAckownledgerBean b1_;

	@Inject
	PaymentProcessorBean b2_;

	@GET
	@Path("/{userId}")
	public User getUser(@PathParam("userId") int userId) {
		try {
			return dao.getUserFromId(userId);
		} catch (NoSuchUserException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@PUT
	@Path("/{userId}")
	public Response updateUserAddress(@PathParam("userId") int userId, Address address) {
		try {
			dao.updateUserAddress(userId, address);
			return Response.ok().build();
		} catch (NoSuchUserException e) {
			throw new WebApplicationException(404);
		}
	}

	@DELETE
	@Path("/{userId}")
	public Response deleteMemberShip(@PathParam("userId") int userId) throws UserHasDebtException {
		try {
			service.cancelMemberShip(userId);
			return Response.ok().build();
		} catch (

		NoSuchUserException e) {
			throw new WebApplicationException("no such user", 404);
		}
	}

}
