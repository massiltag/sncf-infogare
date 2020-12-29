package fr.pantheonsorbonne.ufr27.miage.dao;

import fr.pantheonsorbonne.ufr27.miage.jpa.Correspondance;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

public class CorrespondanceDAO implements Dao<Correspondance> {
    @Inject
    EntityManager em;

    public Correspondance find(int id) {
        Correspondance c = em.find(Correspondance.class, id);
        if (c == null) {
            throw new NoSuchElementException("Pas d'instance correspondante");
        }
        return c;
    }

    public boolean exists(int id) {
        return em.find(Correspondance.class, id) != null;
    }

    public List<Correspondance> findByTrainAndRupture(int trajetId, boolean rupture) {
        return em.createQuery(
                "SELECT c " +
                        "FROM Correspondance c " +
                        "WHERE c.rupture = " + rupture + " " +
                        "AND c.trajet.id = " + trajetId)
                .getResultList();
    }

    @Override
    public List<Correspondance> getAll() {
        return em.createQuery("SELECT c FROM Correspondance c").getResultList();
    }

    @Override
    public boolean save(Correspondance c) {
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(Correspondance c) {
        try {
            em.getTransaction().begin();
            em.remove(c);
            em.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Set rupture de correspondance, params = [boolean, Date]
    @Override
    public void update(Correspondance c, Object[] params) {
        em.getTransaction().begin();
        c.setRupture((Boolean) params[0]);
        c.setNewDate((Date) params[1]);
        em.getTransaction().commit();
    }
}
