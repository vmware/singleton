/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.synch.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * 
 *
 * @author shihu
 *
 */
public class GitUtils {

	private static Logger logger = LoggerFactory.getLogger(GitUtils.class);

	/*
	 * private static Git cloneGit= null;
	 * 
	 * public static Git getCloneGit() { return cloneGit; }
	 */

	public static UsernamePasswordCredentialsProvider getUserAndPasswd(String user, String passwd) {
		return new UsernamePasswordCredentialsProvider(user, passwd);
	}

	public static File cloneRepository(String remoteUri, String tempFolder) {

		File file = new File(tempFolder);

		try {
			if (file.exists()) {
				deleteFolder(file);
			}
			file.mkdirs();
			Git.cloneRepository().setURI(remoteUri).setDirectory(file).call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return file;
	}

	public static File cloneRepository(String remoteUri, String tempFolder, CredentialsProvider credentialsProvider) {

		logger.info("begin clone " + remoteUri + " to " + tempFolder);
		File file = new File(tempFolder);

		try {
			if (file.exists()) {
				deleteFolder(file);
			}
			file.mkdirs();
			Git git = Git.cloneRepository().setURI(remoteUri).setDirectory(file)
					.setCredentialsProvider(credentialsProvider).call();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		logger.info("end clone " + remoteUri + " to " + tempFolder);

		return file;
	}

	public static void deleteFolder(File file) {
		if (file.isFile() || file.list().length == 0) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFolder(files[i]);
				files[i].delete();
			}
		}
	}

	public static File cloneRepBySSh(String remoteUri, String tempFolder, String branch, String user, String passwd,
			String pathprivatekey, String pathPubkey) {
		File file = new File(tempFolder);
		if (file.exists()) {
			deleteFolder(file);
		}
		file.mkdirs();

		Git gitc = cloneRepositoryGit(user, branch, remoteUri, file, passwd, pathprivatekey, pathPubkey);
		// cloneGit = gitc;
		if (gitc != null) {
			gitc.close();
		}
		return file;
	}

	/**
	 * Clones a private remote git repository. Caller is responsible of closing git
	 * repository.
	 *
	 * @param remoteUrl
	 *            to connect.
	 * @param localPath
	 *            where to clone the repo.
	 * @param passphrase
	 *            to access private key.
	 * @param privateKey
	 *            file location. If null default (~.ssh/id_rsa) location is used.
	 *
	 * @return Git instance. Caller is responsible to close the connection.
	 */
	private static Git cloneRepositoryGit(String username, String branch, final String remoteUrl,
			final File localFileDir, final String passphrase, final String pathPrivateKey, final String pathPubKey) {

		SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
			@Override
			protected void configure(OpenSshConfig.Host host, Session session) {
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
			}

			@Override
			protected JSch createDefaultJSch(FS fs) throws JSchException {
				JSch.setConfig("StrictHostKeyChecking", "no");
				JSch defaultJSch = super.createDefaultJSch(fs);
				try {
					defaultJSch.addIdentity(pathPrivateKey, pathPubKey, passphrase.getBytes("UTF-8"));
					// defaultJSch.addIdentity(pathPrivateKey, pathPubKey);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// defaultJSch.addIdentity( pathPrivateKey, passphrase);
				// String
				// knowhost="F:\\vip-codesnew\\g11n-service\\g11n-ws\\modules\\md-data-initdb\\src\\main\\resources\\sshkey\\known_hosts";
				// defaultJSch.setKnownHosts(knowhost);
				return defaultJSch;
			}

		};

		try {
			return Git.cloneRepository().setURI(remoteUrl).setTransportConfigCallback(transport -> {
				SshTransport sshTransport = (SshTransport) transport;
				sshTransport.setSshSessionFactory(sshSessionFactory);
			}).setDirectory(localFileDir).setBranch(branch).call();
		} catch (GitAPIException e) {
			throw new IllegalStateException(e);
		}
	}

