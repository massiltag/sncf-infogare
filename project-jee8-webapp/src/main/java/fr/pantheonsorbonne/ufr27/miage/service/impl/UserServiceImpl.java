package fr.pantheonsorbonne.ufr27.miage.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.ManagedBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.jpa.Customer;
import fr.pantheonsorbonne.ufr27.miage.jpa.Invoice;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Address;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.User;
import fr.pantheonsorbonne.ufr27.miage.service.UserService;

@ManagedBean
public class UserServiceImpl implements UserService {

	@Inject
	EntityManager em;

	private static final ObjectFactory dtoFactory = new ObjectFactory();

	@Override
	public User getUserFromId(int id) throws NoSuchUserException {

		Customer customer = em.find(Customer.class, id);
		if (customer == null) {
			throw new NoSuchUserException();
		}
		User user = new ObjectFactory().createUser();
		user.setFname(customer.getLname());
		user.setLname(customer.getLname());
		user.setMembershipId(customer.getId());
		user.setId(customer.getId());
		return user;

	}

	@Override
	public Address getAddressForUser(int id) throws NoSuchUserException {
		Customer customer = em.find(Customer.class, id);
		if (customer == null) {
			throw new NoSuchUserException();
		}
		Address address = dtoFactory.createAddress();
		address.setCountry(customer.getAddress().getCountry());
		address.setStreetName(customer.getAddress().getStreetName());
		address.setStreetNumber(customer.getAddress().getStreeNumber());
		address.setZipCode(customer.getAddress().getZipCode());
		return address;

	}

	@Override
	public void updateUserAddress(int userId, Address address) throws NoSuchUserException {
		em.getTransaction().begin();
		Customer customer = em.find(Customer.class, userId);
		if (customer == null) {
			throw new NoSuchUserException();
		}
		fr.pantheonsorbonne.ufr27.miage.jpa.Address customerAddress = customer.getAddress();
		customerAddress.setCountry(address.getCountry());
		customerAddress.setStreeNumber(address.getStreetNumber());
		customerAddress.setStreetName(address.getStreetName());
		customerAddress.setZipCode(address.getZipCode());

		em.merge(customerAddress);
		em.getTransaction().commit();

	}

	public Collection<Integer> getInvoices4Customer(int customerId) {

		Collection<Integer> res = new ArrayList<Integer>();
		Customer cust = em.find(Customer.class, customerId);

		cust.getContracts()
				.forEach(c -> res.addAll(c.getInvoices().stream().map(i -> i.getId()).collect(Collectors.toList())));

		return res;

	}

}
