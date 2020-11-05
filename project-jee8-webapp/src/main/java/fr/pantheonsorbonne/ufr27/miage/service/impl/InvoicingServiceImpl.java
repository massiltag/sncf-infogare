package fr.pantheonsorbonne.ufr27.miage.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jpa.Contract;
import fr.pantheonsorbonne.ufr27.miage.jpa.Customer;
import fr.pantheonsorbonne.ufr27.miage.jpa.Invoice;
import fr.pantheonsorbonne.ufr27.miage.service.InvoicingService;
import fr.pantheonsorbonne.ufr27.miage.service.MailingService;

@Stateless
public class InvoicingServiceImpl implements InvoicingService {

	@Inject
	EntityManager em;
	
	@Inject
	MailingService ms;

	@Override
	public void sendNextInvoice(int customerId) {
		em.getTransaction().begin();
		Customer customer = em.find(Customer.class, customerId);
		LocalDateTime today = LocalDateTime.now();
		Set<Contract> contracts = customer.getContracts();
		for (Contract contract : contracts) {
			boolean isInvoiceNeeded = true;
			if (contract.getEnDate() == null || contract.getEnDate().after(new Date())) {
				for (Invoice invoice : contract.getInvoices()) {
					if (invoice.getDate().toInstant().compareTo(today.toInstant(ZoneOffset.UTC)) > 0) {
						isInvoiceNeeded = false;
						break;
					}
				}
				if (isInvoiceNeeded) {
					Invoice invoice = new Invoice();
					invoice.setContract(contract);
					invoice.setDate(Date.from(today.plusMonths(1).toInstant(ZoneOffset.UTC)));
					em.persist(invoice);
					contract.getInvoices().add(invoice);
					
				}
			}
			em.merge(contract);
		}
		em.getTransaction().commit();

	}

}
