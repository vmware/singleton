using I18NClient.Net.Abstractions.Domains;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace I18NClient.Net.Extensions
{
    /// <summary>
    /// The extension of component list to enhance its functionality.
    /// </summary>
    internal static class ComponentListExtension
    {
        /// <summary>
        /// Loads messages for a batch of components in parallel with a throttling strategy.
        /// </summary>
        /// <param name="components">The components that need to be loaded for messages</param>
        /// <param name="loadingMethod">The delegate encapsulates a method to load messages.</param>
        /// <param name="limit">The max number of component loading tasks can be run in parallel.</param>
        /// <returns></returns>
        public static async Task<IList<Component>> LoadMessagesParallel(
            this IList<Component> components,
            Func<Component, Task<Component>> loadingMethod,
            int limit)
        {
            // Store all Tasks.
            var allTasks = new List<Task<Component>>();
            var activeTasks = new List<Task<Component>>();

            // Throttling will be handled.
            foreach (var item in components)
            {
                if (activeTasks.Count >= limit)
                {
                    var completedTask = await Task.WhenAny(activeTasks).ConfigureAwait(false);
                    activeTasks.Remove(completedTask);
                }

                // Continue to add task.
                var task = loadingMethod(item);
                allTasks.Add(task);
                activeTasks.Add(task);
            }

            // Wait for all tasks to complete.
            await Task.WhenAll(allTasks).ConfigureAwait(false);

            // Get the values from the tasks and put them in a list.
            List<Component> loadedComponentList = new List<Component>();
            foreach (var task in allTasks)
            {
                loadedComponentList.Add(task.Result);
            }

            return loadedComponentList;
        }
    }
}
