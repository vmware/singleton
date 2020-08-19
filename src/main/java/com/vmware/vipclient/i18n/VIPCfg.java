/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.Task;
import com.vmware.vipclient.i18n.base.VIPService;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.CacheMode;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.ProductService;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.LocaleUtility;

/**
 * a class uses to define the global environment setting for I18nFactory
 */
public class VIPCfg {

    Logger                             logger        = LoggerFactory.getLogger(VIPCfg.class);

    // define global instance
    private static VIPCfg              gcInstance;
    private static Map<String, VIPCfg> moduleCfgs    = new HashMap<String, VIPCfg>();
    private VIPService                 vipService;
    private TranslationCacheManager    translationCacheManager;

    // data origin
    @Deprecated
    private DataSourceEnum             messageOrigin = DataSourceEnum.VIP;
    private List<DataSourceEnum>	   msgOriginsQueue = new LinkedList<DataSourceEnum>();
    
    // cache mode
    private CacheMode                  cacheMode     = CacheMode.MEMORY;

    private String                     cachePath;

    // define the global parameters
    private boolean                    pseudo;

    private boolean                    collectSource;
    private boolean                    cleanCache;
    private long                       cacheExpiredTime;
    
    private boolean                    machineTranslation;
    private boolean                    initializeCache;
    private int                        interalCleanCache;
    private String                     productName;
    private String                     version;
    private String                     vipServer;
    private String                     i18nScope     = "numbers,dates,currencies,plurals,measurements";
    private String					   offlineResourcesBaseUrl;
    
    // define key for cache management
    public static final String         CACHE_L3      = "CACHE_L3";
    public static final String         CACHE_L2      = "CACHE_L2";

    public boolean isSubInstance() {
        return isSubInstance;
    }
    
    public void setSubInstance(boolean subInstance) {
        isSubInstance = subInstance;
    }

    private boolean isSubInstance = false;

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
     * create a default instance of VIPCfg
     *
     * @return
     */
    public static synchronized VIPCfg getSubInstance(String productName) {
        if (!VIPCfg.moduleCfgs.containsKey(productName)) {
            VIPCfg cfg = new VIPCfg();
            cfg.isSubInstance = true;
            VIPCfg.moduleCfgs.put(productName, cfg);
        }

        if (VIPCfg.moduleCfgs.containsKey(productName) && VIPCfg.moduleCfgs.get(productName) != null) {
            return VIPCfg.moduleCfgs.get(productName);
        } else {
            return gcInstance;
        }
    }

    /**
     * initialize the instance by parameter
     * 
     * @param vipServer
     * @param productName
     * @param version
     */
    public void initialize(String vipServer, String productName, String version) {
        this.productName = productName;
        this.version = version;
        this.vipServer = vipServer;
    }
    
