#Copyright 2019-2022 VMware, Inc.
#SPDX-License-Identifier: EPL-2.0
#coding:utf-8
'''
Created on Mar 2, 2018

@author: linr
'''

import os, fnmatch, sys, hashlib, random

class PseudoHandler:
    encoding = 'UTF-8'
    sid = 0
    #def __init__(self, rootpath):
    #    self.filepath = rootpath
        
    def lookforMatchedFiles(self, rootpath, pattern):
        result = []
        for root, dirs, files in os.walk(rootpath):
            for name in files:
                if fnmatch.fnmatch(name, pattern):
                    result.append(os.path.join(root, name))
        return result
    
    def remove(self, array):
        for element in array:
            print 'Deleting file: ' + element
            os.remove(element)

    def getPseudoTag(self, key):   
        at = '@'
        uarray = ["张","議","お","중","ç","Ä","ñ"]
        i = random.randint(0, len(uarray)-1)
        j = random.randint(0, len(uarray)-1)
        uStr = uarray[i] + uarray[j]
        m = hashlib.md5()
        m.update(bytes(key))
        hash = m.hexdigest()
        hashStr = hash[0:1] +hash[4:5] + hash[8:9] + hash[15:16]
        self.sid = self.sid + 1
        return at + str(self.sid) + uStr + hashStr + at

    # load the json file and append the pseudo tag before and after key's value
    def getFileWithPseudo(self, fileHandle):
        all_line_txt = fileHandle.readlines()
        started = False
        for index, line in enumerate(all_line_txt):
            stripLine = line.strip()
            if stripLine.find('"messages"') > -1:
                started = True
                continue
            elif stripLine == '},':
                started = False
                break
            if started:
                startStr = '"'
                endStr = '",'
                if stripLine.find(startStr) != 0:
                    continue
                firstDoubleQuoteIndex = line.find(startStr)
                secondDoubleQuoteIndex = line.find(startStr, firstDoubleQuoteIndex + 1)
                thirdDoubleQuoteIndex = line.find(startStr, secondDoubleQuoteIndex + 1)
                # parse to get the value
                if all_line_txt[index+1].strip() == '},': # if next line is end, find (") as the end index, else find (",) as the end index
                    endIndex = line.rindex('"')
                else:
                    endIndex = line.rindex(endStr)
                key = line[firstDoubleQuoteIndex+1 : secondDoubleQuoteIndex]
                pesudoTag = self.getPseudoTag(key)
                all_line_txt[index]  = line[0 : thirdDoubleQuoteIndex+1] + pesudoTag + line[thirdDoubleQuoteIndex+1 : endIndex]+ pesudoTag + line[endIndex:len(line)]
        return all_line_txt
    
    def writePseudo(self, array):        
        for element in array:
            print 'Read and write pseudo for file: ' + element
            readFileHandle = open(element, 'r')
            pseudoText = self.getFileWithPseudo(readFileHandle)
            readFileHandle.close()
            writeFileHandle =open(element, 'w')
            for line in pseudoText:
                writeFileHandle.write(line)
            writeFileHandle.close()

if __name__ == '__main__':
    print 'Start to parse json file...'
    fileloader = PseudoHandler()
    rootpath = sys.argv[1].split('bundle_path=')[1]
    ops = sys.argv[2]
    suffix_latest = "*_latest.json"
    if ops == 'ops=remove_latest':
        files = fileloader.lookforMatchedFiles(rootpath, suffix_latest)
        fileloader.remove(files)
    elif ops == 'ops=create_pesudo':
        files = fileloader.lookforMatchedFiles(rootpath, suffix_latest)
        fileloader.writePseudo(files)
    