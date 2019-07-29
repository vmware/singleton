#!/usr/bin/env python3

import urllib.request
import argparse
import json

def request_check_sonar_quality(url):
    req = urllib.request.Request(url)
    setReqHeader(req)

    response = urllib.request.urlopen(req).read().decode("utf8")
    
    res = json.loads(response)
    if res['projectStatus']['status'] != 'OK':
        raise Exception("ERROR: %s  qualitygate status is not OK" %url)
    
def setReqHeader(requst):
    requst.add_header('Cache-Control','no-cache')
    requst.add_header('accept-encoding','gzip, deflate')
    requst.add_header('Connection','keep-alive')
    requst.add_header('cache-control','no-cache')
    requst.add_header('Accept','*/*')

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('-ProjectKeyPrefixArray',required=True,help='Query Project Key Prefix,Separated by commas, etc projectA,projectB')
    parser.add_argument('-BranchName',default='',help='Branch Name,etc master')
    parser.add_argument('-HostName',required=True,help='SonarQube host name')

    args = parser.parse_args()

    projectKeyPrefixArray = args.ProjectKeyPrefixArray.split(',')

    for projectKeyPrefix in projectKeyPrefixArray:
        queryProjectKey = projectKeyPrefix

        if args.BranchName != "":
            queryProjectKey=projectKeyPrefix + ":" + args.BranchName

        url = "{1}/api/qualitygates/project_status?projectKey={0}".format(queryProjectKey,args.HostName)

        request_check_sonar_quality(url)

    print("check projects qualitygate status successfully!")
