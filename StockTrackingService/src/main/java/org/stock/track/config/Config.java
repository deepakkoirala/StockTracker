package org.stock.track.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@PropertySource("classpath:application.properties")
public class Config {
    private static final Log logger = LogFactory.getLog(Config.class);
    @Value("${key}")
    private String key;

    @Value("${URL}")
    private String URL;

    @Value("${search.url}")
    private String searchUrl;

    @Bean
    public URI serverURI() {
        try {
            logger.info("connecting to " + URL);
            return new URI(URL + key);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Bean
    public String searchPath() {
        return searchUrl + "?token=" + key;
    }
}
