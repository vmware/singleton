/*
 * Copyright 2019 VMware, Inc.
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

    @SuppressWarnings("static-access")
    synchronized public void run() {
        MultiComponentsDTO syncObject = new MultiComponentsDTO();
        synchronized (syncObject) {
            boolean flag = true;
            while (flag) {
                try {
                    this.sleep(ConstantsJob.CHECK_TRANSLATION_TIME_HOUR);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
