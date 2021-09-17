/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using YamlDotNet.RepresentationModel;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient.Implementation;
using System.Collections.Generic;


namespace UnitTestSingleton
{

    [TestClass]
    public class TestClient: BaseCommonTest
    {

        public YamlMappingNode PlanRoot { get; }

        public TestClient()
        {
            string text = BaseIo.obj().LoadResourceText("test_plan");
            PlanRoot = TextUtil.GetYamlRoot(text);
        }

        protected void DoTest(YamlMappingNode node, bool seeCache)
        {
            Console.WriteLine("\n");
            Show("test start");
            int start = System.Environment.TickCount;

            CheckFunction();

            Show("create release object");
            AccessSingleton access = GetAccess(node);
            DoCommonTestConfig(access);
            DoCommonTestTranslation(access);
            if (seeCache)
            {
                DoCommonTestMessages(access);
            }

            int span = System.Environment.TickCount - start;
            Show("test end", span + "ms");
            Console.WriteLine("\n");
        }

        protected YamlSequenceNode GetPlans(string plansName)
        {
            Show("plans", plansName);
            Console.WriteLine("");
            return TextUtil.GetArrayChild(PlanRoot, plansName);
        }

        protected YamlSequenceNode PrepareBefore()
        {
            SingletonConfig.SetFake("res.source2", new string[] { "about", "contact" });

            YamlSequenceNode node = GetPlans("plans_before");
            LoadTestData(new string[] { "test_prepare.txt", "test_define_before.txt", "test_define2.txt" });
            RunTestData(null, "TestLoadBeforeService");
            return node;
        }

        protected YamlSequenceNode Prepare()
        {
            SingletonConfig.SetFake("res.source2", new string[] { "about", "aboutadd", "contact" });

            YamlSequenceNode node = GetPlans("plans");
            LoadTestData(new string[] { "test_prepare.txt", "test_define.txt", "test_define2.txt" });
            RunTestData(null, "TestLoadService");
            return node;
        }

        protected YamlSequenceNode PreparePseudo()
        {
            SingletonConfig.SetFake("res.source2", new string[] { "about", "aboutadd", "contact" });

            YamlSequenceNode node = GetPlans("plans_pseudo");
            LoadTestData(new string[] { "test_prepare.txt", "test_define_pseudo.txt", "test_define2.txt" });
            RunTestData(null, "TestLoadPseudoService");
            return node;
        }

        protected void RunPlans(YamlSequenceNode plans, List<int> ids, bool seeCache)
        {
            RunTestData(null, "TestDelay1");

            int id = -1;
            foreach (var tuple in plans.Children)
            {
                id++;
                if (ids.Count > 0 && !ids.Contains(id))
                {
                    continue;
                }
                DoTest((YamlMappingNode)tuple, seeCache);
            }
        }

        [TestMethod]
        public void TestApi()
        {
            RunPlans(PrepareBefore(), new List<int> {}, true);
            RunPlans(Prepare(), new List<int> {}, true);
            RunPlans(PreparePseudo(), new List<int> {}, false);
        }
    }
}
