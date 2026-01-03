package dev.prince.nimbus.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:nimbus}")
    private String appName;

    private static final String CONTACT_NAME = "NIMBUS";
    private static final String CONTACT_URL = "https://github.com/princeabhijeet/nimbus";

    @Bean
    public OpenAPI customOpenAPI() {

        Contact contact = new Contact();
        contact.setName(CONTACT_NAME);
        contact.setUrl(CONTACT_URL);

        Info info = new Info()
                .title(appName)
                .description(appName + " APIs")
                .contact(contact).version("1.0");

        return new OpenAPI()
                .info(info);
    }

}
