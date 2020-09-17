/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.ehcache.Cache;

//import net.sf.ehcache.constructs.web.ShutdownListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.EhCache3Manager;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

/**
 * This is a listener class for spring application.Also it's a child class of
 * ehcache listener class which shut down ehcache when server is down.
 */
@WebListener
public class ServerShutdownListener implements ServletContextListener    {
	@Value("${source.bundle.file.basepath}")
	private String basePath;
	private static Logger LOGGER = LoggerFactory.getLogger(ServerShutdownListener.class);

	/**
	 * Behaviors after servlet context is initialized.
	 *
	 * @param servletContextEvent
	 */
	/*
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOGGER.info("ServletContex initialized!");
		LOGGER.info("Server info : {}", sce.getServletContext().getServerInfo());
		try {
			long diskStoreSize = TranslationCache3
					.getDiskStoreSize(CacheName.SOURCE);
			if (diskStoreSize == 0) {
				LOGGER.info("No data stored on Disk!");
			} else {
				LOGGER.info("Disk cache size: {}", diskStoreSize);
				List<String> ehcachekeyList = TranslationCacheDES
						.getKeys(CacheName.SOURCE);
				LOGGER.info("restore cache key list: {}", ehcachekeyList);
				LOGGER.info("restore cache content: ");
				for (String ehcachekey : ehcachekeyList) {
					ComponentSourceDTO cachedComponentSourceDTO = (ComponentSourceDTO) TranslationCacheDES
							.getCachedObject(CacheName.SOURCE, ehcachekey);
					if (!StringUtils.isEmpty(cachedComponentSourceDTO)) {
						LOGGER.info(cachedComponentSourceDTO.toJSONString());
					}else {
						LOGGER.info("ehcachkey :{} no content", ehcachekey);
					}
				}
			}
		} catch (VIPCacheException e) {
			LOGGER.error("Error occurs when perform method contextInitialized.", e);
		}
	}
  */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		LOGGER.info("ServletContex initialized!");
		LOGGER.info("Server info : {}", sce.getServletContext().getServerInfo());
	}

	/**
	 * Behaviors after servlet context is destoryed.Here is shut down the
	 * ehcache.
	 *
	 * @param servletContextEvent
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
		try {
			Cache<String, ComponentSourceDTO> cache= TranslationCache3.getCache(CacheName.SOURCEBACKUP, ComponentSourceDTO.class);
			Map<String, ComponentSourceDTO> map = new HashMap<String,ComponentSourceDTO>();
			cache.forEach((action)->{
				map.put(action.getKey(), action.getValue());
			});
			
			if(map.size()>0) {
				try {
					DiskQueueUtils.createQueueFile(map, basePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOGGER.error(e.getMessage(), e);
				}
				
			}
			
		} catch (VIPCacheException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occurs when perform method context destroyed.", e);
		}
		try {
			EhCache3Manager.getInstance().closeCacheManager();
		} catch (VIPCacheException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage(), e);
		}
		LOGGER.info("ServletContex destroyed!");
		
	}
}
