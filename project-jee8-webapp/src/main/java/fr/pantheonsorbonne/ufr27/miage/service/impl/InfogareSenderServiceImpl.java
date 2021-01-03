package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.jms.TrainPublisher;
import fr.pantheonsorbonne.ufr27.miage.service.InfogareSenderService;

import javax.inject.Inject;

public class InfogareSenderServiceImpl implements InfogareSenderService {

    @Inject
    TrainPublisher trainPublisher;

    @Override
    public void send(String message) {
        trainPublisher.publish(message);
    }

}
