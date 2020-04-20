/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service;

import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;

/**
 * This class processes strings which need to translate.
 */
public interface SourceService {
    /**
     * Cache strings to be translated.
     * 
     * @param stringSourceDTO the object which packages source
     * @return cache result, true represents success and false represents failed.
     */
    public boolean cacheSource(StringSourceDTO stringSourceDTO)  throws L10nAPIException;
}
