# @Time 2022/10/19 13:52
# Author: beijingm


import requests


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

    def revert_string_de6(self):
        url: str = self._base_url + "/i18n/api/v2/translation/products/PythonClient/versions/1.1.1.1.1"
        body: dict = {
            "data": {
                "creation": {"operationid": "string"},
                "dataOrigin": "string",
                "machineTranslation": False,
                "productName": "PythonClient",
                "pseudo": False,
                "translation": [
                    {"component": "about", "locale": "de", "messages": {"about.message": "test__de_value"}}],
                "version": "1.1.1.1.1"},
            "requester": "string"
        }

        self._session.put(url=url, json=body)

    def modify_string_de6(self):
        url: str = self._base_url + "/i18n/api/v2/translation/products/PythonClient/versions/1.1.1.1.1"
        body: dict = {
            "data": {
                "creation": {"operationid": "string"},
                "dataOrigin": "string",
                "machineTranslation": False,
                "productName": "PythonClient",
                "pseudo": False,
                "translation": [
                    {"component": "about", "locale": "de", "messages": {"about.message": "test__de_value_change"}}],
                "version": "1.1.1.1.1"},
            "requester": "string"}
        self._session.put(url, json=body)


class ContextStringsDe6(BaseContextManage):

    def __init__(self, base_url: str):
        super().__init__(base_url=base_url)

    def __enter__(self):
        self.revert_string_de6()

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.modify_string_de6()


if __name__ == '__main__':
    with ContextStringsDe6('http://localhost:8091') as f:
        pass
