package sample.camel;

import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class SampleCamalaInitApplication {

    @Bean
    public BeforeSuite embeddedTodoApp() {
        return new SequenceBeforeSuite.Builder()
                .actions(context -> SpringApplication.run(SampleCamelApplication.class))
                .build();
    }
}