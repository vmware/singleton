package com.vmware.vip.test.javaclient.init;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.testng.annotations.Test;

import com.vmware.vip.test.common.Utils;
import com.vmware.vip.test.common.annotation.TestCase;
import com.vmware.vip.test.common.annotation.TestCase.Priority;
import com.vmware.vip.test.javaclient.Constants;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vipclient.i18n.VIPCfg;

public class InitTest extends TestBase{
	@Test(enabled=false, priority=0)
	@TestCase(id = "001", name = "Init_External_Property", priority=Priority.P0,
	description = "Test VIP initialized from external property file, test both true and false value for boolean type items.")
	public void initExternalProperties() throws Exception {
		String testConfig = String.format("%s/vipconfigtest.properties", Constants.TEST_DATA_FOLDER);
		String oppositeConfig = String.format("%s/vipconfigtest-boolean-opposite.properties", Constants.TEST_DATA_FOLDER);
		log.info(String.format("Test vip initialization with config file '%s'", testConfig));
		testConfig(testConfig);
		log.info(String.format("Test vip initialization with config file '%s', "
				+ "the boolean value in this config file is opposite against above one", oppositeConfig));
		testConfig(oppositeConfig);
	}

	private void testConfig(String vipConfigPath) throws MalformedURLException {
		VIPCfg vipCfg = VIPCfg.getInstance();
		vipCfg.initialize(Utils.removeFileExtension(Utils.getFileName(vipConfigPath)));
        vipCfg.initializeVIPService();
        Properties prop = Utils.loadProperties(vipConfigPath);
        if (prop==null) {
        	log.error(String.format("Get properties failed from '%s'", vipConfigPath));
        } else {
        	log.verifyEqual("Product name in vip config instance is same with what in property file.",
        			vipCfg.getProductName(), prop.getProperty(Constants.VIP_CONF_KEY_PRODUCT));
        	log.verifyEqual("Version in vip config instance is same with what in property file.",
        			vipCfg.getVersion(), prop.getProperty(Constants.VIP_CONF_KEY_VERSION));
        	log.verifyEqual("VIP server in vip config instance is same with what in property file.",
        			vipCfg.getVipServer(), prop.getProperty(Constants.VIP_CONF_KEY_VIP_SERVER));
        	log.verifyEqual("Initialze cache in vip config instance is same with what in property file.",
        			vipCfg.isInitializeCache(), Boolean.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_INIT_CACHE)));
        	log.verifyEqual("Pseudo in vip config instance is same with what in property file.",
        			vipCfg.isPseudo(), Boolean.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_PSEUDO)));
        	log.verifyEqual("Collect source in vip config instance is same with what in property file.",
        			vipCfg.isCollectSource(), Boolean.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_COLLECT_SROUCE)));
        	log.verifyEqual("Clean cache in vip config instance is same with what in property file.",
        			vipCfg.isCleanCache(), Boolean.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_CLEAN_CACHE)));
        	log.verifyEqual("MT in vip config instance is same with what in property file.",
        			vipCfg.isMachineTranslation(), Boolean.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_MT)));
        	log.verifyEqual("I18n scope in vip config instance is same with what in property file.",
        			vipCfg.getI18nScope(), prop.getProperty(Constants.VIP_CONF_KEY_I18N_SCOPE));
        	log.verifyEqual("Cache expired time in vip config instance is same with what in property file.",
        			vipCfg.getCacheExpiredTime(), Long.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_CACHE_EXPIRED_TIME)));
        }
	}

