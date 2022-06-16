---
title: "Percent Format Pipe"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---


#### **Overview**

Formats a number into percentage string. Group sizing and separator and other locale-specific configurations are based on the pattern data.

#### **Usage**

```
{{ value | percentFormat [ : formatOptions ] }}

```

#### **Parameters**

|   Parameter   |  Type  | <div style="text-align:center">Description</div>                                                               |
|:-------------:|:------:|:---------------------------------------------------------------------------------------------------------------|
| formatOptions | object | The results formats can be customized using the formatOptions argument. See formatOptions for further details. |


#### **formatOptions**

|     Attribute     |  Type  | Required | <div style="text-align:center">Description</div>                                                               |
|:-----------------:|:------:|----------|:---------------------------------------------------------------------------------------------------------------|
| minIntegerDigits  | number | Optional | The minimum digits of integer numbers used. The possible values are from 1 to 21, and the default value is 1.. |
| minFractionDigits | number | Optional | The minimum digits of fraction numbers used. The possible values are from 0 to 20, and the default value is 0. |
| maxFractionDigits | number | Optional | The maximum digits of fraction numbers used. The possible values are from 0 to 20, and the default value is 3. |


#### **Example Code**

```
// input '0.1'
{{ num | percentFormat }}
// output '10%'

// input '0.1234'
{{ num | percentFormat }}
// output '12%'

// Format options usages
// Please note that pass object as a parameter,
// the end of the brace should keep a whitespace.

// input '0.1234'
{{ num | percentFormat: { minFractionDigits: 2 } }}
// output '12.34%'

// input '12345'
{{ num | percentFormat }}
// output '1,234,500%'

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