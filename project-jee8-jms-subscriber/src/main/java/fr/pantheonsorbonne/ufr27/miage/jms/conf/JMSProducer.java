package fr.pantheonsorbonne.ufr27.miage.jms.conf;

import org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

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

		// SNCF
		jndiBindings.put("topic.ParisTopic", "ParisTopic");
		jndiBindings.put("topic.BordeauxTopic", "BordeauxTopic");
		jndiBindings.put("topic.AmiensTopic", "AmiensTopic");
		jndiBindings.put("topic.LilleTopic", "LilleTopic");
		jndiBindings.put("topic.LyonTopic", "LyonTopic");

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
	public ConnectionFactory getJMSConnectionFactory() throws NamingException {
		return (ConnectionFactory) JNDI_CONTEXT.lookup("ConnectionFactory");
	}

	// SNCF
	@Produces
	@Named("ParisTopic")
	public Topic getParisJMSTopic() throws NamingException {
		return (Topic) JNDI_CONTEXT.lookup("ParisTopic");
	}

	@Produces
	@Named("BordeauxTopic")
	public Topic getBordeauxJMSTopic() throws NamingException {
		return (Topic) JNDI_CONTEXT.lookup("BordeauxTopic");
	}

	@Produces
	@Named("AmiensTopic")
	public Topic getAmiensJMSTopic() throws NamingException {
		return (Topic) JNDI_CONTEXT.lookup("AmiensTopic");
	}

	@Produces
	@Named("LilleTopic")
	public Topic getLilleJMSTopic() throws NamingException {
		return (Topic) JNDI_CONTEXT.lookup("LilleTopic");
	}

	@Produces
	@Named("LyonTopic")
	public Topic getLyonJMSTopic() throws NamingException {
		return (Topic) JNDI_CONTEXT.lookup("LyonTopic");
	}


}
