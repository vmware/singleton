/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using SingletonClient.Implementation.Support;
using SingletonClient.Implementation.Support.ByKey;

namespace SingletonClient.Implementation.Release
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
        void AddLocalScope(List<string> locales, List<string> components);
        ISingletonUseLocale GetUseLocale(string locale, bool asSource);
        ISingletonUseLocale GetSourceUseLocale();
        ISingletonUseLocale GetRemoteSourceUseLocale();
        bool IsInScope(ISingletonLocale singletonLocale, string component, out ISingletonLocale relateLocale);
    }

    public class SingletonReleaseInternal : SingletonReleaseForCache, ISingletonRelease, ISingletonAccessRemote
    {
        private bool _isLoadedOnStartup = false;

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public void SetConfig(IConfig config)
        {
            InitForBase(config, this);

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
        /// ISingletonAccessRemote
        /// </summary>
        public void GetDataFromRemote()
        {
            string url = _api.GetLocaleListApi();
            _update.UpdateBriefInfo(url, SingletonConst.KeyLocales, _infoRemote.GetLocales());
            UpdateLocaleInfo();

            url = _api.GetComponentListApi();
            _update.UpdateBriefInfo(url, ConfigConst.KeyComponents, _infoRemote.GetComponents());
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
            return _infoLocal.GetLocales().Count;
        }

        private void LoadLocalBundle(string locale, string component)
        {
            ISingletonLocale singletonLocale = SingletonLocaleUtil.GetSingletonLocale(locale);
            bool isSource = singletonLocale.Compare(_useSourceLocale.GetSingletonLocale());
            _update.LoadLocalMessage(singletonLocale, component, isSource, false);
        }

        private void LoadLocalOnStartup(Dictionary<string, bool> done)
        {
            List<string> componentLocalList = _config.GetLocalComponentList();

            foreach (string component in componentLocalList)
            {
                if (done != null && done.ContainsKey(component))
                {
                    continue;
                }

                List<string> localeList = _config.GetComponentLocaleList(component);
                foreach (string locale in localeList)
                {
                    LoadLocalBundle(locale, component);
                }
            }
        }

        private void LoadRemoteOnStartup()
        {
            List<string> componentLocalList = _config.GetConfig().GetComponentList();

            List<SingletonLoadRemoteBundle> loads = new List<SingletonLoadRemoteBundle>();

            bool isFirst = true;
            foreach (string locale in _infoRemote.GetLocales())
            {
                ISingletonUseLocale useLocale = GetUseLocale(locale, false);

                foreach (string component in _infoRemote.GetComponents())
                {
                    if (componentLocalList.Count == 0 || componentLocalList.Contains(component))
                    {
                        SingletonLoadRemoteBundle loadRemote = new SingletonLoadRemoteBundle(useLocale, component);
                        if (isFirst)
                        {
                            loadRemote.Load();
                            isFirst = false;
                        }
                        else
                        {
                            loads.Add(loadRemote);
                        }
                    }
                }
            }

            int maxCountInGroup = Environment.ProcessorCount * 2;
            List<Task> tasksGroup = new List<Task>();
            for (int i = 0; i < loads.Count; i++)
            {
                tasksGroup.Add(loads[i].CreateAsyncTask());
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

            Dictionary<string, bool> done = new Dictionary<string, bool>(StringComparer.InvariantCultureIgnoreCase);
            foreach (string component in _infoRemote.GetComponents())
            {
                done[component] = true;
            }

            LoadLocalOnStartup(done);
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
                    LoadLocalOnStartup(null);
                }
            }
        }
    }
}
