package com.vmware.vip.i18n.api.v2.translation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;

import io.swagger.annotations.ApiParam;
@RestController("v2-TestTreamAPI")
public class TestControl {
	
	@RequestMapping(value="/streamProdTrans/{productName}/{version}", method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public void getMultipleComponentsTranslation(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
	    	@ApiParam(name = APIParamName.COMPONENTS, required = false, value = APIParamValue.COMPONENTS) @RequestParam(value = APIParamName.COMPONENTS, required = false, defaultValue = "") String components,
			@ApiParam(name = APIParamName.LOCALES, required = false, value = APIParamValue.LOCALES) @RequestParam(value = APIParamName.LOCALES, required = false, defaultValue = "") String locales,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
            HttpServletRequest req, HttpServletResponse resp)  throws Exception {
        
    }
	
	
	
	
	
	
	


}
