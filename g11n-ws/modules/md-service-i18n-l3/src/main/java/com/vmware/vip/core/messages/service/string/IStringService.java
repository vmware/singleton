/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.string;

import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.dto.StringBasedDTO;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;

/**
 * This class handles the translation by String.
 *
 */
public interface IStringService {
    /**
     * Get a string of translation.
     * <p>
     * If the translation is cached, get it directly;
     * otherwise get it from local bundle.
     *
     * @param componentMessagesDTO
     *        the object of ComponentMessagesDTO.
     * @param key
     *        The unique identify for source in component's resource file.
     * @param source
     *        The English string which need translate.
     * @return TranslationDTO
     *         a DTO object of StringBasedDTO, containing translation.
     */
    public StringBasedDTO getStringTranslation(ComponentMessagesDTO componentMessagesDTO,String key,String source) throws L3APIException;

    /**
     * Get multiple keys' translations.
     * <p>
     * If the translation is cached, get it directly;
     * otherwise get it from local bundle.
     *
     * @param c
     * @param keyArr
     * @return
     * @throws L3APIException
     */
	public SingleComponentDTO getMultKeyTranslation(ComponentMessagesDTO compMsg, String[] keyArr)throws L3APIException;
}
