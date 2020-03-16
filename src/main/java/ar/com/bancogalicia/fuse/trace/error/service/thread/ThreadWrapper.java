package ar.com.bancogalicia.fuse.trace.error.service.thread;

import java.util.concurrent.Callable;

public interface ThreadWrapper {

    <T> Callable<T> wrap(Callable<T> target, String descriptor);

    <T> Callable<T> wrap(Callable<T> target);

    Runnable wrap(Runnable target, String descriptor);

    Runnable wrap(Runnable target);

}
