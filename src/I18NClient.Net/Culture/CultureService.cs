using I18NClient.Net.Abstractions.Plugable.Culture;
using I18NClient.Net.Abstractions.Plugable.Logger;
using I18NClient.Net.Abstractions.Plugable.Option;
using System;
using System.Globalization;
using System.Threading;

namespace I18NClient.Net.Culture
{
    /// <inheritdoc/>
    public class CultureService : ICultureService
    {
        private readonly CultureInfo _defaultCulture = new CultureInfo("en-US");

        private readonly ILogger _logger;

        /// <inheritdoc/>
        public CultureService(AbstractI18NClientOptions abstractI18NOptions, ILogger logger)
        {
            _defaultCulture = TryCreateCultureInfo(abstractI18NOptions?.DefaultLanguage) ?? _defaultCulture;
            _logger = logger;
        }

        /// <inheritdoc/>
        public void SetCurrentCulture(string cultureName)
        {
            if (string.IsNullOrEmpty(cultureName)) cultureName = GetDefaultCulture().Name;

            Thread.CurrentThread.CurrentCulture = TryCreateCultureInfo(cultureName) ?? _defaultCulture;
            Thread.CurrentThread.CurrentUICulture = Thread.CurrentThread.CurrentCulture;
        }

        private CultureInfo TryCreateCultureInfo(string name)
        {
            if (!string.IsNullOrEmpty(name))
            {
                try
                {
                    return new CultureInfo(name);
                }
                catch (CultureNotFoundException ex)
                {
                    _logger.LogError(ex, "Unsupported culture name {0}.", name);
                }
            }
            return null;
        }

        /// <inheritdoc/>
        public CultureInfo GetDefaultCulture()
        {
            return _defaultCulture;
        }

        /// <inheritdoc/>
        public CultureInfo GetCurrentCulture()
        {
            return Thread.CurrentThread.CurrentCulture;
        }

        /// <inheritdoc/>
        public string GetCurrentNeutralCulture()
        {
            return GetNeutralCulture(Thread.CurrentThread.CurrentCulture.Name);
        }

        /// <inheritdoc/>
        public string GetNeutralCulture(string cultureName)
        {
            if (!cultureName.Contains("-")) return cultureName;

            return cultureName.Split('-')[0];
        }

        /// <inheritdoc/>
        public bool IsDefaultCulture(string cultureName)
        {
            if (string.IsNullOrWhiteSpace(cultureName)) return false;

            if (!cultureName.Contains("-"))
                return GetNeutralCulture(_defaultCulture.Name).Equals(cultureName, StringComparison.InvariantCultureIgnoreCase);

            if (!_defaultCulture.Name.Contains("-"))
                return GetNeutralCulture(cultureName).Equals(_defaultCulture.Name, StringComparison.InvariantCultureIgnoreCase);

            return _defaultCulture.Name.Equals(cultureName, StringComparison.InvariantCultureIgnoreCase);
        }
    }
}
