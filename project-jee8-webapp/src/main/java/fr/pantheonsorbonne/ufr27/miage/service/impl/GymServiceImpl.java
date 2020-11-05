/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.pantheonsorbonne.ufr27.miage.service.impl;

import java.util.Date;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.dao.InvoiceDAO;
import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.exception.UserHasDebtException;
import fr.pantheonsorbonne.ufr27.miage.jpa.Address;
import fr.pantheonsorbonne.ufr27.miage.jpa.Card;
import fr.pantheonsorbonne.ufr27.miage.jpa.Contract;
import fr.pantheonsorbonne.ufr27.miage.jpa.Customer;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.FreeTrialPlan;
import fr.pantheonsorbonne.ufr27.miage.service.GymService;
import fr.pantheonsorbonne.ufr27.miage.service.InvoicingService;

@ManagedBean
public class GymServiceImpl implements GymService {

	@Inject
	EntityManager em;

	@Inject
	InvoicingService is;

	@Inject
	InvoiceDAO invoiceDao;

	@Override
	public int createMembership(FreeTrialPlan plan) {

		Customer customer = new Customer();
		customer.setLname(plan.getUser().getLname());
		customer.setFname(plan.getUser().getFname());

		Address address = new Address();
		address.setCountry(plan.getAddress().getCountry());
		address.setStreeNumber(plan.getAddress().getStreetNumber());
		address.setStreetName(plan.getAddress().getStreetName());
		address.setZipCode(plan.getAddress().getZipCode());
		customer.setAddress(address);

		Contract contract = new Contract();
		contract.setMonthlyFare(19.99);
		contract.setCustomer(customer);

		Card card = new Card();
		card.setActive(true);
		card.setContract(contract);

		customer.getContracts().add(contract);

		em.persist(customer);
		em.persist(contract);
		em.persist(card);

		is.sendNextInvoice(customer.getId());

		return customer.getId();

	}

	@Override
	public void cancelMemberShip(int userId) throws UserHasDebtException, NoSuchUserException {
		em.getTransaction().begin();
		Customer customer = em.find(Customer.class, userId);

		if (!customer.isActive()) {
			throw new NoSuchUserException();
		}

		double debt = invoiceDao.getUserDebt(userId);

		if (debt > 0) {
			throw new UserHasDebtException(debt, userId);
		}

		for (Contract c : customer.getContracts()) {

			for (Card card : c.getCards()) {
				card.setActive(false);
				em.merge(card);

			}

			c.setEnDate(new Date());
			em.merge(c);
		}

		customer.setActive(false);
		em.merge(customer);

		em.getTransaction().commit();
	}

}
