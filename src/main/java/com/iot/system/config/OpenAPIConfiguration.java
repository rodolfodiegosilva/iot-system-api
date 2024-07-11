package com.iot.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Value("${url.environment}")
    private String urlEnvironment;

    @Value("${environment}")
    private String environment;


    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl(environment);
        server.setDescription(urlEnvironment);

        Contact myContact = new Contact();
        myContact.setName("Rodolfo Silva");
        myContact.setEmail("rodolfo.diego.gomes@gmail.com");

        Info information = new Info()
                .title("IoT System API")
                .version("1.0.0")
                .description("This API exposes endpoints to manage IoT devices.")
                .contact(myContact);

        return new OpenAPI().info(information).servers(List.of(server));
    }
}
