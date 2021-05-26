# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

from setuptools import setup, find_packages


setup(
    name = "sgtnclient",
    version = "1.0.1",

    packages = find_packages(),

    include_package_data = True,

    zip_safe = False
    )
