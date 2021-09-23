/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient;
using SingletonClient.Implementation;


namespace UnitTestSingleton
{
    public class AccessSingleton
    {
        public IConfig Config  { get; }
        public ISingletonConfig ConfigWrapper { get; }
        public string Product { get; }
        public string Version { get; }
        public IRelease Release { get; }
        public bool HasRemote { get; }
        public bool HasLocal { get; }
        public bool Mixed { get; }
        public IReleaseMessages Messages { get; }
        public ITranslation Translation
        {
            get { return Release != null ? Release.GetTranslation() : null; }
        }

        public AccessSingleton(IConfig cfg)
        {
            Product = cfg.GetItem(ConfigConst.KeyProduct).GetString();
            Version = cfg.GetItem(ConfigConst.KeyVersion).GetString();

            Release = I18N.GetRelease(cfg);
            Config = Release.GetConfig();
            Messages = Release.GetMessages();

            ConfigWrapper = new SingletonConfigWrapper(null, Config);

            string internalRoot = ConfigWrapper.GetInternalResourceRoot();
            string externalRoot = ConfigWrapper.GetExternalResourceRoot();

            HasRemote = !string.IsNullOrEmpty(ConfigWrapper.GetServiceUrl());
            HasLocal = !string.IsNullOrEmpty(internalRoot) || !string.IsNullOrEmpty(externalRoot);

            Mixed = HasLocal && HasRemote;
        }

        public ISource Source(string component, string key, string source = null, string comment = null)
        {
            return Translation.CreateSource(component, key, source, comment);
        }

        public void PrepareData()
        {
            string[] locales = { "en", "de", "zh-CN" };
            string[] components = { "about", "aboutadd", "contact" };

            for (int i = 0; i < locales.Length; i++)
            {
                for (int k = 0; k < components.Length; k++)
                {
                    Translation.GetString(locales[i], Source(components[k], "$"));
                }
            }
        }
    }
}
