/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.mt;

import com.vmware.vip.common.i18n.dto.StringBasedDTO;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;

/**
 * This class handles the translation by single component.
 *
 */
public interface IMTService {

	/**
	 * get translation of one component
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
	public ComponentMessagesDTO getComponentMTTranslation(
			ComponentMessagesDTO componentMessagesDTO) throws L3APIException;

	/**
	 * get one string's MT translation
	 * 
	 * @param comDTO
	 * @param key
	 * @param source
	 * @return
	 * @throws L3APIException
	 */
	public StringBasedDTO getStringMTTranslation(ComponentMessagesDTO comDTO,
			String key, String source) throws L3APIException;
}
