---
title: "Number Format Pipe"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 28
---


#### **Overview**

Formats a number as text. Group sizing and separator and other locale-specific configurations are based on the pattern data.

#### **Usage**

```
{{ value | numberFormat [ : formatOptions ] }}

```


#### **Input Value**

| Parameter |      Type       | <div style="text-align:center">Description</div> |
|:---------:|:---------------:|:-------------------------------------------------|
|   value   | number / string | The number to be formatted.                      |


#### **Parameters**

|   Parameter   |  Type  | Required | <div style="text-align:center">Description</div>                                                               |
|:-------------:|:------:|----------|:---------------------------------------------------------------------------------------------------------------|
| formatOptions | object | Optional | The results formats can be customized using the formatOptions argument. See formatOptions for further details. |



#### **formatOptions**

|     Attribute     |  Type  | Required | <div style="text-align:center">Description</div>                                                                                                   |
|:-----------------:|:------:|----------|:---------------------------------------------------------------------------------------------------------------------------------------------------|
| minIntegerDigits  | number | Optional | The minimum digits of integer numbers used. The possible values are from 1 to 21, and the default value is 1.                                      |
| minFractionDigits | number | Optional | The minimum digits of fraction numbers used. The possible values are from 0 to 20, and the default value is 0.                                     |
| maxFractionDigits | number | Optional | The maximum digits of fraction numbers used. The possible values are from 0 to 20, and the default value is 3.                                     |
|     notation      | string | Optional | The format in which this number should be displayed. For now only support 'compact' for compact number formats. The default is "standard".         |
|  compactDisplay   | string | Optional | The 'compactDisplay' is only used when notation is "compact". The possible value is "short" (default) or "long", and the default value is "short". |



#### **Example Code**

```
// input '201703.5416926'
{{ num | numberFormat }}
// output '201,703.542'

// input '1.149999'
{{ num | numberFormat }}
// output '1.150'

// input '2.31'
{{ num | numberFormat }}
// output '2.31'

// input '2017120107'
{{ num | numberFormat }}
// output '2,017,120,107'

// input '.23'
{{ num | numberFormat }}
// output '0.23'

// Format options usages
// Please note that pass object as a parameter,
// the end of the brace should keep a whitespace.

// input '0.5416926'
{{ num | numberFormat: { maxFractionDigits: 6 } }}
// output '0.541693'

// input 1000000
{{ num | numberFormat: {notation: 'compact', compactDisplay: 'short'} }}
// output 1M

// input 1000000
{{ num | numberFormat: {notation: 'compact', compactDisplay: 'long'} }}
// output 1 million

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