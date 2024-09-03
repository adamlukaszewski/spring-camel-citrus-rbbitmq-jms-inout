package sample.camel;

import org.citrusframework.TestCaseRunner;
import org.citrusframework.actions.ReceiveMessageAction;
import org.citrusframework.actions.SendMessageAction;
import org.citrusframework.annotations.CitrusEndpoint;
import org.citrusframework.annotations.CitrusResource;
import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.config.CitrusSpringConfig;
import org.citrusframework.jms.endpoint.JmsEndpoint;
import org.citrusframework.junit.jupiter.spring.CitrusSpringSupport;
import org.citrusframework.message.MessageType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Christoph Deppisch
 */
@CitrusSpringSupport
@ContextConfiguration(classes = {CitrusSpringConfig.class, SampleCamelRouteEndpointConfig.class})
public class SampleCamelRouterTest {

    @CitrusEndpoint()
    private JmsEndpoint inOutQueueEndpoint;

    @Test
    @CitrusTest
    void springBeanTest(@CitrusResource TestCaseRunner test) {
        test.variable("todoName", "citrus:concat('todo_', citrus:randomNumber(4))");
        test.variable("todoDescription", "Description: ${todoName}");


        // Use the endpoint configured without a specific destination, set headers in the send action
        test.$(SendMessageAction.Builder.send()
                .endpoint(this.inOutQueueEndpoint)
                .message()
                .type(MessageType.PLAINTEXT)
                .header("JMSType", "TextMessage")
                .body("A foo description")
        );

        test.$(ReceiveMessageAction.Builder.receive()
                .endpoint(this.inOutQueueEndpoint)
                .message()
                .type(MessageType.JSON)
                .body("{\"response\": \"Processed message with ID null and body: A foo description\"}")
        );
    }
}
