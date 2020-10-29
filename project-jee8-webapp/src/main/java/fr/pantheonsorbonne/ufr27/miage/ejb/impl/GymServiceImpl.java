/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.pantheonsorbonne.ufr27.miage.ejb.impl;

import java.util.Date;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.dao.InvoiceDAO;
import fr.pantheonsorbonne.ufr27.miage.ejb.GymService;
import fr.pantheonsorbonne.ufr27.miage.ejb.InvoicingService;
import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.exception.UserHasDebtException;
import fr.pantheonsorbonne.ufr27.miage.jpa.Card;
import fr.pantheonsorbonne.ufr27.miage.jpa.Contract;
import fr.pantheonsorbonne.ufr27.miage.jpa.Customer;

@ManagedBean
public class GymServiceImpl implements GymService {

	@Inject
	EntityManager em;

	@Inject
	InvoicingService is;

	@Override
	public int createMembership(String lname, String fname) {
		Customer customer = new Customer();
		customer.setLname(lname);
		customer.setFname(fname);

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

	@Inject
	InvoiceDAO invoiceDao;

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
