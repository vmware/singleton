# Singeleton client for Ruby

## Prerequisites
- Ruby version: 3.0.0 or above
- Bundler version: 2.2.3 or above

## Run Unit Test
rake spec:unit

## Usage

Basic Usage:

```ruby
require 'singleton-ruby'

include SgtnClient

SgtnClient.load(file, mode)

SgtnClient::Source.loadBundles(locale)

@Result = SgtnClient::Translation.getString(component, key, locale)

```
## API

### Get a string's translation
SgtnClient::Translation.getString(component, key, locale)

### Get a string's translation and format it with placeholders
SgtnClient::Translation.getString_F(component, key, args, locale)





