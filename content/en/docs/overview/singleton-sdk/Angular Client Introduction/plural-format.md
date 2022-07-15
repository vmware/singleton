---
title: "Plural Format"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 25
---


#### **Overview**

Transform a plural message string to a string that pluralizes the value according to locale rules and translations. It can be implemented using l10n utils, the only difference is the definition of the source.


#### **Plural Source Text**

The source text defines the message output for each plural case of the specified locale. Syntax:

```
selector { message }
/**
 *  selector: explicitValue | keyword
 *       explicitValue: '=' number // adjacent, no white space in between
 *       keyword: Pluralization categories
 *  message: string
 */

 ```

 Pluralization categories include (depending on the language):

- zero
- one
- two
- few
- many
- other

There are 6 predefined case keywords in CLDR/ICU. You always have to define a message text for the default plural case "other" which is contained in every rule set. If you do not specify a message text for a particular plural case, the message text of the plural case "other" gets assigned to this plural case.

When formatting, the input number is first matched against the explicitValue clauses. If there is no exact-number match, then a keyword is selected by calling the PluralRules with the input number. If there is no clause with that keyword, then the "other" clauses is returned.

An unquoted pound sign (#) in the selected sub-message itself is replaced by the input number.

>Note: The pipe accept a parameters array, the placeholders in message should be index of variable in parameters array.

#### **Example Code**

source in bundle


```
...
'ngx.singleton.files' : '{0, plural, =0 {No files.} one{There is one file on {1}.} other{There are # files on {1}.} }'

 ```


```
import { L10nService } from '@singleton-i18n/angular-client';

@Component({
    selector: 'test',
    templateUrl: './test.component.html'
})
export class TestComponent {
    constructor(private l10nService: L10nService) {}

    ...
    // static refresh mode
    this.thanslatedFiles = this.l10nService.getMessage('ngx.singleton.files', [ this.files.length, 'XDisk' ]);
    
    ...
    // live update mode
    this.subscription = this.l10nService.stream.subscribe((locale: string) => {
        this.thanslatedFiles = this.l10nService.getMessage('ngx.singleton.files', [ this.files.length, 'XDisk' ]);
    });

}

 ```


```
{{'ngx.singleton.files' | vtranslate:files.length:'XDisk'}}

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
    ul li {
        list-style: circle;
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
    blockquote {
        background: #f5dddb;
        border: 1px solid #f8b5b4;
        color: #575757;
    }    
    blockquote>p {
        display: inline-block;
        margin: 1rem 0;
    }
</style>