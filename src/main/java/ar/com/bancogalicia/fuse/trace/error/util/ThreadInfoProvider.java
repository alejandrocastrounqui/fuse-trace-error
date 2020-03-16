package ar.com.bancogalicia.fuse.trace.error.util;

public interface ThreadInfoProvider {

    ThreadInfo collect(String descriptor);

}
