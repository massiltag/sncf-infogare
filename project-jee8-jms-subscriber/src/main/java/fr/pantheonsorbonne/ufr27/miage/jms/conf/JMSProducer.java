package fr.pantheonsorbonne.ufr27.miage.jms.conf;

import java.util.Hashtable;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory;

/**
 * THis class produces bean to be injected in JMS Classes
 * 
 * @author nherbaut
 *
 */
public class JMSProducer {

	// fake JNDI context to create object
	private static final Context JNDI_CONTEXT;

	static {
		Hashtable<String, String> jndiBindings = new Hashtable<>();
		jndiBindings.put(Context.INITIAL_CONTEXT_FACTORY, ActiveMQInitialContextFactory.class.getName());
		jndiBindings.put("connectionFactory.ConnectionFactory", "tcp://localhost:61616");
		jndiBindings.put("queue.PaymentQueue", "PaymentQueue");
		jndiBindings.put("queue.PaymentAckQueue", "PaymentAckQueue");

		Context c = null;
		try {
			c = new InitialContext(jndiBindings);
		} catch (NamingException e) {
			e.printStackTrace();
			c = null;
			System.exit(-1);

		} finally {
			JNDI_CONTEXT = c;
		}
	}

	@Produces
	@Named("PaymentQueue")
	public Queue getPaymentQueue() throws NamingException {
		return (Queue) JNDI_CONTEXT.lookup("PaymentQueue");
	}

	@Produces
	@Named("PaymentAckQueue")
	public Queue getPaymentAckQueue() throws NamingException {
		return (Queue) JNDI_CONTEXT.lookup("PaymentAckQueue");
	}

	@Produces
	public ConnectionFactory getJMSConnectionFactory() throws NamingException {
		return (ConnectionFactory) JNDI_CONTEXT.lookup("ConnectionFactory");
	}

}
