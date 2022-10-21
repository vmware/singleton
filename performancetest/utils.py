# @Time 2022/10/18 13:20
# Author: beijingm

import os
import json
import threading

# disable InsecureRequestWarning
import urllib3

urllib3.disable_warnings()

__RESOURCE_DIR__ = os.path.join(os.path.dirname(__file__), 'resource')


class TestCase:
    __slots__ = ["name", "url", "method", "headers", "params", "body", "validators"]

    def __init__(self, **kwargs):
        self.name: str = kwargs.get("name", "")
        self.url: str = kwargs.get("url", "")
        self.method: str = kwargs.get("method", "")
        self.headers: dict = kwargs.get("headers", {})
        self.params: dict = kwargs.get("params", {})
        self.body: dict = kwargs.get("body", {})
        self.validators: dict = kwargs.get("validators", {})


def read_json(file: str) -> list[TestCase]:
    file_path = os.path.join(__RESOURCE_DIR__, file)
    test_cases: list[TestCase] = []
    with open(file_path, mode='r', encoding='utf-8') as f:
        testcases: list[dict] = json.load(f)
    for case in testcases:
        test_cases.append(TestCase(**case))
    return test_cases


class Parameters:

    def __init__(self, index: int):
        self.index = index
        self.count = 0
        self.bundles: list[str] = ["VMCUI", "VMCUI1", "VMCUI2", "VMCUI3", "VMCUI4", "VMCUI5"]

    def get_ele(self) -> str:
        ele: str = self.bundles[(self.count + self.index) % len(self.bundles)]
        self.count += 1
        return ele


if __name__ == '__main__':
    r = read_json("VMCUI_v1.json")
    r = r[0]
    print(r.name)
