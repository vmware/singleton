from pathlib import Path
import pytest
import shutil

_CACHE_DIR_ = Path(__file__).parent

# _CACHE_ = _CACHE_DIR_.joinpath(".cache")
_ROOT_ = Path(__file__).parent
_CONFIG_ = _ROOT_.joinpath("config")
_CACHE_ = _ROOT_.joinpath("resources", ".cache")


@pytest.fixture()
def delete_cache():
    shutil.rmtree(_CONFIG_.parent.joinpath(".cache"), ignore_errors=True)
    yield


@pytest.fixture()
def update_cache():
    shutil.rmtree(_CONFIG_.parent.joinpath(".cache"), ignore_errors=True)
    shutil.copytree(_CACHE_, _CONFIG_.parent.joinpath(".cache"), dirs_exist_ok=True)
    yield
