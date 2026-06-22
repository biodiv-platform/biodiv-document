/**
 * 
 */
package com.strandls.document.es.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.strandls.document.service.Impl.DocumentServiceImpl;
import com.strandls.esmodule.pojo.TaxonomyUpdateData;

import jakarta.inject.Inject;

/**
 * 
 * @author vishnu
 *
 */
public class RabbitMQConsumer {

	private final static String DOCUMENT_QUEUE = "documentQueue";
	public static final String DOCSCI_QUEUE       = "docSciQueue";

	@Inject
	private ESUpdate esUpdate;
	
	@Inject
	private DocumentServiceImpl docService;

	@Inject
	private Channel channel;
	
	private final ObjectMapper objectMapper = new ObjectMapper();

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
	
	public void listenToTaxonomyEvents() throws Exception {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("----[DOCUMENT EVENT]----");
            System.out.println("Received: " + message);
            TaxonomyUpdateData event = objectMapper.readValue(message,  TaxonomyUpdateData.class); 

            docService.handleTaxonByName(event);

        };

        channel.basicConsume(DOCSCI_QUEUE, true, deliverCallback, consumerTag -> {});
    }

}
