using System;

namespace I18NClient.Net.Abstractions.Plugable.Logger
{
    /// <summary>
    /// ILogger interface for common scenarios.
    /// </summary>
    public interface ILogger
    {
        /// <summary>Formats and writes a critical log message.</summary>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogCritical(string message, params object[] args);

        /// <summary>Formats and writes a critical log message.</summary>
        /// <param name="exception">The exception to log.</param>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogCritical(Exception exception, string message, params object[] args);

        /// <summary>Formats and writes a debug log message.</summary>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogDebug(string message, params object[] args);

        /// <summary>Formats and writes a debug log message.</summary>
        /// <param name="exception">The exception to log.</param>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogDebug(Exception exception, string message, params object[] args);

        /// <summary>Formats and writes an error log message.</summary>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogError(string message, params object[] args);

        /// <summary>Formats and writes an error log message.</summary>
        /// <param name="exception">The exception to log.</param>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogError(Exception exception, string message, params object[] args);

        /// <summary>Formats and writes an informational log message.</summary>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogInformation(string message, params object[] args);

        /// <summary>Formats and writes an informational log message.</summary>
        /// <param name="exception">The exception to log.</param>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogInformation(Exception exception, string message, params object[] args);

        /// <summary>Formats and writes a trace log message.</summary>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogVerbose(string message, params object[] args);

        /// <summary>Formats and writes a trace log message.</summary>
        /// <param name="exception">The exception to log.</param>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogVerbose(Exception exception, string message, params object[] args);

        /// <summary>Formats and writes a warning log message.</summary>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogWarning(string message, params object[] args);

        /// <summary>Formats and writes a warning log message.</summary>
        /// <param name="exception">The exception to log.</param>
        /// <param name="message">Format string of the log message in message template format.</param>
        /// <param name="args">An object array that contains zero or more objects to format.</param>
        void LogWarning(Exception exception, string message, params object[] args);
    }
}
