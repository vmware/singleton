//Copyright 2019-2020 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.translation.dao.impl;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.amazonaws.SdkClientException;
import com.vmware.l10n.translation.dao.SingleComponentDao;
import com.vmware.l10n.translation.dto.ComponentMessagesDTO;
import com.vmware.l10n.utils.S3Util;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.constants.TranslationQueryStatusType;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.exception.L10nAPIException;


@Repository
public class S3SingleComponentDaoImpl implements SingleComponentDao {
	private static Logger logger = LoggerFactory.getLogger(S3SingleComponentDaoImpl.class);

	@Value("${translation.bundle.file.basepath}")
	private String basePath;

	@Autowired
	private S3Util s3util;

	@Override
	public ComponentMessagesDTO getLocalTranslationFromFile(ComponentMessagesDTO componentMessagesDTO)
			throws L10nAPIException {
		logger.info("[get Translation from S3]");

		ComponentMessagesDTO tempDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(componentMessagesDTO, tempDTO);
		String bunldeString;
		try {
			if (s3util.isBundleExist(basePath, tempDTO)) {
				tempDTO.setStatus("Translation" + TranslationQueryStatusType.FileFound.toString());
				bunldeString = s3util.readBundle(basePath, componentMessagesDTO);
			} else {
				tempDTO.setStatus("Translation" + TranslationQueryStatusType.FileNotFound.toString());
				tempDTO.setLocale(ConstantsUnicode.EN);
				bunldeString = s3util.readBundle(basePath, tempDTO);
				tempDTO.setLocale(componentMessagesDTO.getLocale());
			}
			if (StringUtils.isEmpty(bunldeString)) {
				tempDTO.setMessages(bunldeString);
				tempDTO.setStatus(TranslationQueryStatusType.ComponentNotFound.toString());
				return tempDTO;
			}
		} catch (SdkClientException e) {
			throw new L10nAPIException("Connecting S3 failed.", e);
		}

		SingleComponentDTO caseComponentMessagesDTO;
		try {
			caseComponentMessagesDTO = SingleComponentDTO.getSingleComponentDTOWithLinkedMessages(bunldeString);
			caseComponentMessagesDTO.setProductName(tempDTO.getProductName());
			caseComponentMessagesDTO.setVersion(tempDTO.getVersion());
			caseComponentMessagesDTO.setStatus(tempDTO.getStatus());
		} catch (ParseException e) {
			throw new L10nAPIException("Parse json failed.", e);
		}

		ComponentMessagesDTO msgDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(caseComponentMessagesDTO, msgDTO);
		return msgDTO;

	}

	@Override
	public boolean writeLocalTranslationToFile(ComponentMessagesDTO componentMessagesDTO) {
		try {
			if (s3util.isBundleExist(basePath, componentMessagesDTO)) {
				logger.info("The bunlde file is found, update the bundle file.");
			} else {
				logger.info("The bunlde file is not found, cascade create the dir,add new bundle file ");
			}
		} catch (SdkClientException e) {
			logger.error(e.getMessage(),e);
		}
		
		return s3util.writeBundle(basePath, componentMessagesDTO);
	}
}
