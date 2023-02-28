# CACHE

测试bundle Cache
latest和en，存在about.title的key不同，其他的key相同

功能实现
1.支持失效时间
2.缓存component+locale的翻译{messages_$locale.json}
3.缓存更新，读取缓存旧值返回，如果缓存过期，触发服务器更新。
4.支持online模式和mixed模式，不支持offline模式

1.配置项
如果服务端接口返回中包含max-age，客户端直接采用max-age作为缓存失效时间
在BOOT-INF\classes\application-bundle.properties文件下修改配置项

```properties
cache-control.value=max-age=83600, public
```

如果服务端没有返回max-age(老服务器可能不支持这个字段)，使用yaml配置缓存过期时间，默认60s。

```yaml
cache_path: .cache
cache_expired_time: 4
```

2.不同模式下规则
offline模式不生效。
online模式生效：未过期，从缓存读取，过期了从服务器读取并且更新缓存。
mixed混合模式生效：
a.服务器获取到，更新缓存，下次从缓存获取
b.服务器未获取到，尝试从缓存读取，缓存失效后在从本地读取。
c.本地获取到的内容，不会更新缓存。

3.缓存更新
a.缓存失效后，下次访问，还会读取缓存旧值，此时缓存会触发服务器请求更新缓存。
b.缓存有效内，即使服务器资源更新，也只能读取缓存旧值。


实际测试：
1.offline模式下，对应的component+locale的翻译只会获取一次，就会缓存下来，即使源文件修改，不过由于不支持cache_path，作为脚本启动时正常的。
2.offline模式，即使配置了`cache_path: .cache`也不会生效

3.online模式下，cache过期后，下次还是返回cache的旧值，同时请求服务，更新cache，再下次可以获取到cache更新的值。
4.如果服务器没有配置`cache-control`，或者配置`cache-control=max-age=0`。采用本地`cache_expired_time`配置。
5.如果服务区配置了`cache-control=max-age=10`, 会覆盖本地`cache_expired_time=50`，过期时间为10s
6.缓存只缓存en和对应locale的translation。请求不同locale和component的时候，会请求数据。请求相同component的locale的不同的key，直接使用缓存。
7.如果采用run as script模式每次会加载`cache_path`，但是每次加载cache_path后，先返回，再判断是否过期，过期则请求服务器获取更新。

8.混合模式下，如果online获取到数据了。直接走online模式。
9.混合模式下，如果online返回4xx或者5xx，则直接走cache，cache不会失效，直到下次获取到服务器值，才会触发更新。