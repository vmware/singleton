/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonIcu;

namespace UnitTestSingletonIcu
{
    public class TestBase
    {
        protected IUseIcu icu;
        protected IPlural dp;
        protected IMessage dm;

        protected TestBase()
        {
            icu = UseCldr.GetIcu();
            dp = icu.GetPlural();
            dm = icu.GetMessage();
        }
    }
}
