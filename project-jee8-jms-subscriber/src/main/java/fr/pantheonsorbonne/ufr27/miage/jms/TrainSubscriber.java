package fr.pantheonsorbonne.ufr27.miage.jms;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

@ApplicationScoped
public class TrainSubscriber implements Closeable {

    @Inject
    @Named("TrainTopic")
    private Topic topic;

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
            messageConsumer = session.createDurableSubscriber(topic, "order-subscription");
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

    public String consume() {
        try {
            Message message = messageConsumer.receive();
            System.out.println(((TextMessage) message).getText());
            return ((TextMessage) message).getText();
        } catch (JMSException e) {
            System.out.println("failed to consume message ");
            return "";
        }
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

}
