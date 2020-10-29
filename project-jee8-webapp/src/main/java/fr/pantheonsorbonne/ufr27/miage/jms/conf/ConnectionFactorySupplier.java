package fr.pantheonsorbonne.ufr27.miage.jms.conf;

import java.util.Hashtable;
import java.util.function.Supplier;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory;

public class ConnectionFactorySupplier implements Supplier<ConnectionFactory> {

	private static final Context JNDI_CONTEXT;

	static {
		Hashtable<String, String> jndiBindings = new Hashtable<>();
		jndiBindings.put(Context.INITIAL_CONTEXT_FACTORY, ActiveMQInitialContextFactory.class.getName());
		jndiBindings.put("connectionFactory.ConnectionFactory", "tcp://localhost:61616");

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
	public ConnectionFactory get() {
		try {
			return (ConnectionFactory) JNDI_CONTEXT.lookup("ConnectionFactory");
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

}
