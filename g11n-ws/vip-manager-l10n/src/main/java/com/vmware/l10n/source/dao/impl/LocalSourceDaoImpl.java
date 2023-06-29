/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.dao.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.vmware.l10n.record.model.RecordModel;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.utils.SourceUtils;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPResourceOperationException;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.resourcefile.LocalJSONReader;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import com.vmware.vip.common.utils.SortJSONUtils;

@Repository
@Profile(value="bundle")
public class LocalSourceDaoImpl implements SourceDao {
    private static Logger LOGGER = LoggerFactory.getLogger(LocalSourceDaoImpl.class);

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
			

			
			return true;
		}
	}


	@Override
	public List<RecordModel> getUpdateRecords(String productName, String version, long lastModifyTime) throws L10nAPIException{

		StringBuilder prefix = new StringBuilder();
		prefix.append(basepath);
		prefix.append(ConstantsFile.L10N_BUNDLES_PATH);
		if (!StringUtils.isEmpty(productName)) {
			prefix.append(productName);
			prefix.append(ConstantsChar.BACKSLASH);
		}
		if (!StringUtils.isEmpty(version)) {
			prefix.append(version);
			prefix.append(ConstantsChar.BACKSLASH);
		}

		File targetFile = new File(prefix.toString());
		LOGGER.info("local bundle file base path:{}", targetFile.getAbsolutePath());
		List<RecordModel> records = new ArrayList<RecordModel>();
		String latestJsonFile = ConstantsFile.LOCAL_FILE_SUFFIX+ConstantsChar.UNDERLINE+ConstantsKeys.LATEST+ConstantsFile.FILE_TPYE_JSON;
		try(Stream<Path> sp = Files.walk(targetFile.toPath())){
			sp.filter(Files :: isRegularFile).filter(path -> path.endsWith(latestJsonFile) && (path.toFile().lastModified()>lastModifyTime))
					.forEach(currPath ->{
						File file = currPath.toFile();
						LOGGER.info("Need Update:{}:{}", file.getAbsolutePath(), file.lastModified());
						records.add(SourceUtils.parseKeyStr2Record(file.getAbsolutePath(),this.basepath, file.lastModified()));
					});


		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new L10nAPIException("Local disk bundle can't get update record!");
		}

		return records;
	}

}
