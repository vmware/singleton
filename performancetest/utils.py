# @Time 2022/10/18 13:20
# Author: beijingm

import os
import json

# disable InsecureRequestWarning
import urllib3
urllib3.disable_warnings()

__RESOURCE_DIR__ = os.path.join(os.path.dirname(__file__), 'resource')


def read_json(file: str) -> list[dict]:
    file_path = os.path.join(__RESOURCE_DIR__, file)
    with open(file_path, mode='r', encoding='utf-8') as f:
        return json.load(f)


if __name__ == '__main__':
    import urllib.parse

    r = urllib.parse.unquote("http://localhost/i18n/api/v1/translation/components?components=AngularJS2%2Ccomponent2&locales=ja&productName=VMCUI&pseudo=false&version=1.0.0")
    print(r)