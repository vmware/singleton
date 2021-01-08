/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.l10n.record.dao.SqlLiteDao;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.utils.SourceUtils;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPResourceOperationException;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.resourcefile.LocalJSONReader;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import com.vmware.vip.common.utils.SortJSONUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Repository
@Profile(value="bundle")
public class LocalSourceDaoImpl implements SourceDao {
    private static Logger LOGGER = LoggerFactory.getLogger(LocalSourceDaoImpl.class);

    @Autowired
    private SqlLiteDao sqlLite;

    /**
     * the path of local resource file,can be configed in spring config file
     **/
    @Value("${source.bundle.file.basepath}")
    private String basepath;

	@Override
	public String getFromBundle(SingleComponentDTO singleComponentDTO) {
		String result = "";
		String component = singleComponentDTO.getComponent();
		if (StringUtils.isEmpty(component)) {
			component = ConstantsFile.DEFAULT_COMPONENT;
			singleComponentDTO.setComponent(component);
		}
		String filepath = ConstantsFile.L10N_BUNDLES_PATH
				+ ResourceFilePathGetter
						.getProductVersionConcatName(singleComponentDTO)
				+ ConstantsChar.BACKSLASH
				+ component
				+ ConstantsChar.BACKSLASH
				+ ResourceFilePathGetter
						.getLocalizedJSONFileName(ConstantsKeys.LATEST);
		LOGGER.info("Read content from file: {}{}",  basepath, filepath);

		if (new File(basepath + filepath).exists()) {
			result = new LocalJSONReader().readLocalJSONFile(basepath
					+ filepath);
		}
		return result;
	}

	
	@Override
	public boolean updateToBundle(ComponentMessagesDTO componentMessagesDTO) {
		LOGGER.info("[updateLocalTranslationToFile]");
		String component = componentMessagesDTO.getComponent();
		if (StringUtils.isEmpty(component)) {
			component = ConstantsFile.DEFAULT_COMPONENT;
			componentMessagesDTO.setComponent(component);
		}
		String filepath = ConstantsFile.L10N_BUNDLES_PATH
				+ ResourceFilePathGetter
						.getProductVersionConcatName(componentMessagesDTO)
				+ ConstantsChar.BACKSLASH
				+ component
				+ ConstantsChar.BACKSLASH
				+ ResourceFilePathGetter
						.getLocalizedJSONFileName(componentMessagesDTO
								.getLocale());
		LOGGER.info("Read content from file: {}{}",  basepath, filepath);

		File targetFile = new File(basepath + filepath);
		if (targetFile.exists()) {
			LOGGER.info("The bunlde file path {}{} is found, update the bundle file.", basepath, filepath);
			try {
				String existingBundle =  new LocalJSONReader().readLocalJSONFile(basepath
						+ filepath);
				SingleComponentDTO latestDTO = SourceUtils.mergeCacheWithBundle(componentMessagesDTO, existingBundle);
				SortJSONUtils.writeJSONObjectToJSONFile(basepath
						+ filepath, latestDTO);
				sqlLite.updateModifySourceRecord(componentMessagesDTO);
				return true;
			} catch (VIPResourceOperationException e) {
				LOGGER.error(e.getMessage(), e);
				return false;
			}
		} else {
			LOGGER.info("The bunlde file path {}{} is not found, cascade create the dir,add new bundle file ",  basepath, filepath);
			try {
				FileUtils.write(targetFile, "", "UTF-8", true);
				SortJSONUtils.writeJSONObjectToJSONFile(basepath
						+ filepath, componentMessagesDTO);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				return false;
			}
			
			sqlLite.createSourceRecord(componentMessagesDTO);
			
			return true;
		}
	}

}
