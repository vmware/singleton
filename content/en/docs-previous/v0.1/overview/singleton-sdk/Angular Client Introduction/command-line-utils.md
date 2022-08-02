---
title: "Command Line Utils"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 31
---



#### **Load Translation**

Download the specified languages of translation files in your project location.

##### **Command line arguments description**


|  Parameter  |    Type    |  Value   | <div style="text-align:center">Description</div>                                                                                                              |
|:-----------:|:----------:|:--------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| --directory |   string   | required | The directory you want to put translations in.                                                                                                                |
|   --host    |   string   | required | This is singleton service which provides clients with translations and pattern.                                                                               |
|  --product  |   string   | required | Product name. For now, singleton service doesn’t explicitly restrict name of product, but it's better to keep short and sync with the name in release master. |
| --component |   string   | required | Component name.                                                                                                                                               |
|  --version  |   string   | required | Release version.                                                                                                                                              |
| --languages |   string   | required | Specified the languages your product supports; Separated by , for example, zh-cn,en-US.                                                                       |
|  --verbose  | Don't need | optional | If set, it will show all information during command execution for debug purpose.                                                                              |


##### **For example**

```
load-translation
  --directory `pwd`/src/app/assets
  --host https://singleton.service.com:8090 // It requires to deploy singleton service
  --product Testing
  --component default
  --version 1.0
  --languages zh-cn,en-US

```

##### **Config script in package.json**

```
{
    ...
    scripts: {   
        "load-translation": "load-translation 
                            --directory `pwd`/src/app/assets
                            --host <Singleton Service host>
                            --product <product>
                            --component <component>
                            --version <product version>
    }
    ...
}

```

##### **Use the script**

```
npm run load-translation

```

#### **Collect Source Bundle**

The @singleton-i18n/angular-client provides a script to collect source strings from source bundles. This script collects the collection named **ENGLISH** from the files named ***.l10n.ts**, and then send them to the singleton server.

##### **Command line arguments description**

|    Parameter    |       Type       |  Value   | <div style="text-align:center">Description</div>                                                                                                              |
|:---------------:|:----------------:|:--------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  --source-dir   |      string      | required | The directory your source code in.                                                                                                                            |
|     --host      |      string      | required | This is singleton service which provides clients with translations and pattern.                                                                               |
|    --product    |      string      | required | Product name. For now, singleton service doesn’t explicitly restrict name of product, but it's better to keep short and sync with the name in release master. |
|   --component   |      string      | required | Component name.                                                                                                                                               |
|    --version    |      string      | required | Release version.                                                                                                                                              |
| --refresh-token |      string      | optional | Refresh token is only required for the CSP environment.                                                                                                       |
|    --verbose    | Don't need value | optional | If set, will show all information during command execution for debug purpose.                                                                                 |


##### **Create source bundle**
```
export const ENGLISH = {
    "network-error": 'Network instability.'',
    "data-error": 'Data error.',
    ...
}

```

##### **Config script in package.json**

```
collect-source-bundle
            --source-dir `pwd`/src/app
            --host https://singleton.service.com:8090 // It requires to deploy singleton service
            --product Testing
            --component Angular
            --version 1.0
    
```


```
{
    ...
    scripts: {   
        "collect-source-bundle": "collect-source-bundle 
                                --source-dir pwd`/src/app 
                                --host <Singleton Service host>
                                --product <product>
                                --component <component>
                                --version <product version>
    }
    ...
}
    
```
        
##### **Use the script**

```
npm run collect-source-bundle

```


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
    article section.page pre {
        background-color: #444;
        border: 0.5px solid #DBDBDB; 
        padding: 1.5rem 1rem 1.5rem 1rem;
        border-radius: 5px;
        margin: 16px auto;
    }
    article section.page code {
        font-size: 90%;
        color: #17ff0b;  
        white-space: pre-wrap;
    }
    article section.page pre span.copy-to-clipboard {
        color: #b0bec5;
        cursor: pointer;
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