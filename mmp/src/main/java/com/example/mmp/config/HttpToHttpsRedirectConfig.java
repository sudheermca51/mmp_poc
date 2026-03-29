package com.example.mmp.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpToHttpsRedirectConfig {

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat =
                new TomcatServletWebServerFactory() {
                    @Override
                    protected void postProcessContext(org.apache.catalina.Context context) {
                        org.apache.tomcat.util.descriptor.web.SecurityConstraint securityConstraint =
                                new org.apache.tomcat.util.descriptor.web.SecurityConstraint();
                        securityConstraint.setUserConstraint("CONFIDENTIAL");

                        org.apache.tomcat.util.descriptor.web.SecurityCollection collection =
                                new org.apache.tomcat.util.descriptor.web.SecurityCollection();
                        collection.addPattern("/*");

                        securityConstraint.addCollection(collection);
                        context.addConstraint(securityConstraint);
                    }
                };

        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    private Connector httpConnector() {
        Connector connector =
                new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}