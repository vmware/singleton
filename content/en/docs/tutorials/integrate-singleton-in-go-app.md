---
title: "Integrate Singleton in Go App"
date: 2023-05-11T10:07:56Z
draft: false
---

## Requirements

- Go 1.13 or higher  

## Install Singleton client package

```shell
go get -u github.com/vmware/singleton@v0.7.1-Singleton-Go-Client
```

Change `v0.7.1-Singleton-Go-Client` to the version of Singleton client you will use.

## Sample code

```Go
package main

import (
  "fmt"

  sgtn "github.com/vmware/singleton"
)

func main() {
  cfg := &sgtn.Config{
    ServerURL:         "https://localhost:8090",
    LocalSourceBundle: "./sources",
    DefaultLocale:     "en",
    SourceLocale:      "en",
  }
  sgtn.Initialize(cfg)
  trans := sgtn.GetTranslation()

  // Get translation of a string
  message, err := trans.GetStringMessage("SgtnTest", "1.0.0", "zh-Hans", "sunglow", "application.title")
  if err != nil {
    fmt.Println(err)
    return
  }
  fmt.Printf("\nThe translation is '%s'.\n", message)
}
```
