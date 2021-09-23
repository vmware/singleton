/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */


using SingletonClient;
using System.Reflection;

namespace UnitTestSingleton
{
    public abstract class AbsPlanTest : BaseCommonTest
    {
        public ITranslation Translation
        {
            get { return access != null ? access.Translation : null; }
        }

        protected AccessSingleton access;

        public AbsPlanTest()
        {
            string[] resStrings = GetResStrings();
            IConfig cfgOutside = I18N.LoadConfigFromText(resStrings[4]);
            if (resStrings[3] != null)
            {
                this.LoadTestData(resStrings[3].Split(','));
            }

            BaseIo.obj().LoadOneResponse(resStrings[2], cfgOutside.GetItem("product").GetString(), BaseTest.Version);

            IConfig cfg = I18N.LoadConfig(resStrings[0], Assembly.GetExecutingAssembly(), resStrings[1], cfgOutside);
            access = new AccessSingleton(cfg);
        }

        public abstract string[] GetResStrings();
    }
}
