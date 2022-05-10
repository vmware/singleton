# Singleton Client for Ruby

## Prerequisites
- Ruby version: 3.0.0 or above
- Bundler version: 2.2.3 or above

## Run Unit Test
`rake spec`

## Usage

Basic Usage:

```ruby
require 'singleton-client'

Sgtn.load_config(file, env)
result = Sgtn.translate(key, component, locale)
```
## API Usage

### Get a string's translation
`result = Sgtn.translate(key, component, locale)`

### Get a string's translation with default value when no translation
`result = Sgtn.translate(key, component, locale) { 'default value' }`

### Get a string's translation and format it with placeholders
`result = Sgtn.translate(key, component, locale, **args)`

### Get pluralized translation
`result = Sgtn.translate(key, component, locale, **args)`

### Get translations of a bundle
`result = Sgtn.get_translations(component, locale)`

### Set locale for a request
`Sgtn.locale = 'en'`

### Get locale of the request
`result = Sgtn.locale`

### Get a string's translation with locale set
```ruby
Sgtn.locale = 'en'
result = Sgtn.translate(key, component)
```

### Get translations of a bundle with locale set
```ruby
Sgtn.locale = 'en'
result = Sgtn.get_translations(component)
```
