# @Time 2022/10/24 17:20
# Author: beijingm
from pathlib import Path

import requests


class BaseFileContextManage:

    def __init__(self, file: Path):
        self._file: Path = file

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        raise NotImplementedError


class ModifyFileContext(BaseFileContextManage):

    def __init__(self, file: Path):
        super().__init__(file=file)

    def __enter__(self):
        with open(self._file, mode='r', encoding='utf-8') as f:
            data = f.readlines()
        data[5] = '"about.message" : "1234",\n'
        text = ''.join(data)
        with open(self._file, mode='w', encoding='utf-8') as f:
            f.write(text)
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        with open(self._file, mode='r', encoding='utf-8') as f:
            data = f.readlines()
        data[5] = '"about.message" : "test de key",\n'
        text = ''.join(data)
        with open(self._file, mode='w', encoding='utf-8') as f:
            f.write(text)


class BaseContextManage:

    def __init__(self, base_url: str):
        self._base_url: str = base_url
        self._session: requests.Session = requests.Session()
        self._session.headers.update({
            "accept": "application/json;charset=UTF-8",
            "Content-Type": "application/json"
        })

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        raise NotImplementedError


class ContextStringsDe1(BaseContextManage):

    def __init__(self, base_url: str):
        super().__init__(base_url=base_url)

    def revert_string_de1(self):
        # curl -X PUT "http://localhost:8091/i18n/api/v2/translation/products/PythonClient/versions/5.0.0" -H "accept: application/json;charset=UTF-8" -H "Content-Type: application/json" -d "{ \"data\": { \"creation\": { \"operationid\": \"string\" }, \"dataOrigin\": \"string\", \"machineTranslation\": false, \"productName\": \"PythonClient\", \"pseudo\": false, \"translation\": [ { \"component\": \"about\", \"locale\": \"de\", \"messages\": { \"about.message\": \"test__de_value_change\" } } ], \"version\": \"5.0.0\" }, \"requester\": \"string\"}"
        url: str = self._base_url + "/i18n/api/v2/translation/products/PythonClient/versions/5.0.0"
        body: dict = {
            "data": {
                "creation": {"operationid": "string"},
                "dataOrigin": "string",
                "machineTranslation": False,
                "productName": "PythonClient",
                "pseudo": False,
                "translation": [
                    {"component": "about", "locale": "de", "messages": {"about.message": "test__de_value"}}],
                "version": "5.0.0"},
            "requester": "string"
        }

        self._session.put(url=url, json=body)

    def modify_string_de1(self):
        url: str = self._base_url + "/i18n/api/v2/translation/products/PythonClient/versions/5.0.0"
        body: dict = {
            "data": {
                "creation": {"operationid": "string"},
                "dataOrigin": "string",
                "machineTranslation": False,
                "productName": "PythonClient",
                "pseudo": False,
                "translation": [
                    {"component": "about", "locale": "de", "messages": {"about.message": "test__de_value_change"}}],
                "version": "5.0.0"},
            "requester": "string"
        }
        self._session.put(url, json=body)

    def __enter__(self):
        self.modify_string_de1()

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.revert_string_de1()


class ContextStringsFr1(BaseContextManage):

    def __init__(self, base_url: str):
        super().__init__(base_url=base_url)

    def revert_string_fr1(self):
        url: str = self._base_url + "/i18n/api/v2/translation/products/PythonClient/versions/5.0.0"
        body: dict = {
            "data": {
                "creation": {"operationid": "string"},
                "dataOrigin": "string",
                "machineTranslation": False,
                "productName": "PythonClient",
                "pseudo": False,
                "translation": [
                    {"component": "about", "locale": "fr", "messages": {"about.message": "test_fr_value"}}],
                "version": "5.0.0"},
            "requester": "string"
        }

        self._session.put(url=url, json=body)

    def modify_string_fr1(self):
        url: str = self._base_url + "/i18n/api/v2/translation/products/PythonClient/versions/5.0.0"
        body: dict = {
            "data": {
                "creation": {"operationid": "string"},
                "dataOrigin": "string",
                "machineTranslation": False,
                "productName": "PythonClient",
                "pseudo": False,
                "translation": [
                    {"component": "about", "locale": "fr", "messages": {"about.message": "test_fr_value_change"}}],
                "version": "5.0.0"},
            "requester": "string"
        }
        self._session.put(url, json=body)

    def __enter__(self):
        self.modify_string_fr1()

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.revert_string_fr1()


class ContextModifyCacheDe1(BaseFileContextManage):

    def __init__(self, file: Path):
        super().__init__(file=file)

    def modify_cache_de(self):
        with open(self._file, mode='r', encoding='utf-8') as f:
            data = f.readlines()
        data[10] = '\t"about.message": "test__de_value_change_cache",\n'
        text = ''.join(data)

        with open(self._file, mode='w', encoding='utf-8') as f:
            f.write(text)

    def revert_cache_de(self):
        with open(self._file, mode='r', encoding='utf-8') as f:
            data = f.readlines()
        data[10] = '\t"about.message": "test__de_value",\n'
        text = ''.join(data)

        with open(self._file, mode='w', encoding='utf-8') as f:
            f.write(text)

    def __enter__(self):
        self.modify_cache_de()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.revert_cache_de()


if __name__ == '__main__':
    files = Path(__file__).parent.joinpath(".cache", "PythonClient", "6.0.0", "about", "messages_de.json")
    with ContextModifyCacheDe1(files):
        ...
