/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.fetcher.monitor;

import com.vmware.vip.common.constants.ConstantsJob;
import com.vmware.vip.common.i18n.dto.MultiComponentsDTO;

/**
 * The class represents a update thread.
 * At present, this code is not used
 */
public class UpdateThread extends Thread {
    private static boolean flag = true;
    synchronized public void run() {
        MultiComponentsDTO syncObject = new MultiComponentsDTO();
        synchronized (syncObject) {
           
            while (flag) {
                try {
                    syncObject.wait(ConstantsJob.CHECK_TRANSLATION_TIME_HOUR);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
