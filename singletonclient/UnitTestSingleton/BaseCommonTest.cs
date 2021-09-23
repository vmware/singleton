/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;

using SingletonClient;
using SingletonClient.Implementation;


namespace UnitTestSingleton
{
    public class BaseCommonTest : BaseTest
    {
        protected void DoCommonTestConfig(AccessSingleton access)
        {
            Show("test config", access.Product);
            Assert.AreEqual(Version, access.Version);

            Console.WriteLine("      product : " + access.Product);
            Console.WriteLine("      version : " + access.Version);
            Console.WriteLine("      remote : " + access.ConfigWrapper.GetServiceUrl());
            Console.WriteLine("      internal : " + access.ConfigWrapper.GetInternalResourceRoot());
            Console.WriteLine("      external : " + access.ConfigWrapper.GetExternalResourceRoot());
            Console.WriteLine("      default_locale : " + access.ConfigWrapper.GetDefaultLocale());
            Console.WriteLine("      source_locale : " + access.ConfigWrapper.GetSourceLocale());
            Console.WriteLine("      pseudo : " + access.ConfigWrapper.IsPseudo());

            ISingletonConfig configWrapper = new SingletonConfigWrapper(null, access.Config);
            string productName = configWrapper.GetProduct();
            Assert.AreEqual(access.Product, productName);

            SingletonConfig theConfig = (SingletonConfig)access.Config;
            string jointKey = configWrapper.GetReleaseName();
            Assert.AreEqual(access.Product + "^" + access.Version, jointKey);

            IConfigItem textItem = configWrapper.GetConfig().GetItem("_none");
            Assert.AreEqual(null, textItem);

            Assert.AreEqual(null, I18N.GetConfig(access.Product, null));

            IConfig config = I18N.GetConfig(access.Product, access.Version);
            List<string> localeList = config.GetLocaleList(null);
            Assert.AreEqual(0, localeList.Count);
            localeList = config.GetLocaleList("");
            Assert.AreEqual(0, localeList.Count);
            localeList = config.GetLocaleList("NONE");
            Assert.AreEqual(0, localeList.Count);
        }

        protected void DoCommonTestTranslationBefore(AccessSingleton access)
        {
            ISource src = access.Source("about", null, null, null);
            Assert.AreEqual(null, src);
            src = access.Source(null, null, null, null);
            Assert.AreEqual(null, src);

            src = access.Source("about", "about.message", "_content", "_comment");
            Assert.AreEqual("_comment", src.GetComment());

            string translation = access.Translation.GetString("de", null);
            Assert.AreEqual(null, translation);

            access.Translation.SetCurrentLocale(Locale);
            string current = access.Translation.GetCurrentLocale();
            Show("current", current);
            Assert.AreEqual("de", current);

            CheckLocale(access.Translation, "ZH_cn");
            CheckLocale(access.Translation, "EN_us");
        }

        protected void DoCommonTestTranslation(AccessSingleton access)
        {
            RunTestData(access, "TestShowCache");
            DoCommonTestTranslationBefore(access);

            if (access.ConfigWrapper.IsPseudo())
            {
                if (access.HasRemote)
                {
                    RunTestData(access, "TestGetStringPseudoOnline");
                    if (access.HasLocal)
                    {
                        RunTestData(access, "TestGetStringPseudoOnlineWithLocal");
                    }
                    else
                    {
                        RunTestData(access, "TestGetStringPseudoOnlineOnly");
                    }
                }
                else
                {
                    RunTestData(access, "TestGetStringPseudoOffline");
                }

                RunTestData(access, "TestShowCache");
                return;
            }

            RunTestData(access, "TestGetString1");
            RunTestData(access, "TestGetString1T");
            RunTestData(access, "TestGetString1A");
            RunTestData(access, "TestGetString2");

            if (access.Mixed)
            {
                RunTestData(access, "TestGetString3");
            }

            string groupName = access.ConfigWrapper.IsSourceLocaleDefault() ? 
                "TestGetStringSameLocale" : "TestGetStringDifferentLocale";
            RunTestData(access, groupName);
            RunTestData(access, "TestGetStringTemp");

            DoCommonTestTranslationAfter(access);
            RunTestData(access, "TestShowCache");
        }

        protected void DoCommonTestTranslationAfter(AccessSingleton access)
        {
            ISource srcObj = access.Source("about", "about.message");
            string translation = access.Translation.Format("de", srcObj, "AAA");
            Assert.AreEqual("Ihrer Bewerbungs Beschreibung Seite.", translation);
            translation = access.Translation.Format("de", null, "AAA");
            Assert.AreEqual(null, translation);

            access.Translation.SetCurrentLocale("zh-CN");
            string locale = access.Translation.GetCurrentLocale();
            Assert.AreEqual("zh-CN", locale);

            access.Translation.SetCurrentLocale("zh-Hans");
            locale = access.Translation.GetCurrentLocale();
            Assert.AreEqual("zh-Hans", locale);

            ISource src = access.Source("about", "about.title", null, null);
            translation = access.Translation.Format("zh-CN", src, access.Product, access.Version);
            Assert.AreEqual("关于 Version 1.0.0 of Product " + access.Product, translation);
            translation = access.Translation.Format("zh-CN", src, access.Product);
            Assert.AreEqual("关于 Version {1} of Product " + access.Product, translation);
        }

        protected void DoCommonTestMessages(AccessSingleton access)
        {
            Show("test messages", access.Product);
            if (!access.ConfigWrapper.IsLoadOnStartup())
            {
                PrepareData(access);
            }

            IReleaseMessages messages = access.Messages;
            List<string> localeList = messages.GetLocaleList();
            Assert.AreEqual(true, MatchLocale(access, localeList, "de"));
            List<string> componentList = messages.GetComponentList();
            Assert.AreEqual(true, componentList.Contains("about"));

            ILocaleMessages languageMessages = messages.GetLocaleMessages(null);
            Assert.AreEqual(null, languageMessages);

            languageMessages = messages.GetLocaleMessages("de");
            componentList = languageMessages.GetComponentList();
            Assert.AreEqual(true, componentList.Contains("about"));

            string translation = languageMessages.GetString("about", null);
            Assert.AreEqual(null, translation);
            translation = languageMessages.GetString(null, "key");
            Assert.AreEqual(null, translation);

            RunTestData(access, "TestGetStringFromMessages1");

            IComponentMessages componentMessages = languageMessages.GetComponentMessages("about");
            Assert.AreEqual("de", componentMessages.GetLocale());
            Assert.AreEqual("about", componentMessages.GetComponent());

            ICollection<string> keys = componentMessages.GetKeys();

            Dictionary<string, ILocaleMessages> allTranslations = messages.GetAllLocaleMessages();
            languageMessages = allTranslations["de"];
            translation = languageMessages.GetString("about", "about.message");
            Assert.AreEqual("Ihrer Bewerbungs Beschreibung Seite.", translation);
        }
    }
}
