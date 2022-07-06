"""
PMeter     --  ThreadGroup - -Collection1 -- -Api1
                                             -Api2
                                             -Api3
                           - -Collection1

           --  ThreadGroup - -Collection2
                             -Collection2

PMeter Mainly responsible for starting the serial startup Tasks, all the Tasks are executed, and the data is analyzed. Display data.
ThreadGroup Collection for multi-threaded concurrent testing. Thread_number and loop_count can be configured to control the concurrency scenarios of the collection. Thread group corresponding to jmete
HttpCollection Used to add the interface under test. Multiple APIs can be added. Use requests.Session multiplexing to reduce network overhead

Test scenario 1, 10 threads, loop 10 times, test 1 scenario 22api
thread_group = ThreadGroup()
thread_group.create_task(thread_number=10, loop_count=2, HttpCollection())
thread_group.run()

Test scenario 2, 1 thread, duration 60 seconds
thread_group = ThreadGroup()
thread_group.create_task(thread_number=2, duration=60, HttpCollection(1))
thread_group.create_task(thread_number=2, duration=10, HttpCollection(2))
thread_group.run()
"""
import json
import os
import uuid
import threading
from typing import Optional
from loguru import logger
import requests
from queue import Queue
from datetime import datetime, timedelta
import sys

logger.remove()
logger.add(sys.stderr, level="INFO")
requests.packages.urllib3.disable_warnings()

BASE_URL: str = "https://127.0.0.1:8090"


class HttpCollection:
    """
    1. To achieve support for multiple APIs, loop execution, and provide a unified scheduling entry __call__()
    2，read from json file
    3，
    """

    def __init__(self, file: str, name: str = None):
        self.id: str = str(uuid.uuid4())
        self.name: str = name if name else f'TaskName'
        self.http_session = requests.Session()
        self.base_dir: str = os.getcwd()
        self.cases: list[dict] = self.read_json(file)

    def read_json(self, file: str) -> list[dict]:
        file_path = os.path.join(self.base_dir, 'resource', file)
        with open(file_path, mode='r', encoding='utf-8') as f:
            return json.load(f)

    def validate(self, response: requests.Response, case: dict):
        error_msg = f' Actual status_code: {response.status_code} ' \
                    f' expected status_code: {case.get("response_data").get("response").get("code")} are inconsistent'
        assert response.status_code == case.get('response_data').get("response").get("code"), error_msg

        validate_time: float = case.get("validate_time")
        error_msg3 = f' Actual response time: {"%.3f" % (response.elapsed.total_seconds() * 1000)}ms' \
                     f'longer than expected response time: {case.get("validate_time")}ms'
        assert response.elapsed.total_seconds() * 1000 < validate_time, error_msg3

    def execute(self, case, q, stop=None, i=None):
        thread_id: str = threading.current_thread().name
        data: dict = {
            'success': False,
            'response_time': 1000,
            'data': {},
            'thread_id': thread_id,
            'case_name': case.get("name")
        }

        try:
            r: requests.Response = self.http_session.request(case.get('method'), BASE_URL + case.get('url'),
                                                             json=case.get('request_data'), verify=False)
            logger.debug(
                f'thread_id={threading.current_thread().name},case_name={case.get("name")}  response_time={"%.3f" % (r.elapsed.total_seconds() * 1000)}ms')

        except Exception as e:
            logger.error(f'Request failed, {data}, {e}')

        else:
            try:
                self.validate(r, case)
            except AssertionError as e:
                data['response_time'] = r.elapsed.total_seconds()
                data['data'] = r.json()
                logger.error(
                    f'Response time verification failed, case_name="{case.get("name")}" , url="{BASE_URL}{case.get("url")}",json="{case.get("request_data")}",error_msg="{e}"')

            except Exception as e:
                logger.error(f'{data}')
                raise e
            else:
                data['response_time'] = r.elapsed.total_seconds()
                data['data'] = r.json()
                data['success'] = True
                logger.debug(f'{case.get("name")} ')
        q.put(data)

    def __call__(self, q: Queue, loop_count: Optional[int], duration: Optional[float]):
        """
        There are durations, which are executed through durations. Cycle times fail
        Otherwise, execute the number of loops
        """

        if duration:
            start: float = datetime.now().timestamp()
            stop: float = (datetime.now() + timedelta(seconds=duration)).timestamp()

            while start <= stop:
                for case in self.cases:
                    self.execute(case, q, stop=stop)
                    start = datetime.now().timestamp()

        else:
            for i in range(loop_count):
                for case in self.cases:
                    self.execute(case, q, i=i)


