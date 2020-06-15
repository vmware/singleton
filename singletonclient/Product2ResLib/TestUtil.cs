using SingletonClient.Implementation.Helpers;
using System;

namespace Product2ResLib
{
    class TestUtil
    {
        public void DoSomething()
        {
            string cultrueName = "EN_US";

            Console.WriteLine(cultrueName);
            for (int i = 0; i < 10 * 10000; i++)
            {
                cultrueName = CultureHelper.GetCulture(cultrueName).Name;
            }
            Console.WriteLine(cultrueName);
        }
    }
}
