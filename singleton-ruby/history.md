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
- Fixed Bugs:
    https://jira.eng.vmware.com/browse/VIP-2963
    https://jira.eng.vmware.com/browse/VIP-2965
    https://jira.eng.vmware.com/browse/VIP-2968
    
# 0.0.6
- Change 'cache_expiry_period' unit from hour to minute
- Fixed Bugs:
    https://jira.eng.vmware.com/browse/VIP-2964
    https://jira.eng.vmware.com/browse/VIP-2975
    https://jira.eng.vmware.com/browse/VIP-2976

# 0.0.7
- Support some types of L2:
    1. Date(full, long, medium, short)
    2. DateTime(full, long, medium, short)
    3. Time(full, long, medium, short)

# 0.0.8
- Fix dependency missing issue when not manually add it in project
- Fix bug: https://jira.eng.vmware.com/browse/VIP-3015

# 0.0.9
- Remove L2 support

# 0.1.0
- Support L3 locale fallback
- Improvement: customize a 'default' locale for default bundle, see sample app for usage details.
- Fix bugs: https://jira.eng.vmware.com/browse/VIP-2967
            https://jira.eng.vmware.com/browse/VIP-2965
            



