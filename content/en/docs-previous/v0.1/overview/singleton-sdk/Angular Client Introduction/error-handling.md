---
title: "Error Handling"
date: 2022-06-16T13:30:48+03:00
draft: false
weight: 34
---


Singleton provides two error streams for users to listen to that emit objects of type **VIPError**.

- **_errors$_** on subscription will emit all errors emitted up to this point and each new error after that.
- **_lastError$_** on subscription will emit last error emitted up to this point and each new error after that.

```
export interface VIPError {
    code: VIPErrorCode; // custom code representing the type of failure that occurred
    data: VIPErrorData; // custom data related to the specific error type
    error: any; // api error response if such exists
}

```

User can listen through **VIPService** instance:

```
// user-app.ts
this.vipService.errors$.subscribe(error => {
    if (error.code === VIPErrorCode.ComponentLoadFailure) {
        // handle errors
    }
});

this.vipService.lastError$.subscribe(error => {
    if (error.code === VIPErrorCode.ComponentLoadFailure) {
        // handle last error
    }
});
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