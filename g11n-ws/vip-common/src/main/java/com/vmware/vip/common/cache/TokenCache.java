/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

/*
 
package com.vmware.vip.common.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.vmware.vip.common.constants.ConstantsKeys;

*//**
 * The class represents a token cache
 *
 *//*
public class TokenCache {

    private Cache tokenCache = null;

    *//**
     * Initializes a newly cache object so that it represents a token cache which is define in
     * ehcache.xml.
     *//*
    public TokenCache() {
        tokenCache = EhCacheManager.getInstance().getCacheByName(ConstantsKeys.TOKEN_CACHE_NAME);
    }

    *//**
     * add a token value to cache without version
     *
     * @param key the key for cached object
     * @param value the cached content
     *//*
    public void addToken(String key, String value) {
        Element element = new Element(key, value);
        tokenCache.put(element);
    }

    *//**
     * add a token value to cache with version
     *
     * @param key the key for cached object
     * @param value the cached content
     * @param version the token's version
     *//*
    public void addToken(String key, String value, long version) {
        Element element = new Element(key, value, version);
        tokenCache.put(element);
    }

    *//**
     * get token's version by key from the cache
     *
     * @param key the key of cached object
     * @return version of the token
     *//*
    public Long getTokenVersion(String key) {
        Element element = tokenCache.get(key);
        if (null == element) {
            return null;
        }
        return element.getVersion();
    }

    *//**
     * get token by key from the cache
     *
     * @param key the key of cached object
     * @return token in the cache
     *//*
    public String getToken(String key) {
        Element element = tokenCache.get(key);
        if (null == element) {
            return null;
        }
        Object tokenObject = element.getObjectValue();
        return tokenObject == null ? null : String.valueOf(tokenObject);
    }

}
*/

