package sample.camel;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;
import jakarta.jms.ConnectionFactory;
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.jms.endpoint.JmsEndpoint;
import org.citrusframework.jms.message.JmsMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleCamelRouteEndpointConfig {

    @Bean
//    @ConditionalOnProperty(name = "system.under.test.mode", havingValue = "embedded")
    public BeforeSuite embeddedTodoApp() {
        return new SequenceBeforeSuite.Builder()
                .actions(context -> SpringApplication.run(SampleCamelApplication.class))
                .build();
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
//        RMQDestination jmsDestination = new RMQDestination();
//        jmsDestination.setAmqp(true);
//        jmsDestination.setDestinationName("foo.queue");
//        jmsDestination.setQueue(true);
//        jmsDestination.setAmqpExchangeName("foo");
//        jmsDestination.setAmqpRoutingKey("");
//        jmsDestination.setAmqpQueueName("foo.queue");
        return new RMQDestination("foo.queue", "", "foo.queue", "");
    }

    @Bean(name = "barQueueDestination")
    public RMQDestination barQueueDestination() {
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
    public JmsEndpoint inOutQueueEndpoint(ConnectionFactory connectionFactory, @Qualifier("fooQueueDestination") RMQDestination fooQueueDestination, @Qualifier("barQueueDestination") RMQDestination barQueueDestination) {

        return CitrusEndpoints
                .jms()
//                .asynchronous()
                .synchronous()
                .connectionFactory(connectionFactory)
                .destination(fooQueueDestination)
                .replyDestination("bar.queue")
                .destinationResolver((session, destinationName, pubSubDomain) -> barQueueDestination)
//                .replyDestination(barQueueDestination)
//                .replyDestination(out)
                .build();
    }


}
