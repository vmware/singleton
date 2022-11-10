# CACHE



1. cache config.   

   1.1	if service return max-age , python client will use it as cache expired time.

   ```properties
   cache-control.value=max-age=83600, public
   ```

   1.2	if service no return max-age（old service no support），python client will use yam as cache expired time. default 60s.

   ```yaml
   cache_path: .cache
   cache_expired_time: 4
   ```

   

2. cache effect

   1. expired_time = 10 (from server or yaml)
   2. if python client run as server and not stop forever. it may not use cache_path.
   3. if python client run as script. the cache will clear when script completed,  and run again ,it load cache from cache_path.
   4. if not expired , python return translation from cache. and if expired  from server and update cache_path.

   

3. cache only support online mode ,offline no use cache.

4. (options) server cache update if put request come.