using I18NClient.Net.Constants;
using System;
using System.Net.Http;

namespace I18NClient.Net.Utilities
{
    /// <summary>
    /// A provider to initialize the singleton HttpClient with different configurations.
    /// </summary>
    public static class HttpClientProvider
    {
        private static readonly Func<HttpClient> BatchClientFactory = () =>
        {
            var httpClient = new HttpClient(new HttpClientHandler
            {
                MaxConnectionsPerServer = I18NClientConstants.Parallelism.maxDegreeOfParallelism
            });

            httpClient.DefaultRequestHeaders.ConnectionClose = true;
            return httpClient;
        };

        private static readonly Func<HttpClient> ClientFactory = () =>
        {
            return new HttpClient
            {
                Timeout = TimeSpan.FromSeconds(5)
            };
        };

        private static readonly Lazy<HttpClient> batchClient = new Lazy<HttpClient>(BatchClientFactory);

        private static readonly Lazy<HttpClient> client = new Lazy<HttpClient>(ClientFactory);

        /// <summary>
        /// Gets a HttpClient instance for batch operations.
        /// </summary>
        public static HttpClient GetBatchHttpClient() => batchClient.Value;

        /// <summary>
        /// Gets a HttpClient instance for regular operations.
        /// </summary>
        public static HttpClient GetHttpClient() => client.Value;
    }
}
