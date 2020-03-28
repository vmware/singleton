using System;
using System.Collections;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient.Implementation;
using SingletonClient.Implementation.Support;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestBaseIo
    {
        [TestMethod]
        public void TestAccessService()
        {
            SingletonAccessService service = new SingletonAccessService();
            Hashtable headers = SingletonUtil.NewHashtable();
            String text = service.HttpGet("https://github.com", headers);
            Assert.AreEqual(text.Contains("GitHub"), true);

            headers.Clear();
            text = service.HttpGet("https://github.next.com", headers);
            Assert.AreEqual(text == null, true);

            headers.Clear();
            text = service.HttpPost("https://mail.yahoo.com", "{}", headers);
            Assert.AreEqual(text.Contains("Yahoo"), true);

            headers.Clear();
            text = service.HttpPost("http://github.next.com", "{}", headers);
            Assert.AreEqual(text == null, true);
        }
    }
}
