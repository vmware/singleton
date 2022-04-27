require 'singleton-client'

# load config file to initialize app
Singleton.load_config("./config/sgtnclient.yml", "test")

@Result = {}

# get translation
@Result['tranlate a string'] = Singleton.t("helloworld", "JAVA", "zh-Hans")

# translate a string in source
@Result['tranlate a string only in source'] =  Singleton.t("hello", "JAVA", "zh-Hans")

# get non-existing translation
@Result['tranlate a nonexistent string'] =  Singleton.t("nonexistent", "JAVA", "zh-Hans")

# get pluralized translation
@Result['tranlate a plural string - 0'] = Singleton.t("plural_key", "JAVA", "zh-Hans", :cat_count => 0, place: '房间')
@Result['tranlate a plural string - 1'] = Singleton.t("plural_key", "JAVA", "zh-Hans", :cat_count => 1, place: '盒子')
@Result['tranlate a plural string - 2'] = Singleton.t("plural_key", "JAVA", "zh-Hans", :cat_count => 2, place: 'bush')

# get formatting translation
@Result['tranlate a string with placeholders'] = Singleton.t("welcome", "JAVA", "zh-Hans", place: "虚拟世界", name:"机器人")
