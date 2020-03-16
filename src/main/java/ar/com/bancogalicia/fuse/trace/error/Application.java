package ar.com.bancogalicia.fuse.trace.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder){
        return restTemplateBuilder.build();
    }

    @Bean
    RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        LOG.info("fuse tracing error application is now running.....");
    }
}