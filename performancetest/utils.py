# @Time 2022/10/18 13:20
# Author: beijingm
import json
from pathlib import Path

# disable InsecureRequestWarning
import urllib3

urllib3.disable_warnings()

_RESOURCES_ = Path(__file__).parent.joinpath("resource")


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
    testcases: list[dict] = json.loads(_RESOURCES_.joinpath(file).read_bytes())
    return [TestCase(**case) for case in testcases]


class Parameters:

    def __init__(self, index: int):
        self.index: int = index
        self.count: int = 0
        self.bundles: list[str] = ["VMCUI", "VMCUI1", "VMCUI2", "VMCUI3", "VMCUI4", "VMCUI5"]

    def get_ele(self) -> str:
        ele: str = self.bundles[(self.count + self.index) % len(self.bundles)]
        self.count += 1
        return ele


if __name__ == '__main__':
    r = read_json("VMCUI_v1.json")
    r = r[0]
    print(r.name)
