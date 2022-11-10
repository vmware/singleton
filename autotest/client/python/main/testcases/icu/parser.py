import csv

_TESTCASES_: list = []

with open('resources/plural.csv', encoding='utf-8') as f:
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
