# @Time 2022/11/11 15:48
# Author: beijingm
from pathlib import Path
import pytest
import shutil

_CACHE_ = Path(__file__).parent.joinpath(".cache")


@pytest.fixture(scope="class")
def delete_cache():
    yield
    shutil.rmtree(_CACHE_, ignore_errors=True)
