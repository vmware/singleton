/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.translation.service;

import java.util.List;

import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.l10n.translation.dto.ComponentMessagesDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.TranslationDTO;

/**
 * This class for vIP-Server translation update
 */
public interface TranslationSyncServerService {

    /**
     * Update translation,this is used for on premise environment
     *
     * @param componentMessagesDTO A ComponentMessagesDTO object
     * @return
     */
    public List<TranslationDTO> updateBatchTranslation(List<ComponentMessagesDTO> componentMessagesDTOList) throws L10nAPIException;
}
