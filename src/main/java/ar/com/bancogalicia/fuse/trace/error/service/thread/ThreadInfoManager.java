package ar.com.bancogalicia.fuse.trace.error.service.thread;

import ar.com.bancogalicia.fuse.trace.error.util.ThreadInfo;

public interface ThreadInfoManager {

    ThreadInfo collect(String descriptor);
}
