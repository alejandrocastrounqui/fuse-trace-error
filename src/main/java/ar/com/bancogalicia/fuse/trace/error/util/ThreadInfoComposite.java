package ar.com.bancogalicia.fuse.trace.error.util;

import java.util.List;

public class ThreadInfoComposite implements ThreadInfo {

    List<ThreadInfo> tracingThreadInfoList;

    public ThreadInfoComposite(List<ThreadInfo> tracingThreadInfoList){
        this.tracingThreadInfoList = tracingThreadInfoList;
    }

    @Override
    public void beforeProcess() {
        for (ThreadInfo tracingThreadInfo: tracingThreadInfoList) {
            tracingThreadInfo.beforeProcess();
        }
    }

    @Override
    public void spread() {
        for (ThreadInfo tracingThreadInfo: tracingThreadInfoList) {
            tracingThreadInfo.spread();
        }
    }

    @Override
    public void afterProcess() {
        for (ThreadInfo tracingThreadInfo: tracingThreadInfoList) {
            tracingThreadInfo.afterProcess();
        }
    }

}
