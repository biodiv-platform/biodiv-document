/**
 * 
 */
package com.strandls.document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.strandls.document.util.PropertyFileUtil;

/**
 * @author Abhishek Rudra
 *
 */
public class RabbitMqConnection {

	private final Logger logger = LoggerFactory.getLogger(RabbitMqConnection.class);

	private final static String DOCUMENT_QUEUE = "documentQueue";
	private final static String ROUTING_DOCUMENT = "document";

	public static final String DOCSCI_QUEUE = "docSciQueue";
	public static final String DOCSCI_ROUTING_KEY = "docSci.updated";

	public final static String EXCHANGE_BIODIV;

	static {
		Properties properties = PropertyFileUtil.fetchProperty("config.properties");
		EXCHANGE_BIODIV = properties.getProperty("rabbitmq_exchange");
	}

	public Channel setRabbitMQConnetion() throws IOException, TimeoutException {

		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");

		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		String rabbitmqHost = properties.getProperty("rabbitmq_host");
		Integer rabbitmqPort = Integer.parseInt(properties.getProperty("rabbitmq_port"));
		String rabbitmqUsername = properties.getProperty("rabbitmq_username");
		String rabbitmqPassword = properties.getProperty("rabbitmq_password");
		in.close();

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(rabbitmqHost);
		factory.setPort(rabbitmqPort);
		factory.setUsername(rabbitmqUsername);
		factory.setPassword(rabbitmqPassword);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_BIODIV, "direct");
		channel.queueDeclare(DOCUMENT_QUEUE, false, false, false, null);
		channel.queueBind(DOCUMENT_QUEUE, EXCHANGE_BIODIV, ROUTING_DOCUMENT);
		channel.queueDeclare(DOCSCI_QUEUE, false, false, false, null);
		channel.queueBind(DOCSCI_QUEUE, EXCHANGE_BIODIV, DOCSCI_ROUTING_KEY);

		return channel;

	}
}
