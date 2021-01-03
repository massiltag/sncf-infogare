package fr.pantheonsorbonne.ufr27.miage;

import fr.pantheonsorbonne.ufr27.miage.jms.TrainSubscriber;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.jms.JMSException;

/**
 * Hello world!
 *
 */
public class PaymentValidatorApp {
	public static void main(String[] args) throws InterruptedException, JMSException {
		// initialize CDI 2.0 SE container
		SeContainerInitializer initializer = SeContainerInitializer.newInstance();

		try (SeContainer container = initializer.disableDiscovery().addPackages(true, TrainSubscriber.class)
				.initialize()) {

			TrainSubscriber handler = container.select(TrainSubscriber.class).get();

			while (true) {
				handler.consume();
			}

		}

	}

}
