/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.camel;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route that triggers from a timer and routes to RabbitMQ
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class SampleCamelRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("spring-rabbitmq:%s?queues=%s".formatted("", "foo.queue"))
                .routeId("worker-route") // Optional: Set a route ID for easier identification
                .log(LoggingLevel.INFO, ">>> GOT THE FOLLOWING MESSAGE FROM RABBITMQ: ${body}")
                .process(exchange -> {
                    // Example of processing logic
                    String incomingMessage = exchange.getIn().getBody(String.class);
                    String correlationId = exchange.getIn().getHeader("JMSCorrelationID", String.class);

                    // Simulate generating a response based on the incoming message
                    String jsonResponse = String.format("{\"response\": \"Processed message with ID %s and body: %s\"}", correlationId, incomingMessage);

                    // Set the response body and headers
                    exchange.getMessage().setBody(jsonResponse, String.class);
                })
                .setHeader(Exchange.CONTENT_TYPE, constant("plain/text"))
                .setHeader("CamelRabbitmqContentType", constant("plain/text"))
//                .setHeader("JMSType", constant("TextMessage"))
                .convertBodyTo(String.class)  // Ensure the message is treated as a TextMessage
//                .wireTap("spring-rabbitmq:?queues=foo.queue.debug&routingKey=foo.queue.debug")
//                .wireTap("spring-rabbitmq:?queues=foo.queue.debug&routingKey=foo.queue.debug")
                .log(LoggingLevel.INFO, ">>> SENDING RESPONSE BACK TO PRODUCER: ${body}")

//                .setExchangePattern(ExchangePattern.InOut)
        ; // This is not strictly necessary here, as it defaults to InOut when replying
    }
}
