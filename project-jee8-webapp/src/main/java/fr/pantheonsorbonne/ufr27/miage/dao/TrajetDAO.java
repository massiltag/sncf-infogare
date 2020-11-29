package fr.pantheonsorbonne.ufr27.miage.dao;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.annotation.ManagedBean;
import javax.persistence.EntityManager;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import javax.inject.Inject;


@ManagedBean
public class TrajetDAO implements Dao<Trajet> {

	@Inject
	EntityManager manager;

	public Trajet findById(int trajetId) {
		Trajet t = manager.find(Trajet.class, trajetId);
		if (t == null) {
			throw new NoSuchElementException("Pas de trajet correspondant");
		}
		return t;
	}

	@Override
	public List<Trajet> getAll() {
		List<Trajet> t = manager.createQuery("SELECT t FROM Trajet t").getResultList();
		return t;
	}

	public boolean delete(Trajet trajet) {
		try {
			manager.remove(trajet);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean save(Trajet trajet) {
		try {
			manager.persist(trajet);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void update(Trajet trajet, Object[] params) {
		trajet.setDesserteReelles(Objects.requireNonNull((List)params[0], "DesserteReelle ne peut pas etre nulle"));
		trajet.setDesserteTheoriques(Objects.requireNonNull((List)params[1], "DesserteTheorique ne peut pas etre nulle"));
		manager.merge(trajet);
	}

}
