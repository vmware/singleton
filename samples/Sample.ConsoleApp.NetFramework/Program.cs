using Sample.ConsoleApp.NetFramework.Providers;
using System.Threading.Tasks;
using I18NClient.Net.Extensions;
using System.Diagnostics;

namespace Sample.ConsoleApp.NetFramework
{
    public static class Program
    {
        static void Main(string[] args)
        {
            var client = I18NClientProvider.GetI18NClient();

            // Resource related asynchronous API invocations.
            Trace.Assert(client.ComponentMessageCacheManager.GetCacheNames().Count == 0);
            Trace.Assert(client.ProductInfoCacheManager.GetCacheNames().Count == 0);

            var supportedComponentListAsync = Task.Run(() => client.ResourceService.GetSupportedComponentListAsync()).GetAwaiter().GetResult();
            var supportedLanguageListAsync = Task.Run(() => client.ResourceService.GetSupportedLanguageListAsync()).GetAwaiter().GetResult();
            var componentAsync = Task.Run(() => client.ResourceService.GetTranslationsByComponentAsync("Account", "zh-CN")).GetAwaiter().GetResult();
            var translationAsync = Task.Run(() => client.ResourceService.GetTranslationByKeyAsync("Account", "ALLOW_HTTP_HTTPS", "zh-CN")).GetAwaiter().GetResult();

            var successAsync1 = Task.Run(() => client.ResourceService.TryInitTranslationsByComponentAsync("Apps", "en-US")).GetAwaiter().GetResult();
            var successAsync2 = Task.Run(() => client.ResourceService.TryInitTranslationsByLanguageAsync("en-US")).GetAwaiter().GetResult();
            var successAsync3 = Task.Run(() => client.ResourceService.TryInitTranslationsByProductAsync()).GetAwaiter().GetResult();

            Trace.Assert(client.ComponentMessageCacheManager.GetCacheNames().Count == 9);
            Trace.Assert(client.ProductInfoCacheManager.GetCacheNames().Count == 1);

            var supportedComponentList = client.ResourceService.GetSupportedComponentList();
            var supportedLanguageList = client.ResourceService.GetSupportedLanguageList();
            var component = client.ResourceService.GetTranslationsByComponent("Account", "zh-CN");
            var translation = client.ResourceService.GetTranslationByKey("Account", "ALLOW_HTTP_HTTPS", "zh-CN");

            var success1 = client.ResourceService.TryInitTranslationsByComponent("Apps", "en-US");
            var success2 = client.ResourceService.TryInitTranslationsByLanguage("en-US");
            var success3 = client.ResourceService.TryInitTranslationsByProduct();

            Trace.Assert(supportedComponentListAsync.Count == supportedComponentList.Count);
            Trace.Assert(supportedLanguageListAsync.Count == supportedLanguageList.Count);
            Trace.Assert(componentAsync.Messages.Keys.Count == component.Messages.Keys.Count);
            Trace.Assert(translationAsync == translation);

            Trace.Assert(success1 == successAsync1);
            Trace.Assert(success2 == successAsync2);
            Trace.Assert(success3 == successAsync3);
        }
    }
}
