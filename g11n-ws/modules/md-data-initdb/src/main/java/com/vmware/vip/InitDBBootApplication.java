/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip;

import java.io.File;

import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vmware.vip.initdb.pseudo.Pseudo;
import com.vmware.vip.initdb.service.ProcessService;

/**
 * 
 *
 * @author shihu
 *
 */
@SpringBootApplication
public class InitDBBootApplication implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(InitDBBootApplication.class);

	@Autowired
	private ProcessService ps;
	
	@Autowired
	private Pseudo  peudo;

	public static void main(String[] args) {

		SpringApplication.run(InitDBBootApplication.class, args);
		logger.info("------------------------------end db data----------------------------");
	}

	@Override
	public void run(String... arg0) throws Exception {
		// TODO Auto-generated method stub

		//initLatestdata();
		
		initAndpresudoData();

	}
	
	
	
	
private void initAndpresudoData() {
	  File file = ps.downloadDocBySsh();
	  logger.info("end download the json");
	    peudo.exePythonStream(file.getAbsolutePath());
	  
	  logger.info("end presudo data");
	   ps.clearAndDone(file);
	   logger.info("end init translate data to db--------------------------------------");
	  
}
	
	
public void  presudoDataLocalinitdata() {
    String path = "F:\\g11n-translations\\data\\l10n\\bundles";
    File file = new File(path);
  
    peudo.exePythonStream(file.getAbsolutePath());
	  
	  logger.info("end presudo data");
	   ps.done(file);
    
    
}


  public void  initLatestdata() {
	    String path = "F:\\works\\initdata\\resources\\l10n\\bundles";
	    File file = new File(path);
	  
	    
	    
	    
		ps.doneLatest(file);;
  }
	
  
  
  
	
	
	
   public void  updateDBdata() throws InterruptedException {
	   File file = ps.downloadDocBySsh();
		ps.done(file);
		logger.info("end init translate data to db--------------------------------------");
		ps.gitStatus();

		while (ps.getGitProp().getCheckIntervalTime() > 60000) {

			Thread.sleep(ps.getGitProp().getCheckIntervalTime());

			logger.info("----------------------beign pull status --------------------------");

			logger.info("begin pull from remote");
			PullResult pullRS = ps.gitpull();
			logger.info("end pull from remote");
			logger.info("--------------the pull status-" + pullRS.getMergeResult().getMergeStatus());

			if (pullRS.isSuccessful()
					&& !pullRS.getMergeResult().getMergeStatus().equals(MergeStatus.ALREADY_UP_TO_DATE)) {
				logger.info("remote translation files have updated!!!");
				logger.info("begin update data to DB");
				ps.done(file);
				logger.info("end updated data to DB");

			} else if (pullRS.isSuccessful()
					&& pullRS.getMergeResult().getMergeStatus().equals(MergeStatus.ALREADY_UP_TO_DATE)) {
				logger.info("remote translation file have not updated!!!");
			} else {
				logger.error("remote translation file merged error");
				file = ps.downloadDocBySsh();
				ps.done(file);

			}

		}

		logger.info("------------------------------end done run----------------------------");
   }

}
