using System;
using System.Collections.Generic;

namespace I18NClient.Net.Abstractions.Domains
{
    /// <summary>
    /// A custom concept is used to define a certain amount of messages in the product.
    /// </summary>
    public class Component
    {
        /// <summary>
        /// The name of component, can be defined per module.
        /// </summary>
        public string ComponentName { get; set; }

        /// <summary>
        /// The name of product.
        /// </summary>
        public string ProductName { get; set; }

        /// <summary>
        /// The version of current product release.
        /// </summary>
        public string Version { get; set; }

        /// <summary>
        /// The language of the messages.
        /// </summary>
        public string LanguageCode { get; set; }

        /// <summary>
        /// The messages belongs to the component.
        /// </summary>
        public Dictionary<string, string> Messages { get; set; } = new Dictionary<string, string>(StringComparer.OrdinalIgnoreCase);
    }
}
