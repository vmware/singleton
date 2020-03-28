/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Threading;

namespace SingletonClient.Implementation
{
    /// <summary>
    /// Interface to access data fetched from remote.
    /// </summary>
    public interface ISingletonAccessRemote
    {
        void GetDataFromRemote();
        int GetDataCount();
    }

    /// <summary>
    /// Class to manage remote access tasks.
    /// </summary>
    public class SingletonAccessRemoteTask
    {
        private static int _total = 0;

        private bool _querying = false;
        private bool _trying = false;

        private ISingletonAccessRemote _access;

        private int _interval;
        private int _tryDelay;

        private DateTime _utcCurrent;
        private int _index;

        public SingletonAccessRemoteTask(
            ISingletonAccessRemote access, int interval, int tryDelay)
        {
            _access = access;
            _interval = interval;
            _tryDelay = tryDelay;

            _index = _total;
            _total++;
        }

        public void SetInterval(int interval)
        {
            _interval = interval;
        }

        public void GetFromRemote()
        {
            try
            {
                _access.GetDataFromRemote();
                _trying = false;
            }
            catch (Exception)
            {
                _trying = true;
            }

            _utcCurrent = DateTime.Now.ToUniversalTime();
            _querying = false;
        }

        public bool TryAccessRemote()
        {
            if (_querying)
            {
                while (_access.GetDataCount() == 0 && _querying)
                {
                    Thread.Sleep(100);
                }
            }
            else
            {
                _querying = true;
                if (_access.GetDataCount() == 0)
                {
                    GetFromRemote();
                }
                else
                {
                    LaunchUpdateThread();
                }
            }

            return true;
        }

        public void LaunchUpdateThread()
        {
            Thread th = new Thread(this.GetFromRemote);
            th.Start();
        }

        public bool CheckStatus()
        {
            if (_utcCurrent.Ticks == 0)
            {
                TryAccessRemote();
            }
            else if (_interval > 0)
            {
                DateTime now = DateTime.Now.ToUniversalTime();
                TimeSpan span = now - _utcCurrent;
                if (_trying)
                {
                    if (span.TotalSeconds > _tryDelay)
                    {
                        TryAccessRemote();
                    }
                }
                else
                {
                    if (span.TotalSeconds > _interval)
                    {
                        TryAccessRemote();
                    }
                }
            }

            return true;
        }
    }
}


