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
        self.bundles: list[str] = ["TestBundle1", "TestBundle2", "TestBundle3", "TestBundle4", "TestBundle5",
                                   "TestBundle6", "TestBundle7",
                                   "TestBundle8", "TestBundle9", "TestBundle10", "TestBundle11", "TestBundle12",
                                   "TestBundle13", "TestBundle14", "TestBundle15",
                                   "TestBundle16", "TestBundle17", "TestBundle18", "TestBundle19", "TestBundle20",
                                   "TestBundle21", "TestBundle22",
                                   "TestBundle23", "TestBundle24", "TestBundle25", "TestBundle26", "TestBundle27",
                                   "TestBundle28", "TestBundle29",
                                   "TestBundle30", "TestBundle31", "TestBundle32", "TestBundle33", "TestBundle34",
                                   "TestBundle35", "TestBundle36",
                                   "TestBundle37", "TestBundle38", "TestBundle39", "TestBundle40", "TestBundle41",
                                   "TestBundle42", "TestBundle43",
                                   "TestBundle44", "TestBundle45", "TestBundle46", "TestBundle47", "TestBundle48",
                                   "TestBundle49", "TestBundle50"]

    def get_ele(self) -> str:
        ele: str = self.bundles[(self.count + self.index) % len(self.bundles)]
        self.count += 1
        return ele
