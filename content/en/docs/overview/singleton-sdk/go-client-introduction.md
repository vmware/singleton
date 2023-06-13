---
title: "Go Client Introduction"
date: 2020-04-07T10:30:59+08:00
draft: false
---

The Singleton Go Client is a Singleton SDK to fetch translations from Singleton service or bundles easily.

## Features in Go Client SDK

------------

- Supported interfaces:
  - GetLocaleList: Get supported locale list
  - GetComponentList: Get supported component list
  - GetStringMessage: Get a message with optional arguments. Fallback to default locale is provided.
  - GetComponentMessages: Get messages of a component. Fallback to default locale is **NOT** provided.
- Provide cache management as well as cache registration.
- Support fallback to local bundles when failing to get from server
- Support fallback to default locale when failing to get a **string** message of a nondefault locale.

## APIs Available

### Global

#### LoadConfig

Load configuration from a file.

- Parameters

| Parameter | Type   | Description               |
| :-------- | :----- | :------------------------ |
| path      | string | The path to a config file |

- Return values

| Type    | Description                 |
| :------ | :-------------------------- |
| *Config | The config instance created |
| error   | nil if successful           |

- Example

  ```go
  cfg, err := sgtn.LoadConfig("resource/conf/singletonconfig.json")
  ```

#### Initialize

Initialize the client by the config.

- Parameters

| Parameter | Type    | Description                     |
| :-------- | :------ | :------------------------------ |
| cfg       | *Config | The config to initialize client |

- Return values

None

- Example

  ```go
  sgtn.Initialize(cfg)
  ```

#### GetTranslation

Get translation instance.

- Parameters

None

- Return values

| Type        | Description              |
| :---------- | :----------------------- |
| Translation | The translation instance |

- Example

  ```go
  trans := sgtn.GetTranslation()
  ```

#### SetHTTPHeaders

Set customized http headers. These headers will be sent to server in each request to server.

- Parameters

| Parameter | Type              | Description        |
| :-------- | :---------------- | :----------------- |
| h         | map[string]string | The headers to set |

- Return values

None

- Example

  ```go
  sgtn.SetHTTPHeaders(map[string]string{
    "user": "username",
    "pass": "password",
  })
  ```

#### SetLogger

Set logger for client. If a logger is set, client will write log messages to the logger.

- Parameters

| Parameter | Type   | Description       |
| :-------- | :----- | :---------------- |
| l         | Logger | The logger to set |

- Return values

None

- Example

  ```go
  type MyLogger struct {
    debug, info, warn, err *log.Logger
  }
  func NewLogger() *MyLogger {
    l := MyLogger{}
    l.debug = log.New(os.Stdout, "debug: ", log.LstdFlags)
    l.info = log.New(os.Stdout, "info: ", log.LstdFlags)
    l.warn = log.New(os.Stdout, "warn: ", log.LstdFlags)
    l.err = log.New(os.Stdout, "error: ", log.LstdFlags)
    return &l
  }
  func (l *MyLogger) Debug(message string) {
    l.debug.Println(message)
  }
  func (l *MyLogger) Info(message string) {
    l.info.Println(message)
  }
  func (l *MyLogger) Warn(message string) {
    l.warn.Println(message)
  }
  func (l *MyLogger) Error(message string) {
    l.err.Println(message)
  }

  sgtn.SetLogger(NewLogger())
  ```

#### RegisterCache

Register a separate cache implementation. Client will use this cache to store translation bundles.

- Parameters

| Parameter | Type  | Description           |
| :-------- | :---- | :-------------------- |
| c         | Cache | The cache to register |

- Return values

None

- Example

  ```go
  type MyCache struct {
    m *sync.Map
  }
  func NewCache() MyCache {
    return MyCache{new(sync.Map)}
  }
  func (c MyCache) Get(key interface{}) (value interface{}, found bool) {
    return c.m.Load(key)
  }
  func (c MyCache) Set(key interface{}, value interface{}) {
    c.m.Store(key, value)
  }

  sgtn.RegisterCache(NewCache())
  ```

