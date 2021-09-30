namespace I18NClient.Net.Constants
{
    internal static class I18NClientConstants
    {
        public static class ProductInfoCacheKey
        {
            public const string SupportedLanguages = "SupportedLanguages";

            public const string SupportedComponents = "SupportedComponents";
        }

        public static class Parallelism
        {
            /// <summary>
            /// The loading of the messages is an IO operation and does not require too much CPU participation.
            /// Almost all network IO goes through the BSD Sockets API, relying on select to get information 
            /// about socket status without blocking. So the current setting is just to prevent the upper-level 
            /// bugs from causing too many tasks in case.
            /// </summary>
            public const int maxDegreeOfParallelism = 30;
        }
    }
}