	public static File cloneRepository(String remoteUri, String tempFolder, String branch,
			CredentialsProvider credentialsProvider) {

		File file = new File(tempFolder);

		try {
			if (file.exists()) {
				deleteFolder(file);
			}
			file.mkdirs();
			Git.cloneRepository().setURI(remoteUri).setDirectory(file).setBranch(branch)
					.setCredentialsProvider(credentialsProvider).call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return file;
	}

	public static void gitCheckout(File repoDir, String version) {
		File RepoGitDir = new File(repoDir.getAbsolutePath() + File.separator + ".git");
		if (!RepoGitDir.exists()) {
			logger.error("Error! Not Exists : " + RepoGitDir.getAbsolutePath());
		} else {
			Repository repo = null;
			Git git = null;
			try {
				repo = new FileRepository(RepoGitDir.getAbsolutePath());
				git = new Git(repo);
				CheckoutCommand checkout = git.checkout();
				checkout.setName(version);
				checkout.call();
				logger.info("Checkout to " + version);

				PullCommand pullCmd = git.pull();
				pullCmd.call();

				logger.info("Pulled from remote repository to local repository at " + repo.getDirectory());
			} catch (Exception e) {
				logger.error(e.getMessage() + " : " + RepoGitDir.getAbsolutePath());
			} finally {
				if (git != null) {
					git.close();
				}
				if (repo != null) {
					repo.close();
				}
			}
		}
	}

	
	
	
	
	
	
	

	public static void gitShowStatus(File repoDir) {
		
		Status status = null;
	
			Git git = null;
			try {
			
				git = Git.open(repoDir);
				status = git.status().call();
				logger.info("Git Change: " + status.getChanged());
				logger.info("Git Modified: " + status.getModified());
				logger.info("Git UncommittedChanges: " + status.getUncommittedChanges());
				logger.info("Git Untracked: " + status.getUntracked());
			} catch (Exception e) {
				logger.error(e.getMessage() + " : " + repoDir.getAbsolutePath());
			} finally {
				if (git != null) {
					git.close();
				}
				
			}
		
		
	}

	
	
	
	public static PullResult gitPull(File repoDir, String branch, final String passphrase, final String pathPrivateKey, final String pathPubKey) {
		File RepoGitDir = new File(repoDir.getAbsolutePath() + File.separator + ".git");
		PullResult pr = null;
		if (!RepoGitDir.exists()) {
			logger.error("Error! Not Exists : " + RepoGitDir.getAbsolutePath());
		} else {
		
			Git git = null;
			try {
		
				git = Git.open(repoDir);
				PullCommand pullCmd = git.pull();
				SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
					@Override
					protected void configure(OpenSshConfig.Host host, Session session) {
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						session.setConfig(config);
					}

					@Override
					protected JSch createDefaultJSch(FS fs) throws JSchException {
						JSch.setConfig("StrictHostKeyChecking", "no");
						JSch defaultJSch = super.createDefaultJSch(fs);
						try {
							defaultJSch.addIdentity(pathPrivateKey, pathPubKey, passphrase.getBytes("UTF-8"));
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error(e.getMessage(), e);
						}
						return defaultJSch;
					}

				};
				
			
				pr = pullCmd.setRemoteBranchName(branch).setTransportConfigCallback(transport -> {
					SshTransport sshTransport = (SshTransport) transport;
					sshTransport.setSshSessionFactory(sshSessionFactory);
				}).call();

				logger.info("Pulled from remote repository to local repository at " + repoDir.getAbsolutePath());
			} catch (Exception e) {
				logger.error(e.getMessage() + " : " + RepoGitDir.getAbsolutePath());
			} finally {
				if (git != null) {
					git.close();
				}
			}

		}
		return pr;
	}
	
	
	
	
	
	
	public static String commit2localResp(File repoDir, List<String> fileNames, String msg) {
		List<DiffEntry> diffEntries;
		Git git = null;
		try {
		
			git = Git.open(repoDir);
			diffEntries = git.diff().setShowNameAndStatusOnly(true).call();

			if (diffEntries == null || diffEntries.size() == 0) {
				logger.warn("there no changes files in repository :" + repoDir.getAbsolutePath());
				return null;
			}

		
			AddCommand addCmd = git.add();
			for (String file : fileNames) {
				logger.info("git add file:{}", file);
				addCmd.addFilepattern(file);
			}

			addCmd.call();
			CommitCommand commitCmd = git.commit();
			for (String file : fileNames) {
				commitCmd.setOnly(file);
			}
			RevCommit revCommit = commitCmd.setMessage(msg).call();
			return revCommit.getName();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage(), e);
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage(), e);
		}finally {
			if (git != null) {
				git.close();
			}
		}

		return null;
	
	}
	

	
	
	
	
	
	
	public static String commit2localResp(File repoDir, String msg) {

		
		List<DiffEntry> diffEntries;
		Git git = null;
		try {
		
			git = Git.open(repoDir);
			diffEntries = git.diff().setShowNameAndStatusOnly(true).call();

			if (diffEntries == null || diffEntries.size() == 0) {
				logger.warn("there no changes files in repository :" + repoDir.getAbsolutePath());
				return null;
			}

			List<String> updateFiles = new ArrayList<String>();
			ChangeType changeType;
			for (DiffEntry entry : diffEntries) {
				changeType = entry.getChangeType();
				switch (changeType) {
				case ADD:
					updateFiles.add(entry.getNewPath());
					break;
				case COPY:
					updateFiles.add(entry.getNewPath());
					break;
				case DELETE:
					updateFiles.add(entry.getOldPath());
					break;
				case MODIFY:
					updateFiles.add(entry.getOldPath());
					break;
				case RENAME:
					updateFiles.add(entry.getNewPath());
					break;
				}
			}
		
			AddCommand addCmd = git.add();
			for (String file : updateFiles) {
				logger.info("git add file:{}", file);
				addCmd.addFilepattern(file);
			}

			addCmd.call();
			CommitCommand commitCmd = git.commit();
			for (String file : updateFiles) {
				commitCmd.setOnly(file);
			}
			RevCommit revCommit = commitCmd.setMessage(msg).call();
			return revCommit.getName();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage(), e);
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage(), e);
		}finally {
			if (git != null) {
				git.close();
			}
		}

		return null;
	}


	
	
	public static Iterable<PushResult> gitPush(File repoDir, String branch,final String passphrase, final String pathPrivateKey, final String pathPubKey) {
		File RepoGitDir = new File(repoDir.getAbsolutePath() + File.separator + ".git");
		 Iterable<PushResult> pr = null;
		if (!RepoGitDir.exists()) {
			logger.error("Error! Not Exists : " + RepoGitDir.getAbsolutePath());
		} else {
		
			Git git = null;
			try {
		
				git = Git.open(repoDir);
				PushCommand pushCmd = git.push();
				SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
					@Override
					protected void configure(OpenSshConfig.Host host, Session session) {
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						session.setConfig(config);
					}

					@Override
					protected JSch createDefaultJSch(FS fs) throws JSchException {
						JSch.setConfig("StrictHostKeyChecking", "no");
						JSch defaultJSch = super.createDefaultJSch(fs);
						try {
							defaultJSch.addIdentity(pathPrivateKey, pathPubKey, passphrase.getBytes("UTF-8"));
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error(e.getMessage(), e);
						}
						return defaultJSch;
					}

				};
				
			
				pr = pushCmd.setTransportConfigCallback(transport -> {
					SshTransport sshTransport = (SshTransport) transport;
					sshTransport.setSshSessionFactory(sshSessionFactory);
				}).call();
				Iterator<PushResult> iterator = pr.iterator();
				while(iterator.hasNext()) {
					PushResult purt = iterator.next();
					Iterator<RemoteRefUpdate> updates = purt.getRemoteUpdates().iterator();
					while(updates.hasNext()) {
						RemoteRefUpdate update = updates.next();
					logger.info(update.getRemoteName()+":"+update.getStatus().name());
					}
					
				}
				

				logger.info("Pushed from local repository to remote repository at " + repoDir.getAbsolutePath());
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			} finally {
				if (git != null) {
					git.close();
				}
			}

		}
   return pr;
	}
	

}
