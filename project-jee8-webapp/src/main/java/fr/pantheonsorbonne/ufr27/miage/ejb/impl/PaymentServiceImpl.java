package fr.pantheonsorbonne.ufr27.miage.ejb.impl;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.dao.InvoiceDAO;
import fr.pantheonsorbonne.ufr27.miage.ejb.PaymentService;
import fr.pantheonsorbonne.ufr27.miage.exception.NoDebtException;
import fr.pantheonsorbonne.ufr27.miage.exception.NoSuchUserException;
import fr.pantheonsorbonne.ufr27.miage.jpa.Payment;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Ccinfo;

@ApplicationScoped
@ManagedBean
public class PaymentServiceImpl implements PaymentService {

	@Inject
	EntityManager em;

	@Inject
	InvoiceDAO invoiceDao;

	@Inject
	private ConnectionFactory connectionFactory;

	@Inject
	@Named("PaymentQueue")
	private Queue queue;

	private Connection connection;

	private Session session;

	
	private MessageProducer messageProducer;

	@PostConstruct
	private void init() {

		try {
			connection = connectionFactory.createConnection("nicolas", "nicolas");
			connection.start();
			session = connection.createSession();
			messageProducer = session.createProducer(queue);
		} catch (JMSException e) {
			throw new RuntimeException("failed to create JMS Session", e);
		}
	}

	@Override
	public int initiatePayAllDebts(Ccinfo info, int userId) throws NoDebtException, NoSuchUserException {
		em.getTransaction().begin();
		try {

			double amount = invoiceDao.getUserDebt(userId);
			if (amount <= 0) {
				throw new NoDebtException();
			}
			Payment p = new Payment();
			p.setAmount(amount);
			p.setValidated(false);
			p.getInvoices().addAll(invoiceDao.getUnpaiedInvoices(userId));
			em.persist(p);

			Message message = session.createMessage();
			message.setStringProperty("ccnumber", info.getNumber());
			message.setStringProperty("date", info.getValidityDate());
			message.setIntProperty("ccv", info.getCcv());
			message.setDoubleProperty("amount", amount);
			message.setIntProperty("userId", userId);
			message.setIntProperty("paymentId", p.getId());
			messageProducer.send(message);
			em.getTransaction().commit();
			return p.getId();

		} catch (JMSException e) {
			em.getTransaction().rollback();
			throw new RuntimeException("failed to initiate payment", e);
		}
	}

	@Override
	public int initiatePayment(Ccinfo info, int userId, int invoiceId, double amount) {
		em.getTransaction().begin();
		try {

			Payment p = new Payment();
			p.setAmount(amount);
			p.setValidated(false);
			em.persist(p);

			Message message = session.createMessage();
			message.setStringProperty("ccnumber", info.getNumber());
			message.setStringProperty("date", info.getValidityDate());
			message.setIntProperty("ccv", info.getCcv());
			message.setDoubleProperty("amount", amount);
			message.setIntProperty("userId", userId);
			message.setIntProperty("paymentId", p.getId());
			messageProducer.send(message);
			em.getTransaction().commit();
			return p.getId();

		} catch (JMSException e) {
			em.getTransaction().rollback();
			throw new RuntimeException("failed to initiate payment", e);
		}
		
	}

}
