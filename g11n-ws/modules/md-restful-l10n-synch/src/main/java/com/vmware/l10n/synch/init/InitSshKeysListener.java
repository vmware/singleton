/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.synch.init;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.vmware.l10n.synch.schedule.Send2GitSchedule;
import com.vmware.vip.common.constants.ConstantsChar;

@Component
public class InitSshKeysListener implements ApplicationListener<ApplicationReadyEvent> {
	private static Logger logger = LoggerFactory.getLogger(InitSshKeysListener.class);

	@Value("${translation.git.remote.enable}")
	private boolean gitRemoteEnabled;

	@Autowired
	private Send2GitSchedule schedule;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (gitRemoteEnabled) {
			// TODO Auto-generated method stub
			String pubKeyFileName = "sshkey" + ConstantsChar.BACKSLASH + "id_rsa.pub";
			String priKeyFileName = "sshkey" + ConstantsChar.BACKSLASH + "id_rsa";
			InputStream pubIs = InitSshKeysListener.class.getResourceAsStream(ConstantsChar.BACKSLASH + pubKeyFileName);
			InputStream priIs = InitSshKeysListener.class.getResourceAsStream(ConstantsChar.BACKSLASH + priKeyFileName);

			String sshDir = "SSHkey" + File.separator;
			File pubOutput = new File(sshDir + "id_rsa.pub");
			if (!pubOutput.exists()) {
				if (!pubOutput.getParentFile().exists()) {
					pubOutput.getParentFile().mkdirs();
				}

				try {
					pubOutput.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				}
			}

			File privOutput = new File(sshDir + "id_rsa");
			if (!privOutput.exists()) {
				if (!privOutput.getParentFile().exists()) {
					privOutput.getParentFile().mkdirs();
				}
				try {
					privOutput.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				}
			}

			try {
				Files.copy(pubIs, pubOutput.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
			} finally {
				if (pubIs != null) {
					try {
						pubIs.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error(e.getMessage(), e);
					}
				}
			}
			try {
				Files.copy(priIs, privOutput.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
			} finally {
				if (priIs != null) {
					try {
						priIs.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error(e.getMessage(), e);
					}
				}
			}

			String pubabsPath = pubOutput.getAbsolutePath();
			String privabsPath = privOutput.getAbsolutePath();

			schedule.setPubKeyPath(pubabsPath);
			schedule.setPriKeyPath(privabsPath);
			logger.info("the pubkey file path:{}", pubabsPath);
			logger.info("the privatekey file path:{}", privabsPath);

			schedule.initRepos();

		}
	}

}
