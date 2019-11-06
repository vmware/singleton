/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.Map;
import java.util.Set;

public interface Cache {

    /**
     * get a component's strings by key
     * 
     * @param key
     * @return map of all strings under the component
     */
    public Map<String, String> get(String key);

    /**
     * put strings to cache by key
     * 
     * @param key
     * @param map
     * @return false if failed to put
     */
    public boolean put(String key, Map<String, String> map);

    /**
     * remove a component from cache by key
     * 
     * @param key
     * @return false if failed to remove
     */
    public boolean remove(String key);

    /**
     * clear all components in the cache
     * 
     * @return false if failed to clear
     */
    public boolean clear();

    /**
     * get count of current cached components
     * 
     * @return count of cached components
     */
    public int size();

    /**
     * get the set of cached keys
     * 
     * @return set of cached keys
     */
    public Set<String> keySet();

    /**
     * get expired time
     * 
     * @return long time
     */
    public long getExpiredTime();

    /**
     * set expired time
     * 
     * @param millis
     */
    public void setExpiredTime(long millis);

    /**
     * get time of last clean
     * 
     * @return long time
     */
    public long getLastClean();

    /**
     * set time of last clean
     * 
     * @param millis
     */
    public void setLastClean(long millis);

    /**
     * set the cache's capacity by specifying the count of components
     * 
     * @param capacityX
     */
    public void setXCapacity(int capacityX);

    /**
     * get the cache capacity
     * 
     * @return cache capacity
     */
    public int getXCapacity();

    /**
     * check if the cache is expired
     * 
     * @return false if expired
     */
    public boolean isExpired();

    /**
     * get a id of translation drop
     * 
     * @return a drop id
     */
    public String getDropId();

}
