using System.Collections.Generic;

namespace I18NClient.Net.Models
{
    class SingletonSupportedLocaleList
    {
        public string productName { get; set; }

        public string version { get; set; }

        public List<string> locales { get; set; }
    }
}
