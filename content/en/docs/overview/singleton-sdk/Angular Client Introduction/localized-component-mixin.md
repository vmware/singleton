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

```
// source.10n.ts
export const ENGLISH = {
    "my-key": "Some english for my-key {0} {1}",
    "my-key-with-html": `
        <em>Some english in emphasis {0} {1}</em>
    `
};

```

```
// demo.component.ts
import { Component } from "@angular/core";
import { L10nService, LocalizedComponent, Mixin } from "@singleton-i18n/angular-client";
import { ENGLISH } from "./my-component.l10n.ts";

@Component({
    selector: "my-component",
    templateUrl: "./my-component.template.html",
})
@Mixin([LocalizedComponent], {
    L10nKey: "my-component",
    L10nSourceMap: ENGLISH,
})
export class MyComponent implements LocalizedComponent {
    translate: (key: string, ...args: any[]) => string;

    constructor(public l10nService: L10nService) {}
}

```


```
<p>
    {{translate('my-key', 'argument 1', 'argument 2')}}
</p>

<div [innerHTML]="translate('my-key-with-html', 'argument 1', 'argument 2')"></div>

```

#### **Collecting/Extracting Source English**

With the English sources isolated to a single type of file, we can easily write a script that collects all the English and dumps it into a JSON file for passing it to the translation team.

The Singleton Angular Client library provides a script called collect-mixin-source which will find all files in your project that end in .l10n.ts and pass them through the TypeScript compiler and send them to singleton for translation.

>Note that **.l10n.ts** files must be imported with relative path to work correctly.


##### **Command line arguments description**

```
collect-mixin-source
    --source-dir `pwd`/src/app
    --host <Singleton Service host>
    --product <product>
    --component <component>
    --version <product version>
    --refresh-token <csp refresh token>

```

##### **Config script in package.json**

>Note that if you use refresh token in the host that isn't supported, it will through unexpected error, and the vice versa.

When the refresh token is required by your host

```
{
    ...
    scripts: {   
        "source-collection": "collect-mixin-source 
                            --source-dir `pwd`/source/directory 
                            --host <Singleton Service host>
                            --product <product>
                            --component <component>
                            --version <product version>
                            --refresh-token CSPRefreshToken"
    }
    ...
}

```

When the refresh token isn't required by your host

```
{
    ...
    scripts: {   
        "source-collection": "collect-mixin-source 
                            --source-dir `pwd`/source/directory 
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
npm run source-collection

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
    ul li {
        list-style: disc;
    }
    blockquote {
        background: #f5dddb;
        border: 1px solid #f8b5b4;
        color: #575757;
    }    
    blockquote>p {
        display: inline-block;
        margin: 1rem 0;
    }
    article section.page h1:first-of-type {
        text-transform: inherit;
        font-family: inherit;
    }
</style>