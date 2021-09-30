using System;

namespace I18NClient.Net.Abstractions.Container
{
    /// <summary>
    /// Defines a service (a named business function) in terms of its Type and implementations.
    /// </summary>
    public interface IServiceDefinition
    {
        /// <summary>
        /// The factory to create the instance.
        /// </summary>
        Func<IServiceProvider, object> ImplementationFactory { get; }

        /// <summary>
        /// The instance of the interface.
        /// </summary>
        object ImplementationInstance { get; }

        /// <summary>
        /// The type of the implementation class.
        /// </summary>
        Type ImplementType { get; }

        /// <summary>
        /// The type of the interface class.
        /// </summary>
        Type ServiceType { get; }

        /// <summary>
        /// Gets the type of the implementation class.
        /// </summary>
        /// <returns>The type of the implementation class.</returns>
        Type GetImplementType();
    }
}