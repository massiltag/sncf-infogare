package fr.pantheonsorbonne.ufr27.miage.service;

import javax.ejb.Remote;

import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.exception.UserHasDebtException;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.FreeTrialPlan;

@Remote
public interface GymService {

	public void cancelMemberShip(int userId) throws UserHasDebtException, NoSuchUserException;

	public int createMembership(FreeTrialPlan plan);
}