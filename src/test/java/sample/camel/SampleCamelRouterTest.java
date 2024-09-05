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
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Christoph Deppisch
 */
@CitrusSpringSupport
@ContextConfiguration(classes = {CitrusSpringConfig.class, SampleCamalaInitApplication.class, SampleCamelRouteEndpointConfig.class})
public class SampleCamelRouterTest {

    @CitrusEndpoint()
    private JmsEndpoint inOutQueueEndpoint;

    @Test
    @CitrusTest
    void springBeanTest(@CitrusResource TestCaseRunner a) {
        a.variable("todoName", "citrus:concat('todo_', citrus:randomNumber(4))");
        a.variable("todoDescription", "Description: ${todoName}");

        // Use the endpoint configured without a specific destination, set headers in the send action
        a.$(SendMessageAction.Builder.send()
                .endpoint(this.inOutQueueEndpoint)
                .message()
                .type(MessageType.PLAINTEXT)
                .header("JMSType", "TextMessage")
                .body("A foo description")
        );

        a.$(ReceiveMessageAction.Builder.receive()
                .endpoint(this.inOutQueueEndpoint)
                .message()
                .type(MessageType.JSON)
                .body("{\"response\": \"Processed message with ID null and body: A foo description\"}")
        );
    }
}
