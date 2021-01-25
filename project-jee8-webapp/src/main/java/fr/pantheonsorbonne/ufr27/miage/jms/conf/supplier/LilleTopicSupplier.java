package fr.pantheonsorbonne.ufr27.miage.jms.conf.supplier;

import org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory;

import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.function.Supplier;

public class LilleTopicSupplier implements Supplier<Topic> {

    private static final Context JNDI_CONTEXT;

    static {
        Hashtable<String, String> jndiBindings = new Hashtable<>();
        jndiBindings.put(Context.INITIAL_CONTEXT_FACTORY, ActiveMQInitialContextFactory.class.getName());
        jndiBindings.put("topic.LilleTopic", "LilleTopic");


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
    public Topic get() {
        try {
            return (Topic) JNDI_CONTEXT.lookup("LilleTopic");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
