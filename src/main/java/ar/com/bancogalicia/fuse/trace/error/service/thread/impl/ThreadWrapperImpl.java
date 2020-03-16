package ar.com.bancogalicia.fuse.trace.error.service.thread.impl;

import ar.com.bancogalicia.fuse.trace.error.util.CallableWrapper;
import ar.com.bancogalicia.fuse.trace.error.util.RunnableWrapper;
import ar.com.bancogalicia.fuse.trace.error.util.ThreadInfo;
import ar.com.bancogalicia.fuse.trace.error.service.thread.ThreadInfoManager;
import ar.com.bancogalicia.fuse.trace.error.service.thread.ThreadWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Service
public class ThreadWrapperImpl implements ThreadWrapper {

    @Autowired
    ThreadInfoManager threadInfoManager;

    @Override
    public <T> Callable<T> wrap(Callable<T> target, String descriptor) {
        ThreadInfo tracingThreadInfo = threadInfoManager.collect(descriptor);
        return new CallableWrapper<T>(target, tracingThreadInfo);
    }

    @Override
    public <T> Callable<T> wrap(Callable<T> target) {
        return wrap(target, null);
    }

    @Override
    public Runnable wrap(Runnable target, String descriptor) {
        ThreadInfo tracingThreadInfo = threadInfoManager.collect(descriptor);
        return new RunnableWrapper(target, tracingThreadInfo);
    }

    @Override
    public Runnable wrap(Runnable target) {
        return wrap(target, null);
    }

}
