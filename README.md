# Singleton Client for Ruby

## Prerequisites
- Ruby version: 3.0.0 or above
- Bundler version: 2.2.3 or above

## Run Unit Test
`rake spec`

## Usage

### Basic Usage:

```ruby
require 'singleton-client'

Singleton.load_config(file, env)
result = Singleton.translate(key, component, locale)
```
### API Usage

#### Get a string's translation
`result = Singleton.translate(key, component, locale)`

#### Get a string's translation with default value when no translation
`result = Singleton.translate(key, component, locale) { 'default value' }`

#### Get a string's translation and format it with placeholders
`result = Singleton.translate(key, component, locale, **args)`

#### Get pluralized translation
`result = Singleton.translate(key, component, locale, **args)`

#### Get translations of a bundle
`result = Singleton.get_translations(component, locale)`

#### Set locale for a request
`Singleton.locale = 'en'`

#### Get locale of the request
`result = Singleton.locale`

#### Get a string's translation with locale set
```
Singleton.locale = 'en'
result = Singleton.translate(key, component)
```

#### Get translations of a bundle with locale set
```
Singleton.locale = 'en'
result = Singleton.get_translations(component)
```
