SHELL = /bin/bash

export LC_ALL := en_US.UTF-8
export LANG := en_US.UTF-8
export LANGUAGE := en_US.UTF-8

ifeq ($(OS), Windows_NT) # {
$(error ERROR: this build must be run on Linux.)
else # }{
TOPDIR=..
MAKEFILE_DIR=$(abspath $(dir $(firstword $(MAKEFILE))))
export SRCROOT := $(abspath $(MAKEFILE_DIR)$(TOPDIR))
endif # }

# Define variables.
TCROOT                  := /build/toolchain
#GRADLE_BIN              := $(TCROOT)/noarch/gradle-4.3.1/bin/gradle
export JAVA_HOME        := $(TCROOT)/lin64/jdk-1.8.0_191
export MAVEN_OPTS       := -Xmx512m
export JAVA_OPTS        := $(MAVEN_OPTS)

vip: publish

GOBUILD_TARGET := g11n-service
GOBUILD_AUTO_COMPONENTS ?= 1
include $(SRCROOT)/support/mk/defs.mk


prepare:
	$(MKDIR) -p $(SRCROOT)/publish
	@echo "***** Before unzip resources.zip *****"
	ls          $(SRCROOT)/g11n-ws/vip-manager-i18n/src/main/resources
	unzip -o    $(GOBUILD_TRANSLATION_ROOT)/resources.zip -d $(SRCROOT)/g11n-ws/vip-manager-i18n/src/main/resources/
	@echo "***** After unzip resources.zip *****"
	ls          $(SRCROOT)/g11n-ws/vip-manager-i18n/src/main/resources
	@echo "*****java home bin content*****"
	ls          $(JAVA_HOME)/bin/

#remove: prepare
#	@echo "***** Remove *latest.json files *****"
#	$(PYTHON) $(SRCROOT)/g11n-ws/tools/vipScripts/pseudo/pseudo.py bundle_path=$(SRCROOT)/g11n-ws/vip-manager-i18n/src/main/resources/ ops=remove_latest

build: prepare
	chmod u+x ./gradlew
	$(PYTHON) $(SRCROOT)/g11n-ws/tools/vipScripts/build/build.py filepath=$(SRCROOT)/g11n-ws/vip-manager-i18n/src/main/resources/application.properties buildpath=$(SRCROOT) branch=master
#  test gradlew and make publish DIR
	$(MKDIR) -p $(PUBLISH_DIR)
	./gradlew -v
	


#   Create bundle build vip-manager-i18n
#	$(PYTHON) $(SRCROOT)/g11n-ws/tools/vipScripts/pseudo/pseudo.py bundle_path=$(SRCROOT)/g11n-ws/vip-manager-i18n/src/main/resources/ ops=remove_latest
	./gradlew build
	mv -f     $(SRCROOT)/publish/vip-manager-i18n-*.jar $(PUBLISH_DIR)/vip-manager-i18n-0.0.1-SNAPSHOT.jar
	mv -f     $(SRCROOT)/publish/vip-manager-l10n-release-*.jar $(PUBLISH_DIR)/vip-manager-l10n.jar
	$(CP) -f  $(GOBUILD_TRANSLATION_ROOT)/resources.zip $(PUBLISH_DIR)/resources.zip
	
#   create pseudo build
	$(PYTHON) $(SRCROOT)/g11n-ws/tools/vipScripts/pseudo/pseudo.py bundle_path=$(SRCROOT)/g11n-ws/vip-manager-i18n/src/main/resources/ ops=create_pesudo
	./gradlew build	
	mv -f     $(SRCROOT)/publish/vip-manager-i18n-*.jar $(PUBLISH_DIR)/vip-manager-i18n-0.0.1-SNAPSHOT_pseudo.jar
	$(CP) -f  $(GOBUILD_TRANSLATION_ROOT)/resources.zip $(PUBLISH_DIR)/resources_pseudo.zip

#   Create syncdata build
	$(PYTHON) $(SRCROOT)/g11n-ws/tools/vipScripts/build/build_syncdata.py filepath=$(SRCROOT)/g11n-ws/gradle.properties syncdata=true
	./gradlew build 
	mv -f     $(SRCROOT)/publish/vip-manager-i18n-*.jar $(PUBLISH_DIR)/vip-manager-i18n-0.0.1-SNAPSHOT_syncdata.jar
	mv -f     $(SRCROOT)/publish/vip-manager-l10n-release-*.jar $(PUBLISH_DIR)/vip-manager-l10n_syncdata.jar
	
	$(PYTHON) $(SRCROOT)/g11n-ws/tools/vipScripts/build/build_syncdata.py filepath=$(SRCROOT)/g11n-ws/gradle.properties syncdata=false

#   Create DB build
	$(RM) -rf $(SRCROOT)/g11n-ws/vip-manager-i18n/src/main/resources/l10n
	$(RM) -rf $(SRCROOT)/g11n-ws/vip-manager-i18n/build/resources/main/l10n
	$(RM) -f  $(SRCROOT)/g11n-ws/vip-manager-i18n/build/distributions/resources.zip
	$(PYTHON) $(SRCROOT)/g11n-ws/tools/vipScripts/build/build_db.py filepath=$(SRCROOT)/g11n-ws/gradle.properties datatype=pgdb
	./gradlew build -x test
	mv -f     $(SRCROOT)/publish/vip-manager-i18n-*.jar $(PUBLISH_DIR)/vip-manager-i18n-0.0.1-SNAPSHOT_db.jar

publish: build
	@echo "***** Publishing Builds *****"
#	$(MKDIR) -p $(PUBLISH_DIR)
#	$(CP) -f $(GOBUILD_TRANSLATION_ROOT)/resources.zip $(PUBLISH_DIR)/resources.zip
#	mv -f $(SRCROOT)/publish/vip-manager-i18n-*.jar $(PUBLISH_DIR)/vip-manager-i18n-0.0.1-SNAPSHOT.jar
#	mv -f $(SRCROOT)/publish/vip-manager-i18n-*.war $(PUBLISH_DIR)/vip-manager-i18n-0.0.1-SNAPSHOT.war
#	mv -f $(SRCROOT)/publish/vip-manager-l10n-release-*.jar $(PUBLISH_DIR)/vip-manager-l10n.jar
	mv -f $(SRCROOT)/publish/vip4java-*.jar $(PUBLISH_DIR)/vip4java-0.91.jar
	mv -f $(SRCROOT)/publish/vip-manager-l10n-agent-release-*.jar $(PUBLISH_DIR)/vip-manager-l10n-agent.jar

clean:
	$(RM) -rf $(BUILDROOT)
