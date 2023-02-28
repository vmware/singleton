/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.translation.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.l10n.translation.dto.ComponentMessagesDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO;
import com.vmware.vip.common.l10n.exception.L10nAPIException;

/**
 * For single component translation file handle
 */
public interface SingleComponentDao {

	/**
	 * Write the translation to bundle
	 *
	 * @param componentMessagesDTO Translation object,this object content will be written into file
	 * @return if success return true, otherwise false
	 * @throws JsonProcessingException
	 */
	public boolean writeTranslationToFile(ComponentMessagesDTO componentMessagesDTO) throws JsonProcessingException;

	/**
	 * Get translation data from file
	 *
	 * @param t The generic object
	 * @return T
	 */
	public ComponentMessagesDTO getTranslationFromFile(ComponentMessagesDTO componentMessagesDTO) throws L10nAPIException;

	void saveCreationInfo(UpdateTranslationDTO updateTranslationDTO);

	public boolean lockFile(ComponentMessagesDTO componentMessagesDTO);

	public void unlockFile(ComponentMessagesDTO componentMessagesDTO);
}
