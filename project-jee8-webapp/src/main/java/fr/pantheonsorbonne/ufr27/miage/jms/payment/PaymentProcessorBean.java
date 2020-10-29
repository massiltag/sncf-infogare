package fr.pantheonsorbonne.ufr27.miage.jms.payment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

@ApplicationScoped
public class PaymentProcessorBean implements MessageListener {

	@Inject
	private ConnectionFactory connectionFactory;

	@Inject
	@Named("PaymentAckQueue")
	private Queue queueAck;

	@Inject
	@Named("PaymentQueue")
	private Queue queuePayment;

	private Connection connection;

	private Session session;

	private MessageConsumer consumer;
	private MessageProducer producer;

	@PostConstruct
	private void init() {

		try {
			
			connection = connectionFactory.createConnection("nicolas", "nicolas");
			connection.start();
			session = connection.createSession();
			consumer = session.createConsumer(queuePayment);
			producer = session.createProducer(queueAck);
			
			MessageListener listener = this;

			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {
							Message message = consumer.receive();
							listener.onMessage(message);
						} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}).start();

		} catch (JMSException e) {
			throw new RuntimeException("failed to create JMS Session", e);
		}

	}

	@Override
	public void onMessage(Message message) {
		try {
			String ccnumber = message.getStringProperty("ccnumber");
			String date = message.getStringProperty("date");
			int ccv = message.getIntProperty("ccv");
			double amount = message.getDoubleProperty("amount");
			int paymentId = message.getIntProperty("paymentId");

			if (isPaymentValdated(ccnumber, date, ccv, amount)) {
				Message outgoingMessage = this.session.createMessage();
				outgoingMessage.setIntProperty("paymentId", paymentId);
				outgoingMessage.setBooleanProperty("validated", true);
				producer.send(outgoingMessage);
			}

		} catch (JMSException e) {
			throw new RuntimeException("failed to process payment", e);
		}

	}

	private boolean isPaymentValdated(String ccnumber, String date, int ccv, double amount) {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		
			e.printStackTrace();
		}
		
		return true;

	}

}
