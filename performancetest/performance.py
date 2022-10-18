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
import time
import uuid
import threading
from queue import Queue
from typing import Optional

import requests
from loguru import logger

from utils import read_json

BASE_URL: str = "https://127.0.0.1:8090"


class HttpCollection:
    """
    1. To achieve support for multiple APIs, loop execution, and provide a unified scheduling entry __call__()
    2，read from json file
    """

    def __init__(self, name: str, file: str):
        self.id: str = str(uuid.uuid4())
        self.name: str = name
        self.http_session = requests.Session()
        self.testcases: list[dict] = read_json(file)

    def validate(self, response: requests.Response, case: dict):
        error_msg = (f'Actual status_code: {response.status_code} '
                     f'Expected status_code: {200} are inconsistent.')
        assert response.status_code == 200, error_msg
        code: int = response.json().get("response").get("code")
        error_msg2 = (f'Actual status_code: {code} '
                      f'Expected status_code: {case.get("response").get("code")} are inconsistent.')
        assert code == case.get("response").get("code"), error_msg2

        expected_validate_time: float = case.get("validate_time")
        response_time: float = round(response.elapsed.total_seconds() * 1000, 3)
        error_msg3 = (f'Actual response time: {"%.3f" % response_time}ms '
                      f'Expected response time: {expected_validate_time}ms')
        assert response_time < expected_validate_time, error_msg3

    def execute(self, case: dict, q: Queue) -> None:
        method: str = case.get('method')
        url: str = BASE_URL + case.get('url')
        req_json: dict = case.get('request_data')
        case_name: str = case.get("name")

        thread_id: str = threading.current_thread().name
        resp_data: dict = {
            'success': False,
            'response_time': 1000,
            'data': {},
            'thread_id': thread_id,
            'case_name': case_name
        }

        try:
            r: requests.Response = self.http_session.request(method, url, json=req_json, verify=False)
        except Exception as e:
            logger.error(f'[{case_name}] request failed, {e}')

        else:
            try:
                self.validate(r, case)
            except AssertionError as e:
                resp_data['response_time'] = round(r.elapsed.total_seconds() * 1000, 3)
                resp_data['data'] = r.json()
                logger.error((f'TestCase: <{case_name}> execute failed.\n'
                              f'{"*" * 30} request_data {"*" * 30}\n'
                              f'url= {url}\n'
                              f'json= {req_json}\n'
                              f"response={r.json()}\n"
                              f'error_msg= {e}\n'
                              f'{"*" * 74}\n'))

            except Exception as e:
                logger.critical(f'Execution exception, please contact the administrator!!!')
                q.put(resp_data)
                raise e
            else:
                resp_data['response_time'] = round(r.elapsed.total_seconds() * 1000, 3)
                resp_data['data'] = r.json()
                resp_data['success'] = True
                logger.debug(f'TestCase: <{case_name}> execute success!')

        q.put(resp_data)

    def __call__(self, q: Queue, loop_count: Optional[int], duration: Optional[float]):
        """
        There are durations, which are executed through durations. Cycle times fail
        Otherwise, execute the number of loops
        """

        if duration:
            start_tsp: float = time.time()
            current_tsp: float = time.time()

            while current_tsp - start_tsp <= duration:
                for case in self.testcases:
                    self.execute(case, q)
                    current_tsp: float = time.time()

        else:
            for i in range(loop_count):
                for case in self.testcases:
                    self.execute(case, q)


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
        logger.debug(f'ThreadGroups: <{threading.current_thread().name}> start running!')
        for _task in self.group:
            _task.start()
        for _task in self.group:
            _task.join()
        logger.debug(f'ThreadGroups: <{threading.current_thread().name}>  done!!!')


