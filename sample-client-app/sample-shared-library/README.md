Singleton Java Client Sample Shared Library
============

This library can be used to demonstrate usage of the Singleton Java client in a shared library.

Prerequisites
------------
 * Build the Singleton Java client library by following the instructions in [here](https://github.com/vmware/singleton/blob/g11n-java-client/README.md).

How to build and run this sample shared library
------------
 * Go to the root directory of this sample application
   ```
   g11n-java-client/sample-client-app/sample-shared-library
   ```
 * Create a lib folder.
   ```
   mkdir lib
   ```
 * Put the Singleton Java client library into the lib folder.
   ```
   cp ../../build/libs/singleton-client-java-0.1.0.jar ./lib/
   ```
 * Build the sample application.
   ```
   gradle build
   ```
Note: The library jar will be created under "build/libs" directory. This can be used as a shared library in one or more applications. See sample in [here](https://github.com/vmware/singleton/blob/g11n-java-client/sample-client-app/sample-app-with-shared-lib/README.md)

