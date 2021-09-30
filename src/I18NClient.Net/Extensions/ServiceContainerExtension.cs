using I18NClient.Net.Abstractions.Container;
using I18NClient.Net.Containers;
using System;

namespace I18NClient.Net.Extensions
{
    internal static class ServiceContainerExtension
    {
        public static IServiceContainer RegisterInstance<TService>(this IServiceContainer serviceContainer, TService service)
        {
            serviceContainer.Add(new ServiceDefinition(service, typeof(TService)));
            return serviceContainer;
        }

        public static IServiceContainer RegisterType<TService, TServiceImplement>(this IServiceContainer serviceContainer) where TServiceImplement : TService
        {
            serviceContainer.Add(new ServiceDefinition(typeof(TService), typeof(TServiceImplement)));
            return serviceContainer;
        }

        public static IServiceContainer RegisterFactory<TService>(this IServiceContainer serviceContainer, Func<IServiceProvider, object> func)
        {
            serviceContainer.Add(new ServiceDefinition(typeof(TService), func));
            return serviceContainer;
        }

        public static TService Resolve<TService>(this IServiceProvider serviceProvider) => (TService)serviceProvider.GetService(typeof(TService));
    }
}
