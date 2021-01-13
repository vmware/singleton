/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.schedule;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

	@Autowired
	private RecordService recordService;

	@Autowired
	private SingleComponentService singleComponentService;

	@Autowired
	private PropertyConfigs configs;

	public final static long SECOND = 1000;

	@PostConstruct
	public void initSendFile() {
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
		//logger.info(path);
		basePath = basePath+File.separator;
	  //	logger.info(basePath);
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
			// TODO Auto-generated catch block
			logger.error("convert collect resouce error ",e);
		}

		if (comp != null ) {
			RecordModel model = new RecordModel();
			model.setProduct(comp.getProduct());
			model.setVersion(comp.getVersion());
			model.setComponent(comp.getComponent());
			model.setLocale(comp.getLocale());
			model.setStatus(1);
			if(comp.isMessageNotNull()) {
				try {
					TaskSysnQueues.SendComponentTasks.put(model);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
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

				//System.out.println("Dir==>" + subf.getAbsolutePath());
				recoverDirectory(subf);
			} else {
				logger.info("file==>" + subf.getAbsolutePath());
				//subf.setReadable(true);
				addTheFile2Queue(subf);

			}
		}
	}

	@Scheduled(cron = "${remote.source.schedule.cron}")
	public void lauchInstructToSync() {
		try {
			TaskSysnQueues.InstructTasks.put("DONE");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	@Scheduled(fixedDelay = SECOND * 10)
	public void syncToInternali18nManager() {
		while (!TaskSysnQueues.SendComponentTasks.isEmpty()) {
			logger.info("begin synch local component Model to VIP i18n");

			RecordModel record = TaskSysnQueues.SendComponentTasks.poll();
			if (record != null) {

				boolean result = singleComponentService.synchComponentFile2I18n(record);

				if (result) {
					singleComponentService.delSourceComponentFile(record);
					logger.info("synch component Model to VIP successfully!!!");
				} else {
					logger.error("synch component Model to VIP failure!!!");
				}

			} else {
				logger.info("no synch component!!!!!");
			}

		}

	}

	/**
	 * Synchronize the updated source to local resource file and GRM timingly
	 */
	@Scheduled(fixedDelay = SECOND * 2)
	public void syncSourcefromRemoteToLocal() {

		while (!TaskSysnQueues.InstructTasks.isEmpty()) {
			
			
			TaskSysnQueues.InstructTasks.poll();
			
			
			logger.info("begin synch component Model from remote l10n to local agent");
			List<RecordModel> list = recordService.getRecordModelsByRemote();

			if (list != null && list.size() > 0) {

				for (RecordModel record : list) {

					ComponentSourceModel component = recordService.getComponentByRemote(record);

					if (component != null && !component.getMessages().isEmpty() ) {
                       
						boolean write = singleComponentService.writerComponentFile(component);
						if (write) {
							boolean synch = recordService.synchRecordModelsByRemote(record);
							if (synch) {
								try {
									TaskSysnQueues.SendComponentTasks.put(record);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
								
									logger.error(e.getMessage(), e);
									Thread.currentThread().interrupt();
								}
								logger.info("synch Record Model from Remote successfully!!!");
							} else {
								logger.info("synch Record Model to Remote failure!!!");
							}
						} else {
							logger.error("write local ComponentSourceModel error");
						}
					} else {
						 recordService.synchRecordModelsByRemote(record);
						logger.info("there no ComponentSourceModel from remote");
					}

				}

			} else {
				logger.info("there no record model in remote server");
			}

		}

	}

}
