import os,json,HTMLTestRunner,unittest
from excelutility import excelutil
from ddt import ddt,data
from datetime import datetime
import constant


def curfoldercount(dir):
    return str(len(os.listdir(dir)))


def rootfoldercount(dir):
    L = []
    for paths in os.walk(dir):
        L.append(paths)
    # L includes the root, should -1 to get the folder count under it
    return str(len(L)-1)

def rootfilecount(dir):
    L = []
    for paths, folders, files in os.walk(dir):
        L.extend(files)
    return str(len(L))

def curfilename(dir):
    L = []
    for paths, folders, files in os.walk(dir):
        L.extend(files)
    return L



def readjson(dir, filename, key):
    with open(dir+filename, 'r', encoding='utf-8') as f:
        temp = json.load(f)
        keylist = key.split('.')
        for i in keylist:
            temp = temp[i]
    return str(temp)



@ddt
class Test(unittest.TestCase):

    @data(*excelutil())
    def test(self, case):

        if (case['category'] == 'FolderCountCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            curfolder = curfoldercount(constant.rootdir + case['path'])
            self.assertEqual(curfolder, case['expected'])
        elif (case['category'] == 'FileNameCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            curfileList = curfilename(constant.rootdir + case['path'])
            self.assertEqual(curfileList.sort(), eval(case['expected']).sort())
        elif (case['category'] == 'JsonValueCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            result = readjson(constant.rootdir + case['path'], case['filename'], case['key'])
            self.assertEqual(result, case['expected'])



if __name__ == "__main__":

    print('=====AutoTest Start======')
    discover = unittest.defaultTestLoader.discover(constant.test_dir, pattern='test.py')
    now = datetime.now().strftime('%Y-%m-%d_%H_%M_%S_')
    filename = constant.test_dir + now + 'result.html'
    fp = open(filename, 'wb')
    runner = HTMLTestRunner.HTMLTestRunner(stream=fp, title='test report', description='test result')
    result = runner.run(discover)
    print('Total test case run: %s' % result.testsRun)
    print('Passed test case run: %s' % result.success_count)
    print('Failed test case run: %s' % result.failure_count)
    print('Detailed test report can be found: %s' % os.path.abspath(filename))
    fp.close()
    print('=====AutoTest End======')








