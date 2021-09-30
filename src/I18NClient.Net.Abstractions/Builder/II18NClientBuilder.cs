using I18NClient.Net.Abstractions.Plugable.Cache;
using I18NClient.Net.Abstractions.Plugable.Culture;
using I18NClient.Net.Abstractions.Plugable.Localization;
using I18NClient.Net.Abstractions.Plugable.Logger;
using System.Reflection;

namespace I18NClient.Net.Abstractions.Builder
{
    /// <summary>
    /// A builder to build I18N client with given plug-ins.
    /// </summary>
    public interface II18NClientBuilder
    {
        /// <summary>
        /// Validate components and assemble I18N client.
        /// </summary>
        /// <returns>I18N client</returns>
        II18NClient Build();

        /// <summary>
        /// Specify the assembly where the resource is located.
        /// </summary>
        /// <param name="assembly"></param>
        /// <returns>I18n client builder</returns>
        II18NClientBuilder WithAssembly(Assembly assembly);

        /// <summary>
        /// Plug in customized implementation of the IComponentMessageCacheManager interface.
        /// </summary>
        /// <typeparam name="T">Type of specific implementation class.</typeparam>
        /// <returns>I18n client builder</returns>
        II18NClientBuilder WithComponentMessageCacheManager<T>() where T : class, IComponentMessageCacheManager;

        /// <summary>
        /// Plug in customized implementation of the ILanguageCodeConvertor interface.
        /// </summary>
        /// <typeparam name="T">Type of specific implementation class.</typeparam>
        /// <returns>I18n client builder</returns>
        II18NClientBuilder WithLanguageCodeConvertor<T>() where T : class, ILanguageCodeConvertor;

        /// <summary>
        /// Plug in customized implementation of the ILoadingStrategy interface.
        /// </summary>
        /// <typeparam name="T">Type of specific implementation class.</typeparam>
        /// <returns>I18n client builder</returns>
        II18NClientBuilder WithLoadingStrategy<T>() where T : class, ILoadingStrategy;

        /// <summary>
        /// Plug in customized implementation of the ILocalLoader interface.
        /// </summary>
        /// <typeparam name="T">Type of specific implementation class.</typeparam>
        /// <returns>I18n client builder</returns>
        II18NClientBuilder WithLocalLoader<T>() where T : class, ILocalLoader;

        /// <summary>
        /// Plug in customized implementation of the ILogger interface.
        /// </summary>
        /// <typeparam name="T">Type of specific implementation class.</typeparam>
        /// <returns>I18n client builder</returns>
        II18NClientBuilder WithLogger<T>() where T : class, ILogger;

        /// <summary>
        /// Plug in customized implementation of the IProductInfoCacheManager interface.
        /// </summary>
        /// <typeparam name="T">Type of specific implementation class.</typeparam>
        /// <returns>I18n client builder</returns>
        II18NClientBuilder WithProductInfoCacheManager<T>() where T : class, IProductInfoCacheManager;

        /// <summary>
        /// Plug in customized implementation of the IRemoteLoader interface.
        /// </summary>
        /// <typeparam name="T">Type of specific implementation class.</typeparam>
        /// <returns>I18n client builder</returns>
        II18NClientBuilder WithRemoteLoader<T>() where T : class, IRemoteLoader;
    }
}