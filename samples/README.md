
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

  # # Singleton server
  vip_server: https://server:8090

  # # Translation bundle path, the child folder is product name
  translation_bundle: ./spec/config/locales/l10n/bundles

  # # Source bundle path, the children folders are component names
  source_bundle: ./spec/config/locales/default

  # # memory cache's expration(minutes), default value is 24*60
  cache_expiry_period: 36

development:
  <<: *default

production:
  <<: *default
  mode: live
```

## API Usage: Sgtn.translate

Basic Usage:

```ruby
require 'singleton-client'

Sgtn.load_config(file, env)
result = Sgtn.translate(key, component, locale)
```

More detailed examples:

```ruby
require 'singleton-client'

# Load config file to initialize app
Sgtn.load_config("./config/sgtnclient.yml", "test")

# Get a string's translation
result = Sgtn.translate("helloworld", "JAVA", "zh-Hans")

# Get a string's translation with default value when no translation
result = Sgtn.translate("helloworld", "JAVA", "zh-Hans") { 'default value' }

# Get a string's translation and format it with placeholders
result = Sgtn.translate("welcome", "JAVA", "zh-Hans", name: 'robot', place: 'world')

# Get pluralized translation
result = Sgtn.translate("plural_key", "JAVA", "zh-Hans", :cat_count => 1)

# Get a string's translation with locale set before translating
Sgtn.locale = 'en'
result = Sgtn.translate("helloworld", "JAVA")
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
d.l_full_s(:es)
d.l_long_s(:es)
d.l_medium_s(:es)
d.l_short_s(:es)
# Note: for the date and time, the usages are same with dateTime
```
