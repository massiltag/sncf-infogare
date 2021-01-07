package fr.pantheonsorbonne.ufr27.miage.jms.publisher;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.*;
import java.io.Closeable;
import java.io.IOException;

@ApplicationScoped
public class BordeauxPublisher implements Closeable, Publisher {

    @Inject
    @Named("BordeauxTopic")
    Topic topic;

    @Inject
    ConnectionFactory connectionFactory;

    Connection connection;
    Session session;
    MessageProducer messagePublisher;

    int index = 0;

    @PostConstruct
    private void init() {
        try {
            this.connection = connectionFactory.createConnection("nicolas","nicolas");
            connection.start();
            this.session = connection.createSession();
            this.messagePublisher = session.createProducer(topic);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

    public String publish(String message) {
        try {

            this.messagePublisher.send(this.session.createTextMessage(message));
            return message;
        } catch (JMSException e) {
            System.out.println("Failed to send message to queue");
            return "Nothing sent";
        }
    }

    @Override
    public void close() throws IOException {
        try {
            messagePublisher.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            System.out.println("failed to close JMS resources");
        }

    }

}
