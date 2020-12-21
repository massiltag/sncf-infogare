package fr.pantheonsorbonne.ufr27.miage.dao;

import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;

public class GareDAO implements Dao<Gare> {

    @Inject
    EntityManager em;

    public Gare find(int gareId) {
        Gare g = em.find(Gare.class, gareId);
        if (g == null) {
            throw new NoSuchElementException("Pas de trajet correspondant");
        }
        return g;
    }

    @Override
    public List<Gare> getAll() {
        return em.createQuery("SELECT g FROM Gare g").getResultList();
    }

    @Override
    public boolean save(Gare gare) {
        try {
            em.getTransaction().begin();
            em.persist(gare);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(Gare gare) {
        try {
            em.getTransaction().begin();
            em.remove(gare);
            em.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void update(Gare gare, Object[] params) {}
}
