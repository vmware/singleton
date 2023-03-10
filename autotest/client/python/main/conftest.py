import os
import sys
import types
import inspect
import logging
from types import FrameType
from typing import Optional

import pytest
from pathlib import Path
from sgtnclient import I18N
import shutil

logger = logging.getLogger()


@pytest.fixture(scope="session", autouse=True)
def setup():
    log_path = Path(os.getcwd()).joinpath("logs")
    cache_path = Path(os.getcwd()).joinpath(".cache")
    log_path.mkdir(exist_ok=True)
    yield
    # time.sleep(5)
    # shutil.rmtree(log_path)
    # shutil.rmtree(cache_path, ignore_errors=True)


@pytest.fixture(scope="function", autouse=True)
def reset_i18n_instance():
    yield
    sgtn_client: types.ModuleType = sys.modules["sgtn_client"]
    sgtn_py37_base: types.ModuleType = sys.modules["sgtn_py37_base"]
    sgtn_py37_base._singleton_locale_context.set(None)
    sgtn_client.SingletonReleaseManager._instance = None
    I18N._release_manager = None
