---
title: "DB builder for User guideline"
date: 2020-07-02T11:09:53+08:00
draft: false
weight: 20
---

# 1. Prerequisites

- **Java 8 (Java 9+ are not supported, will get compiler issue)**
- **Git**
- **Docker** 

# 2. Create Singleton Database docker image
## 2.1 Required 
+ **Linux 64-bit Operation System and install docker successfully**

## 2.2 Prepare the init db scripts
### 2.2.1 prepare the create users and databases sql scripts vipinitdb.sql

``` 
CREATE USER pgvipconfig WITH PASSWORD 'vipconfig';
 
CREATE USER pgvipdata WITH PASSWORD 'vipdata';
 
CREATE DATABASE vipconfig OWNER pgvipconfig;
 
CREATE DATABASE vipdata0 OWNER pgvipdata;

CREATE DATABASE vipdata1 OWNER pgvipdata;
```

### 2.2.2 prepare the create config tab sql scripts vipinitconfigtab.sql

```
create table vip_product(id bigint not null, product character varying(100) not null, datasource character varying(50) not null,
                          status  smallint not null default 0, created_userid bigint, crt_time timestamp);
                          
create sequence vip_product_seq increment by 1 minvalue 1 no maxvalue start with 1;
 
alter table vip_product add primary key(id);
 
alter table vip_product alter column id set default nextval('vip_product_seq');
 
alter table vip_product add constraint uk_vip_product unique(product);
```


### 2.2.3 prepare the create data table sql scripts vipinitdatatab.sql

```
create table vip_msg(id bigint not null, product character varying(100) not null,
 
                                         version character varying(50) not null,
 
                                         component character varying(100) not null,
 
                                         locale character varying(100) not null,
 
                                         messages jsonb,
 
                                         crt_time timestamp) partition by list(product);
 
```  



## 2.3 prepare the Dockerfile



```
FROM postgres:10.3
 
MAINTAINER Shi Hu <shihu@wmware.com>
 
 
 
ENV INITDB_PATH /usr/local/initdb
 
 
 
ENV AUTO_RUN_DIR /docker-entrypoint-initdb.d
 
 
 
RUN mkdir -p $INITDB_PATH
 
 
 
COPY ./vipinitdb.sql $INITDB_PATH/
 
COPY ./vipinitdatatab.sql $INITDB_PATH/
 
COPY ./vipinitconfigtab.sql $INITDB_PATH/
 
 
 
COPY ./vipinstalldb.sh $AUTO_RUN_DIR/
 
 
 
RUN chmod a+x $AUTO_RUN_DIR/vipinstalldb.sh
```


 
 
### 2.4 prepare the execute shell vipinstalldb.sh
   
```
psql -U postgres  -d postgres -f $INITDB_PATH/vipinitdb.sql
 
 
 
 psql -U pgvipconfig -d vipconfig -f $INITDB_PATH/vipinitconfigtab.sql
 
 
 
 psql -U pgvipdata -d vipdata0 -f $INITDB_PATH/vipinitdatatab.sql
 
 
 
  psql -U pgvipdata -d vipdata1 -f $INITDB_PATH/vipinitdatatab.sql
```


## 2.5 build the docker image
### 2.5.1 pull the postgresql DB’s official  docker image

```
  docker pull postgres:10.3
```
### 2.5.2 you need the mv the  all file under same directory as following show

```
tiger@docker:~/initdb$ ls
 
Dockerfile  vipinitconfigtab.sql  vipinitdatatab.sql  vipinitdb.sql  vipinstalldb.sh
```

### 2.5.3 execute docker build

```
  docker build -t vipdb_pg:v1 .
```

## 2.6 run docker image

```
tiger@docker:~$ docker run --name='vipdb' -d  -p 5432:5432 vipdb_pg:v1
 
1c9c7864aa3f21af6d0c27932be833465935418a5b6e8a495a1b58dce44fbb78
 
tiger@docker:~$ docker ps
 
CONTAINER ID        IMAGE                                                             COMMAND                  CREATED             STATUS              PORTS                    NAMES
 
1c9c7864aa3f        vipdb_pg:v1   "docker-entrypoint.s..."   4 seconds ago       Up 3 seconds        0.0.0.0:5432->5432/tcp   vipdb
```

