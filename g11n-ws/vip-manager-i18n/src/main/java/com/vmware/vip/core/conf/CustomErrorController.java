package com.vmware.vip.core.conf;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "{\n" +
                "    \"code\": 400,\n" +
                "    \"message\": \"\",\n" +
                "    \"serverTime\": \"\"\n" +
                "  }";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
