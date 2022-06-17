package com.vmware.vip.i18n.api.base;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.multcomponent.ITestStreamService;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.utils.LocaleUtility;
import com.vmware.vip.i18n.api.base.utils.VersionMatcher;

public class TestStreamAction {
	@Autowired
	IProductService productService;
	@Autowired
	ITestStreamService testStreamService;

	public void getMultTranslationDTO(String productName, String version, String components, String locales,
			String pseudo, HttpServletResponse resp) throws L3APIException {
		version = VersionMatcher.getMatchedVersion(version, productService.getSupportVersionList(productName));

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
				localeList.add(getMappingLocale(productName, version, locale.trim()));
			}
		} else {
			localeList = productService.getSupportedLocaleList(productName, version);
		}

		List<File> files = testStreamService.get2JsonFiles(productName, version, componentList, localeList);
		int expSize = componentList.size() * localeList.size();
		StreamProdResp sr = new StreamProdResp(productName, version, localeList, componentList, pseudo, false);
		resp.setContentType("application/json;charset=UTF-8"); 
		byte[] byteComm = (ConstantsChar.COMMA+"\r\n").getBytes();
		if (files.size() == expSize) {
			try {
				WritableByteChannel wbc = Channels.newChannel(resp.getOutputStream());
				wbc.write(sr.getStartBytes(false));
				/**
				ByteBuffer buf = ByteBuffer.allocateDirect(4096);
				for (File outFile : files) {
					try (FileChannel fc = FileChannel.open(outFile.toPath(), StandardOpenOption.READ)) {
						while ((fc.read(buf)) != -1) {
							buf.flip();
							while (buf.hasRemaining()) {
								wbc.write(buf);
							}
							buf.clear(); // 清理Buffer，为下一次写入做准备
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				**/
				try (FileChannel fc = FileChannel.open(files.get(0).toPath(), StandardOpenOption.READ)) {

					fc.transferTo(0, fc.size(), wbc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				for (int indx =1;  indx <files.size(); indx++ ) {
					File outFile = files.get(indx);
					try (FileChannel fc = FileChannel.open(outFile.toPath(), StandardOpenOption.READ)) {
						wbc.write( ByteBuffer.wrap(byteComm));
						fc.transferTo(0, fc.size(), wbc);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				wbc.write(sr.getEndBytes());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (files.size() > 0) {
			try {
				WritableByteChannel wbc = Channels.newChannel(resp.getOutputStream());
				wbc.write(sr.getStartBytes(true));
			
			try (FileChannel fc = FileChannel.open(files.get(0).toPath(), StandardOpenOption.READ)) {

				fc.transferTo(0, fc.size(), wbc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			for (int indx =1;  indx <files.size(); indx++ ) {
				File outFile = files.get(indx);
				try (FileChannel fc = FileChannel.open(outFile.toPath(), StandardOpenOption.READ)) {
					wbc.write( ByteBuffer.wrap(byteComm));
					fc.transferTo(0, fc.size(), wbc);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			wbc.write(sr.getEndBytes());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			throw new L3APIException("not found trans");
		}

	}

	private String getMappingLocale(String productName, String version, String inputLocale) throws L3APIException {
		List<String> supportedLocaleList = productService.getSupportedLocaleList(productName, version);
		List<Locale> supportedLocales = new ArrayList<Locale>();
		for (String supportedLocale : supportedLocaleList) {
			supportedLocale = supportedLocale.replace("_", "-");
			supportedLocales.add(Locale.forLanguageTag(supportedLocale));
		}
		String requestLocale = inputLocale.replace("_", "-");
		Locale fallbackLocale = LocaleUtility.pickupLocaleFromListNoDefault(supportedLocales,
				Locale.forLanguageTag(requestLocale));
		return fallbackLocale.toLanguageTag();
	}

	public void getCompTranslationDTO(String productName, String version, String component, String locale,
			String pseudo, HttpServletResponse response)  throws L3APIException{
	   
		File file = testStreamService.get2JsonFile(productName, version, component, locale);
		response.setContentType("application/json;charset=UTF-8"); 
		StreamCompResp scr = new StreamCompResp( productName,  version,  "", pseudo, false);
		
		try {
			WritableByteChannel wbc = Channels.newChannel(response.getOutputStream());
			wbc.write(scr.getStartBytes());
			try (FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
				fc.transferTo(1, (fc.size()-2), wbc);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			wbc.write(scr.getEndBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
	}

}
