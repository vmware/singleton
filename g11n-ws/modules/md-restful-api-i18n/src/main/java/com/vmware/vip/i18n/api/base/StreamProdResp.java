package com.vmware.vip.i18n.api.base;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

public class StreamProdResp {
	public StreamProdResp(String productName, String version, 
			List<String> locales, List<String> components, String pseudo, boolean machineTranslation) {
		this.productName = productName;
		this.version = version;
		if(dataOrigin != null) {
		 this.pseudo = pseudo;
		}
		this.machineTranslation = machineTranslation;
		this.locales = locales;
		this.components = components;
	}


	private String productName;
    private String version;
    private String dataOrigin = "";
    private String pseudo = "false";
    private boolean machineTranslation = false;
    private List<String> locales;
    private List<String> components;
    
    
    public ByteBuffer getStartBytes(boolean partSucc) {
    	try {
			return ByteBuffer.wrap(getParamStr(partSucc).getBytes("UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    public ByteBuffer getEndBytes() {
			return ByteBuffer.wrap(endStr);
    }
    
    private String getParamStr(boolean partSu) {
    	StringBuilder paramBuilder = new StringBuilder();
    	if(partSu) {
    		paramBuilder.append(partSuccStarStr);
    	}else {
    		paramBuilder.append(succStarStr);

    	}
    
    	paramBuilder.append("    \"productName\": \""+this.productName+"\",\r\n" );
    	paramBuilder.append("    \"version\": \""+this.version+"\",\r\n" );
    	paramBuilder.append("    \"dataOrigin\": \""+this.dataOrigin+"\",\r\n" );
    	paramBuilder.append("    \"pseudo\": "+this.pseudo+",\r\n" );
    	paramBuilder.append("    \"machineTranslation\": "+String.valueOf(this.machineTranslation)+",\r\n" );
    	getLocalesStr(paramBuilder);
    	getComponentsStr(paramBuilder);
    	paramBuilder.append("    \"bundles\": [\r\n");
        return paramBuilder.toString();
    }
    
    private void getLocalesStr(StringBuilder paramBuilder) {
    	paramBuilder.append(	"    \"locales\": [ " );
    	int index=0;
    	
    	for(String localStr : this.locales) {
          if(index != 0) {
	         paramBuilder.append(",");

    		}
	        index++;
    		paramBuilder.append("\"");
    		paramBuilder.append(localStr);
    		paramBuilder.append("\"");
    		
    		
    	}
    	paramBuilder.append(" ],\r\n");
    	
    }
    
    private void getComponentsStr(StringBuilder paramBuilder) {
    	paramBuilder.append("    \"components\": [ ");
    	int index=0;
    	for(String compStr: this.components) {
    		if(index != 0) {
   	         paramBuilder.append(",");

       		}
   	        index++;
    		paramBuilder.append("\"");
    		paramBuilder.append(compStr);
    		paramBuilder.append("\"");
    	}
    	paramBuilder.append(" ],\r\n");
    }
	
	private static String partSuccStarStr = "{\r\n  \"response\": {\r\n    \"code\": 207,\r\n    \"message\": \"OK\",\r\n    \"serverTime\": \"\"\r\n  },\r\n  \"signature\": \"\",\r\n  \"data\": {\r\n";
	
	private static String succStarStr = "{\r\n  \"response\": {\r\n    \"code\": 200,\r\n    \"message\": \"OK\",\r\n    \"serverTime\": \"\"\r\n  },\r\n  \"signature\": \"\",\r\n  \"data\": {\r\n";
	
	private static byte[] endStr =" ],\r\n    \"url\": \"\",\r\n    \"id\": 0\r\n  }\r\n}".getBytes();
	

}
