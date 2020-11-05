package fr.pantheonsorbonne.ufr27.miage.service.impl;

import javax.ejb.Stateless;

import fr.pantheonsorbonne.ufr27.miage.jpa.Address;
import fr.pantheonsorbonne.ufr27.miage.jpa.Invoice;
import fr.pantheonsorbonne.ufr27.miage.service.MailingService;

@Stateless
public class MailingServiceImpl implements MailingService {

	@Override
	public void sendInvoice(Invoice invoice) {
		Address address = invoice.getContract().getCustomer().getAddress();

		System.out.println("sending " + invoice.toString() + " to " + address.toString());

	}

}
