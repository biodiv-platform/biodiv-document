/**
 *
 */
package com.strandls.document;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.servlet.ServletContainer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import com.strandls.activity.controller.ActivityServiceApi;
import com.strandls.document.controllers.DocumentControllerModule;
import com.strandls.document.dao.DocumentDaoModule;
import com.strandls.document.es.util.ESUtilModule;
import com.strandls.document.es.util.RabbitMQConsumer;
import com.strandls.document.service.Impl.DocumentServiceModule;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.file.api.UploadApi;
import com.strandls.geoentities.controllers.GeoentitiesServicesApi;
import com.strandls.landscape.controller.LandscapeApi;
import com.strandls.resource.controllers.ResourceServicesApi;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.userGroup.controller.UserGroupServiceApi;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import jakarta.servlet.ServletContextEvent;

/**
 * @author Abhishek Rudra
 *
 */
public class DocumentServeletContextListener extends GuiceServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(DocumentServeletContextListener.class);

	@Override
	protected Injector getInjector() {

		Injector injector = Guice.createInjector(new ServletModule() {
			@Override
			protected void configureServlets() {

				Configuration configuration = new Configuration();

				try {
					for (Class<?> cls : getEntityClassesFromPackage("com")) {
						configuration.addAnnotatedClass(cls);
					}
				} catch (ClassNotFoundException | IOException | URISyntaxException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}

				configuration = configuration.configure();
				SessionFactory sessionFactory = configuration.buildSessionFactory();

//				Rabbit MQ initialisation: one long-lived Connection for the app;
//				channels are handed out per-thread via RabbitChannelProvider instead
//				of sharing a single Channel across every producer/consumer thread.
				RabbitMqConnection rabbitMqConnection = new RabbitMqConnection();
				Connection rabbitConnection = null;
				try {
					rabbitConnection = rabbitMqConnection.connect();
				} catch (Exception e) {
					logger.error("Failed to establish RabbitMQ connection", e);
				}

				bind(Connection.class).toInstance(rabbitConnection);
				bind(RabbitChannelProvider.class).in(Scopes.SINGLETON);

				GeometryFactory geofactory = new GeometryFactory(new PrecisionModel(), 4326);
				bind(GeometryFactory.class).toInstance(geofactory);

				Map<String, String> props = new HashMap<String, String>();
				props.put("jakarta.ws.rs.Application", ApplicationConfig.class.getName());
				props.put("jersey.config.server.provider.packages", "com");
				props.put("jersey.config.server.wadl.disableWadl", "true");

				ObjectMapper objectMapper = new ObjectMapper();
				bind(ObjectMapper.class).toInstance(objectMapper);

				bind(SessionFactory.class).toInstance(sessionFactory);
				bind(Headers.class).in(Scopes.SINGLETON);
				bind(EsServicesApi.class).in(Scopes.SINGLETON);
				bind(UserServiceApi.class).in(Scopes.SINGLETON);
				bind(UserGroupServiceApi.class).in(Scopes.SINGLETON);
				bind(ResourceServicesApi.class).in(Scopes.SINGLETON);
				bind(GeoentitiesServicesApi.class).in(Scopes.SINGLETON);
				bind(ActivityServiceApi.class).in(Scopes.SINGLETON);
				bind(UploadApi.class).in(Scopes.SINGLETON);
				bind(ActivityServiceApi.class).in(Scopes.SINGLETON);
				bind(LandscapeApi.class).in(Scopes.SINGLETON);
				bind(ServletContainer.class).in(Scopes.SINGLETON);

				serve("/api/*").with(ServletContainer.class, props);
			}
		}, new DocumentControllerModule(), new DocumentDaoModule(), new DocumentServiceModule(), new ESUtilModule());

		try {
			injector.getInstance(RabbitMQConsumer.class).startConsuming();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return injector;

	}

	protected List<Class<?>> getEntityClassesFromPackage(String packageName)
			throws URISyntaxException, IOException, ClassNotFoundException {

		List<String> classNames = getClassNamesFromPackage(packageName);
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String className : classNames) {
			Class<?> cls = Class.forName(className);
			Annotation[] annotations = cls.getAnnotations();

			for (Annotation annotation : annotations) {
				if (annotation instanceof jakarta.persistence.Entity) {
					System.out.println("Mapping entity :" + cls.getCanonicalName());
					classes.add(cls);
				}
			}
		}

		return classes;
	}

	private static ArrayList<String> getClassNamesFromPackage(final String packageName)
			throws URISyntaxException, IOException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ArrayList<String> names = new ArrayList<String>();
		URL packageURL = classLoader.getResource(packageName);

		URI uri = new URI(packageURL.toString());
		File folder = new File(uri.getPath());

		Files.find(Paths.get(folder.getAbsolutePath()), 999, (p, bfa) -> bfa.isRegularFile()).forEach(file -> {
			String name = file.toFile().getAbsolutePath().replaceAll(folder.getAbsolutePath() + File.separatorChar, "")
					.replace(File.separatorChar, '.');
			if (name.indexOf('.') != -1) {
				name = packageName + '.' + name.substring(0, name.lastIndexOf('.'));
				names.add(name);
			}
		});

		return names;
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

		Injector injector = (Injector) servletContextEvent.getServletContext().getAttribute(Injector.class.getName());

		SessionFactory sessionFactory = injector.getInstance(SessionFactory.class);
		sessionFactory.close();

		Connection rabbitConnection = injector.getInstance(Connection.class);
		if (rabbitConnection != null) {
			// abort() (unlike close()) forces the connection down immediately and
			// cancels any in-flight/scheduled automatic-recovery attempt, and never
			// throws. A graceful close() was observed leaving the client's own
			// background recovery thread alive past contextDestroyed(), which then
			// crashed trying to use this webapp's classloader after Tomcat had
			// already stopped it (surfacing as a redeploy/reload memory leak).
			rabbitConnection.abort(AMQP.REPLY_SUCCESS, "context destroyed", 5000);
		}

		super.contextDestroyed(servletContextEvent);
		// ... First close any background tasks which may be using the DB ...
		// ... Then close any DB connection pools ...

		// Now deregister JDBC drivers in this context's ClassLoader:
		// Get the webapp's ClassLoader
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		// Loop through all drivers
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver.getClass().getClassLoader() == cl) {
				// This driver was registered by the webapp's ClassLoader, so deregister it:
				try {
					logger.info("Deregistering JDBC driver {}", driver);
					DriverManager.deregisterDriver(driver);
				} catch (SQLException ex) {
					logger.error("Error deregistering JDBC driver {}", driver, ex);
				}
			} else {
				// driver was not registered by the webapp's ClassLoader and may be in use
				// elsewhere
				logger.trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader",
						driver);
			}
		}

	}
}
