Singleton Library for Golang Client
============

Several client libraries are provided to support sending API requests to Singleton service.

Below are details on how to use the library for Golang client.

Prerequisites
------------
 * Run the Singleton service by following the instructions in [here](https://github.com/vmware/singleton/blob/master/README.md).
 * Ensure the following are installed:     
    - [Golang binary](https://golang.org/doc/install)
    - [Git](https://git-scm.com/downloads)

Get the client library
------------
```console
go get -u github.com/vmware/singleton@g11n-go-client
```

Here g11n-go-client is the branch name, it's better to use a tag name to get a stable release.

Sample code
------------

```go
package main

import (
	"fmt"
	"log"

	sgtn "github.com/vmware/singleton"
)

func main() {
	cfg, err := sgtn.LoadConfig("resource/conf/singletonconfig.json")
	if err != nil {
		log.Fatal(err)
	}
	sgtn.Initialize(cfg)
	trans := sgtn.GetTranslation()

	name, version, locale, component, key := "SgtnTest", "1.0.0", "zh-Hans", "sunglow", "application.title"

	// Get translation of a component
	compData, err := trans.GetComponentMessages(name, version, locale, component)
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Printf("\nComponent translation:\n%#v\n", compData)

	// Get translation of a string
	zhMsg, err := trans.GetStringMessage(name, version, locale, component, key)
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Printf("\nThe translation is '%s'.\n", zhMsg)
}
```

Existing features
------------
 * Supported interfaces: 
    * GetLocaleList: Get supported locale list
    * GetComponentList: Get supported component list
    * GetStringMessage: Get a message with optional arguments. Fallback to default locale is provided.
    * GetComponentMessages: Get messages of a component. Fallback to default locale is **NOT** provided.
 * Provide cache management as well as cache registration.
 * Support fallback to local bundles when failing to get from server
 * Support fallback to default locale when failing to get a string message of a nondefault locale.

Upcoming features 
------------
 * <TO DO: Add upcoming features if any>

Request for contributions from the community
------------
 * 
