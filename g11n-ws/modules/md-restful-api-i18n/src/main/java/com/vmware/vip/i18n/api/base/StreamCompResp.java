package com.vmware.vip.i18n.api.base;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class StreamCompResp {
	private static String succStarStr = "{\r\n  \"response\": {\r\n    \"code\": 200,\r\n    \"message\": \"OK\",\r\n    \"serverTime\": \"\"\r\n  },\r\n  \"signature\": \"\",\r\n  \"data\": {\r\n";
	
	private static byte[] endStr = ",\r\n    \"status\": \"\",\r\n    \"id\": 0\r\n  }\r\n}".getBytes();
	
	
	public StreamCompResp(String productName, String version, String dataOrigin, String pseudo,
			boolean machineTranslation) {
		this.productName = productName;
		this.version = version;
		this.dataOrigin = dataOrigin;
		this.pseudo = pseudo;
		this.machineTranslation = machineTranslation;
	}

	private String productName;
    private String version;
    private String dataOrigin = "";
    private String pseudo = "false";
    private boolean machineTranslation = false;
	
    /**
     * 
     "productName": "VMCUI",
    "version": "1.0.0",
    "dataOrigin": "cache",
    "pseudo": false,
    "machineTranslation": false,
    "component": "test",
    "messages": {
      "workflow-status.deployment-id": "Deployment ID",
      "workflow-status.deployment-type": "Deployment Type",
      "workflow-status.phase-start-times": "Phase Start Times",
      "workflow-status.sddc-id": "SDDC ID"
    },
    "locale": "en",
    "status": "",
    "id": 0
     */
	 private String getParamStr() {
	    	StringBuilder paramBuilder = new StringBuilder(succStarStr);
	    	    
	    	paramBuilder.append("    \"productName\": \""+this.productName+"\",\r\n" );
	    	paramBuilder.append("    \"version\": \""+this.version+"\",\r\n" );
	    	paramBuilder.append("    \"dataOrigin\": \""+this.dataOrigin+"\",\r\n" );
	    	paramBuilder.append("    \"pseudo\": "+this.pseudo+",\r\n" );
	    	paramBuilder.append("    \"machineTranslation\": "+String.valueOf(this.machineTranslation)+",\r\n" );
	 
	        return paramBuilder.toString();
	    }
	 
	 public ByteBuffer getEndBytes() {
			return ByteBuffer.wrap(endStr);
  }

	public ByteBuffer getStartBytes() {
		// TODO Auto-generated method stub
		try {
			return ByteBuffer.wrap(getParamStr().getBytes("UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
