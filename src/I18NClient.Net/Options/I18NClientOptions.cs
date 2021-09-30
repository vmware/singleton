using I18NClient.Net.Abstractions.Plugable.Option;
using I18NClient.Net.Constants;
using System.ComponentModel.DataAnnotations;

namespace I18NClient.Net.Options
{
    /// <summary>
    /// Provides programmatic configuration for I18N client.
    /// </summary>
#pragma warning disable S101 // Types should be named in PascalCase
    public class I18NClientOptions : AbstractI18NClientOptions
#pragma warning restore S101 // Types should be named in PascalCase
    {
        /// <inheritdoc/>
        [Required(ErrorMessage = "Product name is required")]
        public override string ProductName { get; set; }

        /// <inheritdoc/>
        [Required(ErrorMessage = "Version name is required")]
        public override string Version { get; set; }

        /// <inheritdoc/>
        [Range(1, I18NClientConstants.Parallelism.maxDegreeOfParallelism, ErrorMessage = "Value for {0} must be between {1} and {2}.")]
        public override int MaxDegreeOfParallelism { get; set; } = 30;
    }
}
