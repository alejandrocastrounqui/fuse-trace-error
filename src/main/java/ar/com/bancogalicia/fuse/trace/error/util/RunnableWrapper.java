package ar.com.bancogalicia.fuse.trace.error.util;

public class RunnableWrapper implements Runnable {

    private Runnable target;
    private ThreadInfo tracingThreadInfo;

    public RunnableWrapper(Runnable target, ThreadInfo tracingThreadInfo) {
        this.target = target;
        this.tracingThreadInfo = tracingThreadInfo;
    }

    @Override
    public void run() {
        tracingThreadInfo.beforeProcess();
        tracingThreadInfo.spread();
        target.run();
        tracingThreadInfo.afterProcess();
    }
}
