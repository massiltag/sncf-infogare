package fr.pantheonsorbonne.ufr27.miage.ejb;

import fr.pantheonsorbonne.ufr27.miage.exception.NoDebtException;
import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Ccinfo;

public interface PaymentService {
	public int initiatePayAllDebts(Ccinfo info, int userId) throws NoDebtException, NoSuchUserException;
	public int initiatePayment(Ccinfo info, int userId, int invoiceId, double amount);
}