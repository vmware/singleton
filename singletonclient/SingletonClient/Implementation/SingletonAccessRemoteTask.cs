/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Threading;

namespace SingletonClient.Implementation
{
    /// <summary>
    /// Interface to update data fetched from remote or local.
    /// </summary>
    public interface ISingletonAccessRemote
    {
        /// <summary>
        /// Get singleton configuration.
        /// </summary>
        /// <returns></returns>
        ISingletonConfig GetSingletonConfig();

        /// <summary>
        /// Get data from remote service.
        /// </summary>
        void GetDataFromRemote();

        /// <summary>
        /// Get data item count.
        /// </summary>
        /// <returns></returns>
        int GetDataCount();
    }

    public interface ISingletonAccessTask
    {
        /// <summary>
        /// Set expire time.
        /// </summary>
        /// <param name="interval"></param>
        void SetInterval(int interval);

        /// <summary>
        /// Check time span for updating.
        /// </summary>
        void Check();
    }

    /// <summary>
    /// Class to manage remote access tasks.
    /// </summary>
    public class SingletonAccessRemoteTask: ISingletonAccessTask
    {
        private readonly ISingletonAccessRemote _update;
        private readonly int _tryWaitMs;

        private bool _querying = false;
        private bool _trying = false;
        private bool _expireEnabled = true;

        private int _interval;
        private int _ticks;

        public SingletonAccessRemoteTask(
            ISingletonAccessRemote update, int interval, int tryWait)
        {
            _update = update;
            _interval = interval * 1000;
            _tryWaitMs = tryWait * 1000;
        }

        /// <summary>
        /// ISingletonAccessTask
        /// </summary>
        public void SetInterval(int interval)
        {
            _interval = interval;
        }

        /// <summary>
        /// ISingletonAccessTask
        /// </summary>
        public void Check()
        {
            if (!_update.GetSingletonConfig().IsOnlineSupported() || !_expireEnabled)
            {
                return;
            }

            if (_ticks == 0)
            {
                TryAccessRemote();
            }
            else if (_interval > 0)
            {
                int span = System.Environment.TickCount - _ticks;
                if (_trying)
                {
                    if (span > _tryWaitMs)
                    {
                        TryAccessRemote();
                    }
                }
                else
                {
                    if (span > _interval)
                    {
                        TryAccessRemote();
                    }
                }
            }
        }

        private void GetFromRemote()
        {
            try
            {
                _update.GetDataFromRemote();
                _trying = false;
            }
            catch (Exception)
            {
                _trying = true;
            }

            if (!_update.GetSingletonConfig().IsExpireEnabled() && _update.GetDataCount() > 0)
            {
                _expireEnabled = false;
            }

            _ticks = System.Environment.TickCount;
            _querying = false;
        }

        private void TryAccessRemote()
        {
            if (_querying)
            {
                while (_update.GetDataCount() == 0 && _querying)
                {
                    Thread.Sleep(100);
                }
            }
            else
            {
                _querying = true;
                if (_update.GetDataCount() == 0)
                {
                    GetFromRemote();
                }
                else
                {
                    LaunchUpdateThread();
                }
            }
        }

        private void LaunchUpdateThread()
        {
            Thread th = new Thread(this.GetFromRemote);
            th.Start();
        }
    }
}
