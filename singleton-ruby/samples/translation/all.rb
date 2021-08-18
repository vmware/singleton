require 'singleton-ruby'

include SgtnClient

# load config file to initialize app
SgtnClient.load("./config/sgtnclient.yml", "test")

# load english bundle for fallback
SgtnClient::Source.loadBundles("default")

# get translation
@Result = SgtnClient::Translation.getString("JAVA", "com.vmware.loginsight.web.settings.stats.StatsTable.host", "zh-Hans")

# get non-existing translation
#@Result = SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")