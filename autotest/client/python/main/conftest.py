# @Time 2022/11/10 15:25
# Author: beijingm
import os
import time

import pytest
from pathlib import Path
import shutil


@pytest.fixture(scope="session", autouse=True)
def setup():
    log_path = Path(os.getcwd()).joinpath("logs")
    cache_path = Path(os.getcwd()).joinpath(".cache")
    log_path.mkdir(exist_ok=True)
    yield
    time.sleep(5)
    shutil.rmtree(log_path)
    shutil.rmtree(cache_path, ignore_errors=True)
