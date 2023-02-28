/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.initdb.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vmware.vip.initdb.conf.GitConfigProperties;
import com.vmware.vip.initdb.model.DBI18nDocument;
import com.vmware.vip.initdb.model.DbResponseStatus;
import com.vmware.vip.initdb.model.TransCompDocFile;
import com.vmware.vip.initdb.model.TransDocProperties;
import com.vmware.vip.initdb.utils.BundleProductUtils;
import com.vmware.vip.initdb.utils.GitUtils;

/**
 * 
 *
 * @author shihu
 *
 */
@Component
public class ProcessService {

	private static Logger logger = LoggerFactory.getLogger(ProcessService.class);

	@Autowired
	private RequestDBJar dbService;

	@Autowired
	private GitConfigProperties gitProp;

	public GitConfigProperties getGitProp() {
		return this.gitProp;
	}

	public File downloadDocBySsh() {

		// File file =
		// GitUtils.cloneRepBySSh(this.gitProp.getRemoteUri(),this.gitProp.getLocalFolder(),this.gitProp.getBranch(),
		// this.gitProp.getUsername(), this.gitProp.getPassword(),
		// this.gitProp.getPriKeyPath(), this.gitProp.getPubKeyPath());
		File file = GitUtils.cloneRepBySSh(this.gitProp.getRemoteUri(), this.gitProp.getLocalFolder(),
				this.gitProp.getBranch(), this.gitProp.getUsername(), this.gitProp.getPassword(),
				this.gitProp.getPriKeyPath(), this.gitProp.getPubKeyPath());
		String absRootPath = file.getAbsolutePath() + File.separator + "l10n" + File.separator + "bundles";
		File prodsDir = new File(absRootPath);
		if (!prodsDir.exists()) {
			logger.error("there no product folder!!!");
			return null;
		}

		return prodsDir;
	}

	public Status gitStatus() {
		return GitUtils.gitShowStatus(new File(this.gitProp.getLocalFolder()));
	}

	public PullResult gitpull() {
		return GitUtils.gitPull(new File(this.gitProp.getLocalFolder()));
	}

	public File downloadDoc() {
		File file = GitUtils.cloneRepository(this.gitProp.getRemoteUri(), this.gitProp.getLocalFolder(),
				this.gitProp.getBranch(),
				GitUtils.getUserAndPasswd(this.gitProp.getUsername(), this.gitProp.getPassword()));

		String absRootPath = file.getAbsolutePath() + File.separator + "l10n" + File.separator + "bundles";
		File prodsDir = new File(absRootPath);
		if (!prodsDir.exists()) {
			logger.error("there no product folder!!!");
			return null;
		}

		return prodsDir;

	}

	public void done(File file) {
		Map<String, List<String>> pashs = BundleProductUtils.listProductVersionPath(file);

		for (Entry<String, List<String>> entryversion : pashs.entrySet()) {
			String product = entryversion.getKey();
			List<String> versions = entryversion.getValue();

			if (!dbService.checkProductAndAdd(product)) {
				logger.error("the product :----{}------check failure!!!!!!!!", product);
				continue;
			}
			
			
			
			
			

			for (String path : versions) {
				System.out.println("version path:"+path);
				
				List<TransDocProperties> componetList = BundleProductUtils.listVersionComponents(new File(path),
						product);
				for (TransDocProperties compprop : componetList) {

					List<TransCompDocFile> docs = BundleProductUtils.listComponentDocFiles(compprop);

					for (TransCompDocFile docFile : docs) {
                        
						addAndUpdate(docFile);

					}

				}

			}

		}

	}
	
	

	public void doneLatest(File file) {
		Map<String, List<String>> pashs = BundleProductUtils.listProductVersionPath(file);

		for (Entry<String, List<String>> entryversion : pashs.entrySet()) {
			String product = entryversion.getKey();
			List<String> versions = entryversion.getValue();

			if (!dbService.checkProductAndAdd(product)) {
				logger.error("the product :---{}------check failure!!!!!!!!", product);
				continue;
			}
			
			
			

			for (String path : versions) {
				List<TransDocProperties> componetList = BundleProductUtils.listVersionComponents(new File(path),
						product);
				for (TransDocProperties compprop : componetList) {

					List<TransCompDocFile> docs = BundleProductUtils.listComponentDocFiles(compprop);

					for (TransCompDocFile docFile : docs) {
						
						if(docFile.getLocale().equals("latest")) {
							addAndUpdate(docFile);
						}

					}

				}

			}

		}

	}

	
	
	public void clearAndDone(File file) {
		Map<String, List<String>> pashs = BundleProductUtils.listProductVersionPath(file);

		for (Entry<String, List<String>> entryversion : pashs.entrySet()) {
			String product = entryversion.getKey();
			List<String> versions = entryversion.getValue();

			if (!dbService.checkProductAndAdd(product)) {
				logger.error("the product :-----{}------check failure!!!!!!!!", product);
				continue;
			}
			
			

			for (String path : versions) {
				logger.info("version path:{}",path);
				
				List<TransDocProperties> componetList = BundleProductUtils.listVersionComponents(new File(path),
						product);
				for (TransDocProperties compprop : componetList) {

					List<TransCompDocFile> docs = BundleProductUtils.listComponentDocFiles(compprop);

					for (TransCompDocFile docFile : docs) {
                        
						clearAndUpdate(docFile);

					}

				}

			}

		}

	}
	
	
	
 	private DbResponseStatus clearAndUpdate(TransCompDocFile docPropFile) {
 		DBI18nDocument doc = null;
		try {
			doc = ReadCompJsonFile.Json2DBDoc(docPropFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			logger.error(e.getMessage(), e);
		}

		if(doc == null) {
			return null;
		}
		
		
		
		try {
			return dbService.DelDocAndAdd2DB(doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		return null;
 	}
	
	private DbResponseStatus addAndUpdate(TransCompDocFile docPropFile) {
		DBI18nDocument doc = null;
		try {
			doc = ReadCompJsonFile.Json2DBDoc(docPropFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			logger.error(e.getMessage(), e);
		}

		if(doc == null) {
			return null;
		}
		
		try {
			return dbService.AddDoc2DB(doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
