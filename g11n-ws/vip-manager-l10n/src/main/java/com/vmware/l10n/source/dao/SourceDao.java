/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.dao;

import java.util.Map;

import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

/**
 * This class handles the source strings, write to local resource file or send
 * them to GRM. The former maybe droped in the future. GRM is a internal tool of
 * VMware which manages products' resource files.
 */
public interface SourceDao {
	/**
	 * Get a component's source strings by reading from english resource file.
	 * 
	 * @param singleComponentDTO
	 *            the object which wraps the base information of a component
	 * @param filePath
	 *            the location where the resource file is placed, can be
	 *            configed in spring config file
	 * @return the content of the resource file
	 */
	public String getFromBundle(SingleComponentDTO singleComponentDTO,
			String filePath);

	/**
	 * Write source strings to local resource file.
	 * 
	 * @param singleComponentDTO
	 *            the object which wraps the content of a component
	 * @param basepath
	 *            the location where the resource file is placed, can be
	 *            configed in spring config file
	 * @return update result, true represents success, false represents failure.
	 */
	public boolean updateToBundle(ComponentMessagesDTO componentMessagesDTO,
			String basepath);

	/**
	 * Send source strings to GRM by component.
	 * 
	 * @param url
	 *            the url of register strings API provided by GRM
	 * @param requestParam
	 *            the request body, it includes 'messages' and 'comments', the
	 *            former represents source strings and the latter represents
	 *            comments for source strings
	 * @return send result, true represents success, false represents failure.
	 */
	public boolean sendToRemote(String url, Map<String, Object> requestParam);

	/*
	 * load the bundle content and merge it with cache.
	 */
	public ComponentMessagesDTO mergeCacheWithBundle(
			ComponentSourceDTO cachedComponentSourceDTO, String bundleJSON);

}
