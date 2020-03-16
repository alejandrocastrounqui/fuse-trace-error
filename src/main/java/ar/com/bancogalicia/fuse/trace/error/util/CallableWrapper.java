package ar.com.bancogalicia.fuse.trace.error.util;

import java.util.concurrent.Callable;

public class CallableWrapper<T> implements Callable<T> {

    Callable<T> target;
    ThreadInfo tracingThreadInfo;

    public CallableWrapper(Callable<T> target, ThreadInfo tracingThreadInfo) {
        this.target = target;
        this.tracingThreadInfo = tracingThreadInfo;
    }

    @Override
    public T call() throws Exception {
        tracingThreadInfo.beforeProcess();
        tracingThreadInfo.spread();
        T result = target.call();
        tracingThreadInfo.afterProcess();
        return result;
    }
}
