---
title: "Isolated Feature Module"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---

#### **Overview**

VIP client provides localization(l3) support for the third-party libraries, managing its resources and configuration separately from other modules.<br/>
*Note: Since it's impossible to ensure that lib's runtime environment is accessible to VIP service, so please mount the translation bundles to the configuration.*


#### **Feature Module**

Register the lib's configuration, and override pipe util to set the specific configuration.


##### **Configuration**

![isolated-feature-module-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/isolated-feature-module/isolated-feature-module-1.png)


##### **Override L10nPipePlus**

![isolated-feature-module-2](https://github.com/zmengjiao/singleton/raw/website/content/en/images/isolated-feature-module/isolated-feature-module-2.png)


##### **Register component**

![isolated-feature-module-3](https://github.com/zmengjiao/singleton/raw/website/content/en/images/isolated-feature-module/isolated-feature-module-3.png)


#### **Host Module With VIP**

There is no special configuration required, the lib will work as well as normal feature module.


#### **Host Module Without VIP**


Import VIP module and invoke the API to set current locale and default locale of the lib if needed.


![isolated-feature-module-4](https://github.com/zmengjiao/singleton/raw/website/content/en/images/isolated-feature-module/isolated-feature-module-4.png)






<style>
    html {
        font-family: Metropolis;
        color: #575757;
    }
    section strong {
        font-weight: 400;
    }
</style>