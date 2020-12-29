package fr.pantheonsorbonne.ufr27.miage.dao;

import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;

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
            throw new NoSuchElementException("Pas de gare correspondante");
        }
        return g;
    }

    public Gare getGareBySeqNumber(Trajet t, int seq) {
        return (Gare) em.createQuery("SELECT g from Trajet t, Gare g, DesserteReelle d " +
                "WHERE t.id = d.trajet.id " +
                "AND g.id = d.gare.id " +
                "AND d.trajet.id = " + t.getId() + " " +
                "AND d.seq = " + seq).getResultList().get(0);
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
