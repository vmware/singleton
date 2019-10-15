/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.Task;
import com.vmware.vipclient.i18n.base.VIPService;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.CacheMode;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.api.opt.local.LocalSourceOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.ProductService;

/**
 * a class uses to define the global environment setting for I18nFactory
 */
public class VIPCfg {

	Logger logger = LoggerFactory.getLogger(VIPCfg.class);

	// define global instance
	private static VIPCfg gcInstance;
	private VIPService vipService;
	private TranslationCacheManager translationCacheManager;

	// data origin
	private DataSourceEnum messageOrigin = DataSourceEnum.VIP;

	// cache mode
	private CacheMode cacheMode = CacheMode.MEMORY;

	private String cachePath;

	// define the global parameters
	private boolean pseudo;
	private boolean collectSource;
	private boolean cleanCache;
	private long cacheExpiredTime;
	private boolean machineTranslation;
	private boolean initializeCache;
	private int interalCleanCache;
	private String productName;
	private String version;
	private String vipServer;

	private ArrayList<Map<String, Object>> components;
	private String i18nScope = "numbers,dates,currencies,plurals,measurements";

	// define key for cache management
	public static final String CACHE_L3 = "CACHE_L3";
	public static final String CACHE_L2 = "CACHE_L2";

	private VIPCfg() {

	}

	/**
	 * create a default instance of VIPCfg
	 * 
	 * @return
	 */
	public static synchronized VIPCfg getInstance() {
		if (gcInstance == null) {
			gcInstance = new VIPCfg();
		}
		return gcInstance;
	}

	/**
	 * initialize the instance by a properties file
	 *
	 * @param cfg
	 * @throws IOException
	 */
	@SuppressWarnings("serial")
	public void initialize(String cfg) throws IOException {
		InputStream stream = ClassLoader.getSystemResourceAsStream(cfg);
		LinkedHashMap<String, Object> data = new Yaml().loadAs(stream, (new LinkedHashMap<String, Object>() {}).getClass());

		for (Entry<String, Object> entry : data.entrySet()) {
			try {
				this.getClass().getDeclaredField(entry.getKey()).set(this, entry.getValue());
			} catch (IllegalArgumentException e) {
				throw new VIPJavaClientException(
						String.format("Invalid value '%s' for setting '%s'!", entry.getValue(), entry.getKey()), e);
			} catch (NoSuchFieldException e) {
				throw new VIPJavaClientException("Invalid setting item: " + entry.getKey());
			} catch (SecurityException | IllegalAccessException e) {
				throw new VIPJavaClientException("Unknow errorr");
			}
		}

		LocalSourceOpt.loadResources(this.components);
	}

	/**
	 * initialize VIPService instances to provide HTTP requester 
	 */
	public void initializeVIPService() {
		this.vipService = VIPService.getVIPServiceInstance();
		try {
			this.vipService.initializeVIPService(this.productName, this.version,
					this.vipServer);
		}
		catch (MalformedURLException e) {
			logger.error("'vipServer' in configuration isn't a valid URL!");
		}
	}

	/**
	 * set cache from out-process
	 * 
	 * @param c
	 */
	public void setTranslationCache(Cache c) {
		this.translationCacheManager = TranslationCacheManager
				.createTranslationCacheManager();
		if (this.translationCacheManager != null) {
			this.translationCacheManager.registerCache(VIPCfg.CACHE_L3, c);
			logger.info("Translation Cache created.");
		}
		if (this.isInitializeCache()) {
			logger.info("Initializing Cache.");
			this.initializeMessageCache();
		}
		if (this.isCleanCache()) {
			logger.info("startTaskOfCacheClean.");
			Task.startTaskOfCacheClean(VIPCfg.getInstance(), interalCleanCache);
		}
		Cache createdCache = TranslationCacheManager
				.getCache(VIPCfg.CACHE_L3);
		if (createdCache != null && this.getCacheExpiredTime() > 0) {
			c.setExpiredTime(this.getCacheExpiredTime());
		}
	}

