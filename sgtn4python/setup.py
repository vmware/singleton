# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

from setuptools import setup, find_packages

setup(
    name="sgtnclient",
    version="0.7.1",
    author="Jasper Jin",
    author_email="jasperj@vmware.com",
    description="Singleton Client",
    license="MIT",
    url="https://github.com/vmware/singleton/tree/g11n-python-client",
    packages=['sgtnclient'],
    install_requires=[
        "PyYAML"
        ],
    classifiers=[
        "Operating System :: OS Independent"
    ]
)
