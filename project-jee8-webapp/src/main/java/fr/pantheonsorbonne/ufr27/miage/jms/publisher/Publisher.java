package fr.pantheonsorbonne.ufr27.miage.jms.publisher;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InfoDTO;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringWriter;

public interface Publisher {

    /**
     * <p>
     *     Marshal le DTO en XML et l'envoie sur le Topic.
     * </p>
     * @param infoDTO
     */
    default void publish(InfoDTO infoDTO) {
        try {
            StringWriter writer = new StringWriter();
            JAXBContext jaxbContext = JAXBContext.newInstance(InfoDTO.class);
            jaxbContext.createMarshaller().marshal(infoDTO, writer);
            this.publish(writer.toString());
        } catch (JAXBException e) {
            System.err.println("Failed to marshal InfoDTO : " + infoDTO.toString());
            e.printStackTrace();
        }
    }

    /**
     * <p>
     *     Publie un message au format texte.
     * </p>
     * @param message   Chaîne de caractères à envoyer
     * @return  Le message envoyé
     */
    String publish(String message);

}