# 3. Compile DB builder installer 

## 3.1 Clone the repository using Git.
```
git clone https://github.com/vmware/singleton.git
```
Or
```
git clone git@github.com:vmware/singleton.git
```

## 3.2 Modify the configurations
### 3.2.1 Modify gradle.properties and set datatype to pgdb

```
branchName =

buildNumber = 0.1.0
profileName = 
# if datatype value is pgdb, will include md-data-api-pgimpl module, if datatype value is s3, will include md-data-api-s3impl module, 
# else will include md-data-api-bundleimpl module
datatype=pgdb
# add the sync data module
syncdata=false
```

### 3.2.2 Create vip-manager-i18n/src/main/resources/application-pgdb.yml as following:

```
spring:
  application:
    name: Singleton 
#the following is datasoures configuration
  datasource: 
    name: config
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: org.postgresql.Driver
    url: jdbc:postgresql://{yourIPAddress}:5432/vipconfig
    username: pgvipconfig
    password: vipconfig
#Initialize the connection pool parameters.
    initialSize: 5
    minIdle: 5
    maxActive: 30
#max wait time ms when get a db connection
    maxWait: 60000
# the interval time of check the idle db connection 
    timeBetweenEvictionRunsMillis: 60000
# the min life time of a connection   
    minEvictableIdleTimeMillis: 300000
    validationQuery: SELECT 'x'
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
# open the PSCache and configurate the capacity of every connection  
    poolPreparedStatements: true
    maxPoolPreparedStatementPerConnectionSize: 20
    postgres:
      datasoures: 
        # the vip datanodes' data source
        - name: vipdata1
          type: com.alibaba.druid.pool.DruidDataSource
          driverClassName: org.postgresql.Driver
          url: jdbc:postgresql://{yourIPAddress}:5432/vipdata1
          username: pgvipdata
          password: vipdata
  #Initialize the connection pool parameters.
          initialSize: 5
          minIdle: 5
          maxActive: 30

          maxWait: 60000
 
          timeBetweenEvictionRunsMillis: 60000
 
          minEvictableIdleTimeMillis: 300000

          validationQuery: SELECT 'x'
          testWhileIdle: true
          testOnBorrow: false
          testOnReturn: false
 
          poolPreparedStatements: true
          maxPoolPreparedStatementPerConnectionSize: 20
#configurate  Monitoring & Statistics filters  
        # filters: stat,wall,slf4j
# open merge sql and statistic slow SQL function
         #connectionProperties:druid.stat.mergeSql:true;druid.stat.slowSqlMillis:5000
# combine  DruidDataSources monitor data
         #useGlobalDataSourceStat:true 
    
        - name: vipdata0
          type: com.alibaba.druid.pool.DruidDataSource
          driverClassName: org.postgresql.Driver
          url: jdbc:postgresql://{yourIPAddress}:5432/vipdata0
          username: pgvipdata
          password: vipdata
    #Initialize the connection pool parameters.
          initialSize: 5
          minIdle: 5
          maxActive: 30
          maxWait: 60000
 
          timeBetweenEvictionRunsMillis: 60000
 
          minEvictableIdleTimeMillis: 300000

          validationQuery: SELECT 'x'
          testWhileIdle: true
          testOnBorrow: false
          testOnReturn: false
 
          poolPreparedStatements: true
          maxPoolPreparedStatementPerConnectionSize: 20
 
        # filters: stat,wall,slf4j

         #connectionProperties:druid.stat.mergeSql:true;druid.stat.slowSqlMillis:5000

         #useGlobalDataSourceStat:true 

# logging
logging:
  config: classpath:log4j2-spring.xml

# EMBEDDED SERVER CONFIGURATION (ServerProperties)
server:
  scheme: https
  port: 8090
  max-http-header-size: 8192
  https: 
    key-store: classpath:vip.jks
    key-store-password: 123456
    key-password: 123456
    key-store-type: JKS
    key-alias: server
  http: 
    port: -1
  trace:
    enable: false
#collection source server
source:
  cache:
    flag: false
    server:
      url: https://localhost:8088
#translation synch
translation:
  synch:
    git:
      flag: false
pseudo:
  notExistSourceTag: '@@'
  existSourceTag: '#@'
csp:
  api:
    auth:
      enable: false
vipservice:
  authority:
    enable: false
    session:
      expiretime: 30
    token:
      expiretime: 130
  cross:
    domain:
      enable: true
      alloworigin: '*'
      maxage: 3600
      allowmethods: GET, POST, PUT, DELETE, OPTIONS
      allowheaders: csp-auth-token, Content-Type
cache-control:
  value=max-age: 604800, public
#swagger2-ui switch
swagger-ui:
  enable: true
#actuator
management:
  endpoints:
    web:
      base-path: /actuator
      exposure:
        include: info, health
    jmx: 
      exposure:
        exclude: '*'
```

