package fr.pantheonsorbonne.ufr27.miage.dao;

import javax.annotation.ManagedBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.jpa.Customer;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Address;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.User;

@ManagedBean
public class UserDAO {

	@Inject
	EntityManager em;

	public User getUserFromId(int id) throws NoSuchUserException {

		Customer customer = em.find(Customer.class, id);
		if(customer==null) {
			throw new NoSuchUserException();
		}
		User user = new ObjectFactory().createUser();
		user.setFname(customer.getFname());
		user.setLname(customer.getLname());
		user.setMembershipId(customer.getId());
		return user;

	}

	public void updateUserAddress(int userId, Address address) throws NoSuchUserException {
		em.getTransaction().begin();
		Customer customer = em.find(Customer.class, userId);
		if(customer==null) {
			throw new NoSuchUserException();
		}
		fr.pantheonsorbonne.ufr27.miage.jpa.Address customerAddress = customer.getAddress();
		customerAddress.setCountry(address.getCountry());
		customerAddress.setStreeNumber(address.getStreetNumber());
		customerAddress.setStreetName(address.getStreetName());
		customerAddress.setZipCode(address.getZipCode());

		em.merge(address);
		em.getTransaction().commit();

	}

}
