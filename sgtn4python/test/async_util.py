# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import sys
import threading
import asyncio

import sys
sys.path.append('../sgtnclient')
import I18N


async def hello(head, locale, delay):
    current = I18N.get_current_locale()
    print("{0}: default: {1}".format(head, current))

    tid = threading.current_thread().ident
    I18N.set_current_locale(locale)
    print("{0}: Hello world! th{1} {2}".format(head, tid, locale))
    r = await asyncio.sleep(delay)
    first_level(head, tid, 0)

def first_level(head, tid, fsn):
    second_level(head, tid, fsn)

def second_level(head, tid, ssn):
    output(head, tid, ssn)

def output(head, tid, osn):
    current = I18N.get_current_locale()
    thid = threading.current_thread().ident
    print("{0}: Hello again! th{1} {2}".format(head, tid, current))

async def test_async(tt):
    await asyncio.sleep(tt.idThread % 2 * 0.4)
    for i in range(tt.times):
        needPrint = (i == 0) and tt.needPrint
        for one in tt.group['tests']:
            delay = tt.do_one_item(one, needPrint)
            if delay > 0:
                await asyncio.sleep(tt.group['_interval']*0.001)
    tt.end_test()


class AsyncWork():

    async def do_hello(self):
        task1 = asyncio.create_task(hello('run-a', 'en', 5))
        task2 = asyncio.create_task(hello('run-b', 'de', 3))
        await task1
        await task2

    def hello(self):
        asyncio.run(self.do_hello())

    async def do_test(self, group, batches):
        tasks = []
        for tt in batches:
            task = asyncio.create_task(test_async(tt))
            tasks.append(task)

        for task in tasks:
            await task

    def test(self, group, batches):
        asyncio.run(self.do_test(group, batches))


if __name__ == '__main__':
    AsyncWork().hello()
