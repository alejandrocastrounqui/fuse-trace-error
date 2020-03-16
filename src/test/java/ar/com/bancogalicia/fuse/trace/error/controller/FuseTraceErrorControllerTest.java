package ar.com.bancogalicia.fuse.trace.error.controller;

import ar.com.bancogalicia.fuse.trace.error.configuration.ThreadManagementProperties;
import ar.com.bancogalicia.fuse.trace.error.service.impl.FuseTraceErrorServiceImpl;
import ar.com.bancogalicia.fuse.trace.error.service.thread.impl.ThreadInfoManagerImpl;
import ar.com.bancogalicia.fuse.trace.error.service.thread.impl.ThreadWrapperImpl;
import ar.com.bancogalicia.fuse.trace.error.util.MDCUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableWebMvc
@WebAppConfiguration
@ContextConfiguration(
    loader = AnnotationConfigWebContextLoader.class,
    classes = {
        ThreadManagementProperties.class,
        FuseTraceErrorServiceImpl.class,
        ThreadInfoManagerImpl.class,
        ThreadWrapperImpl.class,
        FuseTraceErrorController.class
    }
)
public class FuseTraceErrorControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build();
    }

    @Test
    public void whenMDCIsPropagatedThenCallableCanAccessMDC() throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/spring-web/tracing");
        this.mockMvc.perform(request)
                .andExpect(content().json("[" + MDCUtil.MDC_TEST_VALUE + "," + MDCUtil.MDC_TEST_VALUE +"]"));
    }

    @Test
    public void whenEndpointIsPresentThenReturnOk() throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/spring-web/tracing");
        this.mockMvc.perform(request)
            .andExpect(status().isOk());
    }

    @Test
    public void whenThereWasNoControllerThenApiReturnNotFound() throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/spring-web/no-found-path");
        this.mockMvc.perform(request).andExpect(status().isNotFound());
    }

}
