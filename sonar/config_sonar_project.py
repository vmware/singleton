#!/usr/bin/env python3

import requests
from requests.auth import HTTPBasicAuth
import sys

if __name__ == "__main__":
    project_name = sys.argv[1]
    project_key = sys.argv[2]
    org_key = sys.argv[3]
    gate_name = sys.argv[4]
    token = sys.argv[5]
    auth=HTTPBasicAuth(token,'')

    print('1. Create project')
    data={'name':project_name,'project':project_key,'organization':org_key,'visibility':'public'}
    res = requests.post("https://sonarcloud.io/api/projects/create", data=data, auth=auth)
    print(res.json())

    print('2. Create quality gate')
    data={'name':gate_name,'organization':org_key}
    response = requests.post("https://sonarcloud.io/api/qualitygates/create", data=data, auth=auth)
    gate_create_res = response.json()
    print(gate_create_res)
    try:
        gate_id = gate_create_res['id']
    except KeyError:
        print('quality gate exists')
    else:
        print("quality gate id='%s'" % gate_id)
        #create multiple conditions
        print('3. Create conditions for quality gate')
        data={'error':'1600','gateId':gate_id, 'metric':'code_smells', 'op':'GT','organization':org_key}
        res = requests.post("https://sonarcloud.io/api/qualitygates/create_condition", data=data, auth=auth)
        print(res.json())

        print('4. Associate project to quality gate')
        data={'gateId':gate_id, 'organization':org_key, 'projectKey':project_key}
        requests.post("https://sonarcloud.io/api/qualitygates/select", data=data, auth=auth)
