package ar.com.bancogalicia.fuse.trace.error.configuration;

import ar.com.bancogalicia.fuse.trace.error.util.MDCUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ThreadManagementProperties {

    public String[] getMdcKeys(){
        String[] result = {
            MDCUtil.MDC_TEST_KEY
        };
        return result;
    }

}
