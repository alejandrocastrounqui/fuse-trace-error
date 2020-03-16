package ar.com.bancogalicia.fuse.trace.error.service.thread.impl;

import ar.com.bancogalicia.fuse.trace.error.configuration.ThreadManagementProperties;
import ar.com.bancogalicia.fuse.trace.error.util.OpenTracingAndMDCThreadInfoImpl;
import ar.com.bancogalicia.fuse.trace.error.util.ThreadInfo;
import ar.com.bancogalicia.fuse.trace.error.util.ThreadInfoComposite;
import ar.com.bancogalicia.fuse.trace.error.util.ThreadInfoProvider;
import ar.com.bancogalicia.fuse.trace.error.service.thread.ThreadInfoManager;
import ar.com.bancogalicia.fuse.trace.error.util.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@Service
public class ThreadInfoManagerImpl implements ThreadInfoManager {

    @Autowired(required = false)
    List<? extends ThreadInfoProvider> tracingThreadProviders;

    @Autowired
    ThreadManagementProperties threadManagementProperties;

    ImmutableList<String> mdcKeys;

    @PostConstruct
    public void initialize(){
        String[] mdcKeys = threadManagementProperties.getMdcKeys();
        this.mdcKeys = new ImmutableList<String>(mdcKeys);
        if(tracingThreadProviders == null){
            tracingThreadProviders = new LinkedList<>();
        }
    }

    public ThreadInfo collect(String descriptor){
        List<ThreadInfo> tracingThreadInfoList = new LinkedList<ThreadInfo>();
        for (ThreadInfoProvider tracingThreadInfoProvider : tracingThreadProviders) {
            ThreadInfo tracingThreadInfo = tracingThreadInfoProvider.collect(descriptor);
            tracingThreadInfoList.add(tracingThreadInfo);
        }
        OpenTracingAndMDCThreadInfoImpl openTracingAndMDCThreadInfo = new OpenTracingAndMDCThreadInfoImpl(this.mdcKeys, descriptor);
        tracingThreadInfoList.add(openTracingAndMDCThreadInfo);
        ThreadInfoComposite tracingThreadInfoComposite = new ThreadInfoComposite(tracingThreadInfoList);
        return tracingThreadInfoComposite;
    }

}
