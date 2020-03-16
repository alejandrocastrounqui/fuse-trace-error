package ar.com.bancogalicia.fuse.trace.error.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
public class FuseTraceErrorRouteTest {

    @Autowired
    protected CamelContext camelContext;

    @Autowired
    private ProducerTemplate template;

    @Test
    public void whenMDCIsPropagatedThenCallableCanAccessMDC() throws Exception {

    }

    @Test
    public void whenEndpointIsPresentThenReturnOk() throws Exception {
        String endpoint = "direct:restFuseTracing";
    }

    @Test
    public void whenThereWasNoControllerThenApiReturnNotFound() throws Exception {
    }

}
