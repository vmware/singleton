---
title: "Data Initialization"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---


In general, for most of products, it's good enough to load all translations and patterns one time in root module when the application starts, but for some special requirements, such like: isolated translation for feature module or using lazy module to enhance performance, please refer to [Isolated feature module](https://ngx.eng.vmware.com/@vmw/ngx-vip/isolated-module/documentation) and [Lazy Load Module](https://ngx.eng.vmware.com/@vmw/ngx-vip/lazy-load/documentation). singleton client provides two ways to load i18n data and initialize the services in root module:

##### **Blocking loading**

Get the translations from an singleton service API and keep it ready before the application renders the page.


**Pros**: There is no race condition when UI rendering, the page will be rendered only when the translations are settled. <br/>
**Cons**: When the size of data is large, the time required to load data in different environments is uncontrollable. It will block the application start.


This loading method is suitable for the number of UI strings does not exceed 5000.

![data-initialization-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/data-initialization/data-initialization-1.png)


##### **Non-blocking loading**

The page is updated by asynchronous requesting data from the singleton service. If the data load failed, the UI will show the source string instead.

**Pros**: Will not block application start.<br/>
**Cons**: If the data load slowly, UI will display blank in a short period of time.


This loading method is suitable for the number of UI strings more than 5000.

![data-initialization-2](https://github.com/zmengjiao/singleton/raw/website/content/en/images/data-initialization/data-initialization-2.png)


<style>
    html {
        font-family: Metropolis;
        color: #575757;
    }
    section strong {
        font-weight: 400;
    }
    section p>strong {
        font-weight: 600;
    }
</style>