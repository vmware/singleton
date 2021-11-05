
## Prerequisites
- Ruby 2.7.2 or below(Ruby 3.0.0 not worked to run rackup)

## Run Sample App
    1. cd samples folder
    2. bundle install
    3. bundle exec rackup -p 3000

## Run Sample in web
    http://localhost:3000/translation/all
    
    For formatting's localization, run: http://localhost:3000/formatting/all

## Run Sample in console

    bundle exec ruby translation/all.rb
    bundle exec ruby formatting/all.rb

## Samples directory

1. ./translation    - transaltion samples
2. ./formatting     - ormatting samples
3. ./config         - config files
4. ./views          - ui files

## Configuration

Configuration in `config/sgtnclient.yml`

```yaml
test: &default

  # Mode can be 'live' or 'sandbox'
  # For sandbox mode, it will produce debug log messages
  mode: sandbox

  # # Product name
  product_name: test

  # # Bundle version
  version: 4.8.1

  # # HTTP proxy
  vip_server: https://server:8090

  # # Mode of bundle: online/offline
  bundle_mode: offline

  # # Translation bundle path, the child folder is product name
  translation_bundle: ./spec/config/locales/l10n/bundles

  # # Source bundle path, the children folders are component names
  source_bundle: ./spec/config/locales/default

  # # memory cache's expration(minutes), default value is 24*60
  cache_expiry_period: 36

  # # disable cache, it's optional setting
  disable_cache: true

development:
  <<: *default

production:
  <<: *default
  mode: live

```

## API Usage: getString

Basic Usage:

```ruby
require 'singleton-ruby'

include SgtnClient

SgtnClient.load(file, mode)

SgtnClient::Source.loadBundles(locale)

@Result = SgtnClient::Translation.getString(component, key, locale)

```

More detailed examples:

```ruby

require 'singleton-ruby'

include SgtnClient


# Load config file to initialize app
SgtnClient.load("./config/sgtnclient.yml", "test")

# Load the default bundles of all components existing in the path {source_bundle}
# The {source_bundle} is defined in configuration file sgtnclient.yaml
SgtnClient::Source.loadBundles("default")

# Get translation
@Result = SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")

```

## API Usage: DateTime/Date/Time

Basic Usage:

```ruby
require 'singleton-cldr'

DateTime.new(...).to_<format>_s(locale)
Date.new(...).to_<format>_s(locale)
Time.new(...).to_<format>_s(locale)

```

More detailed examples:
```ruby
# localize a datetime to es(Spanish)
d = DateTime.new(2007,11,19,8,37,48,"-06:00")
d.to_full_s(:es)
d.to_long_s(:es)
d.to_medium_s(:es)
d.to_short_s(:es)
# Note: for the date and time, the usages are same with dateTime


# pluralize a string
str = 'there %<{ "cat_count": { "0": "no cat", one": "is one cat", "other": "are %{cat_count} cats" } }> in the room'
result0 = str.to_plural_s(:en, { :cat_count => 0 })
result = str.to_plural_s(:en, { :cat_count => 1 }) # the result would be 'there is one cat in the room'

```