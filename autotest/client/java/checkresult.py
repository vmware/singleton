#!/usr/bin/python
#-------------------------------------------------------------------------------
# /*
#  * Copyright 2019-2020 VMware, Inc.
#  * SPDX-License-Identifier: EPL-2.0
#  */
#-------------------------------------------------------------------------------

import os
import sys
import xml.etree.ElementTree

if __name__ == "__main__":
   result = 'PASS'
   test = xml.etree.ElementTree.parse('./result/result.xml').getroot()
   for testSet in test.findall('TestSet'):
      currResult = testSet.get('Result')
      if currResult == 'FAIL':
         result = 'FAIL'
   if result == 'FAIL':
   	  print('Test failed.')
   	  sys.exit(1)