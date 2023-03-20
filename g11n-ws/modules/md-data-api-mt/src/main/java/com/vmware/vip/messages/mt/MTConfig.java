/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.mt;

import java.util.HashMap;

public class MTConfig {

	public static String MTSERVER;
	public static String KEY;
	public static String TRANSLATED_MAX;
	public static String TRANSLATECOUNT;
	public static String REGION;

	private MTConfig() {}
	
	public static void setMTSERVER(String mTSERVER) {
		MTSERVER = mTSERVER;
	}

	public static String getMTSERVER() {
		return MTSERVER;
	}

	public static void setKEY(String kEY) {
		KEY = kEY;
	}

	public static void setTRANSLATED_MAX(String tRANSLATED_MAX) {
		TRANSLATED_MAX = tRANSLATED_MAX;
	}

	public static void setTRANSLATECOUNT(String tRANSLATECOUNT) {
		TRANSLATECOUNT = tRANSLATECOUNT;
	}

	public static final HashMap<String, Integer> TRANSLATED_CACHE = new HashMap<String, Integer>();

	public synchronized static void updateTranslationCache(String key, int size) {
		if (isTimeOut()) {
			MTConfig.TRANSLATED_CACHE.clear();
			MTConfig.setCurrentMills(System.currentTimeMillis());
		}
		Integer i = MTConfig.TRANSLATED_CACHE.get(key);
		Integer n = Integer.valueOf((i == null ? 0 : i.intValue()) + size);
		MTConfig.TRANSLATED_CACHE.put(key, n);
	}

	public static boolean isTranslatedFull(String key) {
		boolean f = false;
		Integer v = TRANSLATED_CACHE.get(key);
		int vol = Integer.parseInt(TRANSLATED_MAX);
		if (v != null && v.intValue() > vol) {
			f = true;
		}
		return f;
	}

	private static long currentMills = System.currentTimeMillis();
	private static long hr24 = 86400000;

	public synchronized static long getCurrentMills() {
		return currentMills;
	}

	public synchronized static void setCurrentMills(long c) {
		currentMills = c;
	}

	public static boolean isTimeOut() {
		return (System.currentTimeMillis() - getCurrentMills()) > hr24;
	}

	public static void setREGION(String rEGION) {
		REGION = rEGION;
	}
}
