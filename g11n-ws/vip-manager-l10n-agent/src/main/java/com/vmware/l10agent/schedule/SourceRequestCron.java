/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.schedule;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.vmware.l10agent.utils.ValidationUtils;
import com.vmware.vip.common.constants.ValidationMsg;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.vmware.l10agent.base.TaskSysnQueues;
import com.vmware.l10agent.conf.PropertyConfigs;
import com.vmware.l10agent.model.ComponentSourceModel;
import com.vmware.l10agent.model.RecordModel;
import com.vmware.l10agent.service.RecordService;
import com.vmware.l10agent.service.SingleComponentService;
import com.vmware.l10agent.utils.ResouceFileUtils;


/**
 * 
 *
 * @author shihu
 *
 */
/**
 * This implementation of interface SourceService.
 */
@Service
public class SourceRequestCron {
	private static Logger logger = LoggerFactory.getLogger(SourceRequestCron.class);
    private final static String instructV1 = "v1";
    private final static String instructS3 = "s3";
    private final static Map<String,Long> prodVersionLastModify = new HashMap<>();
	@Autowired
	private RecordService recordService;

	@Autowired
	private SingleComponentService singleComponentService;

	@Autowired
	private PropertyConfigs configs;
	

	public final static long SECOND = 1000;

	private static boolean recovedDir = false;

	@PostConstruct
	public void initSendFile() {
		if(configs.getRecordApiVersion().equalsIgnoreCase("s3")) {
			long lastModifyTime = configs.getSyncStartDatetime();
			Map<String, List<String>> allowList = getSyncS3List();
			if (allowList != null) {
				for (Entry<String, List<String>> entry : allowList.entrySet()) {
					String productName = entry.getKey();
					List<String> versionStrs = entry.getValue();
					for (String versionStr : versionStrs) {
						prodVersionLastModify.put(productName + versionStr, lastModifyTime);
					}
				}
			}
		}
		
		logger.info("begin recover the remained resource!!");
		File file = new File(configs.getSourceFileBasepath());

		if (file.exists()) {
			recoverDirectory(file);
			logger.info("begin sync local recover directory component Model to remote");
			while (!TaskSysnQueues.SendComponentTasks.isEmpty()) {
				RecordModel record = TaskSysnQueues.SendComponentTasks.poll();
				syncResource(record);

			}
			logger.info("end sync local recover directory component Model to remote");
		}

		logger.info("end recover the remained resource!!");
		recovedDir = true;
	}

	
	private ComponentSourceModel convertCompFileFormat(ComponentSourceModel model, File file) {
		String path = file.getAbsolutePath();
		 File baseFile = new File(configs.getSourceFileBasepath());
		String basePath =  baseFile.getAbsolutePath();
		logger.info("File absolute path: {}", path);
		basePath = basePath+File.separator;
	    logger.debug(basePath);
		String resultStr = path.replace(basePath, "");
		logger.debug(resultStr);
		String pattern = File.separator;
		String os = System.getProperty("os.name");  
		if(os.toLowerCase().startsWith("win")){  
		  pattern = pattern+ File.separator;
		}  
		String[] strs = resultStr.split(pattern);
		logger.info("Manually add collect resource--"+strs[0].trim()+"---"+strs[1].trim()+"---"+strs[2].trim());
		model.setProduct(strs[0].trim());
		model.setVersion(strs[1].trim());
		
		return model;
	}
	
	private void addTheFile2Queue(File file) {
		ComponentSourceModel comp = null;
		try {
			comp = ResouceFileUtils.readerResource(file);
			if(comp.getProduct()==null || comp.getProduct().equals("")) {
				comp = convertCompFileFormat(comp,file);
			}
			
		} catch (IOException e) {
			logger.error("convert collect resouce error ",e);
		}

		if (comp != null ) {
			RecordModel model = new RecordModel();
			model.setProduct(comp.getProduct());
			model.setVersion(comp.getVersion());
			model.setComponent(comp.getComponent());
			model.setLocale(comp.getLocale());
			model.setStatus(0);
			if(comp.isMessageNotNull()) {
				try {
					TaskSysnQueues.SendComponentTasks.put(model);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
					 Thread.currentThread().interrupt();
				}
			}else {
				singleComponentService.delSourceComponentFile(model);
			}
		}

	}

	private void recoverDirectory(File file) {
		if(file.isFile()) {
			logger.info("file==>" + file.getAbsolutePath());
			//file.setReadable(true);
			addTheFile2Queue(file);
			return;
		}
		File flist[] = file.listFiles();
		if (flist == null || flist.length == 0) {
			return;
		}
		for (File subf : flist) {
			if (subf.isDirectory()) {
				recoverDirectory(subf);
			} else {
				logger.info("file==>" + subf.getAbsolutePath());
				addTheFile2Queue(subf);

			}
		}
	}

