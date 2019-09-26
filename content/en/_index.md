---
title: "About Singleton"
date: 2019-09-24T18:41:11+08:00
draft: false
---
After decades of progress and advancement in international communication, the world is more connected than ever. But how much has the rise of modern technology helped us deliver products to customers in their language?

Software globalization (G11n) is the process of delivering software applications in multiple languages. It consists of two parts:

- Localization (L10n) – the process of adapting software applications for a specific international market
- Internationalization (i18n) – the process of developing software applications in a way that enables L10n.

**Singleton** is an open-source application for streamlining software globalization. It standardizes and simplifies software application globalization—not just in L10n, but in i18n, too.

Singleton was originally developed under the R&D Operations and Central Services team in VMware. The core application is written using the Java™ programming language. Client-side code implementation is also available – in JavaScript (Angular, AngularJS and NodeJS web frameworks), C# and Java™ programming languages. While VMware continues to lead the development and maintenance of Singleton, the organization has decided to make it available to the open source community. In May 2019, VMware decided to release an [open-source repository](https://github.com/vmware/singleton) in GitHub. We invite you to collaborate with us to enrich Singleton’s resources and help organizations that adopt it to thrive.

### **How does Singleton work?**

The L10n capabilities of Singleton decouple localized resources from the application software. Singleton delivers a web service that provides an API for sending source artifacts for translation. These artifacts are processed externally, and localized resources are then embedded into the Singleton Service. L10n functions in Singleton are separated from the core application, which makes it possible to update or add new language support independently from the core application’s release cycle—a requirement for fast-paced, agile releases, as well as the SaaS world. Singleton’s interoperability with multiple applications results in a simpler, more consistent quality of L10n across the board.

The i18n capabilities of Singleton eliminate the need for developers to learn different APIs for i18n across technologies and programming languages. It acts as an abstraction layer that provides consistent i18n format (e.g. date, time, number and currency) to various applications that may be written in different programming languages. Its web service API exposes REST endpoints for i18n, which naturally provides abstraction across multiple clients. This allows for a programming language–agnostic i18n implementation, significantly reducing the implementation effort of software developers, allowing them to focus on implementing new features and innovation. It also allows for consistent quality of i18n.

![Singleton](http://blogs.vmware.com/opensource/files/2019/06/Screen-Shot-2019-06-24-at-4.14.35-PM.png)

### **In a Microservice Architecture**

The Singleton approach also allows simplification of globalization in an application with Microservices Architecture. When an application is split into a set of smaller, interconnected services, each of these services exposes its own API, consumes APIs provided by other services and/or provides its own implementation of a web UI. Each microservice operates independently, and some may even have its own database. Singleton provides a single, uniform and abstracted globalization component to these microservices.

![Singleton](http://blogs.vmware.com/opensource/files/2019/06/Screen-Shot-2019-06-24-at-4.15.42-PM.png)

### **Singleton in VMware**

VMware has successfully used Singleton to support the deployment of several products, saving effort and improving quality. It’s either delivered as an on-premise software (SaaP) or consumed as a service (SaaS).

### **On-Premise Environment**

![Singleton](http://blogs.vmware.com/opensource/files/2019/06/Screen-Shot-2019-06-24-at-4.17.03-PM.png)

### **SaaS Environment**

![Singleton](http://blogs.vmware.com/opensource/files/2019/06/Screen-Shot-2019-06-24-at-4.23.31-PM.png)