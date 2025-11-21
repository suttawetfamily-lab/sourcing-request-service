package com.pantavanij.sourcingreq.services.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {
    private static final String PROTOCOL = "AJP/1.3";

    @Value("${tomcat.ajp.port}") // Defined on application.properties
    private int ajpPort;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
        return server -> {
            if (server instanceof TomcatServletWebServerFactory) {
                ((TomcatServletWebServerFactory) server)
                        .addAdditionalTomcatConnectors(redirectConnector());

            }
        };
    }

    private Connector redirectConnector() {
        Connector connector = new Connector(PROTOCOL);
        connector.setPort(ajpPort);
        connector.setSecure(false);
        connector.setAllowTrace(false);
        ((AbstractAjpProtocol) connector.getProtocolHandler()).setSecretRequired(false);
        return connector;
    }
}
