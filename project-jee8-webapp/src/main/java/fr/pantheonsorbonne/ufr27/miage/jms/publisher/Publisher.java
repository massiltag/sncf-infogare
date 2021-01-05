package fr.pantheonsorbonne.ufr27.miage.jms.publisher;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InfoDTO;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringWriter;

public interface Publisher {

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

    String publish(String message);

}
