## Developer's Guide for Singleton Service

### How to setup Intellij IDEA for development

You can flow below steps to quickly setup your Dev environment:

1. Install JDK 1.8(or later version) and Gradle 4.10.3(or later version)

2. Install Intellij IDEA community version and configure Gradle home

3. Import Singleton service codes as Gradle project on IntelliJ
[Import Singleton project ](https://github.com/vmware/singleton/tree/master/g11n-ws/docs/img/intellij_import.png)

4. Run build.gradle with 'clean build' to generate the build on IntelliJ
[Create 'build' Task ](https://github.com/vmware/singleton/tree/master/g11n-ws/docs/img/intellij_build.png)

5. Configure and run com.vmware.vip.BootApplication to start Singleton i18n service on IntelliJ
[Run i18n Application](https://github.com/vmware/singleton/tree/master/g11n-ws/docs/img/intellij_run.png)

After Singleton i18n service is startup, you could open swagger-ui to try the API: https://localhost:8090/swagger-ui.html#

6. Configure and run com.vmware.l10n.BootApplication to start Singeton l10n service for source collection
[Run l10n Application](https://github.com/vmware/singleton/tree/master/g11n-ws/docs/img/intellij_runl10n.png)




