using I18NClient.Net.Abstractions.Container;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Reflection;

namespace I18NClient.Net.Containers
{
    internal sealed class ServiceContainer : IServiceContainer
    {
        internal readonly List<IServiceDefinition> _services;

        private readonly ConcurrentDictionary<Type, object> _scopedInstances;

        public ServiceContainer()
        {
            _services = new List<IServiceDefinition>();
            _scopedInstances = new ConcurrentDictionary<Type, object>();
        }

        public void Add(IServiceDefinition item)
        {
            _services.Add(item);
        }

        private bool _disposed;

        public void Dispose()
        {
            if (_disposed)
            {
                return;
            }

            lock (_scopedInstances)
            {
                if (_disposed)
                {
                    return;
                }

                _disposed = true;

                foreach (var instance in _scopedInstances.Values)
                {
                    (instance as IDisposable)?.Dispose();
                }

                _scopedInstances.Clear();
            }
        }

        public object GetService(Type serviceType)
        {
            var serviceDefinition = _services.LastOrDefault(_ => _.ServiceType == serviceType);
            if (null == serviceDefinition) return null;

            return _scopedInstances.GetOrAdd(serviceType, (t) => GetServiceInstance(t, serviceDefinition));
        }

        private object GetServiceInstance(Type serviceType, IServiceDefinition serviceDefinition)
        {
            if (serviceDefinition.ImplementationInstance != null)
                return serviceDefinition.ImplementationInstance;

            if (serviceDefinition.ImplementationFactory != null)
                return serviceDefinition.ImplementationFactory.Invoke(this);

            var implementType = (serviceDefinition.ImplementType ?? serviceType);

            if (implementType.IsInterface || implementType.IsAbstract)
            {
                throw new InvalidOperationException(
                    $"Invalid service registered, serviceType: {serviceType.FullName}, implementType: {serviceDefinition.ImplementType}");
            }

            // According to the implementation type to the constructor Information.
            ConstructorInfo ctor = GetConstructorInfo(implementType);

            // If it is a parameterless constructor, call it directly.
            var parameters = ctor.GetParameters();
            if (parameters.Length == 0) return Expression.Lambda<Func<object>>(Expression.New(ctor)).Compile().Invoke();

            // Try to resolve the parameters from cache.
            object[] ctorParams = ResolveConstructorParams(parameters);

            // Call parameterized constructor once the parameters are resolved.
            return Expression.Lambda<Func<object>>(Expression.New(ctor, ctorParams.Select(Expression.Constant))).Compile().Invoke();
        }

        private object[] ResolveConstructorParams(ParameterInfo[] parameters)
        {
            var ctorParams = new object[parameters.Length];

            for (var index = 0; index < parameters.Length; index++)
            {
                var parameter = parameters[index];
                var param = GetService(parameter.ParameterType);

                if (param == null && parameter.HasDefaultValue)
                {
                    param = parameter.DefaultValue;
                }

                ctorParams[index] = param ?? throw new InvalidOperationException(
                    $"Service parameter {parameter.ParameterType.FullName} can't be resolved.");
            }

            return ctorParams;
        }

        private static ConstructorInfo GetConstructorInfo(Type implementType)
        {
            var ctorInfos = implementType.GetConstructors(BindingFlags.Instance | BindingFlags.Public);

            if (ctorInfos.Length == 0)
            {
                throw new InvalidOperationException(
                    $"Service {implementType.FullName} does not have any public constructors.");
            }

            ConstructorInfo ctor;

            if (ctorInfos.Length == 1)
            {
                ctor = ctorInfos[0];
            }
            else
            {
                ctor = ctorInfos.OrderBy(_ => _.GetParameters().Length).First();
            }

            return ctor;
        }

        public bool IsRegistered<TService>() where TService : class
        {
            var serviceDefinition = _services.LastOrDefault(_ => _.ServiceType == typeof(TService));
            return serviceDefinition != null;
        }
    }
}
