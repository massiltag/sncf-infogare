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

    /**
     * <p>
     *     Envoie les données à la gare passée en paramètre.
     * </p>
     * @param codeGare  Le code de la gare
     * @param infoDTO   Le DTO à envoyer
     */
    @Override
    public void send(String codeGare, InfoDTO infoDTO) {
        this.getPublisherOf(codeGare).publish(infoDTO);
    }

    /**
     * <p>
     *     Détermine le Publisher à utiliser selon le code de la gare.
     * </p>
     * @param codeGare  Le code de la gare
     * @return  Le {@link Publisher} à utiliser
     */
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
