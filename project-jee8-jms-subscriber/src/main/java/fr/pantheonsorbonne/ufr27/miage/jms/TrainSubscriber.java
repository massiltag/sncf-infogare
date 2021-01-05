package fr.pantheonsorbonne.ufr27.miage.jms;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InfoDTO;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InfoTypeEnum;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static fr.pantheonsorbonne.ufr27.miage.util.StringUtil.*;

@ApplicationScoped
public class TrainSubscriber implements Closeable {

    // BUSINESS
    @Inject
//    @Named("BordeauxTopic")
//    @Named("ParisTopic")
    @Named("AmiensTopic")
//    @Named("LilleTopic")
//    @Named("LyonTopic")
    private Topic topic;

    private static final String infoGareType = InfoTypeEnum.ARRIVAL.value();

    private Map<Integer, InfoDTO> data = new HashMap<>();


    // JMS
    @Inject
    private ConnectionFactory connectionFactory;

    private Connection connection;
    private MessageConsumer messageConsumer;

    private Session session;

    @PostConstruct
    void init() {
        try {
            connection = connectionFactory.createConnection("nicolas", "nicolas");
            connection.setClientID("Train subscriber " + UUID.randomUUID());
            connection.start();
            session = connection.createSession();
            clearConsoleAndPrintName(topic);
            messageConsumer = session.createDurableSubscriber(topic, "train-topic");
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

    public void consume() {
        try {
            TextMessage message = (TextMessage) messageConsumer.receive();
            JAXBContext jaxbContext = JAXBContext.newInstance(InfoDTO.class);
            InfoDTO infoDTO = (InfoDTO) jaxbContext.createUnmarshaller().unmarshal(new StringReader(message.getText()));

            clearConsoleAndPrintName(topic);
            handle(infoDTO);
        } catch (JMSException | JAXBException e) {
            System.out.println("failed to consume message");
        }
    }

    public void handle(InfoDTO infoDTO) {
        System.out.println(printColor("Received Trains Info\n¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯", ANSI_CYAN));

        // Si on a l'info du train, l'enlever
        if (this.data.containsKey(infoDTO.getTrainId()))
            this.data.remove(infoDTO.getTrainId());
        // Ajouter la nouvelle info
        this.data.put(infoDTO.getTrainId(), infoDTO);

        // Affichage des données
        System.out.print(ANSI_BLUE);
        System.out.printf("%5s %10s %20s %30s %27s", "TrainID", "InfoType", "TrainName", "TrainType", "Timestamp");
        System.out.print("\n=============================================================");
        System.out.println("==========================================================");
        System.out.print(ANSI_RESET);
        this.data.forEach((id, dto) -> {
            System.out.printf("%7s %15s %40s %26s %41s",
                    printColor(String.valueOf(dto.getTrainId()), ANSI_CYAN),
                    dto.getInfoType(),
                    printColor(dto.getTrainName(), ANSI_RED),
                    dto.getTrainType().equals("TGV")
                            ? printColor(dto.getTrainType(), ANSI_PURPLE) : printColor(dto.getTrainType(), ANSI_YELLOW),
                    (dto.getTimestamp().equals("null")) ? "Train sans arrêt" : dto.getTimestamp()
            );
            System.out.println();
        });
    }


    @Override
    public void close() throws IOException {
        try {
            messageConsumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            System.out.println("Failed to close JMS resources");
        }

    }


    // Osef un peu quand même
    public String consumeStr() {
        try {
            Message message = messageConsumer.receive();
            clearConsoleAndPrintName(topic);
            System.out.println(((TextMessage) message).getText());
            return ((TextMessage) message).getText();
        } catch (JMSException e) {
            System.out.println("failed to consume message ");
            return "";
        }
    }

}
