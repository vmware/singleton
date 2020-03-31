---
title: "Angular Client Introduction"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---

singleton for Angular client is common lib developed by VMware G11n team to provide i18n support for Angular framework. The client will communicate with singleton server for fetching translation and i18n patterns.

Most translations provided by singleton service comes from Human translators, when new application is connected to singleton service, source strings (English strings), required language and related information will be sent to singleton service, singleton service doesn’t return real translation immediately since the human translators need time to figure out the quilted translation, at this moment, singleton service just delivers pseudo tag as placeholder to indicate singleton client works well with singleton service and App itself in development stage. Once the translations are ready, when the client side calls singleton service again, the real translations will be delivered via HTTP request. Please note that under English locale, the client doesn't make a network call to singleton service, but return English directly.

Besides Localization, singleton client also provides I18n support for Datetime format, number format, currency and plural. singleton I18n service is based on CLDR data repository and keep the same scope in supported locales set. For the specific usages and API details please refer to following sections, if you find below APIs can't cover all usages of your application requirement, please feel free to contact with our team to add related API.



#### **Features**

- Level-2 Dates, Times, Numbers and Currencies formatting support. For more details refer to  [i18n Level-2](https://confluence.eng.vmware.com/display/GQ/I18n+Level-2).
- Level-3 support. For more details refer to [i18n Level-3](https://confluence.eng.vmware.com/display/GQ/I18n+Level-3).


#### **Version**

- 8.x (support Angular8)
- 7.x (support Angular7.x)
- 6.x (support Angular6.x)
- 1.x (support Angular4.x & Angular5.x)
- 0.7.x is no longer maintained, only for bugfix

>"VIPServiceConfig" has been removed from Version 8.x and the singleton host information are listed here: [Singleton instances](https://ngx.eng.vmware.com/@vmw/ngx-vip/vip-instance/documentation). For more changes in Version 8.x, please check the changelog.


#### **Installation**

<p class="install"> <span class="function">1 | </span><span class="token">npm install</span> <span class="function">--registry</span> https://build-artifactory.eng.vmware.com/artifactory/api/npm/npm <span class="function">@vmw/ngx-vip --save</span><p>

<!-- ![ngx-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/ngx-1.png) -->

#### **Strings externalization**

![ngx-2](https://github.com/zmengjiao/singleton/raw/website/content/en/images/ngx-2.png)

#### **Configuration**

![ngx-3](https://github.com/zmengjiao/singleton/raw/website/content/en/images/ngx-3.png)


#### **Parameters**

|     Parameter      | Required |              Type              |                         Description                          |
| :----------------: | :------: | :----------------------------: | :----------------------------------------------------------: |
|     productID      | required |             string             | Product name. singleton service doesn’t explicitly restrict name of product, but it's better to keep short and sync with the name in release master. |
|     component      | required |             string             | Component name. singleton internal concept is used to divide UI strings into different units according to certain rules, make sure each component doesn’t exceed 5000 strings. |
|      version       | required |             string             | Release version. singleton internal concept is used to indicate the current version of the UI strings. |
|        host        | required |             string             | URL of singleton host, please refer to the known [Singleton Instances](https://confluence.eng.vmware.com/pages/viewpage.action?spaceKey=GQ&title=VIP+Instances). The host can be optional when an isolated feature module which configured with translationBundles. |
|     i18nScope      | optional |               []               | Define the scope of i18n support. The default scope is empty.<br/>PatternCategories.DATE means date&time format support.<br/>PatternCategories.NUMBER means decimal and percent format support.<br/>PatternCategories.PLURAL means single plural support.<br/>PatternCategories.CURRENCIES means currency format support.<br/>PatternCategories.DATEFIELDS means relative time format support. |
|      isPseudo      | optional |             string             | A flag to determine whether to show pseudo translation in debugging or developing stage. |
|   collectSource    | optional |             string             | A flag to determine whether to push source strings to singleton service, but new UI strings will be only collected on singleton staging instance for the translations when collectSource is set properly to true. The application in CSP platform which needs to consume singleton service should add auth token for source collection request. |
|    sourceBundle    | optional | {key: string]: source: string} | Source string collection.<br/>Externalize all source strings to separated typescript file instead of hardcode strings in template files. Thus use [L10nPlusPipe](https://ngx.eng.vmware.com/@vmw/ngx-vip/translate-pipe/documentation) and [translate directive](https://ngx.eng.vmware.com/@vmw/ngx-vip/translate-directive/documentation) to consume translation without specifying source strings in template. |
|     i18nAssets     | optional |             string             | The folder path is used to save translation files. Files naming rule: translation_${language}.json<br/>The all translations could be fetched from singleton service into local bundle files at build time, thus the application can support localization without singleton service at runtime. |
| translationBundles | optional |   { [key: string]: object }    | A collection of languages and their corresponding translations. Naming rule: { ${language}: { key: translation_string } }<br/>The difference with 'i18nAssets' is this object will compiled into the final bundle, but 'i18nAssets' relies on Http call to get translation bundle per language. |
#### **singleton module initialization**

![ngx-4](https://github.com/zmengjiao/singleton/raw/website/content/en/images/ngx-4.png)

#### **Getting Help**

If you cannot find the necessary documentation or the answer to your question by looking through the [code](https://gitlab.eng.vmware.com/core-build/vmw-ngx-components/tree/master), reach out in the [#ngx](slack://channel?id=CNCHU5R4N&team=T024JFTN4) Slack channel.






<style>
    html {
        font-family: Metropolis;
        color: #575757;
    }
    section strong {
        font-weight: 400;
    }
    ul li {
        list-style: circle;
    }
    blockquote {
        background: #f5dddb;
        border: 1px solid #f8b5b4;
        /* height: 3.5rem;
        line-height: 0rem; */
        color: #575757;
    }
    article section.page table th {
        font-weight:500;
        text-transform: inherit;
    }
    table thead tr th:first-child {
        width:13rem;
    }
    table thead tr th:nth-child(2) {
        width:10rem;
    }
    table thead tr th:nth-child(3) {
        width:10rem;
    }
    .install {
        font-family: Consolas,Monaco,'Andale Mono','Ubuntu Mono',monospace;
        border: 1px solid #ccc;
        background-color: #FDF6E3;
        width:100%;
        height:4rem;
        line-height:4rem;
        padding-left:1.5rem;
        border-radius: 4px;
    }
    .token {
        color: #b58900;
    }
    .function {
        color: #657C83;
    }
</style>