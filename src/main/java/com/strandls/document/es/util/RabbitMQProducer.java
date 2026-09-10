/**
 * 
 */
package com.strandls.document.es.util;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.strandls.document.RabbitChannelProvider;
import com.strandls.document.util.PropertyFileUtil;

import jakarta.inject.Inject;

/**
 *
 * @author vishnu
 *
 */
public class RabbitMQProducer {

	private String EXCHANGE = PropertyFileUtil.fetchProperty("config.properties", "rabbitmq_exchange");

	@Inject
	private RabbitChannelProvider channelProvider;

	public void setMessage(final String routingKey, String message, String documentId) throws Exception {

		try {
			BasicProperties properties = new BasicProperties(null, null, null, 2, 1, null, null, null, null, null,
					documentId, null, null, null);
			Channel channel = channelProvider.get();
			channel.basicPublish(EXCHANGE, routingKey, properties, message.getBytes("UTF-8"));
			System.out.println(" [RABBITMQ] Sent Document Id: '" + message + "'");
		} catch (IOException e) {
			System.out.print("==================================");
			System.out.print(e.toString());
			System.out.print("==================================");
		}
	}

}
