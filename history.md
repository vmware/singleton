# 0.0.1
- Support singleton client configuration
- Support memory-based cache
- Support log
- Support string-based API(online mode)

# 0.0.2
- Support string-based API(offline mode)
- Add exception handling

# 0.0.3
- Fix build issue

# 0.0.4
- Support default yaml bundle definition and fallback
- Activate 'mode' parameter to support sandbox build and live build
- Improvments and bug fix: 
    1. Fix cache can't work issue
    2. Change coniguration name 'offline_bundle' to 'translation_bundle'
    3. Change some info logs to debug logs

# 0.0.5
- Support disabling cache by configuration 'disable_cache'
- Support setting expired period by configuration 'cache_expiry_period'
- Fixed Bugs
    
# 0.0.6
- Change 'cache_expiry_period' unit from hour to minute
- Fixed Bugs

# 0.0.7
- Support some types of L2:
    1. Date(full, long, medium, short)
    2. DateTime(full, long, medium, short)
    3. Time(full, long, medium, short)

# 0.0.8
- Fix dependency missing issue when not manually add it in project
- Fix bug

# 0.0.9
- Remove L2 support

# 0.1.0
- Support L3 locale fallback
- Improvement: customize a 'default' locale for default bundle, see sample app for usage details.
- Fix bugs

# 0.1.1
- Fix bugs:
[VIP-3120] [Ruby Client]Add feature of replacing placeholders for interface getString
[VIP-3126] [Ruby Client]Don’t restrict the version of rest-client gem
[VIP-3127] [Ruby Client]Don't hardcode source file name as 'default.yml'

# 0.1.2
- Add component-based API: getStrings(component, locale)

# 0.1.3
- Bug fix and code clean

# 0.1.4
- Add a set of simplest translation APIs for simplying product calling codes
- Support request_store to reduce component and locale arugments in APIs

# 0.1.5
- Support placeholders order adjustment, e.g "%2$s, welcome login %1$s!"
- Code cleanup
