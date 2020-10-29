package fr.pantheonsorbonne.ufr27.miage.dao;

import java.util.NoSuchElementException;

import javax.annotation.ManagedBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jpa.Payment;

import javax.inject.Inject;

@ManagedBean
public class PaymentDAO {

	@Inject
	EntityManager manager;

	public boolean isPaymentValidated(int paymentId) {

		Payment p = manager.find(Payment.class, paymentId);
		if (p == null) {
			throw new NoSuchElementException("No Such Payment");
		}
		return p.isValidated();

	}

}
