Singleton Java Client Sample Application
============

This sample application demonstrates the most common usages of Singleton Java client. 

Prerequisites
------------
 * Build the Singleton Java client library by following the instructions in [here](https://github.com/vmware/singleton/blob/g11n-java-client/README.md).

How to build and Run sample application
------------
 * Create a lib folder in the root directory of this sample application
 * Put the Singleton Java client library into the lib folder.
 * Change sampleconfig.properties as needed.
   The file src/main/resources/sampleconfig.properties contains properties that can be configured as needed. The following are preconfigured to work with default Singleton service settings (See [here](https://github.com/vmware/singleton/blob/master/README.md)). 
   ```
    productName 
    version
    vipServer
   ```
 * Build the sample application.
   ```
   gradle build
   ```
Note: The library jar will be created under "build/libs" directory.
 * Run the sample application to see the output.
   ```
   java -jar build/libs/<jar file> <locale>
   ```
