//Copyright 2019-2020 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.translation.dao.impl;

import java.io.File;

import javax.annotation.PostConstruct;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.l10n.translation.dao.SingleComponentDao;
import com.vmware.l10n.translation.dto.ComponentMessagesDTO;
import com.vmware.l10n.utils.S3Util;
import com.vmware.l10n.utils.S3Util.Locker;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.constants.TranslationQueryStatusType;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.exception.L10nAPIException;


@Repository
@Profile("s3")
public class S3SingleComponentDaoImpl implements SingleComponentDao {
	private static Logger logger = LoggerFactory.getLogger(S3SingleComponentDaoImpl.class);

	@Value("${translation.bundle.file.basepath}")
	private String basePath;

	@Autowired
	private S3Util s3util;

	@PostConstruct
	private void init() {
		if (basePath.startsWith("/")) {
			basePath = basePath.substring(1);
		}
		if (!basePath.isEmpty() && !basePath.endsWith(ConstantsChar.BACKSLASH)) {
			basePath += ConstantsChar.BACKSLASH;
		}
		basePath += ConstantsFile.L10N_BUNDLES_PATH;
		basePath = basePath.replace("\\", ConstantsChar.BACKSLASH);
	}

	/**
	 * Get translation data from file
	 *
	 * @param componentMessagesDTO Specify the bundle file to get.
	 * @return componentMessagesDTO
	 */
	@Override
	public ComponentMessagesDTO getTranslationFromFile(ComponentMessagesDTO componentMessagesDTO)
			throws L10nAPIException {
		logger.debug("[get Translation from S3]");

		String bunldeString;
		if (s3util.isBundleExist(basePath, componentMessagesDTO)) {
			componentMessagesDTO.setStatus("Translation" + TranslationQueryStatusType.FileFound.toString());
			bunldeString = s3util.readBundle(basePath, componentMessagesDTO);
		} else {
			componentMessagesDTO.setStatus("Translation" + TranslationQueryStatusType.FileNotFound.toString());
			ComponentMessagesDTO tempDTO = new ComponentMessagesDTO();
			BeanUtils.copyProperties(componentMessagesDTO, tempDTO);
			tempDTO.setLocale(ConstantsUnicode.EN);
			bunldeString = s3util.readBundle(basePath, tempDTO);
		}
		if (StringUtils.isEmpty(bunldeString)) {
			componentMessagesDTO.setStatus(TranslationQueryStatusType.ComponentNotFound.toString());
			return componentMessagesDTO;
		}

		SingleComponentDTO caseComponentMessagesDTO;
		try {
			caseComponentMessagesDTO = SingleComponentDTO.getSingleComponentDTOWithLinkedMessages(bunldeString);
			caseComponentMessagesDTO.setProductName(componentMessagesDTO.getProductName());
			caseComponentMessagesDTO.setVersion(componentMessagesDTO.getVersion());
			caseComponentMessagesDTO.setStatus(componentMessagesDTO.getStatus());
		} catch (ParseException e) {
			throw new L10nAPIException("Parsing json failed.", e);
		}

		ComponentMessagesDTO msgDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(caseComponentMessagesDTO, msgDTO);
		return msgDTO;
	}

	@Override
	public boolean writeTranslationToFile(ComponentMessagesDTO componentMessagesDTO) throws JsonProcessingException {
		if (s3util.isBundleExist(basePath, componentMessagesDTO)) {
			logger.debug("The bunlde file is found, update the bundle file.");
		} else {
			logger.debug("The bunlde file is not found, cascade create the dir,add new bundle file ");
		}

		return s3util.writeBundle(basePath, componentMessagesDTO);
	}

	@Override
	public boolean lockFile(ComponentMessagesDTO componentMessagesDTO) {
		Locker locker = s3util.new Locker(basePath, componentMessagesDTO);
		return locker.lockFile();
	}

	@Override
	public void unlockFile(ComponentMessagesDTO componentMessagesDTO) {
		Locker locker = s3util.new Locker(basePath, componentMessagesDTO);
		locker.unlockFile();
	}
}
