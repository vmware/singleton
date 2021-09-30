using System;

namespace I18NClient.Net.Abstractions.Container
{
    /// <summary>
    /// Provides a container for i18n pluggable services.
    /// </summary>
    public interface IServiceContainer : IServiceProvider, IDisposable
    {
        /// <summary>
        /// Registers a service through the definition.
        /// </summary>
        /// <param name="item">The instance of IServiceDefinition.</param>
        void Add(IServiceDefinition item);

        /// <summary>
        /// Describes whether or not the service is registered.
        /// </summary>
        /// <typeparam name="TService"></typeparam>
        /// <returns>True if the service is registered.</returns>
        bool IsRegistered<TService>() where TService : class;
    }
}