	@Scheduled(cron = "${remote.source.schedule.cron}")
	public void lauchInstructToSync() {
		if (!recovedDir){
			return;
		}

		try {
			if(configs.getRecordApiVersion().equalsIgnoreCase("s3") ) {
				TaskSysnQueues.InstructTasks.put(instructS3);
			}else {
				TaskSysnQueues.InstructTasks.put(instructV1);
			}
			
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	@Scheduled(fixedDelay = SECOND * 10)
	public void syncToInternali18nManager() {
	
			Set<RecordModel> set = new HashSet<RecordModel>();
			while (!TaskSysnQueues.SendComponentTasks.isEmpty() && recovedDir) {
				logger.info("begin synch local component Model to VIP i18n");
				RecordModel record = TaskSysnQueues.SendComponentTasks.poll();
			    set.add(record);
			}
	       for(RecordModel record : set) {
			   boolean flag = writeLocalResource(record);
			   if (flag){
				   syncResource(record);
			   }
			}
	}

	private void syncResource(RecordModel record) {
		boolean result = singleComponentService.synchComponentFile2Internal(record);
		if (result) {
			singleComponentService.delSourceComponentFile(record);
			logger.info("synch component Model to VIP successfully!!!");
		} else {
			logger.error("synch component Model to VIP failure!!!");
		}
	}
	
	private boolean writeLocalResource(RecordModel record) {
		logger.info("query record content-{}-{}-{}-{}",record.getProduct(), record.getVersion(), record.getComponent(), record.getLocale());
		ComponentSourceModel component = recordService.getComponentByRemote(record);
		if (component != null && !component.getMessages().isEmpty() ) {
			boolean write = singleComponentService.writerComponentFile(component);
			if(!write) {
				logger.error("write local ComponentSourceModel error! record-{}-{}-{}",record.getProduct(), record.getVersion(), record.getComponent());
				try {
					TaskSysnQueues.SendComponentTasks.put(record);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
					Thread.currentThread().interrupt();
				}
			
			}
			return write;
		}else {
			logger.warn("get source content is null-{}-{}-{}-{}",record.getProduct(), record.getVersion(), record.getComponent(), record.getLocale());
			return false;
		}
    }
	
	
	
	
	/**
	 * Synchronize the updated source to local resource file and GRM timingly
	 */
	@Scheduled(fixedDelay = SECOND * 2)
	public void syncSourcefromRemoteToLocal() {

		while (!TaskSysnQueues.InstructTasks.isEmpty() ) {
			String doneVerion = TaskSysnQueues.InstructTasks.poll();
			if(instructS3.equals(doneVerion)) {
				  doRecordApiS3();
			   }else {
				  doRecordApiV1();
			   }
				
	    }
	}
	
	private void doRecordApiS3() {
		Map<String, List<String>> allowList = getSyncS3List();
		if (allowList != null) {
			for (Entry<String, List<String>> entry : allowList.entrySet()) {
				String product = entry.getKey();
				for (String version : entry.getValue()) {
					try {
						processS3SycSource(product, version);
					}catch (Exception e){
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	private void processS3SycSource(String product, String version) {
	    long maxModifyTime = prodVersionLastModify.get(product+version);
		List<RecordModel> list = recordService.getRecordModelsByRemoteS3(product, version, maxModifyTime);
		if (list != null) {
			for (RecordModel rm : list) {
				logger.debug("{},{},{},{},{}", rm.getProduct(), rm.getVersion(), rm.getLocale(), rm.getComponent(),
						rm.getStatus());
				try {
					TaskSysnQueues.SendComponentTasks.put(rm);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
					Thread.currentThread().interrupt();
				}
				if (rm.getStatus() > maxModifyTime) {
					maxModifyTime = rm.getStatus();
				}
				rm.setStatus(0);
			}
			prodVersionLastModify.put(product+version, maxModifyTime);
		}
	}

	private void doRecordApiV1() {
		for(int i=0; i<configs.getRecordReqThread(); i++) {
			Thread thread = new Thread(new MutiThreadReqApiV1(this.recordService));
			thread.start();
		}
	}
	
	
	
	private Map<String,List<String>> getSyncS3List(){
		File file = new File(configs.getSyncListPath());
		if(file.exists()) {
			try {
				String result = ResouceFileUtils.readerFile2String(file);
				@SuppressWarnings("unchecked")
				HashMap<String,List<String>> arry = JSON.parseObject(result, HashMap.class);

				for (Entry<String, List<String>> entry : arry.entrySet()) {
					String productName = entry.getKey();
					if(ValidationUtils.validateProductName(productName)){
						List<String> versionStrs = entry.getValue();
						for (String versionStr : versionStrs) {
							if (ValidationUtils.validateVersion(versionStr)){
								logger.info("sync source List: {}--{}", productName, versionStr);
							}else {
								logger.error(ValidationMsg.VERSION_NOT_VALIDE+": {}/{}", productName, versionStr);
								versionStrs.remove(versionStr);
							}
						}
					}else {
						logger.error(ValidationMsg.PRODUCTNAME_NOT_VALIDE+"--{}", productName);
						arry.remove(productName);

					}
				}

				return arry;
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}else {
			logger.error("-----------------Not find sync s3 list file!--------------");
		}
		return null;
	}
	
	


}
