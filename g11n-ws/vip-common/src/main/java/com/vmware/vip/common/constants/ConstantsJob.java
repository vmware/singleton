/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.constants;

/**
 * Constant time setting for Job
 * 
 */
public class ConstantsJob {
    public static final long CHECK_TRANSLATION_TIME_HOUR = 60L * 60 * 1000; // 1 hour
    public static final long CHECK_TRANSLATION_TIME_WEEK =CHECK_TRANSLATION_TIME_HOUR * 24 * 7; // 1 week
    public static final long CHECK_TRANSLATION_TIME_MONTH = CHECK_TRANSLATION_TIME_HOUR * 24 * 30; // 1 month
}
