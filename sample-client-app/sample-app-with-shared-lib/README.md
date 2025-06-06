Singleton Java Client Sample Application with Shared Library
============

This application can be used to demonstrate usage of a shared library that uses the Singleton Java client.

Prerequisites
------------
 * Build the Singleton Java client library by following the instructions in [here](https://github.com/vmware/singleton/blob/g11n-java-client/README.md).

How to build and run this sample application as an executable jar
------------
 * Go to the root directory of this sample application
   ```
   g11n-java-client/sample-client-app/sample-app-with-shared-lib
   ```
 * Create a lib folder.
   ```
   mkdir lib
   ```
 * Put the Singleton Java client library into the lib folder.
   ```
   cp ../../build/libs/singleton-client-java-0.1.0.jar ./lib/
   ```
 * Put the sample shared library into the lib folder. See how to build the sample shared library in [here](https://github.com/vmware/singleton/blob/g11n-java-client/sample-client-app/sample-shared-library/README.md)
   ```
   cp ../sample-shared-library/build/libs/sample-shared-library-1.0.jar ./lib/
   ```
 * Build this sample application.
   ```
   gradle build
   ```
 * Run this sample application.
   ```
   java -jar ./build/libs/sample-with-shared-lib-1.0.jar
   ```
 
How to build and run this sample application as a Spring Boot executable jar
------------
 * Go to the root directory of this sample application
   ```
   g11n-java-client/sample-client-app/sample-app-with-shared-lib
   ```
 * Create a lib folder.
   ```
   mkdir lib
   ```
 * Put the Singleton Java client library into the lib folder.
   ```
   cp ../../build/libs/singleton-client-java-0.1.0.jar ./lib/
   ```
 * Put the sample shared library into the lib folder. See how to build the sample shared library in [here](https://github.com/vmware/singleton/blob/g11n-java-client/sample-client-app/sample-shared-library/README.md)
   ```
   cp ../sample-shared-library/build/libs/sample-shared-library-1.0.jar ./lib/
   ```
 * Build and run this sample application:
   ```
   gradle -b build-spring-boot.gradle build
   java -jar ./build/libs/sample-spring-boot-with-shared-lib-1.0.jar
   ```
