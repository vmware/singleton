#
# Copyright 2023-2024 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import os
import ruamel.yaml
import yaml


def update_config_with_abs_paths():
    """
    The purpose of this script is to walk through all the subdirectories of the testcases and to find
    all the yml config files. They contain offline_resources_base_url, which in the original form uses
    relative path representation. However, in this way GitHub workflows cannot recognize the relative
    path and can only work with absolute paths. Testcases are working perfectly well in local machine,
    however in cloud environment like GitHub, the paths have to be absolute.

    :return: It returns the yml objects, but this time updated.
    """
    key_to_change = "offline_resources_base_url"
    base_dir = os.getcwd() + '/main/testcases'
    for root, dirs, files in os.walk(base_dir):
        for file in files:
            # Check if the file has a YAML extension
            if file.endswith(".yml") or file.endswith(".yaml"):
                new = os.path.dirname(root)
                file_path = os.path.join(root, file)
                yaml_files = ruamel.yaml.YAML() # Using ruamel does not affect the original order of the yml file
                with open(file_path, "r") as f:
                    data = yaml_files.load(f)
                if key_to_change in data:
                    new_value = 'file://' + new + '/resources/l10n/PythonClient/' + data['l10n_version'] + '/'
                    print(new_value)
                    data[key_to_change] = new_value
                    with open(file_path, "w") as f:
                        yaml_files.dump(data, f)


if __name__ == '__main__':
    update_config_with_abs_paths()
