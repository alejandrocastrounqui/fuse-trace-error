package ar.com.bancogalicia.fuse.trace.error.util;

import ar.com.bancogalicia.fuse.trace.error.route.FuseTraceErrorRoute;
import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultUnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public class CustomUnitOfWork extends DefaultUnitOfWork {

    private static final Logger LOGGER = LoggerFactory.getLogger(FuseTraceErrorRoute.class);

    private Integer index;
    private String[] mdcKeys;
    private final Map<String, String> mdcMapping = new HashMap<String, String>();

    public CustomUnitOfWork(Exchange exchange, Integer index, String ...mdcKeys) {
        super(exchange);
        this.index = index;
        LOGGER.info("unitOfWork:" + index + " constructor collect MDC keys " + String.join(",", mdcKeys));
        this.mdcKeys = mdcKeys;
        for(String mdcKey: mdcKeys){
            String mdcValue = MDC.get(mdcKey);
            if(mdcValue != null){
                LOGGER.info("unitOfWork:" + index + " constructor found MDC key " + mdcKey + ":" + mdcValue);
                mdcMapping.put(mdcKey, mdcValue);
            }
            else{
                LOGGER.info("unitOfWork:" + index + " constructor MDC key " + mdcKey + " not  found");
            }
        }
    }

    public AsyncCallback beforeProcess(Processor processor, Exchange exchange, AsyncCallback callback) {
        return new CustomCallback(callback);
    }

    class CustomCallback implements AsyncCallback {

        private final AsyncCallback asyncCallback;
        private final Map<String, String> mdcMappingCallback = new HashMap<String, String>();

        private CustomCallback(AsyncCallback asyncCallback) {
            this.asyncCallback = asyncCallback;
            for(String mdcKey: mdcKeys){
                String mdcValue = MDC.get(mdcKey);
                if(mdcValue != null){
                    LOGGER.info("unitOfWork:" + index + " beforeProcess found MDC key " + mdcKey + ":" + mdcValue);
                    mdcMappingCallback.put(mdcKey, mdcValue);
                }
                else{
                    LOGGER.info("unitOfWork:" + index + " beforeProcess MDC key " + mdcKey + " not  found");
                }
            }
        }

        public void done(boolean doneSync) {
            try {
                if (!doneSync) {
                    LOGGER.info("unitOfWork:" + index + " done async");
                    for(String mdcKey: mdcKeys) {
                        String mappingValue = mdcMappingCallback.get(mdcKey);
                        if (mappingValue != null) {
                            MDC.put(mdcKey, mappingValue);
                        }
                    }
                }
                else{
                    LOGGER.info("unitOfWork:" + index + " done sync");
                }
            } finally {
                // muse ensure delegate is invoked
                asyncCallback.done(doneSync);
            }
        }
    }

    public void afterProcess(Processor processor, Exchange exchange, AsyncCallback callback, boolean doneSync) {
        if (!doneSync) {
            LOGGER.info("unitOfWork:" + index + " afterProcess async");
            for(String mdcKey: mdcKeys) {
                MDC.remove(mdcKey);
            }
        }
        else{
            LOGGER.info("unitOfWork:" + index + " afterProcess sync");
        }
        super.afterProcess(processor, exchange, callback, doneSync);
    }

}