    /**
     * Initialize VIPCfg instance using a configuration file
     * 
     * @param cfg The configuration file
     */
    public void initialize(String cfg) throws VIPClientInitException {
    	ResourceBundle prop = ResourceBundle.getBundle(cfg);
    	if (prop == null) {
    		throw new VIPClientInitException("Can't not initialize VIPCfg, resource bundle is null.");
    	}

        if (prop.containsKey("productName"))
            this.productName = prop.getString("productName");
        if (this.isSubInstance() && !VIPCfg.moduleCfgs.containsKey(this.productName)) {
            throw new VIPClientInitException(
                    "Can't not initialize sub VIPCfg instance, the product name is not defined in config file.");
        }
        if (prop.containsKey("version"))
            this.version = prop.getString("version");
        
        // Remote VIP resources takes priority over offline resources
        // so add DataSourceEnum.VIP first to msgOriginsQueue
        if (prop.containsKey("vipServer")) {
            this.vipServer = prop.getString("vipServer");
            this.addMsgOriginsQueue(DataSourceEnum.VIP);
        }
        if (prop.containsKey("offlineResourcesBaseUrl")) {
        	this.offlineResourcesBaseUrl = prop.getString("offlineResourcesBaseUrl");
        	this.addMsgOriginsQueue(DataSourceEnum.Bundle);
        }
        
        if (prop.containsKey("pseudo"))
            this.pseudo = Boolean.parseBoolean(prop.getString("pseudo"));
        if (prop.containsKey("collectSource"))
            this.collectSource = Boolean.parseBoolean(prop
                    .getString("collectSource"));
        if (prop.containsKey("initializeCache"))
            this.initializeCache = Boolean.parseBoolean(prop
                    .getString("initializeCache"));
        if (prop.containsKey("cleanCache"))
            this.cleanCache = Boolean.parseBoolean(prop
                    .getString("cleanCache"));
        if (prop.containsKey("machineTranslation"))
            this.machineTranslation = Boolean.parseBoolean(prop
                    .getString("machineTranslation"));
        if (prop.containsKey("i18nScope"))
            this.i18nScope = prop.getString("i18nScope");
        if (prop.containsKey("cacheExpiredTime"))
            this.cacheExpiredTime = Long.parseLong(prop
                    .getString("cacheExpiredTime"));
        if (prop.containsKey("defaultLocale")) {
        	LocaleUtility.setDefaultLocale(Locale.forLanguageTag(prop.getString("defaultLocale")));
        	LocaleUtility.setFallbackLocales(new LinkedList<Locale>(Arrays.asList(LocaleUtility.getDefaultLocale(), Locale.forLanguageTag(ConstantsKeys.SOURCE))));
        }
        if (prop.containsKey("sourceLocale"))
        	LocaleUtility.setSourceLocale(Locale.forLanguageTag(prop.getString("sourceLocale")));
	}	

    /**
     * initialize VIPService instances to provide HTTP requester
     */
    public void initializeVIPService() {
        this.vipService = VIPService.getVIPServiceInstance();
        try {
            this.vipService.initializeVIPService(this.productName, this.version,
                    this.vipServer);
        } catch (MalformedURLException e) {
            logger.error("'vipServer' " + this.vipServer + " in configuration isn't a valid URL!");
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
        if (createdCache != null && this.getCacheExpiredTime() != 0) {
            c.setExpiredTime(this.getCacheExpiredTime());
        }
    }

    /**
     * create translation cache
     * 
     * @param cacheClass
     * @return
     */
    public synchronized Cache createTranslationCache(Class cacheClass) {
        this.translationCacheManager = TranslationCacheManager
                .createTranslationCacheManager();
        if (this.translationCacheManager != null) {
            if (TranslationCacheManager.getCache(VIPCfg.CACHE_L3) == null) {
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
                if (c != null && this.getCacheExpiredTime() != 0) {
                    c.setExpiredTime(this.getCacheExpiredTime());
                }
            }

            return TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
        } else {
            return null;
        }

    }

    /**
     * create cache for formatting data
     * 
     * @param cacheClass
     */
    public Cache createFormattingCache(Class cacheClass) {
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

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVipServer() {
        return vipServer;
    }

    public void setVipServer(String vipServer) {
        this.vipServer = vipServer;
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
    
    @Deprecated
    public DataSourceEnum getMessageOrigin() {
        return messageOrigin;
    }

    @Deprecated
    public void setMessageOrigin(DataSourceEnum messageOrigin) {
        this.messageOrigin = messageOrigin;
    }

    public boolean isInitializeCache() {
        return initializeCache;
    }

    public void setInitializeCache(boolean initializeCache) {
        this.initializeCache = initializeCache;
    }
    
    @Deprecated
    public long getCacheExpiredTime() {
        return cacheExpiredTime;
    }
    
    @Deprecated
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

	public String getOfflineResourcesBaseUrl() {
		return offlineResourcesBaseUrl;
	}

	public void setOfflineResourcesBaseUrl(String offlineResourcesBaseUrl) {
		this.offlineResourcesBaseUrl = offlineResourcesBaseUrl;
	}

	public List<DataSourceEnum> getMsgOriginsQueue() {
		return msgOriginsQueue;
	}

	public void addMsgOriginsQueue(DataSourceEnum dataSource) {
		this.msgOriginsQueue.add(dataSource);
	}

	public void setMsgOriginsQueue(List<DataSourceEnum> msgOriginsQueue) {
		this.msgOriginsQueue = msgOriginsQueue;
	}
	
}
