Singleton
============

A service that provides support for Software Internationalization and Localization

Introduction
------------
Singleton is an open-source application for streamlining software globalization. It standardizes and simplifies software application globalization – not just in L10n, but in i18n too. 

Singleton was originally developed under the R&D Operations and Central Services (ROCS) team in VMware. The core application is written using the Java™ programming language. Client-side code implementation is also available – in JavaScript (Angular, AngularJS and NodeJS web frameworks), C#, and Java™ programming languages. While VMware continues to lead development and maintenance of Singleton, the organization has decided to make it available to the open source community. In May 2019, VMware has decided to release an open-source repository in GitHub. We invite you to collaborate with us to enrich Singleton's resources and help organizations that will adopt it thrive. 

How does it work?
------------
The L10n capabilities of Singleton decouple localized resources from the application software. Singleton delivers a web service that provides an API for sending source artifacts for translation. These artifacts are processed externally, and localized resources are then embedded into the Singleton Service. L10n functions in Singleton are separated from the core application, which makes it possible to update or add new language support independently from the core application's release cycle – a requirement for fast-paced, agile releases as well as the SaaS world. Singleton's interoperability with multiple applications results to a simpler, more consistent quality of L10n across the board.

The i18n capabilities of Singleton eliminate the need for developers to learn different APIs for i18n across technologies and programming languages. It acts as an abstraction layer that provides consistent i18n format (e.g. date, time, number, and currency) to various applications that may be written in different programming languages. Its web service API exposes REST endpoints for i18n, which naturally provides abstraction across multiple clients. This allows for a programming language–agnostic i18n implementation, significantly reducing the implementation effort of software developers, allowing them to focus on implementing new features and innovation. It also allows for consistent quality of i18n.

Features
---------
 * Singleton Core API - RESTful web services for delivering localized resources.
 * Source Collection - RESTful web service to collect source text that are to be translated. To use this feature, the Localization Manager has to be installed in addition to the service installation described in the succeeding section. See User Guide for details.
 * Pseudo-translation - a way to test product compatibility with locale when translated resources are not yet available. It uses auto-generated text for testing various aspects of localization readiness, including character set support, UI design, and hard coding. See User Guide for details.
 
 
Upcoming features
---------
 * (To Do)
 *

Prerequisites
---------
 * [Java 17](https://www.oracle.com/java/technologies/downloads/#jdk17) 
 * [Git](https://git-scm.com/downloads)

Building from source code
---------
 Clone the repository using Git.
 ```
 git clone https://github.com/vmware/singleton.git
 Or
 git clone git@github.com:vmware/singleton.git
 ```
 Go to singleton/g11n-ws to run a build using Gradle.
 ```
 cd singleton/g11n-ws
 ./gradlew build
 ```
 Jar files will be generated inside the following location:
 ```
 singleton/publish (Eg. singleton/publish/singleton-0.1.0.jar)
 ```
 
To start using Singleton Service
---------
 Navigate to singleton/publish and run the Spring Boot main application.
 ```
 cd ../publish
 java -jar singleton-0.1.0.jar
 ```
 A user interface for testing all available API endpoints will be available in the following URL:
 ```
 https://localhost:8090/i18n/api/doc/swagger-ui.html
 ```
 or
  ```
 http://localhost:8091/i18n/api/doc/swagger-ui.html
 ```
 Sample translation resources will be in the following location:
 ```
 singleton/l10n/bundles
 ```
 Use the following as URI/request parameters to test the API. These parameters come from the sample translation resources.
 ```  
 productName: "SampleProject"
 version: "1.0.0"
 component: "component1" or "component2"
 locale: "en", "ja" or "es"
 ```

Singleton Clients
----------------
 Singleton also provides client bindings to talk with Singleton Service.

 * [JAVA Client](https://github.com/vmware/singleton/tree/g11n-java-client)
 * [Angular Client](https://github.com/vmware/singleton/tree/g11n-angular-client)
 * [JS Client](https://github.com/vmware/singleton/tree/g11n-js-client)
 * [Go Client](https://github.com/vmware/singleton/tree/g11n-go-client)

Online Resources
----------------
 We welcome discussions about and contributions to the project!
 
 * Feel free to start a conversation with us at [Stackoverflow](https://stackoverflow.com/). Tag your question with "vmware-singleton" and we will get back to you.
 * User Guide: 
 * Website: https://vmware.github.io/singleton/


License
--------
Singleton is released under [EPL v2.0 license](https://github.com/vmware/singleton/blob/master/LICENSE.txt).

