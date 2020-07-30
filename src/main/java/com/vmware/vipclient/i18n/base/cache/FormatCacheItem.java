/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import com.vmware.vipclient.i18n.VIPCfg;

public class FormatCacheItem implements CacheItem {
	public FormatCacheItem() {
		
	}

	private String etag;
	private long timestamp;
	private Long maxAgeMillis = 86400000l;

	public synchronized String getEtag() {
		return etag;
	}

	public synchronized void setEtag(String etag) {
		this.etag = etag;
	}

	public synchronized long getTimestamp() {
		return timestamp;
	}

	public synchronized void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public synchronized Long getMaxAgeMillis() {
		return maxAgeMillis;
	}

	public synchronized void setMaxAgeMillis(Long maxAgeMillis) {
		this.maxAgeMillis = maxAgeMillis;
	}

	public boolean isExpired() {
		// If offline mode only, cache never expires.
		if (VIPCfg.getInstance().getVipServer() == null) {
			return false;
		}
		// If maxAgeFromConfig is present, it means it is using the old way
		// of caching expiration, so do not expire individual CacheItem object
		if (VIPCfg.getInstance().getCacheExpiredTime() != 0) {
			return false;
		}

		Long responseTimeStamp = this.getTimestamp();
		if (responseTimeStamp == null) {
			return true;
		}

		Long maxAgeResponse = this.getMaxAgeMillis();
		if (maxAgeResponse != null) {
			maxAgeMillis = maxAgeResponse;
		}

		return System.currentTimeMillis() - responseTimeStamp > maxAgeMillis;
	}
}