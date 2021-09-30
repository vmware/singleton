using I18NClient.Net.Abstractions;
using I18NClient.Net.Builder;
using I18NClient.Net.Loaders;
using Sample.ConsoleApp.NetFramework.Utilities;
using System;

namespace Sample.ConsoleApp.NetFramework.Providers
{
    internal static class I18NClientProvider
    {
        private static readonly Lazy<II18NClient> lazy =
            new Lazy<II18NClient>(() => BuildI18NClient());

        public static II18NClient GetI18NClient()
        {
            return lazy.Value;
        }

        private static II18NClient BuildI18NClient()
        {
            var options = I18NClientOptionsHelper.GetI18NClientOptions();
            return new I18NClientBuilder(options)
                .WithLoadingStrategy<MixedLoadingStrategy>()
                .Build();
        }
    }
}
