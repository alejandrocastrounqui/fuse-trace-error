package ar.com.bancogalicia.fuse.trace.error.configuration;

import ar.com.bancogalicia.fuse.trace.error.util.CustomUnitOfWork;
import ar.com.bancogalicia.fuse.trace.error.util.MDCUtil;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.opentracing.OpenTracingTracer;
import org.apache.camel.spi.UnitOfWork;
import org.apache.camel.spi.UnitOfWorkFactory;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FuseTraceErrorConfiguration {

    @Bean({"tracingCamelContextConfiguration"})
    public CamelContextConfiguration tracingCamelContextConfiguration() {
        return new CamelContextConfiguration() {
            public void beforeApplicationStart(CamelContext context) {
                OpenTracingTracer tracer = new OpenTracingTracer();
                tracer.init(context);
            }

            public void afterApplicationStart(CamelContext context) {
                //do nothing
            }
        };
    }

    @Bean
    UnitOfWorkFactory customUnitOfWorkFactory() {
        return new UnitOfWorkFactory() {
            Integer count = 0;
            Logger LOGGER = LoggerFactory.getLogger("CustomUnitOfWorkFactory");
            public synchronized Integer next(){
                return count++;
            }
            public UnitOfWork createUnitOfWork(Exchange exchange) {
                Integer index = next();
                LOGGER.info("unitOfWork:" + index + " createUnitOfWork");
                return new CustomUnitOfWork(exchange, index, MDCUtil.MDC_TEST_KEY);
            }
        };
    }

}
