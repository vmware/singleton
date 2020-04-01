#!/usr/bin/env python3

import urllib.request
import argparse
import json
import sys

def request_check_sonar_quality(url):
    req = urllib.request.Request(url)
    set_Req_Header(req)
    response = urllib.request.urlopen(req).read().decode("utf8")
    json_res = json.loads(response)
    return json_res

def set_Req_Header(requst):
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

    qualified = True
    account_pass = 0
    account_failed = 0
    print("============================= Summary =============================")
    for projectKeyPrefix in projectKeyPrefixArray:
        queryProjectKey = projectKeyPrefix
        if args.BranchName != "":
            queryProjectKey=projectKeyPrefix + ":" + args.BranchName
        url = "{}/api/qualitygates/project_status?projectKey={}".format(args.HostName, queryProjectKey)
        json_res = request_check_sonar_quality(url)
        if json_res['projectStatus']['status'] == 'OK':
            quality_summary = "Quality is OK!!!"
            account_pass = account_pass + 1
        else:
            quality_summary = "Quality is not OK!!!"
            account_failed = account_failed + 1
            qualified = False
        print("{}\nProject key: '{}' ".format(quality_summary, queryProjectKey))
        for condition in json_res['projectStatus']['conditions']:
            if condition['comparator'] == "LT" or condition['comparator'] == "WT" :
                comparator = ">="
            elif condition['comparator'] == "GT" :
                comparator = "<="
            status_print_str = condition['status']
            if condition['status'] =="OK":
                status_print_str = condition['status']+"   "
            print("{}    Metric: {}, Actual: {}, Required: {} {}".format(
                status_print_str, condition['metricKey'], condition['actualValue'], comparator, condition['errorThreshold']))
        print("Details refer to URL {}/dashboard?id={}".format(args.HostName, queryProjectKey))
        print("==================================================================")

    if qualified:
        print("All pass, total {} project".format(len(projectKeyPrefixArray)))
        sys.exit(0)
    else:
        print("Failed, total {} project, {} pass, {} failed".format(len(projectKeyPrefixArray), account_pass, account_failed))
        sys.exit(1)
