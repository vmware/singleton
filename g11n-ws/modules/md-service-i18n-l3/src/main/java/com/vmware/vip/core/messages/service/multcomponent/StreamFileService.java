package com.vmware.vip.core.messages.service.multcomponent;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.messages.data.dao.api.IStreamFileDao;
import com.vmware.vip.messages.data.dao.exception.DataException;

@Service
public class StreamFileService implements IStreamFileService{
	
	@Autowired
	private IStreamFileDao streamDao;
	
	
	public List<File> getTranslationFiles(String productName, String version, List<String> components,
			List<String> locales) throws L3APIException{
	   try {
		return streamDao.getTranslationFiles(productName, version, components, locales);
	} catch (DataException e) {
		throw new L3APIException(e.getMessage(), e);
	}
	};
	
	public File getTranslationFile(String productName, String version, String component,
			String locale) throws L3APIException{
		try {
			return streamDao.getTranslationFile(productName, version, component, locale);
		} catch (DataException e) {
			throw new L3APIException(e.getMessage(), e);
		}
	}

}
