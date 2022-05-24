package com.vmware.l10n.record.controller;

import java.io.File;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.l10n.L10nI18nAPI;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;

import io.swagger.annotations.ApiOperation;
@RestController
public class TestController {
    private Logger logger = LoggerFactory.getLogger(RecordController.class);
	@Autowired
	private SourceDao scd;
	AntPathMatcher path = new AntPathMatcher();
	@ApiOperation(value = "test static file", notes = "test the get static file")
	@GetMapping(L10nI18nAPI.BASE_COLLECT_SOURCE_PATH+"/api/v2/test")
	public void getSourceComponentModel(HttpServletRequest request, HttpServletResponse resp) throws Exception{
		
     try {
        
    	 SingleComponentDTO cmd = new SingleComponentDTO();
 		cmd.setVersion("1.0.0");
 		cmd.setProductName("test");
 		cmd.setComponent("test");
 		cmd.setLocale("latest");
 		System.out.print(request.getContentType());
 		String result = scd.getFromBundle(cmd);
 		ObjectMapper mapper = new ObjectMapper();
 		SingleComponentDTO scD = mapper.readValue(result, SingleComponentDTO.class);
 		Map<String, Object> map = ((Map<String, Object>)scD.getMessages());
 		
 		if(map == null) {
 			System.out.println("this is null");
 		}
 		
 		String val = (String) map.get("testsvg3");
 		
 
 		byte[] bytes = val.getBytes("UTF-8");
     
        
        
        
        resp.setHeader("Content-type","image/svg+xml");
       
 		resp.getOutputStream().write(bytes);
 		resp.getOutputStream().flush();
     }catch(Exception e) {
    	 logger.error(e.getMessage(), e);
     }
		
		
	}
	
	     @ApiOperation(value = "test upload static file", notes = "test the get static file")
	    @PostMapping(value = "/uploadFile")
	    public  String uploadFileHandler( @RequestParam("file")  MultipartFile file) {
	        if (!file.isEmpty()) {
	            try {
	                // 文件存放服务端的位置
	                String rootPath = "d:/tmp";
	                File dir = new File(rootPath + File.separator + "tmpFiles");
	                if (!dir.exists())
	                    dir.mkdirs();
	                // 写文件到服务器
	                File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
	                file.transferTo(serverFile);
	                return "You successfully uploaded file=" +  file.getOriginalFilename();
	            } catch (Exception e) {
	                return "You failed to upload " +  file.getOriginalFilename() + " => " + e.getMessage();
	            }
	        } else {
	            return "You failed to upload " +  file.getOriginalFilename() + " because the file was empty.";
	        }
	    }

	
	


}
