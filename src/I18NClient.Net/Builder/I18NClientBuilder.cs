using I18NClient.Net.Abstractions;
using I18NClient.Net.Abstractions.Builder;
using I18NClient.Net.Abstractions.Container;
using I18NClient.Net.Abstractions.Plugable.Cache;
using I18NClient.Net.Abstractions.Plugable.Culture;
using I18NClient.Net.Abstractions.Plugable.Localization;
using I18NClient.Net.Abstractions.Plugable.Logger;
using I18NClient.Net.Abstractions.Plugable.Option;
using I18NClient.Net.Abstractions.Resource;
using I18NClient.Net.Caches;
using I18NClient.Net.Containers;
using I18NClient.Net.Culture;
using I18NClient.Net.Extensions;
using I18NClient.Net.Loaders;
using I18NClient.Net.Loggers;
using I18NClient.Net.Resources;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Reflection;

namespace I18NClient.Net.Builder
{
    /// <summary>
    /// The specific implementation of I18n builder.
    /// </summary>
#pragma warning disable S101 // Types should be named in PascalCase
    public class I18NClientBuilder : II18NClientBuilder
#pragma warning restore S101 // Types should be named in PascalCase
    {
        private readonly IServiceContainer _container;

        /// <summary>
        /// Exposed service container for the builder extension.
        /// </summary>
        public IServiceContainer Container => _container;

        /// <summary>
        /// Validate options and instantiate builder.
        /// </summary>
        /// <param name="abstractI18NClientOptions">I18n client options.</param>
        public I18NClientBuilder(AbstractI18NClientOptions abstractI18NClientOptions)
        {
            _container = new ServiceContainer();
            ValidateOptions(abstractI18NClientOptions);
            _container.RegisterInstance(abstractI18NClientOptions);
        }

        /// <inheritdoc/>
        public II18NClientBuilder WithLogger<T>() where T : class, ILogger
        {
            _container.RegisterType<ILogger, T>();
            return this;
        }

        /// <inheritdoc/>
        public II18NClientBuilder WithAssembly(Assembly assembly)
        {
            _container.RegisterInstance(assembly);
            return this;
        }

        /// <inheritdoc/>
        public II18NClientBuilder WithRemoteLoader<T>() where T : class, IRemoteLoader
        {
            _container.RegisterType<IRemoteLoader, T>();
            return this;
        }

        /// <inheritdoc/>
        public II18NClientBuilder WithLocalLoader<T>() where T : class, ILocalLoader
        {
            _container.RegisterType<ILocalLoader, T>();
            return this;
        }

        /// <inheritdoc/>
        public II18NClientBuilder WithLoadingStrategy<T>() where T : class, ILoadingStrategy
        {
            _container.RegisterType<ILoadingStrategy, T>();
            return this;
        }

        /// <inheritdoc/>
        public II18NClientBuilder WithLanguageCodeConvertor<T>() where T : class, ILanguageCodeConvertor
        {
            _container.RegisterType<ILanguageCodeConvertor, T>();
            return this;
        }

        /// <inheritdoc/>
        public II18NClientBuilder WithProductInfoCacheManager<T>() where T : class, IProductInfoCacheManager
        {
            _container.RegisterType<IProductInfoCacheManager, T>();
            return this;
        }

        /// <inheritdoc/>
        public II18NClientBuilder WithComponentMessageCacheManager<T>() where T : class, IComponentMessageCacheManager
        {
            _container.RegisterType<IComponentMessageCacheManager, T>();
            return this;
        }

        /// <inheritdoc/>
        public II18NClient Build()
        {
            // Register foundation Services.
            RegisterIfNotPresent(Assembly.GetCallingAssembly());
            RegisterIfNotPresent<ILogger, TraceLogger>();
            RegisterIfNotPresent<ILanguageCodeConvertor, LanguageCodeConvertor>();
            RegisterIfNotPresent<ICultureService, CultureService>();

            // Register resource loading related services.
            RegisterIfNotPresent<ILocalLoader, ResxLocalLoader>();
            RegisterIfNotPresent<IRemoteLoader, SingletonRemoteLoader>();
            RegisterIfNotPresent<ILoadingStrategy, MixedLoadingStrategy>();

            // Register global cache related services.
            RegisterIfNotPresent<IComponentMessageCacheManager, ComponentMessageCacheManager>();
            RegisterIfNotPresent<IProductInfoCacheManager, ProductInfoCacheManager>();

            // Register resource service.
            RegisterIfNotPresent<IResourceService, ResourceService>();

            // Register I18N client.
            RegisterIfNotPresent<II18NClient, I18NClient>();

            // Validate the options under the specific strategy.
            ValidateLoadingStrategy();

            return _container.Resolve<II18NClient>();
        }

        private void ValidateLoadingStrategy()
        {
            var strategy = _container.Resolve<ILoadingStrategy>();

            if (strategy.GetType() == typeof(MixedLoadingStrategy) || strategy.GetType() == typeof(RemoteLoadingStrategy))
            {
                _ = _container.Resolve<AbstractI18NClientOptions>().BackendServiceUrl ??
                    throw new ValidationException("BackendServiceUrl is required for MixLoadingStrategy or RemoteLoadingStrategy.");
            }
        }

        private void ValidateOptions(AbstractI18NClientOptions abstractI18NClientOptions)
        {
            var validationContext = new ValidationContext(abstractI18NClientOptions);
            var results = new List<ValidationResult>();
            var isValid = Validator.TryValidateObject(abstractI18NClientOptions, validationContext, results, true);

            if (!isValid) throw new ValidationException(string.Join(",", results.Select(x => x.ErrorMessage).ToList()));
        }

        private void RegisterIfNotPresent<TService, TImplementation>()
                        where TImplementation : class, TService
                        where TService : class
        {
            if (!_container.IsRegistered<TService>())
            {
                _container.RegisterType<TService, TImplementation>();
            }
        }

        private void RegisterIfNotPresent<TService>(TService instance)
                where TService : class
        {
            if (!_container.IsRegistered<TService>())
            {
                _container.RegisterInstance(instance);
            }
        }
    }
}
