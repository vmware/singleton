using I18NClient.Net.Abstractions.Container;
using System;

namespace I18NClient.Net.Containers
{
    internal class ServiceDefinition : IServiceDefinition
    {
        public Type ImplementType { get; }

        public Type ServiceType { get; }

        public object ImplementationInstance { get; }

        public Func<IServiceProvider, object> ImplementationFactory { get; }

        public Type GetImplementType()
        {
            if (ImplementationInstance != null)
                return ImplementationInstance.GetType();

            if (ImplementationFactory != null)
                return ImplementationFactory.Method.DeclaringType;

            if (ImplementType != null)
                return ImplementType;

            return ServiceType;
        }

        public ServiceDefinition(object instance, Type serviceType)
        {
            ImplementationInstance = instance;
            ServiceType = serviceType;
        }

        public ServiceDefinition(Type serviceType, Type implementType)
        {
            ServiceType = serviceType;
            ImplementType = implementType ?? serviceType;
        }

        public ServiceDefinition(Type serviceType, Func<IServiceProvider, object> factory)
        {
            ServiceType = serviceType;
            ImplementationFactory = factory;
        }
    }
}
