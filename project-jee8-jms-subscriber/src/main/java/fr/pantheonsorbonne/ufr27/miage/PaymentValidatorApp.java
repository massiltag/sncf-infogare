package fr.pantheonsorbonne.ufr27.miage;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import fr.pantheonsorbonne.ufr27.miage.jms.PaymentProcessorBean;

/**
 * Hello world!
 *
 */
public class PaymentValidatorApp {
	public static void main(String[] args) throws InterruptedException, JMSException {
		// initialize CDI 2.0 SE container
		SeContainerInitializer initializer = SeContainerInitializer.newInstance();

		try (SeContainer container = initializer.disableDiscovery().addPackages(true, PaymentProcessorBean.class)
				.initialize()) {

			PaymentProcessorBean processor = container.select(PaymentProcessorBean.class).get();

			while (true) {
				processor.consume();
			}

		}

	}

}
