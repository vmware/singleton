/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.multcomponent;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import com.vmware.vip.core.messages.exception.L3APIException;


/**
 * This class handles the translation by single component.
 *
 */
public interface IMultComponentService {

    /**
     * Get translation of multiple components.
     * <p>
     * If the translation is cached, get it directly;
     * otherwise will get it from local bundle.
     * 
     * @param translationDTO
     *         The object of TranslationDTO, containing the information of multiple components for translate.
     * @return TranslationDTO
     *         The object of TranslationDTO, containing translation.
     */
    public TranslationDTO getMultiComponentsTranslation(TranslationDTO translationDTO)  throws L3APIException;
    
    
    public List<ReadableByteChannel> getTranslationChannels(String productName, String version, List<String> components,
			List<String> locales) throws L3APIException;
    
}
