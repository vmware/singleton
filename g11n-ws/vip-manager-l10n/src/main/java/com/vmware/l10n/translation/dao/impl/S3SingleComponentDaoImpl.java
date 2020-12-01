//Copyright 2019-2020 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.translation.dao.impl;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
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
@Profile("s3")
public class S3SingleComponentDaoImpl implements SingleComponentDao {
	private static Logger logger = LoggerFactory.getLogger(S3SingleComponentDaoImpl.class);

	@Value("${translation.bundle.file.basepath}")
	private String basePath;

	@Autowired
	private S3Util s3util;

    /**
     * Get translation data from local file
     *
     * @param componentMessagesDTO Specify the bundle file to get.
     * @return componentMessagesDTO
     */
	@Override
	public ComponentMessagesDTO getLocalTranslationFromFile(ComponentMessagesDTO componentMessagesDTO)
			throws L10nAPIException {
		logger.info("[get Translation from S3]");

		
		String bunldeString;
		try {
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
		} catch (SdkClientException e) {
			throw new L10nAPIException("Connecting S3 failed.", e);
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
