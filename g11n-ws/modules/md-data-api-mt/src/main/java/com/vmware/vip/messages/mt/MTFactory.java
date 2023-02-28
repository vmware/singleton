/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vip.messages.mt;

import com.vmware.vip.messages.data.dao.api.IMTProcessor;
import com.vmware.vip.messages.mt.azure.AzureTranslatingProcessor;
import com.vmware.vip.messages.mt.intento.IntentoTranslatingProcessor;
import org.apache.commons.lang3.StringUtils;

/**
 * A factory to create MT processor by the server URL
 */
public class MTFactory {

    private static IMTProcessor processor = null;

    private MTFactory() {

    }

    /**
     * get a mt processor
     *
     * @return
     */
    public static synchronized IMTProcessor getMTProcessor() {
        if (processor != null) {
            return processor;
        }
        String mtServer = MTConfig.getMTSERVER();
        if (!StringUtils.isEmpty(mtServer)) {
            if (mtServer.contains("microsoft")) {
                processor = new AzureTranslatingProcessor();
            } else if (mtServer.contains("inten.to")) {
                processor = new IntentoTranslatingProcessor();
            }
        }
        if (processor == null) {
            processor = new AzureTranslatingProcessor();
        }
        return processor;
    }
}
