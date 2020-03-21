---
title: "Plural Format"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
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

Note: The pipe accept a parameters array, the placeholders in message should be index of variable in parameters array.

#### **Example Code**

source in bundle


![plural-format-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/plural-format/plural-format-1.png)


![plural-format-2](https://github.com/zmengjiao/singleton/raw/website/content/en/images/plural-format/plural-format-2.png)


![plural-format-3](https://github.com/zmengjiao/singleton/raw/website/content/en/images/plural-format/plural-format-3.png)


<style>
    html {
        font-family: Metropolis;
        color: #575757;
    }
    section strong {
        font-weight: 400;
    }
    article section.page pre {
        background-color: #fafafa;
        border:1px solid #ccc;
        padding-top: 2rem;
    }
    ul li {
        list-style: circle;
    }
</style>