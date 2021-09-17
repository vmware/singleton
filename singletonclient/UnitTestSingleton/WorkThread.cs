/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Threading.Tasks;


namespace UnitTestSingleton
{
    public class WorkThread : BaseWork
    {
        public WorkThread(BaseTest bt)
        {
            this.baseTest = bt;
        }

        public async void TestInAsyncMode()
        {
            await AsyncTask();
        }

        private async Task AsyncTask()
        {
            await Task.Run(() =>
            {
                DoGroupOperation();
            });
        }
    }
}
