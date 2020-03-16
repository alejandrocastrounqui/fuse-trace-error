package ar.com.bancogalicia.fuse.trace.error.service.impl;

import ar.com.bancogalicia.fuse.trace.error.service.FuseTraceErrorService;
import ar.com.bancogalicia.fuse.trace.error.service.thread.ThreadWrapper;
import ar.com.bancogalicia.fuse.trace.error.util.MDCUtil;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class FuseTraceErrorServiceImpl implements FuseTraceErrorService {

    @Autowired
    private ThreadWrapper threadWrapper;

    public List<String> tracing(){
        MDC.put(MDCUtil.MDC_TEST_KEY, MDCUtil.MDC_TEST_VALUE);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CompletionService<String> completionService = new ExecutorCompletionService<String>(executor);
        Callable<String> trace1 = () -> {
            // evaluates asynchronously
            return MDC.get(MDCUtil.MDC_TEST_KEY);
        };
        completionService.submit(threadWrapper.wrap(trace1, "tracing-1"));
        completionService.submit(threadWrapper.wrap(trace1, "tracing-2"));
        Future<String> future;
        String item;
        List<String> result = new LinkedList<String>();
        try {
            for (int i = 0; i < 2; i++) {
                future = completionService.take();
                item = future.get();
                result.add(item);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

}
