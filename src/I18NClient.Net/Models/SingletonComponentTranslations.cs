using System.Collections.Generic;

namespace I18NClient.Net.Models
{
    class SingletonComponentTranslations
    {
        public string productName { get; set; }

        public string version { get; set; }

        public string dataOrigin { get; set; }

        public bool pseudo { get; set; }

        public bool machineTranslation { get; set; }

        public string component { get; set; }

        public string locale { get; set; }

        public string status { get; set; }

        public long id { get; set; }

        public Dictionary<string, string> messages { get; set; }
    }
}
