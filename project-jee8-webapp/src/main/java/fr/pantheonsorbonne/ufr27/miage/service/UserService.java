package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Address;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.User;

public interface UserService {

	User getUserFromId(int id) throws NoSuchUserException;

	Address getAddressForUser(int id) throws NoSuchUserException;

	void updateUserAddress(int userId, Address address) throws NoSuchUserException;

}