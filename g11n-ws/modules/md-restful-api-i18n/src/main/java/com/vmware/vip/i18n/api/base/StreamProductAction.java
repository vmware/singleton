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

public class StreamProductAction extends TranslationProductAction{
	@Autowired
	IProductService productService;
	
	@Autowired
	IMultComponentService multComponentService;
	
	
	public void writeMultTranslationResponse(String productName, String version, String components, String locales,
			String pseudo, HttpServletResponse resp) throws Exception {
		String oldVersion = version;
		version = super.getAvailableVersion(productName, oldVersion);

		List<String> componentList = null;
		if (StringUtils.isEmpty(components)) {
			componentList = productService.getComponentNameList(productName, version);
		} else {
			componentList = new ArrayList<String>();
			for (String component : components.split(",")) {
				componentList.add(component.trim());
			}
		}

		List<String> localeList = new ArrayList<String>();
		if (new Boolean(pseudo)) {
			localeList.add(ConstantsKeys.LATEST);
		} else if (!StringUtils.isEmpty(locales)) {
			for (String locale : locales.split(",")) {
				localeList.add(super.getMappingLocale(productName, version, locale.trim()));
			}
		} else {
			localeList = productService.getSupportedLocaleList(productName, version);
		}

		
	
		List<ReadableByteChannel>  readChannels = multComponentService.getTranslationChannels(productName, version, componentList, localeList);
		if(readChannels.isEmpty()) {
            throw new L3APIException(ConstantsMsg.TRANS_IS_NOT_FOUND);
        }
		
		int expSize = componentList.size() * localeList.size();
		StreamProductResp sr = new StreamProductResp(productName, version, localeList, componentList, pseudo, false);
		resp.setContentType("application/json;charset=UTF-8"); 
		WritableByteChannel wbc = Channels.newChannel(resp.getOutputStream());
		
		 if(oldVersion.equals(version)) {
			 if(readChannels.size() == expSize) {
				 wbc.write(sr.getRespStartBytes(APIResponseStatus.OK.getCode()));
			 }else {
				 wbc.write(sr.getRespStartBytes(APIResponseStatus.MULTTRANSLATION_PART_CONTENT.getCode()));
			 }
		 }else {
			 wbc.write(sr.getRespStartBytes(APIResponseStatus.VERSION_FALLBACK_TRANSLATION.getCode()));
		 }
		 
		 transferTo(readChannels.get(0), wbc);
		 ByteBuffer byteComm = ByteBuffer.wrap((ConstantsChar.COMMA+"\r\n").getBytes());
		 for (int idx =1;  idx <readChannels.size(); idx++ ) {
			 byteComm.rewind();
			 wbc.write(byteComm);
			 transferTo(readChannels.get(idx), wbc);
		 }
		 
		 wbc.write(sr.getEndBytes());
	}
	

	



	private void transferTo(ReadableByteChannel readChannel, WritableByteChannel writeChannel) throws Exception {

		if (readChannel instanceof FileChannel) {
			try (FileChannel fileChannel = (FileChannel) readChannel) {
				fileChannel.transferTo(0, fileChannel.size(), writeChannel);
			}
		} else {
			ByteBuffer buf = ByteBuffer.allocateDirect(4096);
			try {
				while ((readChannel.read(buf)) != -1) {
					buf.flip();
					while (buf.hasRemaining()) {
						writeChannel.write(buf);
					}
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
