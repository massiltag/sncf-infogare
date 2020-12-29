package fr.pantheonsorbonne.ufr27.miage.dao;

import fr.pantheonsorbonne.ufr27.miage.jpa.Passager;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;

public class PassagerDAO implements Dao<Passager> {

    @Inject
    EntityManager em;

    public Passager find(int passagerId) {
        Passager p = em.find(Passager.class, passagerId);
        if (p == null) {
            throw new NoSuchElementException("Pas de passager correspondant");
        }
        return p;
    }

    public List<Passager> findByTrainId(int trainId) {
        return em.createQuery("SELECT p FROM Passager p WHERE p.trajet.id = " + trainId).getResultList();
    }


    @Override
    public List<Passager> getAll() {
        return em.createQuery("SELECT p FROM Passager p").getResultList();
    }

    @Override
    public boolean save(Passager passager) {
        try {
            em.getTransaction().begin();
            em.persist(passager);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(Passager passager) {
        try {
            em.getTransaction().begin();
            em.remove(passager);
            em.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void update(Passager passager, Object[] params) {}

}
