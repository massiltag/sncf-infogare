package fr.pantheonsorbonne.ufr27.miage.dao;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.annotation.ManagedBean;
import javax.persistence.EntityManager;
import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import javax.inject.Inject;
//pas de besoin de transaction.begin ou close ou commit 
//voir le fichier GymServiceImpl dans package Impl

@ManagedBean
public class DesserteReelleDAO implements Dao<DesserteReelle> {

	@Inject
	EntityManager manager;

	public DesserteReelle findById(int desserteId) {
		DesserteReelle d = manager.find(DesserteReelle.class, desserteId);
		if (d == null) {
			throw new NoSuchElementException("Pas de desserte correspondant");
		}
		return d;
	}

	@Override
	public List<DesserteReelle> getAll() {
		List<DesserteReelle> dr = manager.createQuery("SELECT d FROM DesserteReelle d").getResultList();
		return dr;
	}

	public boolean delete(DesserteReelle desserte) {
		try {
			manager.getTransaction().begin();
			manager.remove(desserte);
			manager.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean save(DesserteReelle desserte) {
		try {
			manager.getTransaction().begin();
			manager.persist(desserte);
			manager.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void update(DesserteReelle desserte, Object[] params) {
		manager.getTransaction().begin();
		desserte.setSeq(Objects.requireNonNull((int) params[0], "sequence ne peut pas etre nulle"));
		desserte.setDesservi(Objects.requireNonNull((boolean) params[1], "desservi ne peut pas etre nulle"));
		desserte.setArrivee(Objects.requireNonNull((Date) params[2], "Heure arrivee ne peut pas etre nulle"));
		desserte.setGare(Objects.requireNonNull((Gare) params[3], "Gare ne peut pas etre nulle"));
		desserte.setTrajet(Objects.requireNonNull((Trajet) params[4], "trajet ne peut pas etre nulle"));
		//manager.merge(desserte);
		manager.getTransaction().commit();
	}

}