import os,json,HTMLTestRunner,unittest
from excelutility import excelutil
from ddt import ddt,data
from datetime import datetime
from zipfile import ZipFile
import constant


def curfoldercount(dir):
    return str(len(os.listdir(dir)))

def curfilecount(dir):
    return str(len(os.listdir(dir)))


def readzipfile(zipdir, innerzipdir, filename, key ):
    with ZipFile(constant.rootdir+zipdir) as zf:
        file_data = zf.read(innerzipdir+filename)
        json_str = file_data.decode('utf-8')
        data = eval(json_str)
        keylist = key.split('.')
        for i in keylist:
            data = data.get(i)
    return str(data)


# result = readzipfile('./singleton-g11n-cldr-pattern/src/main/resources/cldr/data/32.0.0/cldr-core-32.0.0.zip', 'cldr-core-32.0.0/supplemental' ,'/aliases.json', 'supplemental.metadata.alias.languageAlias.zyb._reason')
# print('result is: %s' % result)



def readjson(dir, filename, key):
    with open(dir+filename, 'r', encoding='utf-8') as f:
        temp = json.load(f)
        keylist = key.split('.')
        for i in keylist:
            temp = temp[i]
    return str(temp)


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

def unzipfile(dir, path):
    try:
        filename = curfilename(dir)
        for i in filename:
            with ZipFile(constant.rootdir + constant.version + "/" + i) as zf:
                for f in zf.namelist():
                    zf.extract(f, path)
        zf.close()
    except:
        print(f'{i} is unzip failed.')

# unzipfile('./singleton-g11n-cldr-pattern/src/main/resources/cldr/data/32.0.0', constant.unzipfile_dir)



@ddt
class Test(unittest.TestCase):

    @data(*excelutil())
    def test(self, case):



        #Check the count of languages
        if (case['category'] == 'FolderCountCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            curfolder = curfoldercount(constant.unzipfile_dir + case['innerzipdir'])
            self.assertEqual(curfolder, case['expected'])
        #Check the count of files
        if (case['category'] == 'FileCountCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            curfile = curfilecount(constant.unzipfile_dir + case['innerzipdir'])
            self.assertEqual(curfile, case['expected'])
        #Check the included files are correct and no loss
        elif (case['category'] == 'FileNameCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            curfileList = curfilename(constant.rootdir + case['zipdir'])
            self.assertEqual(curfileList.sort(), eval(case['expected']).sort())
        #Check the data in json file is correct
        elif (case['category'] == 'JsonValueCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            result = readzipfile(case['zipdir'], case['innerzipdir'], case['filename'], case['key'])
            self.assertEqual(result, case['expected'])



if __name__ == "__main__":

    print('=====AutoTest Start======')
    unzipfile(constant.rootdir + constant.version, constant.unzipfile_dir)
    discover = unittest.defaultTestLoader.discover(constant.test_dir, pattern='test.py')
    now = datetime.now().strftime('%Y-%m-%d_%H_%M_%S_')
    filename = constant.test_dir + now + 'result.html'
    fp = open(filename, 'wb')
    runner = HTMLTestRunner.HTMLTestRunner(stream=fp, title='test report', description='test result')
    result = runner.run(discover)
    print('Total test case run: %s' % result.testsRun)
    print('Passed test case run: %s' % result.success_count)
    unpass = int(result.error_count) + int(result.failure_count)
    print('Failed and error test case run: %d' % unpass)
    print('Detailed test report can be found: %s' % os.path.abspath(filename))
    fp.close()
    print('=====AutoTest End======')