class ThreadGroup:
    """
    Need to execute HttpRequest in multiple threads
    The number of loops indicates that within the thread, the for loop makes several interface requests
    The number of threads is the number of users. Execute collection concurrently
    """

    def __init__(self, thread_number: int, loop_count: Optional[int], duration: Optional[float], q: Queue):
        self.thread_number: int = thread_number
        self.loop_count: Optional[int] = loop_count
        self.duration: Optional[float] = duration
        self.group: list[threading.Thread] = []
        self.q: Queue = q

    def create(self, collection: HttpCollection):
        for _ in range(self.thread_number):
            self.group.append(
                threading.Thread(target=collection, args=(self.q, self.loop_count, self.duration)))
        return self

    def __call__(self, *args, **kwargs):
        logger.info(f'ThreadGroups{threading.current_thread().name} start')
        for _task in self.group:
            _task.start()
        for _task in self.group:
            _task.join()
        logger.info(f'ThreadGroups{threading.current_thread().name} done!!!')


class PMeter:
    """
    Do thread management, simulate multiple ThreadGroups in jmeter
    Block the main thread. Statistical data
    """

    def __init__(self):
        self.task_group: list[threading.Thread] = []
        self.q_map: dict[HttpCollection, Queue] = {}

    def create_task(self, collection: HttpCollection, thread_group: str = None,
                    thread_number: int = 1, loop_count: Optional[int] = 1,
                    duration: Optional[float] = None) -> 'PMeter':
        q = Queue()
        target = ThreadGroup(thread_number=thread_number, q=q,
                             loop_count=loop_count, duration=duration).create(collection)
        self.task_group.append(threading.Thread(target=target, name=thread_group))
        self.q_map[collection] = q
        return self

    def run(self):
        for task_group_thread in self.task_group:
            task_group_thread.start()
            task_group_thread.join()

    def analysis(self):
        result: bool = True
        collections_map: dict[HttpCollection, dict] = {}
        collections_result: dict[HttpCollection, bool] = {}
        logger.debug(f'*********** Start analysis ************')
        for collection, q in self.q_map.items():
            _collection: dict = {}
            data_list: list = list()
            for _ in range(q.qsize()):
                data: dict = q.get()
                data_list.append(data)
            collections_result[collection] = all([_data.get('success') for _data in data_list])
            for data in data_list:
                case_name: str = data.get('case_name')
                response_time: float = data.get('response_time')
                _collection.setdefault(case_name, []).append(data)
            collections_map[collection] = _collection
        for collection, _result in collections_result.items():
            if not _result:
                logger.error(f'exit with {_result} ')
                logger.error(f'This CI execution failed')
                result = result and False

        for collection, data in collections_map.items():
            logger.debug(f'{"-" * 20} analysis {collection.name} start!!! {"-" * 20}')
            logger.debug(f'{"-" * 20} start calculating the average {"-" * 20}')
            self.average(data)
            logger.debug(f'{"-" * 20} start calculating the Median {"-" * 20}')
            self.median(data)
            logger.debug(f'{"-" * 20} start calculating the 90% Line {"-" * 20}')
            self.ninety(data)
            logger.debug(f'{"-" * 20} analysis {collection.name} end!!! {"-" * 20}')

        logger.info(f'*********** Finished analysis ************')
        logger.info('The test is completed, the CI execution is successful')

        return result

    def average(self, collections: dict[str, list]):
        for case_name, response_list in collections.items():
            response_time_list: list[float] = [response_time.get('response_time') for response_time in response_list]
            avg: float = sum(response_time_list) / len(response_time_list)
            logger.debug(f'{case_name} Response Time average is {avg * 1000}ms')

    def median(self, collections: dict[str, list]):
        for case_name, response_list in collections.items():
            response_time_list: list[float] = [response_time.get('response_time') for response_time in response_list]
            response_time_list.sort()
            size: int = len(response_time_list)
            if size % 2 == 0:
                median_time: float = (response_time_list[size // 2] + response_time_list[(size // 2) - 1]) / 2
            else:
                median_time: float = response_time_list[size // 2]
            logger.debug(f'{case_name} Response Time Median is {median_time * 1000}ms')

    def ninety(self, collections: dict[str, list]):
        for case_name, response_list in collections.items():
            response_time_list: list[float] = [response_time.get('response_time') for response_time in response_list]
            response_time_list.sort()
            size: int = len(response_time_list)
            ninety_time: float = response_time_list[int(size * 0.9) - 1]
            logger.debug(f'{case_name} Response Time 90% Line is {ninety_time * 1000}ms')


if __name__ == '__main__':
    pmeter = PMeter()
    pmeter.create_task(collection=HttpCollection(name='Scenes1', file='data.json'), thread_number=1, loop_count=1,
                       thread_group='Singleton_api_by_times')
    pmeter.run()
    exit(0) if pmeter.analysis() else exit(-1)
