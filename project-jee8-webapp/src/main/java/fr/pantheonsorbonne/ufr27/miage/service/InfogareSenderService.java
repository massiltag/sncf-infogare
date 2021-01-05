package fr.pantheonsorbonne.ufr27.miage.service;


import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InfoDTO;

public interface InfogareSenderService {

    void send(String codeGare, InfoDTO infoDTO);

}
