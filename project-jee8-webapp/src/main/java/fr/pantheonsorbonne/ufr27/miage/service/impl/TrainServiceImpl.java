package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.DelayInfo;
import fr.pantheonsorbonne.ufr27.miage.service.TrainService;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
@ManagedBean
public class TrainServiceImpl implements TrainService {

    @Inject
    EntityManager em;

    @Override
    public void processDelay(DelayInfo infoRetard) {
        System.out.println(infoRetard.getMessage());
        // Business Logic goes here
        /*
            if > 2h etc
         */
    }
}
