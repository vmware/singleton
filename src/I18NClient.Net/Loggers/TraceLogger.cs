using I18NClient.Net.Abstractions.Plugable.Logger;
using System;
using System.Diagnostics;

namespace I18NClient.Net.Loggers
{
    /// <inheritdoc />
    public class TraceLogger : ILogger
    {
        /// <inheritdoc />
        public void LogCritical(string message, params object[] args)
        {
            Trace.TraceError(message, args);
        }

        /// <inheritdoc />
        public void LogCritical(Exception exception, string message, params object[] args)
        {
            var newMessage = message + Environment.NewLine + exception;

            Trace.TraceError(newMessage, args);
        }

        /// <inheritdoc />
        public void LogDebug(string message, params object[] args)
        {
            Trace.TraceInformation(message, args);
        }

        /// <inheritdoc />
        public void LogDebug(Exception exception, string message, params object[] args)
        {
            var newMessage = message + Environment.NewLine + exception;

            Trace.TraceInformation(newMessage, args);
        }

        /// <inheritdoc />
        public void LogError(string message, params object[] args)
        {
            Trace.TraceError(message, args);
        }

        /// <inheritdoc />
#pragma warning disable S4144 // Methods should not have identical implementations
        public void LogError(Exception exception, string message, params object[] args)
#pragma warning restore S4144 // Methods should not have identical implementations
        {
            var newMessage = message + Environment.NewLine + exception;

            Trace.TraceError(newMessage, args);
        }

        /// <inheritdoc />
        public void LogInformation(string message, params object[] args)
        {
            Trace.TraceInformation(message, args);
        }

        /// <inheritdoc />
#pragma warning disable S4144 // Methods should not have identical implementations
        public void LogInformation(Exception exception, string message, params object[] args)
#pragma warning restore S4144 // Methods should not have identical implementations
        {
            var newMessage = message + Environment.NewLine + exception;

            Trace.TraceInformation(newMessage, args);
        }

        /// <inheritdoc />
        public void LogVerbose(string message, params object[] args)
        {
            Trace.TraceInformation(message, args);
        }

        /// <inheritdoc />
#pragma warning disable S4144 // Methods should not have identical implementations
        public void LogVerbose(Exception exception, string message, params object[] args)
#pragma warning restore S4144 // Methods should not have identical implementations
        {
            var newMessage = message + Environment.NewLine + exception;

            Trace.TraceInformation(newMessage, args);
        }

        /// <inheritdoc />
        public void LogWarning(string message, params object[] args)
        {
            Trace.TraceWarning(message, args);
        }

        /// <inheritdoc />
        public void LogWarning(Exception exception, string message, params object[] args)
        {
            var newMessage = message + Environment.NewLine + exception;

            Trace.TraceWarning(newMessage, args);
        }
    }
}
