package fr.pantheonsorbonne.ufr27.miage.jms.conf;

import java.util.Hashtable;
import java.util.function.Supplier;

import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory;

public class PaymentAckQueueSupplier implements Supplier<Queue>{

	private static final Context JNDI_CONTEXT;

	static {
		Hashtable<String, String> jndiBindings = new Hashtable<>();
		jndiBindings.put(Context.INITIAL_CONTEXT_FACTORY, ActiveMQInitialContextFactory.class.getName());
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

	@Override
	public Queue get() {
		try {
			return (Queue) JNDI_CONTEXT.lookup("PaymentAckQueue");
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

}