	/**
	 * create translation cache
	 * 
	 * @param cacheClass
	 * @return
	 */
	public synchronized Cache createTranslationCache(Class<?> cacheClass) {
		this.translationCacheManager = TranslationCacheManager
				.createTranslationCacheManager();
		if (this.translationCacheManager != null) { 
			if(TranslationCacheManager.getCache(VIPCfg.CACHE_L3) == null) {
				this.translationCacheManager.registerCache(VIPCfg.CACHE_L3,
						cacheClass);
				logger.info("Translation Cache created.");
				if (this.isInitializeCache()) {
					logger.info("InitializeCache.");
					this.initializeMessageCache();
				}
				if (this.isCleanCache()) {
					logger.info("startTaskOfCacheClean.");
					Task.startTaskOfCacheClean(VIPCfg.getInstance(), interalCleanCache);
				}
				Cache c = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
				if (c != null && this.getCacheExpiredTime() > 0) {
					c.setExpiredTime(this.getCacheExpiredTime());
				}
			}

			return TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
		}
		else {
			return null;
		}

	}

	/**
	 * create cache for formatting data
	 * 
	 * @param cacheClass
	 */
	public Cache createFormattingCache(Class<?> cacheClass) {
		this.translationCacheManager = TranslationCacheManager
				.createTranslationCacheManager();
		if (this.translationCacheManager != null) {
			this.translationCacheManager.registerCache(VIPCfg.CACHE_L2,
					cacheClass);
			logger.info("Formatting cache created.");
		}
		if (this.isCleanCache()) {
			logger.error("clean cache.");
			Task.startTaskOfCacheClean(VIPCfg.getInstance(), interalCleanCache);
		}
		return TranslationCacheManager.getCache(VIPCfg.CACHE_L2);
	}

	/**
	 * load all translation to cache by product
	 */
	public void initializeMessageCache() {
		MessagesDTO dto = new MessagesDTO();
		dto.setProductID(this.getProductName());
		dto.setVersion(this.getVersion());
		new ProductService(dto).getAllComponentTranslation();
		if (this.translationCacheManager != null) {
			logger.info("Translation data is loaded to cache, size is "
					+ this.translationCacheManager.size() + ".");
		}
	}

	public String getProductName() {
		return productName;
	}

	public String getVersion() {
		return version;
	}

	public String getVipServer() {
		return vipServer;
	}

	public boolean isPseudo() {
		return pseudo;
	}

	public void setPseudo(boolean pseudo) {
		this.pseudo = pseudo;
	}

	public boolean isCollectSource() {
		return collectSource;
	}

	public void setCollectSource(boolean collectSource) {
		this.collectSource = collectSource;
	}

	public boolean isCleanCache() {
		return cleanCache;
	}

	public void setCleanCache(boolean cleanCache) {
		this.cleanCache = cleanCache;
	}

	public VIPService getVipService() {
		return vipService;
	}

	public TranslationCacheManager getCacheManager() {
		return translationCacheManager;
	}

	public int getInteralCleanCache() {
		return interalCleanCache;
	}

	public void setInteralCleanCache(int interalCleanCache) {
		this.interalCleanCache = interalCleanCache;
	}

	public String getI18nScope() {
		return i18nScope;
	}

	public void setI18nScope(String i18nScope) {
		this.i18nScope = i18nScope;
	}

	public boolean isMachineTranslation() {
		return machineTranslation;
	}

	public void setMachineTranslation(boolean machineTranslation) {
		this.machineTranslation = machineTranslation;
	}

	public DataSourceEnum getMessageOrigin() {
		return messageOrigin;
	}

	public void setMessageOrigin(DataSourceEnum messageOrigin) {
		this.messageOrigin = messageOrigin;
	}

	public boolean isInitializeCache() {
		return initializeCache;
	}

	public void setInitializeCache(boolean initializeCache) {
		this.initializeCache = initializeCache;
	}

	public long getCacheExpiredTime() {
		return cacheExpiredTime;
	}

	public void setCacheExpiredTime(long cacheExpiredTime) {
		this.cacheExpiredTime = cacheExpiredTime;
	}

	public CacheMode getCacheMode() {
		return cacheMode;
	}

	public void setCacheMode(CacheMode cacheMode) {
		this.cacheMode = cacheMode;
	}

	public String getCachePath() {
		return cachePath;
	}

	public void setCachePath(String cachePath) {
		this.cachePath = cachePath;
	}
}