class PMeter:
    """
    Do thread management, simulate multiple ThreadGroups in jmeter
    Block the main thread. Statistical data
    """

    def __init__(self):
        self.task_group: list[threading.Thread] = []
        self.q_map: dict[HttpCollection, Queue] = {}

        self.collections_data: dict[HttpCollection, list[dict]] = {}
        self.collections_map: dict[HttpCollection, dict] = {}
        self.collections_result: dict[HttpCollection, bool] = {}

    def create_task(self, collection: HttpCollection, thread_group_name: str = None,
                    thread_number: int = 1, loop_count: Optional[int] = 1,
                    duration: Optional[float] = None, need_analysis: bool = True) -> 'PMeter':
        q = Queue()
        target = ThreadGroup(thread_number=thread_number, q=q,
                             loop_count=loop_count, duration=duration).create(collection)
        self.task_group.append(threading.Thread(target=target, name=thread_group_name))
        if need_analysis:
            self.q_map[collection] = q
        return self

    def run(self) -> 'PMeter':
        for task_group_thread in self.task_group:
            task_group_thread.start()
            task_group_thread.join()

        for collection, q in self.q_map.items():
            q_list: list[dict] = []
            for _ in range(q.qsize()):
                resp_data: dict = q.get()
                q_list.append(resp_data)
            self.collections_data[collection] = q_list

        for collection, q_list in self.collections_data.items():
            q_list: list[dict]

            _collection: dict[str, list[dict]] = {}
            for _resp in q_list:
                _resp: dict

                case_name: str = _resp.get('case_name')
                _collection.setdefault(case_name, []).append(_resp)
            self.collections_map[collection] = _collection

        for collection, q_list in self.collections_data.items():
            self.collections_result[collection] = all([_data.get('success') for _data in q_list])

        return self

    def exit(self) -> None:
        result: bool = True
        for _result in self.collections_result.values():
            _result: bool
            result = result and _result
        exit(-1) if not result else None

    def analysis(self):
        for collection, data in self.collections_map.items():
            logger.info("@" + f' Analysis <{collection.name}> '.center(165, '@') + "@")
            self.average(data)
            # self.median(data)
            # self.ninety(data)

    def average(self, collection_data: dict[str, list[dict]]):
        logger.info("|" + f"Average Response Time Table".center(166, '-') + "|")
        logger.info("|" + f"response_time".center(15, '-') + "|" + f"testcase".center(150, '-') + "|")
        for case_name, response_list in collection_data.items():
            response_list: list[dict]

            response_time_list: list[float] = [response_time.get('response_time') for response_time in response_list]
            avg: float = round(sum(response_time_list) / len(response_time_list), 3)
            logger.info("|" + f"{avg}ms".ljust(15) + "|" + f"{case_name}".ljust(150) + "|")
            logger.info("|" + f"-".center(166, '-') + "|")

    def median(self, collections: dict[str, list]):
        logger.info(f'{"-" * 20} start calculating the Median {"-" * 20}')
        for case_name, response_list in collections.items():
            response_time_list: list[float] = [response_time.get('response_time') for response_time in response_list]
            response_time_list.sort()
            size: int = len(response_time_list)
            median_time: float = round(response_time_list[size // 2], 3)
            logger.info(f'TestCase: <{case_name}> Median Response Time is {median_time}ms.')

    def ninety(self, collections: dict[str, list]):
        logger.debug(f'{"-" * 20} start calculating the 90% Line {"-" * 20}')
        for case_name, response_list in collections.items():
            response_time_list: list[float] = [response_time.get('response_time') for response_time in response_list]
            response_time_list.sort()
            size: int = len(response_time_list)
            ninety_time: float = round(response_time_list[int(size * 0.9)], 3)
            logger.debug(f'{case_name} Response Time 90% Line is {ninety_time}ms')


if __name__ == '__main__':
    pmeter = PMeter()
    pmeter.create_task(collection=HttpCollection(name='VMCUI1', file='VMCUI_v1.json'), thread_number=1, loop_count=1,
                       thread_group_name='Singleton_api_testing')
    pmeter.create_task(collection=HttpCollection(name='VMCUI2', file='VMCUI_v2.json'), thread_number=2, loop_count=2,
                       thread_group_name='Singleton_api_testing')
    pmeter.run()
    pmeter.analysis()
    pmeter.exit()
