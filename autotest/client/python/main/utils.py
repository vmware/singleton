# @Time 2022/11/10 15:15
# Author: beijingm

from pathlib import Path

ROOT: Path = Path(__file__).parent

_TESTCASE_: Path = Path(ROOT).joinpath("testcases")

_RESOURCE_: Path = Path(ROOT).joinpath("resources")

_CACHE_: Path = _TESTCASE_.joinpath("cache")

_ICU_: Path = _TESTCASE_.joinpath("icu")

_TRANSLATION_: Path = _TESTCASE_.joinpath("translation")
