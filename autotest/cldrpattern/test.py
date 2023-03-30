import os,json,HTMLTestRunner,unittest,sys,jpype,ast
from excelutility import excelutil
from ddt import ddt,data
from datetime import datetime
from zipfile import ZipFile
import constant


def curfoldercount(directory):
    return str(len(os.listdir(directory)))

def curfilecount(directory):
    return str(len(os.listdir(directory)))


def readzipfile(zipdir, innerzipdir, filename, key):
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



def readjson(directory, filename, key):
    with open(directory+filename, 'r', encoding='utf-8') as f:
        temp = json.load(f)
        keylist = key.split('.')
        for i in keylist:
            temp = temp[i]
    return str(temp)


def rootfoldercount(directory):
    L = []
    for paths in os.walk(directory):
        L.append(paths)
    # L includes the root, should -1 to get the folder count under it
    return str(len(L)-1)

def rootfilecount(directory):
    L = []
    for paths, folders, files in os.walk(directory):
        L.extend(files)
    return str(len(L))

def curfilename(directory):
    L = []
    for paths, folders, files in os.walk(directory):
        L.extend(files)
    return L

def curfoldername(directory):
    L = []
    for paths, folders, files in os.walk(directory):
        L.extend(folders)
    return L

def rootfilename(directory, listvalue, externalfile):
    result = True
    listvalue = ast.literal_eval(listvalue)
    for paths, folders, files in os.walk(directory):
        # only folder under root, different
        if paths == directory:
            continue
        elif paths == directory + '\en':
            if externalfile is None:
                if sorted(files) != sorted(listvalue):
                    print("there is missing/external file: ", paths, files)
                    result = False
            else:
                newlistvalue = listvalue + ast.literal_eval(externalfile)
                if sorted(files) != sorted(newlistvalue):
                    print("there is missing/external file: ", paths, files)
                    result = False
        else:
            if sorted(files) != sorted(listvalue):
                print("there is missing/external file: ", paths, files)
                result = False
    return result

def unzipfile(directory, path):
    try:
        filename = curfilename(directory)
        for i in filename:
            with ZipFile(directory + "/" + i) as zf:
                for f in zf.namelist():
                    zf.extract(f, path)
        zf.close()
    except Exception as e:
        print("%s is unzipped failed with error '%s'" % (i,repr(e)))

# unzipfile('./jar', './new')


@ddt
class Test(unittest.TestCase):

    @data(*excelutil())
    def test(self, case):
        #invoke interface in jar package
        #global javaInstance
        javaClass = jpype.JClass("com.vmware.vipclient.sample.Format2")
        javaInstance = javaClass()
        javaInstance.initVIPServer()

        #Check the count of languages
        if (case['category'] == 'FolderCountCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            curfolder = curfoldercount(constant.unzipfile_dir + case['innerzipdir'])
            self.assertEqual(curfolder, case['expected'])

        #Check the count of files
        elif (case['category'] == 'FileCountCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            curfile = curfilecount(constant.unzipfile_dir + case['innerzipdir'])
            self.assertEqual(curfile, case['expected'])

        #Check the included files are correct and no loss
        elif (case['category'] == 'FileNameCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            if (case['filesource'] == 'jar'):
                curfileList = curfilename(constant.unzipfile_dir + case['zipdir'])
                print('file name is: %s', curfileList)
                self.assertEqual(curfileList.sort(), eval(case['expected']).sort())
                # for file source is zip
            else:
                curfileList = curfilename(constant.rootdir + case['zipdir'])
                self.assertEqual(curfileList.sort(), eval(case['expected']).sort())

        # Check the folder name
        elif (case['category'] == 'FolderNameCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            curfolderList = curfoldername(constant.unzipfile_dir + case['zipdir'])
            self.assertIn(case['expected'], curfolderList)

        #Check the data in json file that is in zip is correct
        elif (case['category'] == 'JsonValueCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            result = readzipfile(case['zipdir'], case['innerzipdir'], case['filename'], case['key'])
            self.assertEqual(result, case['expected'])

        #Check the data in json is correct
        elif (case['category'] == 'PureJsonValueCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            if (case['filesource'] == 'jar'):
                result = readjson(constant.unzipfile_dir + case['zipdir'], case['filename'], case['key'])
                print('result: %s', result)
                self.assertEqual(result, case['expected'])
            # for file source is zip
            else:
                result = readjson(case['zipdir'], case['filename'], case['key'])
                print('result: %s', result)
                self.assertEqual(result, case['expected'])
        # Check no file is lost in locale folder
        elif (case['category'] == 'LocaleFileNameCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            result = rootfilename(constant.unzipfile_dir + case['zipdir'], case['expected'], case['key'])
            self.assertTrue(result)

        # Check the number of files in all locale folders
        elif (case['category'] == 'LocaleFileCountCheck'):
            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            result = rootfilecount(constant.unzipfile_dir + case['zipdir'])
            self.assertEqual(result, case['expected'])

        # Check the cldr jar package works well with java client
        elif (case['category'] == 'CLDRJar'):

            print('############Case ID: %s #####Case Name: %s############' % (case['caseid'], case['casename']))
            if (case['zipdir'] == 'number'):
                numberResult = javaInstance.number()
                self.assertEqual(numberResult, "201,703.542")
            if (case['zipdir'] == 'percent'):
                percentResult = javaInstance.numberofPercent()
                self.assertEqual(percentResult, '23%')
            if (case['zipdir'] == 'currency'):
                currencyResult = javaInstance.numberofCurrency()
                self.assertEqual(currencyResult, "US$201,703.54")
            if (case['zipdir'] == 'datetime'):
                dtResult = javaInstance.dateTime()
                self.assertEqual(dtResult, case['expected'])
            if (case['zipdir'] == 'plural'):
                pluralResult = javaInstance.plural()
                self.assertEqual(pluralResult, case['expected'])


if __name__ == "__main__":

    print('=====AutoTest Start======')
    startime = datetime.today()
    unzipfile(constant.rootdir + constant.version, constant.unzipfile_dir)
    unzipfile(constant.jarrootdir, constant.unzipfile_dir)
    #start JVM environment before cases execution
    jvmpath = jpype.getDefaultJVMPath()
    jpype.startJVM(jvmpath, "-ea", "-Djava.class.path=%s" % constant.jar_path)
    # load and execute cases
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
    for f in result.failures:
        print('Failure details: ', f)
    for e in result.errors:
        print('Error details: ', e)
    print('Detailed test report can be found: %s' % os.path.abspath(filename))
    fp.close()
    # close JVM environment after case execution
    jpype.shutdownJVM()
    endtime = datetime.today()
    detal = endtime - startime
    print('Execution time: ', detal)
    print('=====AutoTest End======')
    if (unpass > 0):
        sys.exit(1)








