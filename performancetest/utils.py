# @Time 2022/10/18 13:20
# Author: beijingm

import os
import json
import threading

# disable InsecureRequestWarning
import urllib3

urllib3.disable_warnings()

__RESOURCE_DIR__ = os.path.join(os.path.dirname(__file__), 'resource')
default = ["VMCUI", "VMCUI1"]
li = ["VMCUI", "VMCUI1"]


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


def get_ele():
    # lock.acquire()
    global li
    try:
        ele = li.pop()
    except IndexError:
        li = default.copy()
        ele = li.pop()
    # lock.release()
    return ele


if __name__ == '__main__':
    r = read_json("VMCUI_v1.json")
    r = r[0]
    print(r.name)
