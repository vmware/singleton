/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Release;
using SingletonClient.Implementation.Support;
using SingletonClient.Implementation.Support.ByKey;
using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace SingletonClient.Implementation
{
    public interface ISingletonRelease
    {
        IRelease GetRelease();
        void SetConfig(IConfig config);
        ISingletonConfig GetSingletonConfig();
        ISingletonApi GetApi();
        ISingletonUpdate GetUpdate();
        ICacheMessages GetReleaseMessages();
        ISingletonByKey GetSingletonByKey();
        IAccessService GetAccessService();
        ILog GetLogger();
        void AddLocalScope(string locale, string component);
        SingletonUseLocale GetUseLocale(string locale, bool asSource);
        bool IsInScope(ISingletonLocale singletonLocale, string component);
    }

    public class SingletonRelease : SingletonReleaseForCache, ISingletonRelease, ISingletonAccessRemote,
        IRelease, IReleaseMessages, ITranslation, ILog
    {
        private bool _isLoadedOnStartup = false;

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public IRelease GetRelease()
        {
            return this;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public void SetConfig(IConfig config)
        {
            InitForBase(this, config, this);

            if (!InitForCache())
            {
                return;
            }

            if (_config.IsOnlineSupported())
            {
                GetDataFromRemote();
            }
            else
            {
                CheckLoadOnStartup(false);
            }
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ISingletonUpdate GetUpdate()
        {
            return _update;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ICacheMessages GetReleaseMessages()
        {
            return _productCache;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ISingletonByKey GetSingletonByKey()
        {
            return _byKey;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        /// <returns></returns>
        public ILog GetLogger()
        {
            return this;
        }

        /// <summary>
        /// ILog
        /// </summary>
        public void Log(LogType logType, string text)
        {
            if (_logger != null && logType >= _logLevel)
            {
                _logger.Log(logType, text);
            }
        }

        /// <summary>
        /// ISingletonAccessRemote
        /// </summary>
        public void GetDataFromRemote()
        {
            string url = _api.GetLocaleListApi();
            _update.UpdateBriefinfo(url, SingletonConst.KeyLocales, _infoRemote.Locales);
            UpdateLocaleInfo();

            url = _api.GetComponentListApi();
            _update.UpdateBriefinfo(url, ConfigConst.KeyComponents, _infoRemote.Components);
            UpdateComponentInfo();

            Log(LogType.Info, "get locale list and component list from remote: " +
                _config.GetProduct() + " / " + _config.GetVersion());

            CheckLoadOnStartup(true);
        }

        /// <summary>
        /// ISingletonAccessRemote
        /// </summary>
        public int GetDataCount()
        {
            return _infoLocal.Locales.Count;
        }

        /// <summary>
        /// IRelease
        /// </summary>
        public IReleaseMessages GetMessages()
        {
            return this;
        }

        /// <summary>
        /// IRelease
        /// </summary>
        public ITranslation GetTranslation()
        {
            return this;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public ILocaleMessages GetAllSource()
        {
            ILocaleMessages languageMessages = GetReleaseMessages().GetLocaleMessages(
                _config.GetSourceLocale(), true);
            return languageMessages;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public ILocaleMessages GetLocaleMessages(string locale, bool asSource = false)
        {
            ILocaleMessages languageMessages = GetReleaseMessages().GetLocaleMessages(
                locale, asSource);
            return languageMessages;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public Dictionary<string, ILocaleMessages> GetAllLocaleMessages()
        {
            Dictionary<string, ILocaleMessages> langDataDict =
                new Dictionary<string, ILocaleMessages>();
            List<string> localeList = GetLocaleList();
            for (int i = 0; i < localeList.Count; i++)
            {
                string locale = localeList[i];
                ILocaleMessages languageData = GetReleaseMessages().GetLocaleMessages(locale);
                if (languageData != null)
                {
                    langDataDict[locale] = languageData;
                }
            }
            return langDataDict;
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public ISource CreateSource(
            string component, string key, string source = null, string comment = null)
        {
            if ((component == null && _byKey == null) || key == null)
            {
                return null;
            }

            if (source == null && _byKey == null)
            {
                source = GetSource(component, key);
            }
            ISource src = new SingletonSource(component, key, source, comment);
            return src;
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string GetString(string locale, ISource source)
        {
            _task.Check();

            return GetRaw(locale, source);
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string GetString(
            string component, string key, string source = null, string comment = null)
        {
            ISource sourceObject = CreateSource(component, key, source, comment);
            string locale = GetCurrentLocale();
            return GetString(locale, sourceObject);
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string Format(string locale, ISource source, params object[] objects)
        {
            if (source == null)
            {
                return null;
            }
            string text = GetString(locale, source);
            if (text != null && objects != null && objects.Length > 0)
            {
                try
                {
                    text = string.Format(text, objects);
                }
                catch (FormatException)
                {
                    string[] strs = Regex.Split(text, "{([0-9]+)}");
                    int maxPlaceHolderIndex = -1;
                    for (int i = 1; i < strs.Length; i += 2)
                    {
                        int temp = Convert.ToInt32(strs[i]);
                        if (temp > maxPlaceHolderIndex)
                        {
                            maxPlaceHolderIndex = temp;
                        }
                    }
                    if (maxPlaceHolderIndex >= 0)
                    {
                        object[] objectsExt = new object[maxPlaceHolderIndex + 1];

                        for (int i = 0; i < maxPlaceHolderIndex + 1; i++)
                        {
                            if (i < objects.Length)
                            {
                                objectsExt[i] = objects[i];
                            }
                            else
                            {
                                objectsExt[i] = "{" + i + "}";
                            }
                        }
                        text = string.Format(text, objectsExt);
                    }
                }
            }
            return text;
        }

        private void LoadLocalOnStartup()
        {
            List<string> componentLocalList = _config.GetConfig().GetComponentList();

            for (int i = 0; i < componentLocalList.Count; i++)
            {
                List<string> localeList = _config.GetConfig().GetLocaleList(componentLocalList[i]);
                for (int k=0; k<localeList.Count; k++)
                {
                    ISingletonLocale singletonLocale = SingletonLocaleUtil.GetSingletonLocale(localeList[k]);

                    _update.LoadOfflineMessage(singletonLocale, false);
                }
            }
        }

        private void LoadRemoteOnStartup()
        {
            List<string> componentLocalList = _config.GetConfig().GetComponentList();

            List<Task> tasks = new List<Task>();

            for (int i = 0; i < _infoRemote.Locales.Count; i++)
            {
                string locale = _infoRemote.Locales[i];
                SingletonUseLocale useLocale = GetUseLocale(locale, false);

                for (int k = 0; k < _infoRemote.Components.Count; k++)
                {
                    string component = _infoRemote.Components[k];
                    if (componentLocalList.Count == 0 || componentLocalList.Contains(component))
                    {
                        SingletonLoadRemoteBundle loadRemote = new SingletonLoadRemoteBundle(useLocale, component);
                        if (i + k == 0)
                        {
                            loadRemote.Load();
                        }
                        else
                        {
                            tasks.Add(loadRemote.CreateAsyncTask());
                        }
                    }
                }
            }

            int maxCountInGroup = Environment.ProcessorCount * 4;
            List<Task> tasksGroup = new List<Task>();
            for (int i=0; i<tasks.Count; i++)
            {
                tasksGroup.Add(tasks[i]);
                if (tasksGroup.Count == maxCountInGroup)
                {
                    Task.WaitAll(tasksGroup.ToArray());
                    tasksGroup.Clear();
                }
            }
            if (tasksGroup.Count > 0)
            {
                Task.WaitAll(tasksGroup.ToArray());
            }
        }

        private void CheckLoadOnStartup(bool byRemote)
        {
            if (!_isLoadedOnStartup && _config.IsLoadOnStartup())
            {
                _isLoadedOnStartup = true;
                if (byRemote)
                {
                    LoadRemoteOnStartup();
                }
                else
                {
                    LoadLocalOnStartup();
                }
            }
        }
    }
}