//
//	@Test(enabled=false, priority=0)
//	@TestCase(id = "001", name = "Init_External_Property", priority=Priority.P0,
//	description = "Test VIP initialized from external property file, test both true and false value for boolean item.")
//	public void initExternalProperty() throws IOException {
////		String externalPropertyFilePath = Constants.VIP_TEST_CONFIG_FILE_PATH;
//		String externalPropertyFilePath = "target/classes/"+Constants.VIP_TEST_CONFIG_FILE_NAME;
//		String produtcName = "testproduct";
//		String version = "2.0.0";
//		String vipServer = "test.vip.server.com";
//		String initializeCache = "false";
//		String pseudo = "false";
//		String collectSource = "false";
//		String cleanCache = "false";
//		String cacheExpiredTime = "1000000";
//		String machineTranslation = "false";
//		String i18nScope = "numbers,dates,currencies,plurals,measurements";
//		Properties properties = new Properties();
//		properties.setProperty(Constants.VIP_CONF_KEY_PRODUCT, produtcName);
//		properties.setProperty(Constants.VIP_CONF_KEY_VERSION, version);
//		properties.setProperty(Constants.VIP_CONF_KEY_VIP_SERVER, vipServer);
//		properties.setProperty(Constants.VIP_CONF_KEY_INIT_CACHE, initializeCache);
//		properties.setProperty(Constants.VIP_CONF_KEY_PSEUDO, pseudo);
//		properties.setProperty(Constants.VIP_CONF_KEY_COLLECT_SROUCE, collectSource);
//		properties.setProperty(Constants.VIP_CONF_KEY_CLEAN_CACHE, cleanCache);
//		properties.setProperty(Constants.VIP_CONF_KEY_MT, machineTranslation);
//		properties.setProperty(Constants.VIP_CONF_KEY_I18N_SCOPE, i18nScope);
//		properties.setProperty(Constants.VIP_CONF_KEY_CACHE_EXPIRED_TIME, cacheExpiredTime);
//		testExternalProperty(properties, externalPropertyFilePath);
//
//        log.info("set opppsite value for boolean items");
//        initializeCache = getOppositeValue(initializeCache);
//	    pseudo = getOppositeValue(pseudo);
//		collectSource = getOppositeValue(collectSource);
//		cleanCache = getOppositeValue(cleanCache);
//		machineTranslation = getOppositeValue(machineTranslation);
//		properties.setProperty(Constants.VIP_CONF_KEY_PRODUCT, "newproduct");
//		properties.setProperty(Constants.VIP_CONF_KEY_INIT_CACHE, initializeCache);
//		properties.setProperty(Constants.VIP_CONF_KEY_PSEUDO, pseudo);
//		properties.setProperty(Constants.VIP_CONF_KEY_COLLECT_SROUCE, collectSource);
//		properties.setProperty(Constants.VIP_CONF_KEY_CLEAN_CACHE, cleanCache);
//		properties.setProperty(Constants.VIP_CONF_KEY_MT, machineTranslation);
//		testExternalProperty(properties, "resource/vipconfigoverride.properties");
//	}
//
//	private void testExternalProperty(Properties properties, String filePath) {
//		String filename = Utils.getFileName(filePath);
//		String filenameWithoutExtension = Utils.removeFileExtension(filename);
//		File externalPropertyFile = new File(filePath);
//		OutputStream outStream = null;
////		InputStream inStream = null;
//		try {
////			inStream = new FileInputStream(externalPropertyFile);
//			outStream = new FileOutputStream(externalPropertyFile);
//			properties.store(outStream, "test properties");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			try {
//				outStream.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		VIPCfg vipCfg = VIPCfg.getInstance();
//		vipCfg.initialize(filenameWithoutExtension);
//        vipCfg.initializeVIPService();
//        log.verifyEqual("Product name in vip config instance is same with what in property file.",
//    			vipCfg.getProductName(), properties.get(Constants.VIP_CONF_KEY_PRODUCT));
//    	log.verifyEqual("Version in vip config instance is same with what in property file.",
//    			vipCfg.getVersion(), properties.get(Constants.VIP_CONF_KEY_VERSION));
//    	log.verifyEqual("VIP server in vip config instance is same with what in property file.",
//    			vipCfg.getVipServer(), properties.get(Constants.VIP_CONF_KEY_VIP_SERVER));
//    	log.verifyEqual("Initialze cache in vip config instance is same with what in property file.",
//    			vipCfg.isInitializeCache(), Boolean.valueOf(properties.get(Constants.VIP_CONF_KEY_INIT_CACHE).toString()));
//    	log.verifyEqual("Pseudo in vip config instance is same with what in property file.",
//    			vipCfg.isPseudo(), Boolean.valueOf(properties.get(Constants.VIP_CONF_KEY_PSEUDO).toString()));
//    	log.verifyEqual("Collect source in vip config instance is same with what in property file.",
//    			vipCfg.isCollectSource(), Boolean.valueOf(properties.get(Constants.VIP_CONF_KEY_COLLECT_SROUCE).toString()));
//    	log.verifyEqual("Clean cache in vip config instance is same with what in property file.",
//    			vipCfg.isCleanCache(), Boolean.valueOf(properties.get(Constants.VIP_CONF_KEY_CLEAN_CACHE).toString()));
//    	log.verifyEqual("MT in vip config instance is same with what in property file.",
//    			vipCfg.isMachineTranslation(), properties.get(Constants.VIP_CONF_KEY_MT));
//    	log.verifyEqual("I18n scope in vip config instance is same with what in property file.",
//    			vipCfg.getI18nScope(), properties.get(Constants.VIP_CONF_KEY_I18N_SCOPE));
//    	log.verifyEqual("Cache expired time in vip config instance is same with what in property file.",
//    			vipCfg.getCacheExpiredTime(), Long.valueOf(properties.get(Constants.VIP_CONF_KEY_CACHE_EXPIRED_TIME).toString()));
//	}
//
//	private String getOppositeValue(String value) {
//		return String.valueOf(!Boolean.valueOf(value));
//	}

	@Test(enabled=true, priority=0)
	@TestCase(id = "002", name = "Init_Property_From_Parameter", priority=Priority.P0,
	description = "Initialize VIP property from parameteres, test both true and false value for boolean type items.")
	public void initParameteres() throws IOException {
		//initialize parameters
		String testServer = "localhost:8090";
		String testProduct = "Testing";
		String testVersion = "1.0.0";
        boolean booleanCleanCache = true;
        boolean booleanCollectSource = true;
        boolean booleanInitializeCache = true;
        boolean booleanMachineTranslation = true;
        String i18nScope = "numbers,dates,currencies";
        int interalCleanCache = 725;
        long cacheExpiredTime = 666666l;
        boolean booleanPseudo = true;

        //initialize vip config with above parameters
		VIPCfg vipCfg = VIPCfg.getInstance();
        vipCfg.initialize(testServer, testProduct, testVersion);
        vipCfg.initializeVIPService();
        vipCfg.setCleanCache(booleanCleanCache);
        vipCfg.setCollectSource(booleanCollectSource);
        vipCfg.setI18nScope(i18nScope);
        vipCfg.setInitializeCache(booleanInitializeCache);
        vipCfg.setInteralCleanCache(interalCleanCache);
        vipCfg.setMachineTranslation(booleanMachineTranslation);
        vipCfg.setPseudo(booleanPseudo);
        vipCfg.setCacheExpiredTime(cacheExpiredTime);

        //verify each parameter
        log.verifyEqual("Product name in vip config instance is same with what in property file.",
    			vipCfg.getProductName(), testProduct);
    	log.verifyEqual("Version in vip config instance is same with what in property file.",
    			vipCfg.getVersion(), testVersion);
    	log.verifyEqual("VIP server in vip config instance is same with what in property file.",
    			vipCfg.getVipServer(), testServer);
    	log.verifyEqual("Initialze cache in vip config instance is same with what in property file.",
    			vipCfg.isInitializeCache(), booleanInitializeCache);
    	log.verifyEqual("Pseudo in vip config instance is same with what in property file.",
    			vipCfg.isPseudo(), booleanPseudo);
    	log.verifyEqual("Collect source in vip config instance is same with what in property file.",
    			vipCfg.isCollectSource(), booleanCollectSource);
    	log.verifyEqual("Clean cache in vip config instance is same with what in property file.",
    			vipCfg.isCleanCache(), booleanCleanCache);
    	log.verifyEqual("MT in vip config instance is same with what in property file.",
    			vipCfg.isMachineTranslation(), booleanMachineTranslation);
    	log.verifyEqual("I18n scope in vip config instance is same with what in property file.",
    			vipCfg.getI18nScope(), i18nScope);
    	log.verifyEqual("Cache expired time in vip config instance is same with what in property file.",
    			vipCfg.getCacheExpiredTime(), cacheExpiredTime);

    	//set opposite value for boolean type
    	booleanCleanCache = !booleanCleanCache;
        booleanCollectSource = !booleanCollectSource;
        booleanInitializeCache = !booleanInitializeCache;
        booleanMachineTranslation = !booleanMachineTranslation;
        booleanPseudo = !booleanPseudo;
        vipCfg.setCleanCache(booleanCleanCache);
        vipCfg.setCollectSource(booleanCollectSource);
        vipCfg.setInitializeCache(booleanInitializeCache);
        vipCfg.setMachineTranslation(booleanMachineTranslation);
        vipCfg.setPseudo(booleanPseudo);

        //verify new settings
    	log.verifyEqual("Initialze cache in vip config instance is same with what in property file.",
    			vipCfg.isInitializeCache(), booleanInitializeCache);
    	log.verifyEqual("Pseudo in vip config instance is same with what in property file.",
    			vipCfg.isPseudo(), booleanPseudo);
    	log.verifyEqual("Collect source in vip config instance is same with what in property file.",
    			vipCfg.isCollectSource(), booleanCollectSource);
    	log.verifyEqual("Clean cache in vip config instance is same with what in property file.",
    			vipCfg.isCleanCache(), booleanCleanCache);
    	log.verifyEqual("MT in vip config instance is same with what in property file.",
    			vipCfg.isMachineTranslation(), booleanMachineTranslation);
	}

	@Test(enabled=false)
	@TestCase(id = "003", name = "Override_External_Property",
	description = "Override VIP property through external file.")
	public void onverrideExternalProperty() throws IOException {
		String overrideFilePath = String.format("%s/vipconfigoverride.properties", Constants.TEST_DATA_FOLDER);
		VIPCfg vipCfg = VIPCfg.getInstance();
		vipCfg.initialize(Utils.removeFileExtension(Constants.VIP_CONFIG_FILE_NAME));
        vipCfg.initializeVIPService();
        //override
		vipCfg.initialize(Utils.removeFileExtension(Utils.getFileName(overrideFilePath)));
        vipCfg.initializeVIPService();
        Properties prop = Utils.loadProperties(overrideFilePath);
        if (prop==null) {
        	log.error(String.format("Get properties failed from '%s'", overrideFilePath));
        } else {
        	log.verifyEqual("Product name in vip config instance is same with what in property file.",
        			vipCfg.getProductName(), prop.getProperty(Constants.VIP_CONF_KEY_PRODUCT));
        	log.verifyEqual("Version in vip config instance is same with what in property file.",
        			vipCfg.getVersion(), prop.getProperty(Constants.VIP_CONF_KEY_VERSION));
        	log.verifyEqual("VIP server in vip config instance is same with what in property file.",
        			vipCfg.getVipServer(), prop.getProperty(Constants.VIP_CONF_KEY_VIP_SERVER));
        	log.verifyEqual("Initialze cache in vip config instance is same with what in property file.",
        			vipCfg.isInitializeCache(), Boolean.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_INIT_CACHE)));
        	log.verifyEqual("Pseudo in vip config instance is same with what in property file.",
        			vipCfg.isPseudo(), Boolean.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_PSEUDO)));
        	log.verifyEqual("Collect source in vip config instance is same with what in property file.",
        			vipCfg.isCollectSource(), Boolean.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_COLLECT_SROUCE)));
        	log.verifyEqual("Clean cache in vip config instance is same with what in property file.",
        			vipCfg.isCleanCache(), Boolean.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_CLEAN_CACHE)));
        	log.verifyEqual("MT in vip config instance is same with what in property file.",
        			vipCfg.isMachineTranslation(), Boolean.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_MT)));
        	log.verifyEqual("I18n scope in vip config instance is same with what in property file.",
        			vipCfg.getI18nScope(), prop.getProperty(Constants.VIP_CONF_KEY_I18N_SCOPE));
        	log.verifyEqual("Cache expired time in vip config instance is same with what in property file.",
        			vipCfg.getCacheExpiredTime(), Long.valueOf(prop.getProperty(Constants.VIP_CONF_KEY_CACHE_EXPIRED_TIME)));
        }
	}
}
