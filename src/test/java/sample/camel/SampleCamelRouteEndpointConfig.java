package sample.camel;

import com.rabbitmq.client.Channel;
import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.jms.endpoint.JmsEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
//@Import(TodoAppAutoConfiguration.class)
public class SampleCamelRouteEndpointConfig {

    Logger logger = LoggerFactory.getLogger(SampleCamelRouteEndpointConfig.class);

    private com.rabbitmq.client.Connection rabbitConnection;  // Hold the RabbitMQ connection



    @Bean
    public RMQConnectionFactory jmsConnectionFactory() throws IOException, TimeoutException {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672); // Replace with appropriate port

        // Initialize RabbitMQ core connection to manage manually
        initializeRabbitConnection(connectionFactory);

        return connectionFactory;
    }

    private void initializeRabbitConnection(RMQConnectionFactory jmsConnectionFactory) throws IOException, TimeoutException {
        // Create a RabbitMQ ConnectionFactory using the same settings as the RMQConnectionFactory
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setHost(jmsConnectionFactory.getHost());
        factory.setPort(jmsConnectionFactory.getPort());
        factory.setUsername(jmsConnectionFactory.getUsername());
        factory.setPassword(jmsConnectionFactory.getPassword());
        factory.setVirtualHost(jmsConnectionFactory.getVirtualHost());

        // Open a connection to RabbitMQ and store it
        this.rabbitConnection = factory.newConnection();
    }

    @Bean(name = "fooQueueDestination")
    public RMQDestination fooQueueDestination() {
        return new RMQDestination("foo.queue", "", "foo.queue", "");
    }

    @Bean(name = "temporaryQueueName")
    public String temporaryQueueName() throws IOException, TimeoutException {
        // Check if the temporary queue name is already cached

        // Use the existing RabbitMQ connection to declare a temporary queue
        try (Channel channel = rabbitConnection.createChannel()) {
            // Declare a temporary queue with a unique name, exclusive and auto-delete properties
            String tempQueueName = channel.queueDeclare("", false, false, true, null).getQueue();
            logger.info("Temporary queue created: {}", tempQueueName);
            return tempQueueName;
        }
    }

    @Bean
    public JmsEndpoint inOutQueueEndpoint(
            @Qualifier("jmsConnectionFactory") RMQConnectionFactory connectionFactory,
            @Qualifier("fooQueueDestination") RMQDestination fooQueueDestination,
            @Qualifier("temporaryQueueName") String temporaryQueueName
    ) {

        logger.info("Endpoint is using this {}", temporaryQueueName);

        RMQDestination tempQueueDestination = new RMQDestination(temporaryQueueName, "", temporaryQueueName, temporaryQueueName);
        tempQueueDestination.setAmqp(true); // Ensure AMQP protocol is used
        tempQueueDestination.setQueue(true);  // Ensure this is recognized as a queue

        return CitrusEndpoints
                .jms()
                .synchronous()
                .connectionFactory(connectionFactory)
                .destination(fooQueueDestination)
                .replyDestination(tempQueueDestination)
                .build();
    }
}