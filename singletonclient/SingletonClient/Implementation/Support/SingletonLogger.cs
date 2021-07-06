/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    public sealed class SingletonLogger : ILog
    {
        /// <summary>
        /// ILog
        /// </summary>
        public void Log(LogType logType, string text)
        {
            string logText = "--- " + logType.ToString() + " --- " + text;
            SingletonBaseIo.GetInstance().ConsoleWriteLine(logText);
        }
    }
}
