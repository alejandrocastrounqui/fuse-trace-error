package ar.com.bancogalicia.fuse.trace.error.route;

import ar.com.bancogalicia.fuse.trace.error.route.strategy.TraceStrategy;
import ar.com.bancogalicia.fuse.trace.error.util.MDCUtil;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;


@Component
public class FuseTraceErrorRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FuseTraceErrorRoute.class);

    @Override
    public void configure() throws Exception {

        restConfiguration()
            .component("servlet")
            .bindingMode(RestBindingMode.json)
            .skipBindingOnErrorCode(false);

        from("direct:tracing-1").process(exchange -> {
            String mdcValue = MDC.get(MDCUtil.MDC_TEST_KEY);
            LOGGER.info("multicast item 1 MDC: " + mdcValue);
            exchange.getIn().setBody(mdcValue);
        });

        from("direct:tracing-2").process(exchange -> {
            String mdcValue = MDC.get(MDCUtil.MDC_TEST_KEY);
            LOGGER.info("multicast item 2 MDC: " + mdcValue);
            exchange.getIn().setBody(mdcValue);
        });

        // you must extract rest handler as new definition in order to be able to test behaviour
        from("direct:restFuseTracing")
            .process(exchange -> {
                MDC.put(MDCUtil.MDC_TEST_KEY, MDCUtil.MDC_TEST_VALUE);
            })
            .multicast(new TraceStrategy())
            .parallelProcessing()
            .to("direct:tracing-1")
            .to("direct:tracing-2")
            .stopOnException()
            .stopOnAggregateException()
            .end();

        rest("/tracing")
            .get()
            .to("direct:restFuseTracing");

    }

}
