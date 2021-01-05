package fr.pantheonsorbonne.ufr27.miage.service.impl;

import fr.pantheonsorbonne.ufr27.miage.jms.publisher.*;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InfoDTO;
import fr.pantheonsorbonne.ufr27.miage.service.InfogareSenderService;

import javax.inject.Inject;

public class InfogareSenderServiceImpl implements InfogareSenderService {

    @Inject
    ParisPublisher parisPublisher;

    @Inject
    BordeauxPublisher bordeauxPublisher;

    @Inject
    AmiensPublisher amiensPublisher;

    @Inject
    LillePublisher lillePublisher;

    @Inject
    LyonPublisher lyonPublisher;

    @Override
    public void send(String codeGare, InfoDTO infoDTO) {
        this.getPublisherOf(codeGare).publish(infoDTO);
    }

    private Publisher getPublisherOf(String codeGare) {
        switch (codeGare) {
            case "PAR":
                return parisPublisher;
            case "BDX":
                return bordeauxPublisher;
            case "AMS":
                return amiensPublisher;
            case "LIL":
                return lillePublisher;
            case "LYN":
                return lyonPublisher;
            default:
                return null;
        }
    }

}