### 3.2.3  Modify vip-manager-i18n/src/main/resources/application.properties as following

```
##
#Copyright 2019 VMware, Inc.
#SPDX-License-Identifier: EPL-2.0
##

#build version information
build.number.builddate=3/14/2018
build.number.buildnumber=7953895
build.number.branch=master
# enable default config 
spring.profiles.active=pgdb
```

### 3.2.4 Build the singleton source code

Go to singleton/g11n-ws to run a build using Gradle.

```
cd singleton/g11n-ws
./gradlew build
```
Jar files will be generated inside the following location:

singleton/publish (Eg. singleton/publish/singleton-0.1.0.jar)

# 4 To start using Singleton Service

## 4.1 Start Singleton server
Navigate to singleton/publish and run the Spring Boot main application.

```
cd ../publish
java -jar singleton-0.1.0.jar
```

A user interface for testing all available API endpoints will be available in the following URL:

https://localhost:8090/i18n/api/doc/swagger-ui.html
or

http://localhost:8091/i18n/api/doc/swagger-ui.html


## 4.2 Add the test data

```
curl -X PUT "https://localhost:8090/i18n/api/v2/translation/products/Testing/versions/1.0.0" -H "accept: application/json;charset=UTF-8" -H "Content-Type: application/json" -d "{ \"data\": { \"creation\": { \"operationid\": \"test12345\" }, \"dataOrigin\": \"string\", \"machineTranslation\": true, \"productName\": \"Testing\", \"pseudo\": false, \"translation\": [ { \"component\" : \"component1\", \"messages\": { \"sample.apple\" : \"apple\", \"sample.banana\" : \"banana\", \"sample.cat\" : \"cat\", \"sample.dog\" : \"dog\", \"sample.egg\" : \"egg\", \"sample.fly\" : \"fly\", \"sample.giant\" : \"giant\" }, \"locale\" : \"en\"},{ \"component\" : \"component1\", \"messages\" : { \"sample.apple\" : \"manzana\", \"sample.banana\" : \"plátano\", \"sample.cat\" : \"gato\", \"sample.dog\" : \"perro\", \"sample.egg\" : \"huevo\", \"sample.fly\" : \"volar\", \"sample.giant\" : \"gigante\" }, \"locale\" : \"es\"} ], \"version\": \"1.0.0\" }, \"requester\": \"ManulTest\"}"
```
You can access other API  use following data.

```
{
"prductName": "Testing",
"version": "1.0.0",
"messages":[
{
    "component" : "component1",
    "messages": {
        "sample.apple" : "apple",
        "sample.banana" : "banana",
        "sample.cat" : "cat",
        "sample.dog" : "dog",
        "sample.egg" : "egg",
        "sample.fly" : "fly",
        "sample.giant" : "giant"
    },
    "locale" : "en"
},
{
  "component" : "component1",
  "messages" : {
    "sample.apple" : "manzana",
    "sample.banana" : "plátano",
    "sample.cat" : "gato",
    "sample.dog" : "perro",
    "sample.egg" : "huevo",
    "sample.fly" : "volar",
    "sample.giant" : "gigante"
  },
  "locale" : "es"
}]
}
```
