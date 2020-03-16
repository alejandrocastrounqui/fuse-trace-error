package ar.com.bancogalicia.fuse.trace.error.controller;

import ar.com.bancogalicia.fuse.trace.error.util.MDCUtil;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
public class FuseTraceErrorRouteTest {

    @Autowired
    protected CamelContext camelContext;

    @Autowired
    private ProducerTemplate template;

    @Test
    public void whenMDCIsPropagatedThenCallableCanAccessMDC() throws Exception {
        Exchange exchange = ExchangeBuilder.anExchange(camelContext).build();
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
        exchange.getIn().setHeader(Exchange.HTTP_URI, "/fuse/tracing");
        String directRestFuseTracing = "direct:restFuseTracing";
        template.send(directRestFuseTracing, exchange);
        List<String> response = exchange.getIn().getBody(List.class);
        assertThat(response, everyItem(is(MDCUtil.MDC_TEST_VALUE)));
    }

    @Test
    public void whenEndpointIsPresentThenReturnOk() throws Exception {
        //TODO: test fuse rest definitions
    }

    @Test
    public void whenThereWasNoControllerThenApiReturnNotFound() throws Exception {
        //TODO: test fuse rest definitions
    }

}
