import csv
from pathlib import Path

_TESTCASES_: list = []

_RESOURCES_ = Path(__file__).parent.joinpath("resources")

with open(_RESOURCES_.joinpath("plural.csv"), mode="r", encoding='utf-8') as f:
    file = csv.reader(f)
    next(file)
    for item in file:
        try:
            format_items = eval(item[9])
        except Exception:
            format_items = None
        _TESTCASES_.append(
            {"CONFIG": item[2], "PRODUCT": item[3], "VERSION": item[4], "COMPONENT": item[5],
             "LOCALE": item[6], "KEY": item[7], "VALUE": item[8], "FORMAT_ITEMS": format_items, "ASSERTION": item[10]})
