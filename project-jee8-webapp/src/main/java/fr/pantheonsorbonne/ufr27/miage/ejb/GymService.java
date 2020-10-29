package fr.pantheonsorbonne.ufr27.miage.ejb;

import javax.ejb.Remote;

import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.exception.UserHasDebtException;

@Remote
public interface GymService {
	/**
	 * 
	 * @param lname customer name
	 * @param fname customer fname
	 * @return the id of the customer
	 */
	public int createMembership(String lname, String fname);

	public void cancelMemberShip(int userId) throws UserHasDebtException, NoSuchUserException;
}