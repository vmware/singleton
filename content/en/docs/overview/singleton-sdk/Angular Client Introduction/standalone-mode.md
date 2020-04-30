---
title: "Standalone Mode"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 31
---

Standalone mode is designed for the application without singleton service at runtime to support internationalization, the translations and formatting patterns need to be downloaded from singleton service into local asset folder at buildtime. In this way, when the application starts, the ngx-vip client will get translations and patterns from application web server through HTTP request.


#### **Specification**

##### **Register product**

No matter the singleton service will be running in product environment or not. For the software globalization process, the first step is always to register the product, component, version and supported locales through singleton service.

##### **Normalize the language code**

The language normalization is required when the language comes from user browser lanugage, however, each browser relies on different standard of langugae code, so the normalization is used to map the different format of language code to the singleton standard.

Most of On-Premise applications have the language seletor to narrow down the language options, in that case the language normalization is only for default selection from user browser language, but just to make sure the language code from the language selector is consistent with the value which defined in the downloading script.


![standalone-mode-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/standalone-mode/standalone-mode-1.png)


##### **Define script to download locale data**

Use npm command line script to download locale data (translation and formatting pattern) into asset folder at buildtime.

###### **load-locale-data**

|  Parameter  |    Type    |  Value   |                         Description                          |
| :---------: | :--------: | :------: | :----------------------------------------------------------: |
| --directory |   string   | required |        The directory you want to put locale data in.         |
|   --host    |   string   | required | This is singleton service which provides clients with translations and pattern. |
|  --product  |   string   | required | Product name. For now, singleton service doesn’t explicitly restrict name of product, but it's better to keep short and sync with the name in release master. |
| --component |   string   | required |                       Component name.                        |
|  --version  |   string   | required |                       Release version.                       |
|  --locales  |   string   | required | Specified the locale your product supports. Parameters are separated by commas, for example zh-CN,en-US. |
|   --scope   |   string   | optional | Specified the i18n scope your product supports. Separated by , for example dates,numbers,currencies,dateFields. If not set, will not download the i18n formatting required data, only download the translation data. |
|  --verbose  | Don't need | optional | If set, it will show all information during command execution for debug purpose. |

For example

![standalone-mode-2](https://github.com/zmengjiao/singleton/raw/website/content/en/images/standalone-mode/standalone-mode-2.png)


###### **Add script in package.json**

![standalone-mode-3](https://github.com/zmengjiao/singleton/raw/website/content/en/images/standalone-mode/standalone-mode-3.png)

###### **Excute script at build time**

![standalone-mode-4](https://github.com/zmengjiao/singleton/raw/website/content/en/images/standalone-mode/standalone-mode-4.png)


##### **Configure i18n assets**

If use the local bundles instead of singleton service to support i18n, the asset path should be specified in singleton configuration, so in that case ngx-vip client will download the locale data from the path at runtime. Moreover, if the application also needs to handle the data formatting, please define the all functional categories which will be used in the configuration as i18nScope and make sure the downloading script contains the same relevant scope defintion.

![standalone-mode-5](https://github.com/zmengjiao/singleton/raw/website/content/en/images/standalone-mode/standalone-mode-5.png)

##### **Limitations**

Using the local data to support i18n is not a recommended way that only for a transition period prior to singleton service deployed. So before adopting standalone mode, please understand the limitations of this mode as below:

- No uniform language fallback support.
- Only support using defaut region from language code to format data.
- Can't update the translation and patterns dynamically without the web server restart.


<style>
    html {
        font-family: Metropolis;
        color: #575757;
    }
    section strong {
        font-weight: 400;
    }
    ul li {
        list-style: disc;
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
    article section.page h1:first-of-type {
        text-transform: inherit;
        font-family: inherit;
    }
</style>