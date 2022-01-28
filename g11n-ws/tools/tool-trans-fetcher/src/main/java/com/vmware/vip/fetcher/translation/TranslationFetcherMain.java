/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.fetcher.translation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.vmware.vip.common.exceptions.VIPResourceOperationException;
import com.vmware.vip.common.i18n.resourcefile.ResourceFileWritter;
import com.vmware.vip.common.l10n.source.util.IOUtil;
import com.vmware.vip.common.utils.HTTPRequester;

/**
 * The class represents to fetch the translation from remote server during building process
 */
public class TranslationFetcherMain {

    /**
     * Fetch translation from remote server
     *
     * @param args The parameters from build commands,
     *        include two,the one is productName,the other one is version
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.print("Argument count is not correct." + " Supposed to be less than 5 arguments.");
            return;
        }
        InputStream inputStream = null;
        try {
            String url = "https://10.117.171.100/bundles/" + args[0] + "/" + args[1]
                    + "/messages.json";
            // String url = "https://10.117.171.100/bundles/devCenter/1.0.0/messages.json";
            inputStream = HTTPRequester.createConnection(new URL(url)).getInputStream();
            String remoteRusult = IOUtils.toString(inputStream, "utf-8");
            ResourceFileWritter.writeStrToMultiJSONFiles(remoteRusult);
        } catch (VIPResourceOperationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeInputStream(inputStream);
        }
    }
}
