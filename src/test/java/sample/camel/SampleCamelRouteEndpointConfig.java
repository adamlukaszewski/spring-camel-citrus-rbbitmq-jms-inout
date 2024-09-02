package sample.camel;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;
import jakarta.jms.ConnectionFactory;
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.jms.endpoint.JmsEndpoint;
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


    @Bean
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

    @Bean
    public JmsEndpoint inOutQueue(ConnectionFactory connectionFactory) {

        RMQDestination jmsDestination = new RMQDestination();
        jmsDestination.setAmqp(true);
        jmsDestination.setDestinationName("foo.queue");
        jmsDestination.setQueue(true);
        jmsDestination.setAmqpExchangeName("foo");
        jmsDestination.setAmqpRoutingKey("");
        jmsDestination.setAmqpQueueName("foo.queue");


        return CitrusEndpoints
                .jms()
//                .asynchronous()
                .synchronous()
                .connectionFactory(connectionFactory)
                .destination(jmsDestination)
//                .replyDestination(out)
                .build();
    }


}
