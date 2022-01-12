/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.singlecomponent;

import org.json.simple.parser.ParseException;

import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.messages.data.dao.exception.DataException;

/**
 * This class handles the translation by single component.
 *
 */
public interface IOneComponentService {
	/**
	 * Get translation of one component from cache and disk
	 * <p>
	 * If the translation is cached, get it directly; otherwise will get it from
	 * local bundle.
	 *
	 * @param componentMessagesDTO
	 *            the object of ComponentMessagesDTO, containing component's
	 *            information for translate.
	 * @return ComponentMessagesDTO the object of ComponentMessagesDTO,
	 *         containing translation.
	 */
	public ComponentMessagesDTO getComponentTranslation(
			ComponentMessagesDTO componentMessagesDTO) throws L3APIException;

	/**
	 * get translation from disk
	 * 
	 * @param componentMessagesDTO
	 * @return
	 * @throws ParseException
	 * @throws DataException
	 */
	public ComponentMessagesDTO getTranslationFromDisk(
			ComponentMessagesDTO componentMessagesDTO) throws ParseException,
			DataException;
}
