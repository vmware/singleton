/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.multcomponent.IMultComponentService;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.messages.data.dao.model.ResultMessageChannel;

public class StreamProductAction extends TranslationProductAction{
	 private static byte[] byteComm = (ConstantsChar.COMMA+"\r\n").getBytes();
	
	@Autowired
	IProductService productService;
	
	@Autowired
	IMultComponentService multComponentService;
	
	
	public void writeMultTranslationResponse(String productName, String version, String components, String locales,
			String pseudo, HttpServletResponse resp) throws Exception {
		
		String newVersion = super.getAvailableVersion(productName, version);
		List<String> componentList = getcomponentList(productName, newVersion, components);
		List<String> localeList = getlocaleList(productName, newVersion, locales, pseudo);
		
		List<ResultMessageChannel>  readChannels = multComponentService.getTranslationChannels(productName, newVersion, componentList, localeList);
		if(readChannels.isEmpty()) {
            throw new L3APIException(ConstantsMsg.TRANS_IS_NOT_FOUND);
        }
		
		int expSize = componentList.size() * localeList.size();
		StreamProductResp sr = new StreamProductResp(productName, newVersion, localeList, componentList, pseudo, false);
		resp.setContentType("application/json;charset=UTF-8"); 
		WritableByteChannel wbc = Channels.newChannel(resp.getOutputStream());
		boolean isPartContent = false;
		 if(version.equals(newVersion)) {
			 isPartContent = (readChannels.size() != expSize);
			 if(isPartContent) {
				 wbc.write(sr.getRespStartBytes(APIResponseStatus.MULTTRANSLATION_PART_CONTENT.getCode()));
			 }else {
				 wbc.write(sr.getRespStartBytes(APIResponseStatus.OK.getCode())); 
			 }
		 }else {
			 wbc.write(sr.getRespStartBytes(APIResponseStatus.VERSION_FALLBACK_TRANSLATION.getCode()));
		 }
		 ByteBuffer buf = null;
		 ReadableByteChannel firstReadChannel =  readChannels.get(0).getReadableByteChannel();
		 boolean flag = (firstReadChannel instanceof FileChannel);
		 if(flag) {
			 transferTo(readChannels.get(0).getReadableByteChannel(), wbc, null);
			 buf = ByteBuffer.wrap(byteComm);
		 }else {
			 buf = ByteBuffer.allocateDirect(16384);
			 transferTo(readChannels.get(0).getReadableByteChannel(), wbc, buf);
		 }
		 
		 
		 for (int idx =1;  idx <readChannels.size(); idx++ ) {
			 if(flag) {
				 buf.rewind();
				 wbc.write(buf);
			 }else {
				 buf.put(byteComm);
			 }
			 transferTo(readChannels.get(idx).getReadableByteChannel(), wbc, buf);
		 }
		 if(isPartContent){
			 wbc.write(ByteBuffer.wrap(addNullMessage(readChannels, componentList, localeList)));
		 }
		 wbc.write(sr.getEndBytes());
	}
	
	
	private List<String> getcomponentList(String productName, String versionStr, String components) throws L3APIException{
		List<String> componentList = null;
		if (StringUtils.isEmpty(components)) {
			componentList = productService.getComponentNameList(productName, versionStr);
		} else {
			componentList = new ArrayList<String>();
			for (String component : components.split(",")) {
				componentList.add(component.trim());
			}
		}
		
		return componentList;
	}
	
	private List<String> getlocaleList(String productName, String versionStr, String locales, String pseudo) throws L3APIException{
		List<String> localeList = new ArrayList<String>();
		if (Boolean.valueOf(pseudo)) {
			localeList.add(ConstantsKeys.LATEST);
		} else if (!StringUtils.isEmpty(locales)) {
			for (String locale : locales.split(",")) {
				localeList.add(super.getMappingLocale(productName, versionStr, locale.trim()));
			}
		} else {
			localeList = productService.getSupportedLocaleList(productName, versionStr);
		}
		
		return localeList;
	}
	
	private byte[] addNullMessage(List<ResultMessageChannel>  readChannels, List<String> components, List<String> locales) {
		StringBuilder sb = new StringBuilder();
		for (String component : components) {
			for (String locale : locales) {
				ResultMessageChannel rmc = new ResultMessageChannel(component, locale, null);
				if(!readChannels.contains(rmc)) {
					sb.append(",");
					sb.append(rmc.generateNullMessage());
				}
				
			}
		}
		return sb.toString().getBytes();
	}
  


	private void transferTo(ReadableByteChannel readChannel, WritableByteChannel writeChannel, ByteBuffer buf) throws Exception {

		if (readChannel instanceof FileChannel) {
			try (FileChannel fileChannel = (FileChannel) readChannel) {
				fileChannel.transferTo(0, fileChannel.size(), writeChannel);
			}
		} else {
			
			try {
				while ((readChannel.read(buf)) != -1) {
					buf.flip();
					do {
						writeChannel.write(buf);
					}while (buf.hasRemaining());
					
					buf.clear();
				}
			} catch (Exception e) {
				throw e;
			} finally {
				readChannel.close();
			}
		}
	}


}
