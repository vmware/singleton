from openpyxl import load_workbook

def excelutil():
    workbook = load_workbook(filename='ExternalPatternLibTestCases.xlsx')
    sheet = workbook.active

    datas = []

    for row in sheet.iter_rows(min_row=2, values_only=True):
        # data = TestData(casename=row[0],
        #                 path=row[1],
        #                 filename=row[2],
        #                 key=row[3],
        #                 expected=row[4])
        data = {"caseid": row[0],
                "casename":row[1],
                "path": row[2],
                "filename": row[3],
                "key": row[4],
                "expected": str(row[5])}
        datas.append(data)
    return datas


# rootdir = r'D:\PycharmProjects\SmokeTesting\singleton-i18n-patterns-core-0.5.1'
# datas = excelutil()
# print(excelutil()[0])
# print(excelutil())
# print(datas[1].key)
# print(len(datas))
# print(datas[19])
# print(rootdir+datas[2].path+datas[2].filename)
# print(datas)
# print(json.dumps(datas))



