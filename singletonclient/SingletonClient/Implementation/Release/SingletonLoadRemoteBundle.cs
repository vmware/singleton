/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Threading.Tasks;

namespace SingletonClient.Implementation.Release
{
    public class SingletonLoadRemoteBundle
    {
        private readonly SingletonUseLocale _useLocale;
        private readonly string _component;

        public SingletonLoadRemoteBundle(SingletonUseLocale useLocale, string component)
        {
            _useLocale = useLocale;
            _component = component;
        }

        public void Load()
        {
            ISingletonComponent singletonComponent = _useLocale.GetComponent(_component, true);
            singletonComponent.GetAccessRemote().GetDataFromRemote();
        }

        public async Task CreateAsyncTask()
        {
            await Task.Run(() =>
            {
                Load();
            });
        }
    }
}
