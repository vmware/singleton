using System.Globalization;

namespace I18NClient.Net.Abstractions.Plugable.Culture
{
    /// <summary>
    /// Defines culture-related methods
    /// </summary>
    public interface ICultureService
    {
        /// <summary>
        /// Gets the culture used by the current thread.
        /// </summary>
        /// <returns>The culture used in the current thread.</returns>
        CultureInfo GetCurrentCulture();

        /// <summary>
        /// Sets the culture used by the current thread.
        /// </summary>
        /// <param name="cultureName">The name of culture.</param>
        void SetCurrentCulture(string cultureName);

        /// <summary>
        /// Gets the culture used by the current thread,
        /// associated with a language but are not specific to a country/region.
        /// </summary>
        /// <returns>The culture used in the current thread which not specific to a country/region.</returns>
        string GetCurrentNeutralCulture();

        /// <summary>
        /// Gets the default culture for threads in the current application domain.
        /// </summary>
        /// <returns>The default culture for threads in the current application domain.</returns>
        CultureInfo GetDefaultCulture();

        /// <summary>
        /// Gets the neutral culture of given culture name.
        /// </summary>
        /// <param name="cultureName">The name of the culture.</param>
        /// <returns>The neutral culture of given culture name.</returns>
        string GetNeutralCulture(string cultureName);

        /// <summary>
        /// Determines whether the given culture is the default culture according to certain rules.
        /// </summary>
        /// <param name="cultureName">The name of the culture.</param>
        /// <returns>True, if the given culture meets the rules, otherwise, false.</returns>
        bool IsDefaultCulture(string cultureName);
    }
}