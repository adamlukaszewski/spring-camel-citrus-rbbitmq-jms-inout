package sample.camel;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;
import com.rabbitmq.jms.util.Util;
import jakarta.jms.*;
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.jms.endpoint.JmsEndpoint;
import org.citrusframework.jms.message.JmsMessageConverter;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleCamelRouteEndpointConfig {

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory3());
    }

    @Bean
//    @ConditionalOnProperty(name = "system.under.test.mode", havingValue = "embedded")
    public BeforeSuite embeddedTodoApp() {
        return new SequenceBeforeSuite.Builder()
                .actions(context -> SpringApplication.run(SampleCamelApplication.class))
                .build();
    }

    @Bean
    public org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory3() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672); // Replace with appropriate port

        return connectionFactory;
    }

    @Bean
    public Queue myDurableQueue() {
        // This queue has the following properties:
        // name: my_durable
        // durable: true
        // exclusive: false
        // auto_delete: false
        Queue queue = new Queue(Util.generateUUID("xxxxcccc-jms-temp-queue-"), true, false, false);
        System.out.println(">>>>> " + queue.getName() + "");
        return queue;
    }


    @Bean()
    public ConnectionFactory connectionFactory() {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672); // Replace with appropriate port
        return connectionFactory;
    }

//    @Bean
//    @Scope("prototype")
//    public RMQDestination jmsDestination() {
//        RMQDestination jmsDestination = new RMQDestination();
//        jmsDestination.setAmqp(false);
//        return jmsDestination;
//    }

    @Bean(name = "fooQueueDestination")
    public RMQDestination fooQueueDestination() {
        return new RMQDestination("foo.queue", "", "foo.queue", "");
    }

    @Bean(name = "barQueueDestination")
    public RMQDestination barQueueDestination(Queue queue) {
//        RMQDestination jmsDestination = new RMQDestination();
//        jmsDestination.setAmqp(true);
//        jmsDestination.setDestinationName("bar.queue");
//        jmsDestination.setQueue(true);
//        jmsDestination.setAmqpExchangeName("bar");
//        jmsDestination.setAmqpRoutingKey("bar.queue");
//        jmsDestination.setAmqpQueueName("bar.queue");
        return new RMQDestination("bar.queue", "", "bar.queue", "bar.queue");
    }


    @Bean
    public JmsEndpoint inOutQueueEndpoint(ConnectionFactory connectionFactory, @Qualifier("fooQueueDestination") RMQDestination fooQueueDestination, @Qualifier("barQueueDestination") RMQDestination barQueueDestination) throws JMSException {

        return CitrusEndpoints
                .jms()
//                .asynchronous()
                .synchronous()
                .connectionFactory(connectionFactory)
                .destination(fooQueueDestination)
                .replyDestination(barQueueDestination)
                .build();
    }


}
