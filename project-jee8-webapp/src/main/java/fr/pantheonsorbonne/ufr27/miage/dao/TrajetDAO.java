package fr.pantheonsorbonne.ufr27.miage.dao;

import fr.pantheonsorbonne.ufr27.miage.jpa.DesserteReelle;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;


@ManagedBean
public class TrajetDAO implements Dao<Trajet> {

	@Inject
	EntityManager em;

	public Trajet find(int trajetId) {
		Trajet t = em.find(Trajet.class, trajetId);
		if (t == null) {
			throw new NoSuchElementException("Pas de trajet correspondant");
		}
		return t;
	}

	public void setDessertesReelles(Trajet trajet, List<DesserteReelle> desserteReelles) {
		em.getTransaction().begin();
		trajet.setDesserteReelles(desserteReelles);
		em.getTransaction().commit();
	}


	public List<Trajet> getTrajetByParcoursId(int parcoursId) {
		return em.createQuery("SELECT t FROM Trajet t WHERE t.parcoursId = " + parcoursId)
				.getResultList();
	}



	@Override
	public List<Trajet> getAll() {
		List<Trajet> t = em.createQuery("SELECT t FROM Trajet t").getResultList();
		return t;
	}

	public boolean delete(Trajet trajet) {
		try {
			em.getTransaction().begin();
			em.remove(trajet);
			em.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean save(Trajet trajet) {
		try {
			em.getTransaction().begin();
			em.persist(trajet);
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void update(Trajet trajet, Object[] params) {
		em.getTransaction().begin();
		trajet.setDesserteReelles(Objects.requireNonNull((List)params[0], "DesserteReelle ne peut pas etre nulle"));
		trajet.setDesserteTheoriques(Objects.requireNonNull((List)params[1], "DesserteTheorique ne peut pas etre nulle"));
		//manager.merge(trajet);
		em.getTransaction().commit();
	}

}
