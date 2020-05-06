---
title: "Localized Component Mixin"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 19
---

#### **Overview**

The LocalizedComponent mixin provides a nice abstraction for localizing components in an Angular app.

Using the Mixin approach allows you to achieve three things:

- Extract English strings to a separate file, keeping your HTML template clean and containing only DOM.
- Provides a consistent pattern of where and how English strings are stored allowing easy automation and parsing of those files to collect the English into singleton.
- Your translation keys will always be prefixed with the key provided in the mixin, helping to avoid namespace clashes with other components.

It also is useful to avoid using the translate pipe in HTML as the Angular linter often has trouble with complex English strings.

#### **Example Code**

![localized-component-mixin-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/localized-component-mixin/localized-component-mixin-1.png)


![localized-component-mixin-2](https://github.com/zmengjiao/singleton/raw/website/content/en/images/localized-component-mixin/localized-component-mixin-2.png)


![localized-component-mixin-3](https://github.com/zmengjiao/singleton/raw/website/content/en/images/localized-component-mixin/localized-component-mixin-3.png)


#### **Collecting/Extracting Source English**

With the English sources isolated to a single type of file, we can easily write a script that collects all the English and dumps it into a JSON file for passing it to the translation team.

The @vmw/ngx-vip library provides a script called collect-mixin-source which will find all files in your project that end in .l10n.ts and pass them through the TypeScript compiler and send them to singleton for translation.

##### **Command line arguments description**

![localized-component-mixin-4](https://github.com/zmengjiao/singleton/raw/website/content/en/images/localized-component-mixin/localized-component-mixin-4.png)

##### **Config script in package.json**

>Note that if you use refresh token in the host that isn't supported, it will through unexpected error, and the vice versa.

When the refresh token is required by your host

![localized-component-mixin-5](https://github.com/zmengjiao/singleton/raw/website/content/en/images/localized-component-mixin/localized-component-mixin-5.png)


When the refresh token isn't required by your host

![localized-component-mixin-6](https://github.com/zmengjiao/singleton/raw/website/content/en/images/localized-component-mixin/localized-component-mixin-6.png)


##### **Use the script**

![localized-component-mixin-7](https://github.com/zmengjiao/singleton/raw/website/content/en/images/localized-component-mixin/localized-component-mixin-7.png)



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
    blockquote {
        background: #f5dddb;
        border: 1px solid #f8b5b4;
        height: 3.5rem;
        line-height: 0rem;
        color: #575757;
    }
    article section.page h1:first-of-type {
        text-transform: inherit;
        font-family: inherit;
    }
</style>