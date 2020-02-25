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

How to get the client library
------------
 * Create your Golang project
   
 * Download the client code by Go get tool.
    ```
    go get -u github.com/vmware/singleton@g11n-go-client
    ```
    Here g11n-go-client is the branch name, it's better to use tag name to get a stable release.

Sample code
------------

```Golang
package main

import (
	"fmt"
	"log"

	sgtn "github.com/vmware/singleton"
)

func main() {
	cfg, err := sgtn.NewConfig("resource/conf/singletonconfig.yaml")
	if err != nil {
		log.Fatal(err)
	}
	inst, _ := sgtn.NewInst(*cfg)
	trans := inst.GetTranslation()

	locale, component, key := "zh-Hans", "sunglow", "application.title"

	// Get translation of a component
	compData, err := trans.GetComponentMessages(locale, component)
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Printf("\nComponent translation:\n%#v\n", compData)

	// Get translation of a string
	zhMsg, err := trans.GetStringMessage(locale, component, key)
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
    * GetComponentMessages: Get component messages. Fallback to default locale is not provided.
    * GetComponentsMessages: Get messages of multiple components. Fallback to default locale is not provided.
 * Provide cache management as well as register cache.
 * Support falling back to locale bundles when failing to get from server
 * Support falling back to default locale when failing to get string message of a specified locale.

Upcoming features 
------------
 * <TO DO: Add upcoming features if any>

Request for contributions from the community
------------
 * 
   

