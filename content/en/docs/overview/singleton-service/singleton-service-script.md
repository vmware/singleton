---
title: "Singleton Service Script"
date: 2020-03-02T14:52:31+08:00
draft: false
---

Singleton adds script files to help user to start/check/stop Singleton Service build.

How to generate Singleton Script
------------------

The steps:

1. Clone Singleton Service code using Git
```
git clone git@github.com:vmware/singleton.git
```

2. Complie a build using Gradle wrapper under `./g11n-ws` folder
```
./gradlew build -x test
```

3. Go to `./Singleton/Publish` folder, find `singletonScripts-0.1.0.zip`, and extract it, you will get 4 files as below:
```
singletonall.sh
singletonstart.sh
singletoncheck.sh
singletonstop.sh
```

How to use Singleton Script
------------------

### singletonall.sh 
This script is used to start, stop, and status detect Singleton Service, also restart it when something wrong in Singleton Service running .

**Prepare:**
Make sure that Singleton Service build (name like *singleton-xxx.jar*)  and *singletonall.sh* to same directory.

**Usage:** 
```
	./singletonall.sh [options]

where options include:
	start	start Singleton Service
	status	check Singleton Service current status
	check	check whether Singleton Sevice is running, will restart it if NO running
	stop	stop Singleton Service
```


### singletonstart.sh 
This script is used to start Singleton Service build name like singleton-xxx.jar by default, also can start specific xxx.jar app by given parameter. 

**Prepare:**
Make sure that Singleton Service build (name like *singleton-xxx.jar*) or any xxx.jar file you want to start, and *singletonstart.sh* to same directory.

**Usages:** 
```
 	 ./singletonstart.sh
  or 
 	 ./singletonstart.sh jarfile
```

### singletoncheck.sh
This script is used to check running status of singleton-xxx.jar by default, also can check the running status of any xxx.jar by given parameter. And it will restart the app when something wrong in running.

**Prepare:**
Make sure that Singleton Service build (name like *singleton-xxx.jar*) or any xxx.jar file you want to check,  and *singletoncheck.sh* to same directory.

**Usage:**
```
	./singletoncheck.sh 
or
	./singletoncheck.sh  jarfile
```


### singletonstop.sh
This script is used to stop Singleton Service build name like singleton-xxx.jar by default, also can stop specific xxx.jar app by given parameter.

**Prepare:**
Make sure that Singleton Service build (name like *singleton-xxx.jar*) or any xxx.jar file you want to check,  and *singletonstop.sh* to same directory.

**Usage:**
```
	./singletonstop.sh
or
	./singletonstop.sh jarfile
```