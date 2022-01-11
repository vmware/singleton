/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.vmware.l10n.utils.DiskQueueUtils;

/**
 * This is a listener class for spring application.
 */
@WebListener
public class CachedFilesServerListener implements ServletContextListener    {
	@Value("${source.bundle.file.basepath}")
	private String basePath;
	private static Logger LOGGER = LoggerFactory.getLogger(CachedFilesServerListener.class);

	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		LOGGER.info("ServletContex initialized!");
		scanCachedFiles();
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
		LOGGER.info("ServletContex Destroyed!");
		scanCachedFiles(); 
	}
	
	
	private void scanCachedFiles() {
		List<File> files = DiskQueueUtils.listSourceQueueFile(this.basePath);
		if(files != null) {
			LOGGER.info("The cached source files need to process:");
			for(File file: files) {
				LOGGER.info(file.getAbsolutePath());
			}
		}
		
		List<File> exceptFiles = DiskQueueUtils.listExceptQueueFile(basePath);
		if(exceptFiles != null) {
			LOGGER.error("The exception source files need to process:");
			for(File exceptFile: exceptFiles) {
				LOGGER.error(exceptFile.getAbsolutePath());
			}
		}
	}
}
