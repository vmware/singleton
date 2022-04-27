# Singeleton client for Ruby

## Prerequisites
- Ruby version: 3.0.0 or above
- Bundler version: 2.2.3 or above

## Run Unit Test
rake spec:unit

## Usage

Basic Usage:

```ruby
require 'singleton-client'

Singleton.load_config(file, mode)

result = Singleton.translate(key, component, locale)

```
## API Usage

### Get a string's translation
Singleton.translate(key, component, locale)

### Get a string's translation and format it with placeholders
Singleton.translate(key, component, locale, **args)

### Get translations of a bundle
Singleton.get_translations(component, locale)
