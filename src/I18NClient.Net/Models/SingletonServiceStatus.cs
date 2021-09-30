using System;

namespace I18NClient.Net.Models
{
    class SingletonServiceStatus
    {
        public DateTime CheckPoint { get; set; }

        public bool IsServiceReachable { get; set; }
    }
}
