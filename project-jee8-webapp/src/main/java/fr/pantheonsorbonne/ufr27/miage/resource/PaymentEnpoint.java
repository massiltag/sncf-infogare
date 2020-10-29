package fr.pantheonsorbonne.ufr27.miage.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.pantheonsorbonne.ufr27.miage.dao.PaymentDAO;
import fr.pantheonsorbonne.ufr27.miage.ejb.PaymentService;
import fr.pantheonsorbonne.ufr27.miage.exception.NoDebtException;
import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Ccinfo;

@Path("payment")
public class PaymentEnpoint {

	@Inject
	PaymentService service;

	@Inject
	PaymentDAO paymentDAO;

	@POST
	@Consumes(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("{userId}")
	public Response payDebt(@PathParam("userId") int userId, Ccinfo ccinfo) throws URISyntaxException {
		try {
			int paymentId = service.initiatePayAllDebts(ccinfo, userId);
			return Response.accepted().location(new URI("/payment/" + paymentId)).build();
		} catch (NoDebtException e) {
			return Response.status(400, "no debts to pay").build();
		} catch (NoSuchUserException e) {
			return Response.status(404, "no such user").build();
		}

	}

	@Path("{paymentId}")
	@GET
	public Response getPaymentInfo(@PathParam("paymentId") int paymentId) {
		if (paymentDAO.isPaymentValidated(paymentId)) {
			return Response.status(200, "payment validated").build();
		} else {
			return Response.status(404, "payment not yet validated").build();
		}
	}

}
