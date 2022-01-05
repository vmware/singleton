/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.translation.dao.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.vmware.l10n.translation.dao.SingleComponentDao;
import com.vmware.l10n.translation.dto.ComponentMessagesDTO;
import com.vmware.l10n.translation.readers.LocalJSONReader;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.constants.TranslationQueryStatusType;
import com.vmware.vip.common.exceptions.VIPResourceOperationException;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.CreationDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.TranslationDTO;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.utils.JSONUtils;
import com.vmware.vip.common.utils.SortJSONUtils;

/**
 * This java class is used to handle translation bundle file or translation cache for single component
 */
@Repository
@Profile(value="bundle")
public class LocalSingleComponentDaoImpl implements SingleComponentDao {


	@Value("${translation.bundle.file.basepath}")
	private String basePath;

    private static Logger LOGGER = LoggerFactory.getLogger(LocalSingleComponentDaoImpl.class);



    /**
     * Get translation from local running environment, it maybe a jar, maybe a war
     * @param componentMessagesDTO
     * @return ComponentMessagesDTO object
     * @see com.vmware.vip.core.translation.dao.BaseComponentDao#getLocalTranslationFromFile(java.lang.Object)
     */
    @Override
    public ComponentMessagesDTO getTranslationFromFile(ComponentMessagesDTO componentMessagesDTO) throws L10nAPIException{
        LOGGER.info("[getLocalTranslation]");
        String result = "";
        String component = componentMessagesDTO.getComponent();
        if (StringUtils.isEmpty(component)) {
            component = ConstantsFile.DEFAULT_COMPONENT;
        }
        String filepath = ConstantsFile.L10N_BUNDLES_PATH
                + ResourceFilePathGetter.getProductVersionConcatName(componentMessagesDTO) + ConstantsChar.BACKSLASH
                + component + ConstantsChar.BACKSLASH
                + ResourceFilePathGetter.getLocalizedJSONFileName(componentMessagesDTO.getLocale());
        String defaultFilePath = filepath.substring(0,
                filepath.lastIndexOf(ConstantsFile.LOCAL_FILE_SUFFIX)+8)+ConstantsChar.UNDERLINE+ ConstantsUnicode.EN
                + ConstantsFile.FILE_TPYE_JSON;
        LOGGER.info("Read content from file: {}",  basePath);
        if (new File(basePath + filepath).exists()) {
            componentMessagesDTO.setStatus("Translation"
                    + TranslationQueryStatusType.FileFound.toString());
            result = new LocalJSONReader().readLocalJSONFile(basePath + filepath);
        } else {
            componentMessagesDTO.setStatus("Translation"
                    + TranslationQueryStatusType.FileNotFound.toString());
            result = new LocalJSONReader().readLocalJSONFile(basePath + defaultFilePath);
        }
        if (StringUtils.isEmpty(result)) {
            componentMessagesDTO.setMessages(result);
            componentMessagesDTO.setStatus(TranslationQueryStatusType.ComponentNotFound.toString());
            return componentMessagesDTO;
        }
        SingleComponentDTO caseComponentMessagesDTO = new SingleComponentDTO();

        try {
            caseComponentMessagesDTO = SingleComponentDTO.getSingleComponentDTOWithLinkedMessages(result);
            caseComponentMessagesDTO.setProductName(componentMessagesDTO.getProductName());
            caseComponentMessagesDTO.setVersion(componentMessagesDTO.getVersion());

            caseComponentMessagesDTO.setStatus(componentMessagesDTO.getStatus());
        } catch (ParseException e){
        	throw new L10nAPIException("Parse json failed.", e);
        }
        ComponentMessagesDTO msgDTO = new ComponentMessagesDTO();
        BeanUtils.copyProperties(caseComponentMessagesDTO, msgDTO);
        return msgDTO;
    }

    /**
     * Write the translation to local bundle file
     *
     * @param componentMessagesDTO Translation object,this object contents will be written into bundle file
     * @return if success return true, otherwise false
     */
	
	@Override
	public boolean writeTranslationToFile(ComponentMessagesDTO componentMessagesDTO) {
		String component = componentMessagesDTO.getComponent();
		if (StringUtils.isEmpty(component)) {
			component = ConstantsFile.DEFAULT_COMPONENT;
		}
		String filepath = ConstantsFile.L10N_BUNDLES_PATH
				+ ResourceFilePathGetter.getProductVersionConcatName(componentMessagesDTO) + ConstantsChar.BACKSLASH + component + ConstantsChar.BACKSLASH
				+ ResourceFilePathGetter.getLocalizedJSONFileName(componentMessagesDTO.getLocale());
		File targetFile = new File(basePath + filepath);
		if (targetFile.exists()) {
			LOGGER.info("The bunlde file path {}{} is found, update the bundle file.", basePath, filepath );
			try {
				SortJSONUtils.writeJSONObjectToJSONFile(basePath + filepath, componentMessagesDTO);
				LOGGER.info("The bunlde file {}{} have updated sucessfully", basePath, filepath);
				return true;
			} catch (VIPResourceOperationException e) {
				
				LOGGER.error(e.getMessage(), e);
				return false;
			}
		} else {
			LOGGER.info("The bunlde file path {}{} is not found, cascade create the dir,add new bundle file ", basePath, filepath);
			try {
				FileUtils.write(targetFile, "","UTF-8",true);
				SortJSONUtils.writeJSONObjectToJSONFile(basePath + filepath, componentMessagesDTO);
				LOGGER.info("The bunlde file {}{} have updated sucessfully", basePath, filepath);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				return false;
			}
			return true;
		}
	}

	@Override
	public boolean lockFile(ComponentMessagesDTO componentMessagesDTO) {
		return true;
	}

	@Override
	public void unlockFile(ComponentMessagesDTO componentMessagesDTO) {
		// There is only one thread when using local bundle
	}

	@Override
	public void saveCreationInfo(UpdateTranslationDTO updateTranslationDTO) {
		UpdateTranslationDataDTO transData = updateTranslationDTO.getData();

		String opId = "";
		CreationDTO creationDTO = transData.getCreation();
		if (creationDTO != null) {
			opId = creationDTO.getOperationid();
		}

		String filepath = basePath + ConstantsFile.L10N_BUNDLES_PATH
				+ ResourceFilePathGetter.getProductVersionConcatName(transData) + ConstantsChar.BACKSLASH
				+ ConstantsFile.CREATION_INFO;

		// read
		Map<String, Object> json = JSONUtils.getMapFromJsonFile(filepath);

		// set
		List<TranslationDTO> groups = transData.getTranslation();
		for (int i = 0; i < groups.size(); i++) {
			json.put(groups.get(i).getLocale(), opId);
		}
		File pareFile = new File(filepath);
		if (!pareFile.getParentFile().exists()) {
			pareFile.getParentFile().mkdirs();
		}
		// write
		JSONUtils.writeMapToJsonFile(filepath, json);
	}
}

