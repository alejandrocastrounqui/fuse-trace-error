package ar.com.bancogalicia.fuse.trace.error.util;

import ar.com.bancogalicia.fuse.trace.error.exceptions.CannotCloseTraceScopeException;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenTracingAndMDCThreadInfoImpl implements ThreadInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTracingAndMDCThreadInfoImpl.class);

    private Span spanParent;
    private Scope running;
    private Map<String, String> mdcBefore;
    private Map<String, String> mdcParent;
    private List<String> mdcKeys;
    private String descriptor;

    public OpenTracingAndMDCThreadInfoImpl(List<String> mdcKeys, String descriptor){
        this.descriptor = descriptor;
        this.mdcKeys = mdcKeys;
        this.mdcParent = getMdcValues();
        this.spanParent = getActiveSpan();
    }

    @Override
    public void beforeProcess() {
        this.mdcBefore = getMdcValues();
    }

    @Override
    public void spread() {
        Tracer tracer = GlobalTracer.get();
        String identifier = "async-processing";
        if(descriptor != null){
            identifier += ":" + descriptor;
        }
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(identifier);
        if(spanParent != null){
            spanBuilder = spanBuilder.asChildOf(spanParent);
        }
        running = spanBuilder.startActive(true);
        Span span = running.span();
        spreadMDCValues(this.mdcParent);
    }

    @Override
    public void afterProcess() {
        spreadMDCValues(this.mdcBefore);
        closeScope();
    }

    private void activeSpan(Span span) {
        if(span == null){
            return;
        }
        Tracer tracer = GlobalTracer.get();
        ScopeManager scopeManager = tracer.scopeManager();
        scopeManager.activate(span, true);
    }

    private HashMap<String, String> getMdcValues() {
        HashMap<String, String> mdcValues = new HashMap<>();
        for(String mdcKey: this.mdcKeys) {
            String current = MDC.get(mdcKey);
            if(current != null){
                mdcValues.put(mdcKey, current);
            }
        }
        return mdcValues;
    }

    private void spreadMDCValues(Map<String, String> mdcvalues) {
        for(String mdcKey: mdcKeys) {
            String current = mdcvalues.get(mdcKey);
            if(current != null){
                MDC.put(mdcKey, current);
            }
            else{
                MDC.remove(mdcKey);
            }
        }
    }

    private Span getActiveSpan() {
        Tracer tracer = GlobalTracer.get();
        ScopeManager scopeManager = tracer.scopeManager();
        Scope scope = scopeManager.active();
        if(scope == null){
            return null;
        }
        Span span = scope.span();
        return span;
    }

    private void closeScope() {
        Tracer tracer = GlobalTracer.get();
        ScopeManager scopeManager = tracer.scopeManager();
        Scope scope = scopeManager.active();
        // Happy Path: all nested scope was closed
        if(running.equals(scope)){
            scope.close();
        }
        else{
            // unexpected behaviour, there is open nested scopes
            LOGGER.error("Unexpected open-tracing behaviour, weird scope found");
            closeScopeRecursively(running);
        }
    }

    private void closeScopeRecursively(Scope wanted) {
        Tracer tracer = GlobalTracer.get();
        ScopeManager scopeManager = tracer.scopeManager();
        Scope scope = scopeManager.active();
        // it should not be able to nest 10000 contexts
        Integer MAX_ITERATION = 10000;
        Integer iterationIndex = 0;
        while(scope != null && iterationIndex < MAX_ITERATION){
            iterationIndex++;
            scope.close();
            if(wanted.equals(scope)){
                // wanted scope was closed, captain flies away
                return;
            }
            scope = scopeManager.active();
        }
        if(scope != null){
            throw new CannotCloseTraceScopeException();
        }
    }

}
