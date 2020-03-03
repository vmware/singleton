#!/usr/bin/env python3

import json
import argparse
import requests
from requests.auth import HTTPBasicAuth


def createProject(project_name, project_key, org_key, auth):
    print('Create project on SonarCloud ......')
    data = {'name':project_name, 'project':project_key, 'organization':org_key, 'visibility':'public'}
    res = requests.post('https://sonarcloud.io/api/projects/create', data=data, auth=auth)
    print(res.json())

def getAllQualityGates(org_key, auth):
    print('Get quality gate id by project on SonarCloud ......')
    params={'organization':org_key}
    response = requests.get('https://sonarcloud.io/api/qualitygates/list', params=params, auth=auth)
    print('organization key: {}'.format(org_key))
    print('params: {}'.format(params))
    print('response string: {}'.format(response))
    res_in_json = response.json()
    print(res_in_json)
    return res_in_json

def getQualityGateByProject(project_key, org_key, auth):
    print('Get quality gate id by project on SonarCloud ......')
    data={'project':project_key, 'organization':org_key}
    response = requests.post('https://sonarcloud.io/api/qualitygates/get_by_project', data=data, auth=auth)
    res_in_json = response.json()
    print(res_in_json)
    return res_in_json

def createQualityGate(gate_name, org_key, auth):
    print('Create quality gate on SonarCloud ......')
    data={'name':gate_name, 'organization':org_key}
    response = requests.post('https://sonarcloud.io/api/qualitygates/create', data=data, auth=auth)
    res_in_json = response.json()
    print(res_in_json)
    return res_in_json

def destroyQualityGate(gate_id, org_key, auth):
    print('Destroy quality gate on SonarCloud ......')
    data={'id':gate_id, 'organization':org_key}
    requests.post('https://sonarcloud.io/api/qualitygates/destroy', data=data, auth=auth)

def createQualityGateConditions(id, conditions_file, org_key, auth):
    print('Create conditions for quality gate')
    with open(conditions_file, 'r') as f:
        conditions_dict = json.load(f)
    for condition in conditions_dict['conditions']:
        data = {'error':condition['error'],'gateId':id, 'metric':condition['metric'], 'op':condition['op'], 'organization':org_key}
        res = requests.post('https://sonarcloud.io/api/qualitygates/create_condition', data=data, auth=auth)
        print(res.json())

def associateQualityGate(id, project_key, org_key, auth):
    print('Associate project to quality gate')
    data={'gateId':id, 'organization':org_key, 'projectKey':project_key}
    requests.post('https://sonarcloud.io/api/qualitygates/select', data=data, auth=auth)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('-ProjectName', required=True, help='Name of project to be created.')
    parser.add_argument('-ProjectKey', required=True, help='Key for generating project.')
    parser.add_argument('-OrgKey', required=True, help='Key of organization in which the project is')
    parser.add_argument('-QualityGateName', required=True, help='Name of quality gate to be created.')
    parser.add_argument('-QualityGateConditions', required=True, help='Conditions description json file.')
    parser.add_argument('-SonarToken', required=True, help='Sonar token for authentication.')
    args = parser.parse_args()
    auth = HTTPBasicAuth(args.SonarToken, '')
    print("SonarToken:{}".format(args.SonarToken))
    print("ProjectName:{}".format(args.ProjectName))
    print("ProjectKey:{}".format(args.ProjectKey))
    print("OrgKey:{}".format(args.OrgKey))
    print("QualityGateName:{}".format(args.QualityGateName))
    print("QualityGateConditions:{}".format(args.QualityGateConditions))

    all_quality_gates = getAllQualityGates(args.OrgKey, auth)
    for gate in all_quality_gates['qualitygates']:
        if gate['name'] == args.QualityGateName:
            destroyQualityGate(gate['id'], args.OrgKey, auth)
            break
    createProject(args.ProjectName, args.ProjectKey, args.OrgKey, auth)
    quality_gate = createQualityGate(args.QualityGateName, args.OrgKey, auth)
    quality_gate_id = quality_gate['id']
    print('Quality gate id="%s"' % quality_gate_id)
    createQualityGateConditions(quality_gate_id, args.QualityGateConditions, args.OrgKey, auth)
    associateQualityGate(quality_gate_id, args.ProjectKey, args.OrgKey, auth)
