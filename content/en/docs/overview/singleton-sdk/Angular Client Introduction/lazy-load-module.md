---
title: "Lazy Load Module"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---


#### **Overview**

As the application is growing, some modules will be defined as the lazy module. In that situation, the translation and patterns could be loaded as lazy mode to enhance performance. The VIP client provides this option to load translation and patterns along with lazy module. Using the 'forChild' API to initialize the VIP module, thus the lazy module will use the separated VIP services instances and injectors.


#### **Example**

![lazy-load-module-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/lazy-load-module/lazy-load-module-1.png)


<style>
    html {
        font-family: Metropolis;
        color: #575757;
    }
    section strong {
        font-weight: 400;
    }
</style>