package fr.pantheonsorbonne.ufr27.miage.dao;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.ManagedBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.jpa.Contract;
import fr.pantheonsorbonne.ufr27.miage.jpa.Customer;
import fr.pantheonsorbonne.ufr27.miage.jpa.Invoice;

@ManagedBean
public class InvoiceDAO {

	@Inject
	EntityManager em;

	public List<Invoice> getUnpaiedInvoices(int userId) throws NoSuchUserException {

		Customer customer = em.find(Customer.class, userId);
		if (customer == null) {
			throw new NoSuchUserException();
		}
		return customer.getContracts().//
				stream().//
				map(Contract::getInvoices).//
				flatMap(Collection::stream).//
				filter(i -> !i.isPayed()).//
				collect(Collectors.toList());
	}

	public double getUserDebt(int userId) throws NoSuchUserException {

		return this.getUnpaiedInvoices(userId).stream().//
				map(i -> i.getContract().getMonthlyFare()).//
				collect(Collectors.summingDouble(Double::doubleValue));
	}

}
