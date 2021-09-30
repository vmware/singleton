namespace I18NClient.Net.Models
{
    class SingletonResponseHeader
    {
        public string code { get; set; }

        public string message { get; set; }

        public string serverTime { get; set; }
    }

    class SingletonResponse<T>
    {
        public SingletonResponseHeader response { get; set; }

        public T data { get; set; }
    }
}
