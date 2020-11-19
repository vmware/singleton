import os,json,HTMLTestRunner,unittest
from excelutility import excelutil
from ddt import ddt,data
from datetime import datetime

# Set testdata directory:
# rootdir = 'D:\\PycharmProjects\\SmokeTesting\\singleton-i18n-patterns-core-0.5.1' #The path of cldr data storage
# test_dir = 'D:\\PycharmProjects\\SmokeTesting' #The directory of generated test report
rootdir = './singleton-i18n-patterns-core-0.5.1' #The path of cldr data storage
test_dir = './' #The directory of generated test report

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

        if (case['casename'] == 'Check the number of folders in cldr folder'):
            print('############Case ID: %s #####Case Name: %s############' %(case['caseid'],case['casename']))
            rootfolder = rootfoldercount(rootdir + case['path'])
            self.assertEqual(rootfolder, case['expected'])
        elif (case['casename'] == 'Check the number of files in cldr folder'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            rootfile = rootfilecount(rootdir + case['path'])
            self.assertEqual(rootfile, case['expected'])
        elif (case['casename'] == 'Check no file in folder data'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            curfile = rootfilecount(rootdir + case['path'])
            self.assertEqual(curfile, case['expected'])
        elif (case['key'] == None and case['filename'] == None and case['path'] != '\\cldr'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            curfolder = curfoldercount(rootdir + case['path'])
            self.assertEqual(curfolder, case['expected'])
        else:
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            result = readjson(rootdir + case['path'], case['filename'], case['key'])
            self.assertEqual(result, case['expected'])



if __name__ == "__main__":

    print('=====AutoTest Start======')
    discover = unittest.defaultTestLoader.discover(test_dir, pattern='test.py')
    now = datetime.now().strftime('%Y-%m-%d_%H_%M_%S_')
    filename = test_dir + now + 'result.html'
    fp = open(filename, 'wb')
    runner = HTMLTestRunner.HTMLTestRunner(stream=fp, title='test report', description='test result')
    result = runner.run(discover)
    print('Total test case run: %s' % result.testsRun)
    print('Passed test case run: %s' % result.success_count)
    print('Failed test case run: %s' % result.failure_count)
    print('Detailed test report can be found: %s' % os.path.abspath(filename))
    fp.close()
    print('=====AutoTest End======')








