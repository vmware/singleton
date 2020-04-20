/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.translation.dao;

import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.l10n.translation.dto.ComponentMessagesDTO;

/**
 * For single component translation file handle
 */
public interface SingleComponentDao{

    /**
     * Write the translation to local bundle
     *
     * @param componentMessagesDTO Translation object,this object content will be written into file
     * @return if success return true, otherwise false
     */
    public boolean writeLocalTranslationToFile(ComponentMessagesDTO componentMessagesDTO);

    /**
     * Get translation data from local file
     *
     * @param t The generic object
     * @return T
     */
    public ComponentMessagesDTO getLocalTranslationFromFile(ComponentMessagesDTO componentMessagesDTO) throws L10nAPIException;
}
