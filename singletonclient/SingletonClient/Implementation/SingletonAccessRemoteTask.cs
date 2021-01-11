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
        void CheckTimeSpan();
    }

    /// <summary>
    /// Class to manage remote access tasks.
    /// </summary>
    public class SingletonAccessRemoteTask: ISingletonAccessTask
    {
        private bool _querying = false;
        private bool _trying = false;

        private readonly ISingletonAccessRemote _update;

        private int _interval;
        private readonly int _tryDelay;

        private DateTime _utcCurrent;

        public SingletonAccessRemoteTask(
            ISingletonAccessRemote update, int interval, int tryDelay)
        {
            _update = update;
            _interval = interval;
            _tryDelay = tryDelay;
        }

        public void SetInterval(int interval)
        {
            _interval = interval;
        }

        public void GetFromRemote()
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

            _utcCurrent = DateTime.Now.ToUniversalTime();
            _querying = false;
        }

        public bool TryAccessRemote()
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

            return true;
        }

        private void LaunchUpdateThread()
        {
            Thread th = new Thread(this.GetFromRemote);
            th.Start();
        }

        public void CheckTimeSpan()
        {
            if (!_update.GetSingletonConfig().IsOnlineSupported())
            {
                return;
            }

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
        }
    }
}


