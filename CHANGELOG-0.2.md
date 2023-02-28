v0.2.0
-------

[Documentation](https://vmware.github.io/singleton/)

## Downloads for v0.2.0

### Service Binaries
filename | sha1 hash | branch/tag
-------- | --- | ------
[singleton-manager-i18n-s3-0.2.0.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-manager-i18n-s3/0.2.0/singleton-manager-i18n-s3-0.2.0.jar) | `3f646a402c5131eea3c8be2ccec3f0d37fc8eede` | master/[v0.2.0-Singleton-Service](https://github.com/vmware/singleton/releases/tag/v0.2.0-Singleton-Service)
[singleton-manager-i18n-0.2.0.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-manager-i18n/0.2.0/singleton-manager-i18n-0.2.0.jar) | `157db7cbf5ded0d0c844ea437e368b91c5912318` | master/[v0.2.0-Singleton-Service](https://github.com/vmware/singleton/releases/tag/v0.2.0-Singleton-Service)
[singleton-manager-l10n-0.2.0.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-manager-l10n/0.2.0/singleton-manager-l10n-0.2.0.jar) | `5883e09c70484a10e1c1b94b203f9a184011eb27` | master/[v0.2.0-Singleton-Service](https://github.com/vmware/singleton/releases/tag/v0.2.0-Singleton-Service)

### Client Binaries
filename | sha1 hash | branch/tag
-------- | --- | ------
[singleton-client-java.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-client-java/0.1.0/) | No update | No update
[@singleton-i18n/js-client](https://www.npmjs.com/package/@singleton-i18n/js-core-sdk/v/0.1.0) | - | No update
[@singleton-i18n/nodejs-client](https://www.npmjs.com/package/@singleton-i18n/js-core-sdk-server/v/0.1.0) | - | No update
[@singleton-i18n/angular-client](https://www.npmjs.com/package/@singleton-i18n/angular-client/v/0.1.0) | - | No update

## Changelog since v0.1.0

### Main Changes
#### Service
- Support storing translation bundles in AWS S3, refer to [Enable S3 storage](https://vmware.github.io/singleton/docs/overview/singleton-service/configurations/enable-s3-storage/) ([#81](https://github.com/vmware/singleton/issues/81))

#### SDK
No Update.


### Known Issues
#### Service
- the Etag in the response header keeps on changing for the same GET request ([#102](https://github.com/vmware/singleton/issues/102))
- Singleton Service s3 build start failed based on https protocol ([#145](https://github.com/vmware/singleton/issues/145))
- Plurals rule doesn't follow with language ([#148](https://github.com/vmware/singleton/issues/148))
- The build failed to start when disable swagger-ui ([#151](https://github.com/vmware/singleton/issues/151))
- Failed to get translation when file(s) existing same location with component in Service S3 build ([#159](https://github.com/vmware/singleton/issues/159))


### Improvement
#### Service
- Return default region when input is a language ([#149](https://github.com/vmware/singleton/issues/149))
- Remove extra l10n folder in Singleton Service s3 build ([#194](https://github.com/vmware/singleton/issues/194))

### Action Required
N/A
