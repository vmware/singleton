package com.vmware.vip.core.messages.service.multcomponent;

import java.io.File;
import java.util.List;

import com.vmware.vip.core.messages.exception.L3APIException;

public interface IComponentChannelService {
	public void getTranslationFiles(String productName, String version, List<String> components,
			List<String> locales) throws L3APIException;
	
	public File getTranslationFile(String productName, String version, String component,
			String locale)  throws L3APIException;

}
