package fr.pantheonsorbonne.ufr27.miage;

import fr.pantheonsorbonne.ufr27.miage.conf.EMFFactory;
import fr.pantheonsorbonne.ufr27.miage.conf.EMFactory;
import fr.pantheonsorbonne.ufr27.miage.conf.PersistenceConf;
import fr.pantheonsorbonne.ufr27.miage.dao.*;
import fr.pantheonsorbonne.ufr27.miage.exception.mapper.ExceptionMapper;
import fr.pantheonsorbonne.ufr27.miage.jms.conf.ConnectionFactorySupplier;
import fr.pantheonsorbonne.ufr27.miage.jms.conf.JMSProducer;
import fr.pantheonsorbonne.ufr27.miage.jms.conf.supplier.*;
import fr.pantheonsorbonne.ufr27.miage.jms.publisher.*;
import fr.pantheonsorbonne.ufr27.miage.jms.utils.BrokerUtils;
import fr.pantheonsorbonne.ufr27.miage.service.*;
import fr.pantheonsorbonne.ufr27.miage.service.impl.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.inject.Singleton;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;

/**
 * Main class.
 *
 */
public class Main {

	public static final String BASE_URI = "http://localhost:8080/";

	public static HttpServer startServer() {

		final ResourceConfig rc = new ResourceConfig()//
				.packages(true, "fr.pantheonsorbonne.ufr27.miage")//
				.register(DeclarativeLinkingFeature.class)//
				.register(JMSProducer.class).register(ExceptionMapper.class).register(PersistenceConf.class)
				.register(new AbstractBinder() {

					@Override
					protected void configure() {

						// JPA, JMS
						bindFactory(EMFFactory.class).to(EntityManagerFactory.class).in(Singleton.class);
						bindFactory(EMFactory.class).to(EntityManager.class).in(RequestScoped.class);
						bindFactory(ConnectionFactorySupplier.class).to(ConnectionFactory.class).in(Singleton.class);

						// SNCF
						bind(TrainServiceImpl.class).to(TrainService.class);
						bind(StopServiceImpl.class).to(StopService.class);
						bind(TERServiceImpl.class).to(TERService.class);
						bind(RuptureServiceImpl.class).to(RuptureService.class);
						bind(DesserteReelleDAO.class).to(DesserteReelleDAO.class);
						bind(TrajetDAO.class).to(TrajetDAO.class);
						bind(GareDAO.class).to(GareDAO.class);
						bind(PassagerDAO.class).to(PassagerDAO.class);
						bind(CorrespondanceDAO.class).to(CorrespondanceDAO.class);
						bind(InfogareSenderServiceImpl.class).to(InfogareSenderService.class);

						// Topics
						bindFactory(ParisTopicSupplier.class).to(Topic.class).named("ParisTopic").in(Singleton.class);
						bindFactory(BordeauxTopicSupplier.class).to(Topic.class).named("BordeauxTopic").in(Singleton.class);
						bindFactory(AmiensTopicSupplier.class).to(Topic.class).named("AmiensTopic").in(Singleton.class);
						bindFactory(LilleTopicSupplier.class).to(Topic.class).named("LilleTopic").in(Singleton.class);
						bindFactory(LyonTopicSupplier.class).to(Topic.class).named("LyonTopic").in(Singleton.class);

						// Publishers
						bind(ParisPublisher.class).to(ParisPublisher.class).in(Singleton.class);
						bind(BordeauxPublisher.class).to(BordeauxPublisher.class).in(Singleton.class);
						bind(AmiensPublisher.class).to(AmiensPublisher.class).in(Singleton.class);
						bind(LillePublisher.class).to(LillePublisher.class).in(Singleton.class);
						bind(LyonPublisher.class).to(LyonPublisher.class).in(Singleton.class);

					}

				});

		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
	}

	/**
	 * Main method.beanbeanbeanbean
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Locale.setDefault(Locale.ENGLISH);
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		final HttpServer server = startServer();

		BrokerUtils.startBroker();

		PersistenceConf pc = new PersistenceConf();
		pc.getEM();
		pc.launchH2WS();

		System.out.println(String.format(
				"Jersey app started with WADL available at " + "%sapplication.wadl\nHit enter to stop it...",
				BASE_URI));

	}
}
