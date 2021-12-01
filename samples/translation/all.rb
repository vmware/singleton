require 'singleton-ruby'

include SgtnClient

# load config file to initialize app
SgtnClient.load("./config/sgtnclient.yml", "test")

# load english bundle for fallback
SgtnClient::Source.loadBundles("default")

# get translation
#@Result = SgtnClient::Translation.getString("JAVA", "helloworld", "zh-Hans")

# get non-existing translation
puts SgtnClient::Translation.getString("JAVA", "hello", "zh-Hans")

# get pluralized translation
#@Result = SgtnClient::Translation.getString_p("JAVA", "plural_key", { :cat_count => 1 }, "zh-Hans")

# get formatting translation
#@Result = SgtnClient::Translation.getString_f("JAVA", "welcome", ["虚拟世界", "机器人"], "zh-Hans")