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

include SgtnClient

SgtnClient.load(file, mode)

SgtnClient::Source.loadBundles(locale)

@Result = SgtnClient::Translation.getString(component, key, locale)

```
## API Usage

### Get a string's translation
SgtnClient::Translation.getString(component, key, locale)

### Get a string's translation and format it with placeholders
SgtnClient::Translation.getString_f(component, key, args, locale)

### Get a component's translations
SgtnClient::Translation.getStrings(component, locale)


## API Usage(with request_store)

Before call below APIs(without locale and component arguments), it requires to set the locale and component in the initial codes.

### Get a string's translation
SgtnClient::T.s(key)

### Get a string's translation and format it with placeholders
SgtnClient::T.s_f(key, args)

### Get a component's translations
SgtnClient::T.c()
