using Microsoft.VisualStudio.TestTools.UnitTesting;
using I18NClient.Net.Options;
using I18NClient.Net.Loaders;
using System.ComponentModel.DataAnnotations;

namespace I18NClient.Net.Builder.Tests
{
    [TestClass()]
    public class I18NClientBuilderTests
    {
        [TestMethod()]
        public void BuildTest_RemoteLoadingStrategy_ShouldNotNull()
        {
            var options = new I18NClientOptions()
            {
                ProductName = "AWConsole",
                Version = "1.0",
                BackendServiceUrl = "https://g11n-vip-dev-1.eng.vmware.com:8090/",
                OfflineResourceRelativePath = "Data.Resources.{0}.LabelValues"
            };

            var client = new I18NClientBuilder(options)
                .WithLoadingStrategy<RemoteLoadingStrategy>()
                .Build();

            Assert.IsNotNull(client.ComponentMessageCacheManager);
            Assert.IsNotNull(client.ProductInfoCacheManager);
            Assert.IsNotNull(client.ResourceService);
            Assert.IsNotNull(client.CultureService);
        }

        [TestMethod()]
        public void BuildTest_MixedLoadingStrategy_ShouldNotNull()
        {
            var options = new I18NClientOptions()
            {
                ProductName = "AWConsole",
                Version = "1.0",
                BackendServiceUrl = "https://g11n-vip-dev-1.eng.vmware.com:8090/",
                OfflineResourceRelativePath = "Data.Resources.{0}.LabelValues"
            };

            var client = new I18NClientBuilder(options)
                .WithLoadingStrategy<MixedLoadingStrategy>()
                .Build();

            Assert.IsNotNull(client.ComponentMessageCacheManager);
            Assert.IsNotNull(client.ProductInfoCacheManager);
            Assert.IsNotNull(client.ResourceService);
            Assert.IsNotNull(client.CultureService);
        }

        [TestMethod()]
        [ExpectedException(typeof(ValidationException), "MaxDegreeOfParallelism must be between 1 and 30.")]
        public void BuildTest_WrongMaxDegreeOfParallelism_ShouldThrowException()
        {
            var options = new I18NClientOptions()
            {
                ProductName = "AWConsole",
                Version = "1.0",
                BackendServiceUrl = "https://g11n-vip-dev-1.eng.vmware.com:8090/",
                OfflineResourceRelativePath = "Data.Resources.{0}.LabelValues",
                MaxDegreeOfParallelism = 100
            };

            _ = new I18NClientBuilder(options)
                .WithLoadingStrategy<RemoteLoadingStrategy>()
                .Build();
        }

        [TestMethod()]
        [ExpectedException(typeof(ValidationException), "Product name is required.")]
        public void BuildTest_MissProductName_ShouldThrowException()
        {
            var options = new I18NClientOptions()
            {
                Version = "1.0",
                BackendServiceUrl = "https://g11n-vip-dev-1.eng.vmware.com:8090/",
                OfflineResourceRelativePath = "Data.Resources.{0}.LabelValues",
                MaxDegreeOfParallelism = 30
            };

            _ = new I18NClientBuilder(options)
                .WithLoadingStrategy<RemoteLoadingStrategy>()
                .Build();
        }

        [TestMethod()]
        [ExpectedException(typeof(ValidationException), "Version is required.")]
        public void BuildTest_MissVersion_ShouldThrowException()
        {
            var options = new I18NClientOptions()
            {
                ProductName = "AWConsole",
                BackendServiceUrl = "https://g11n-vip-dev-1.eng.vmware.com:8090/",
                OfflineResourceRelativePath = "Data.Resources.{0}.LabelValues",
                MaxDegreeOfParallelism = 30
            };

            _ = new I18NClientBuilder(options)
                .WithLoadingStrategy<RemoteLoadingStrategy>()
                .Build();
        }

        [TestMethod()]
        [ExpectedException(typeof(ValidationException), "Version is required.")]
        public void BuildTest_MissURL_ShouldThrowException()
        {
            var options = new I18NClientOptions()
            {
                ProductName = "AWConsole",
                Version = "1.0",
                OfflineResourceRelativePath = "Data.Resources.{0}.LabelValues",
                MaxDegreeOfParallelism = 30
            };

            _ = new I18NClientBuilder(options)
                .WithLoadingStrategy<MixedLoadingStrategy>()
                .Build();
        }
    }
}