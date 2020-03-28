/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;

namespace SingletonClient.Implementation.Support
{
    public sealed class SingletonLogger : ILog
    {
        public void Log(LogType logType, string text)
        {
            string logText = "--- " + logType.ToString() + " --- " + text;
            SingletonBaseIo.get().ConsoleWriteLine(logText);
        }
    }
}

