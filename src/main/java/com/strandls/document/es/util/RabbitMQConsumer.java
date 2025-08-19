/**
 * 
 */
package com.strandls.document.es.util;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import jakarta.inject.Inject;

/**
 * 
 * @author vishnu
 *
 */
public class RabbitMQConsumer {

	private final static String DOCUMENT_QUEUE = "documentQueue";

	@Inject
	private ESUpdate esUpdate;

	@Inject
	private Channel channel;

	public void elasticUpdate() throws Exception {
		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");
			BasicProperties properties = delivery.getProperties();
			String documentId = properties.getType();
			System.out.println("----[RABBIT MQ CONSUMER]---");
			System.out.println("consuming document Id :" + message);
			System.out.println("Updating :" + documentId);

			ESUpdateThread updateThread = new ESUpdateThread(esUpdate, message, documentId);
			Thread thread = new Thread(updateThread);
			thread.start();

		};
		channel.basicConsume(DOCUMENT_QUEUE, true, deliverCallback, consumerTag -> {
		});
	}

}