### Translation interface

#### GetLocaleList

Get available locale list

- Parameters

| Parameter | Type   | Description                |
| :-------- | :----- | :------------------------- |
| name      | string | The name of translation    |
| version   | string | The version of translation |

- Return values

| Type     | Description               |
| :------- | :------------------------ |
| []string | The available locale list |
| error    | nil if successful         |

- Example

  ```go
  locales, err := sgtn.GetTranslation().GetLocaleList(name, version)
  ```

#### GetComponentList

Get available component list

- Parameters

| Parameter | Type   | Description                |
| :-------- | :----- | :------------------------- |
| name      | string | The name of translation    |
| version   | string | The version of translation |

- Return values

| Type     | Description                  |
| :------- | :--------------------------- |
| []string | The available component list |
| error    | nil if successful            |

- Example

  ```go
  components, err := sgtn.GetTranslation().GetComponentList(name, version)
  ```

#### GetStringMessage

Get a message with optional arguments

- Parameters

| Parameter | Type      | Description                            |
| :-------- | :-------- | :------------------------------------- |
| name      | string    | The name of translation                |
| version   | string    | The version of translation             |
| locale    | string    | The locale which the key belongs to    |
| component | string    | The component which the key belongs to |
| key       | string    | The key                                |
| args      | ...string | The arguments to replace placeholders  |

- Return values

| Type   | Description                |
| :----- | :------------------------- |
| string | The translation of the key |
| error  | nil if successful          |

- Example

  ```go
  message, err := sgtn.GetTranslation().GetStringMessage(name, version, locale, component, key, args)
  ```

#### GetComponentMessages

Get messages of a component

- Parameters

| Parameter | Type   | Description                 |
| :-------- | :----- | :-------------------------- |
| name      | string | The name of translation     |
| version   | string | The version of translation  |
| locale    | string | The locale of the component |
| component | string | The component name          |

- Return values

| Type          | Description                   |
| :------------ | :---------------------------- |
| ComponentMsgs | The messages of the component |
| error         | nil if successful             |

- Example

  ```go
  messages, err := sgtn.GetTranslation().GetComponentMessages(name, version, locale, component)
  ```

### ComponentMsgs interface

#### Get

Get a message by key

- Parameters

| Parameter | Type   | Description    |
| :-------- | :----- | :------------- |
| key       | string | The key to get |

- Return values

| Type   | Description                       |
| :----- | :-------------------------------- |
| string | The value of the key              |
| found  | true if found, false if not found |

- Example

  ```go
  messages, err := sgtn.GetTranslation().GetComponentMessages(name, version, locale, component)
  message, found := messages.Get("a key")
  ```

### Cache interface

#### Get

Get an item from cache

- Parameters

| Parameter | Type        | Description    |
| :-------- | :---------- | :------------- |
| key       | interface{} | The key to get |

- Return values

| Type        | Description                       |
| :---------- | :-------------------------------- |
| interface{} | The value of key                  |
| bool        | true if found, false if not found |

#### Set

Set an item to cache

- Parameters

| Parameter | Type        | Description      |
| :-------- | :---------- | :--------------- |
| key       | interface{} | The key to set   |
| value     | interface{} | The value to set |

- Return values

None

### Logger interface

#### Debug

Log a message of *Debug* level

- Parameters

| Parameter | Type   | Description        |
| :-------- | :----- | :----------------- |
| message   | string | The message to log |

- Return values

None

#### Info

Log a message of *Information* level

- Parameters

| Parameter | Type   | Description        |
| :-------- | :----- | :----------------- |
| message   | string | The message to log |

- Return values

None

#### Warn

Log a message of *Warning* level

- Parameters

| Parameter | Type   | Description        |
| :-------- | :----- | :----------------- |
| message   | string | The message to log |

- Return values

None

#### Error

Log a message of *Error* level

- Parameters

| Parameter | Type   | Description        |
| :-------- | :----- | :----------------- |
| message   | string | The message to log |

- Return values

None
