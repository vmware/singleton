Singleton Library for C# Clients
============

Several client libraries are provided to support sending API requests to Singleton service. 

Below are details on how to use the library for C# clients.

Prerequisites
------------
 * Run the Singleton service by following the instructions in [here](https://github.com/vmware/singleton/blob/master/README.md).
 * .NET Framework 4.6.2 or above    

How to build the client library
------------
 * Clone the repository using Git.

 * Go to the project's root directory.
    ```
    cd singletonclient
    ```
 * Build the client library. 

Launch visual studio 2019 and load file 'SingletonClient.sln', then build project 'SingletonClient'

How to use the client library
------------
 * Import the library SingletonClient.dll into your project as one of References

Configuration
------------
[Example](singletonclient/Product1ResLib/SingletonRes/sgtn_sample.yml)

Resource Definition
------------
1. Create a RESX file to include the configuration file.
2. Put source definition in one or more properties file.
3. Include these properties files into the RESX file.
4. Define resource names pointed to these properties files in the configureation file to finish source registration.

Sample code
------------
```C#
IConfig cfg = I18n.LoadConfig("Product2ResLib.SingletonRes.Singleton", assembly, "singleton_config");
IRelease release = I18n.GetRelease(cfg);
ITranslation translation = release.GetTranslation();

ISource src = translation.CreateSource("about", "about.title", "About");
string text = translation.Get("de", src);
```

Existing features
------------
 * Provides default I18n (translation and formatting) instances as well as supporting customized I18n instance.
 * Supports collecting source (English String) to Singleton server.
 * Provides cache management as well as supporting customized cache management.
 * Supports getting pseudo translations on development stage.
 * Supports getting machine translations on development stage.

Upcoming features 
------------
 * <TO DO: Add upcoming features if any>

Request for contributions from the community
------------
 * 
   
