package fr.pantheonsorbonne.ufr27.miage.resource;

import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.pantheonsorbonne.ufr27.miage.dao.InvoiceDAO;
import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.jpa.Invoice;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InvoiceWrapper;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;

@Path("/invoice")
public class InvoiceEndpoint {

	@Inject
	InvoiceDAO invoiceDAO;

	@GET
	@Path("/{userId}")
	@Produces(value = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getInvoice(@PathParam("userId") int userId) throws NoSuchUserException {

		Collection<Invoice> invoices = invoiceDAO.getUnpaiedInvoices(userId);
		InvoiceWrapper wrapper = new ObjectFactory().createInvoiceWrapper();
		for (Invoice invoiceEntity : invoices) {

			fr.pantheonsorbonne.ufr27.miage.model.jaxb.Invoice invoice = new ObjectFactory().createInvoice();
			invoice.setContractId(invoiceEntity.getContract().getId());

			invoice.setDate(invoiceEntity.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
			invoice.setPaid(invoiceEntity.isPayed());
			wrapper.getInvoices().add(invoice);
		}
		return Response.ok(wrapper).build();

	}

}
