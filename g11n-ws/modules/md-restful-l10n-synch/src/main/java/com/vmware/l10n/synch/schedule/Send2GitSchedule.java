/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.synch.schedule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.jgit.api.PullResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.vmware.l10n.synch.init.SynchInfoService;
import com.vmware.l10n.synch.model.SynchFile2GitReq;
import com.vmware.l10n.synch.utils.GitUtils;
import com.vmware.vip.common.constants.ConstantsFile;

/**
 * 
 *
 * @author shihu
 *
 */
@Service
public class Send2GitSchedule {
	private static Logger logger = LoggerFactory.getLogger(Send2GitSchedule.class);
	private final static long SECOND = 1000L;
	public final static BlockingQueue<SynchFile2GitReq> Send2GitQueue = new LinkedBlockingQueue<SynchFile2GitReq>();
    private final static String GITREPOBASE="gitrepo";
    private final static String CHANGEBASE="changes";
	@Value("${translation.git.repository}")
	private String gitRepository;
	@Value("${translation.git.branch}")
	private String gitBranch;
	@Value("${translation.git.gituser}")
	private String gitUser;
	@Value("${translation.git.gitpassword}")
	private String gitPasswd;
	@Value("${translation.git.remoteUri}")
	private String gitRemoteUrl;
	//@Value("${translation.git.priKeyPath}")
	private String priKeyPath;
	//@Value("${translation.git.pubKeyPath}")
	private String pubKeyPath;
	@Value("${translation.bundle.file.basepath}")
	private String bundleBaseDir;
	@Value("${translation.git.remote.enable}")
	private boolean gitRemoteEnabled;

	

	public void initRepos() {
		String productParentPath =getGitRespPath()+ File.separator + "l10n" + File.separator + "bundles";
		File prodsDir = new File(productParentPath);
		if (!prodsDir.exists()) {
			downloadDocBySsh();
		}else {
			gitpull(); 
		}
		
		
	}
	
	
	
	@Scheduled(fixedDelay = SECOND)
	public void syncSourceToRemoteAndLocal() {

		while (!Send2GitQueue.isEmpty()) {
			SynchFile2GitReq req = Send2GitQueue.poll();
			if (req != null) {
				if(gitRemoteEnabled) {
					try {
						// copy the files to tmp repository with product and version limit
						List<File> changeFiles = copyFile2GitTempFiles(req);
						//merge to local repository
						String commitId =  mergeChanges2LocalRepoAndCommit(changeFiles, req.getProductName(), req.getVersion());
						
						// do send commit to remote git
						
						if(commitId != null) {
							
							pushChangeFile2remote();
						}
					}catch(Exception e) {
						logger.error(e.getMessage(), e);
						SynchInfoService.updateCacheToken(req.getProductName(), req.getVersion());
					}
				}else {
					SynchInfoService.updateCacheToken(req.getProductName(), req.getVersion());
				}
			}

		}

	}
	
	
	
	
	
	

	public File downloadDocBySsh() {


		File file = GitUtils.cloneRepBySSh(gitRemoteUrl, getGitRespPath(), gitBranch, gitUser, gitPasswd, priKeyPath,
				pubKeyPath);
		String absRootPath = file.getAbsolutePath() + File.separator + "l10n" + File.separator + "bundles";
		File prodsDir = new File(absRootPath);
		if (!prodsDir.exists()) {
			logger.warn("there no product folder!!!");
			return null;
		}

		return prodsDir;
	}
	
	
	public void gitStatus() {
		GitUtils.gitShowStatus(new File(getGitRespPath()));
	}

	public PullResult gitpull() {
		return GitUtils.gitPull(new File(getGitRespPath()), gitBranch, gitPasswd, priKeyPath,
				pubKeyPath);
	}
	

	public List<File> copyFile2GitTempFiles(SynchFile2GitReq req) {
		
		List<File> destFiles = new ArrayList<File>();
		for (String fileName : req.getItemNames()) {

			if (fileName.contains(req.getProductName())) {
			    logger.info(" i18n bundle file path: {}", fileName);
			    File srcFile = new File(fileName);
			    
			    
			    int startLen = fileName.lastIndexOf(ConstantsFile.L10N_BUNDLES_PATH);
				String destName = tempChangeBasePath()+File.separator+fileName.substring(startLen);
				
				File destFile = new File(destName);
				if(!destFile.getParentFile().exists()) {
					destFile.getParentFile().mkdirs();
				}
				
				
				try {
					Files.copy(srcFile.toPath(), destFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
					destFiles.add(destFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				}
			}

		}

		return destFiles;

	}

	private String mergeChanges2LocalRepoAndCommit(List<File> files, String product, String version) {
		// pull from remote
		    gitpull();
		// merge file to respotory
		List<String> gitFileNames = new ArrayList<String>();
		int noExist=0;
		for (File tempFile : files) {
			String tempFileName = tempFile.getAbsolutePath();
		  logger.info("src file---"+tempFileName );
          String destFileName = tempFileName.replace(CHANGEBASE, GITREPOBASE);
          logger.info("dest file---"+ destFileName);
          File  destFile= new File(destFileName);
          if(!destFile.exists()) {
        	  noExist = noExist+1;
          }
          if(!destFile.getParentFile().exists()) {
        	  destFile.mkdirs();
          }
    
          try {
			Files.copy(tempFile.toPath(), destFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
			String gitDestFile = destFileName.substring(getGitRespPath().length()+1).replace("\\", "/");
			gitFileNames.add(gitDestFile);
			//tempFile.delete();
			logger.info("merge the file:{} to local git repository successfully!!!", destFile.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		}
		
		
		
		
		gitStatus();
		  
		if(gitFileNames.size() >0) {
			String msg = "update the translations to git---"+product+"----"+version;
			String result = GitUtils.commit2localResp(new File(getGitRespPath()), gitFileNames, msg);
			
			if(noExist != files.size()|| result ==null) {
				SynchInfoService.updateCacheToken(product, version);
			}
			logger.info("commit edtion---{}",result);
			return result;
		}
		
		return null;
	}
	
	
	private void pushChangeFile2remote() {
		GitUtils.gitPush(new File(getGitRespPath()), gitBranch, gitPasswd, priKeyPath,pubKeyPath);
	}
	
	
  private String getGitRespPath() {
	 int repLen = this.gitRepository.length();
	 
	 if(this.gitRepository.substring(repLen-1,  repLen).equals(File.separator)) {
		 return  this.gitRepository+GITREPOBASE;
	 }
	 return  this.gitRepository+ File.separator+GITREPOBASE;
  }
	
  private String tempChangeBasePath() {
	  int repLen = this.gitRepository.length();
		 
		 if(this.gitRepository.substring(repLen-1,  repLen).equals(File.separator)) {
			 return  this.gitRepository+CHANGEBASE;
		 }
	  return  this.gitRepository+ File.separator+CHANGEBASE;
  }



public String getPriKeyPath() {
	return priKeyPath;
}



public void setPriKeyPath(String priKeyPath) {
	this.priKeyPath = priKeyPath;
}



public String getPubKeyPath() {
	return pubKeyPath;
}



public void setPubKeyPath(String pubKeyPath) {
	this.pubKeyPath = pubKeyPath;
}

}
