package ar.com.bancogalicia.fuse.trace.error.controller;

import ar.com.bancogalicia.fuse.trace.error.service.FuseTraceErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("spring-web")
public class FuseTraceErrorController {

    @Autowired
    FuseTraceErrorService changeRateAdapterService;

    @ResponseBody
    @GetMapping("tracing")
    public ResponseEntity<Object> tracing() {
        List<String> result = changeRateAdapterService.tracing();
        return new ResponseEntity(result, HttpStatus.OK);
    }

}
