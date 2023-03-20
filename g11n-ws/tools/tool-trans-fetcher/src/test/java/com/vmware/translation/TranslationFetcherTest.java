/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.translation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.vmware.vip.common.exceptions.VIPResourceOperationException;
import com.vmware.vip.common.i18n.resourcefile.ResourceFileWritter;
import com.vmware.vip.common.l10n.source.util.IOUtil;
import com.vmware.vip.common.utils.HTTPRequester;

import junit.framework.TestCase;

/**
 * A main method java class for testing translation fetcher
 */
public class TranslationFetcherTest {

    /**
     * JUnit Test
     * Get remote json file and write it to local bundle file
     */
    //@Test
    public void testRemoteServerAvailable() {
        String url = "https://10.117.170.147/bundles/test/1.0.0/messages.json";
        InputStream inputStream = null;
        try {
            inputStream = HTTPRequester.createConnection(new URL(url)).getInputStream();
            String remoteRusult = IOUtils.toString(inputStream, "utf-8");
            TestCase.assertFalse(StringUtils.isEmpty(remoteRusult));
            ResourceFileWritter.writeStrToMultiJSONFiles(remoteRusult);
        } catch (VIPResourceOperationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            TestCase.assertTrue(false);
            e.printStackTrace();
        } finally {
            IOUtil.closeInputStream(inputStream);
        }
    }
}
