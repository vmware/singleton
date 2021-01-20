/*
 * Copyright 2019-2021 VMware, Inc.
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

import javax.annotation.PostConstruct;

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
	@Autowired
	private RecordService recordService;

	@Autowired
	private SingleComponentService singleComponentService;

	@Autowired
	private PropertyConfigs configs;
	

	public final static long SECOND = 1000;
	private static long lastModifyTime = 0;

	@PostConstruct
	public void initSendFile() {
		if(configs.getRecordApiVersion().equalsIgnoreCase("s3")) {
			lastModifyTime = configs.getSyncStartDatetime();
		}
		logger.info("begin recover the remained resource!!");
		File file = new File(configs.getSourceFileBasepath());

		if (file.exists()) {
			recoverDirectory(file);
		}

		logger.info("end recover the remained resource!!");
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
		try {
			if(configs.getRecordApiVersion().equalsIgnoreCase("s3")) {
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
			while (!TaskSysnQueues.SendComponentTasks.isEmpty()) {
				logger.info("begin synch local component Model to VIP i18n");
				RecordModel record = TaskSysnQueues.SendComponentTasks.poll();
			    set.add(record);
			}
	       for(RecordModel record : set) {
	   		   writeLocalResource(record); 
	    	   syncResource(record);
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
	
	private void writeLocalResource(RecordModel record) {
		logger.error("query record content-{}-{}-{}-{}",record.getProduct(), record.getVersion(), record.getComponent(), record.getLocale());
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
		}
    }
	
	
	
	
	/**
	 * Synchronize the updated source to local resource file and GRM timingly
	 */
	@Scheduled(fixedDelay = SECOND * 2)
	public void syncSourcefromRemoteToLocal() {

		while (!TaskSysnQueues.InstructTasks.isEmpty()) {
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
					processS3SycSource(product, version);
				}
			}
		}
	}

	private void processS3SycSource(String product, String version) {
		 List<RecordModel> list = recordService.getRecordModelsByRemoteS3(product, version,lastModifyTime);
		 for(RecordModel rm :list) {
			 logger.debug("{},{},{},{},{}",rm.getProduct(), rm.getVersion(), rm.getLocale(), rm.getComponent(), rm.getStatus());
	    	 try {
				TaskSysnQueues.SendComponentTasks.put(rm);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
				Thread.currentThread().interrupt();
			}
	    	if(rm.getStatus()>lastModifyTime) {
					lastModifyTime = rm.getStatus();
				}
	    	 rm.setStatus(0); 
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
				return arry;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.error("Not find sync s3 list file!");
		return null;
	}
	
	


}
