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
from requests.exceptions import RequestException
from loguru import logger

from utils import read_json, TestCase, Parameters

BASE_URL: str = "https://localhost:8090"


class CollectionResponse:
    __slots__ = ["case_name", "success", "response_time", "response_content"]

    def __init__(self):
        self.case_name: str = ''
        self.success: bool = False
        self.response_time: float = 1000000.0
        self.response_content: dict = {}


class HttpCollection:
    """
    1. To achieve support for multiple APIs, loop execution, and provide a unified scheduling entry __call__()
    2，read from json file
    """

    def __init__(self, name: str, file: str):
        self.id: str = str(uuid.uuid4())
        self.name: str = name
        self.http_session = requests.Session()
        self.testcases: list[TestCase] = read_json(file)
        self.total_time: float = 0

    def validate(self, response: requests.Response, case: TestCase):
        # assert http status_code == 200
        assert response.status_code == 200, f"Bad Http Request!, status_code: {response.status_code} != 200."

        # assert response return_code
        code: int = response.json().get("response", {}).get("code", -1)
        validate_code: int = case.validators.get("return_code", -100)
        error_msg2 = (f'Actual return_code: {code} '
                      f'Expected return_code: {validate_code} are inconsistent.'
                      f'The Actual Response: {response.json()}')
        assert code == validate_code, error_msg2

        # assert response_time
        expected_validate_time: float = case.validators.get("response_time", 0)
        response_time: float = round(response.elapsed.total_seconds() * 1000, 3)
        error_msg3 = (f'Actual response time: {"%.3f" % response_time}ms '
                      f'Expected response time: {expected_validate_time}ms')
        assert response_time < expected_validate_time, error_msg3

        # assert response_content

    def execute(self, case: TestCase, q: Queue, p: Parameters) -> None:
        thread_id: str = threading.current_thread().name
        resp: CollectionResponse = CollectionResponse()
        resp.case_name = case.name

        _product_name: str = p.get_ele()
        _url = case.url.format(productName=_product_name)
        if case.params.get("productName", None):
            case.params["productName"] = _product_name

        if case.method == "PUT":
            if case.body.get("data", {}).get("productName", None):
                case.body["data"]["productName"] = _product_name

        url: str = BASE_URL + _url
        try:
            execute_start: float = time.time() * 1000
            r: requests.Response = self.http_session.request(case.method, url, params=case.params, json=case.body,
                                                             headers=case.headers, verify=False)
            duration_time: float = round(time.time() * 1000 - execute_start, 3)
        except RequestException as e:
            # http request error
            logger.error((f'[{thread_id}] TestCase: <{case.name}> execute failed.\n'
                          f'{"*" * 30} request_data {"*" * 30}\n'
                          f'url= {url}\n'
                          f'json= {case.body}\n'
                          f"response={{}}\n"
                          f'error_msg= {e}\n'
                          f'{"*" * 74}\n'))

        else:
            # http request ok and validate
            try:
                self.validate(r, case)
            except AssertionError as e:
                resp.response_time = round(r.elapsed.total_seconds() * 1000, 3)
                resp.response_content = r.json()
                logger.error((f'[{thread_id}] TestCase: <{case.name}> execute failed.\n'
                              f'{"*" * 30} request_data {"*" * 30}\n'
                              f'url= {r.request.url}\n'
                              f'json= {case.body}\n'
                              f"response={r.json()}\n"
                              f'error_msg= {e}\n'
                              f'{"*" * 74}\n'))

            except Exception as e:
                # default, code error
                logger.critical((f'[{thread_id}] TestCase: <{case.name}> execute exception!!!\n'
                                 f'{"*" * 30} request_data {"*" * 30}\n'
                                 f'url= {r.request.url}\n'
                                 f'json= {case.body}\n'
                                 f"response={r.json()}\n"
                                 f'error_msg= {e}\n'
                                 f'{"*" * 74}\n'))

            else:
                resp.response_time = round(r.elapsed.total_seconds() * 1000, 3)
                resp.response_content = r.json()
                resp.success = True
                logger.debug(
                    f'[{thread_id}] TestCase: <{case.name}> execute success! duration:{duration_time}ms!')
        q.put(resp)

    def __call__(self, index: int, q: Queue, loop_count: Optional[int], duration: Optional[float]):
        """
        There are durations, which are executed through durations. Cycle times fail
        Otherwise, execute the number of loops
        """
        p = Parameters(index)
        if duration:
            start_tsp: float = time.time()
            current_tsp: float = time.time()

            while current_tsp - start_tsp <= duration:
                # loop if duration and stop if timeout
                for case in self.testcases:
                    current_tsp: float = time.time()
                    if current_tsp - start_tsp <= duration:
                        self.execute(case, q, p)
                    else:
                        break

        else:
            for case in self.testcases:
                for i in range(loop_count):
                    self.execute(case, q, p)


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
        """
        create collection in multi thread.
        """
        for i in range(self.thread_number):
            self.group.append(
                threading.Thread(target=collection, args=(i, self.q, self.loop_count, self.duration)))
        return self

    def __call__(self, *args, **kwargs):
        logger.debug(f'HttpCollection: <{threading.current_thread().name}> start running!')
        tsp: float = time.time() * 1000
        for _task in self.group:
            _task.start()
        for _task in self.group:
            _task.join()
        cost: float = round(time.time() * 1000 - tsp, 3)
        logger.debug(f'HttpCollection: <{threading.current_thread().name}>  completed in {cost}ms!')


class PMeter:
    """
    Do thread management, simulate multiple ThreadGroups in jmeter
    Block the main thread. Statistical data
    """

    def __init__(self):
        self.task_group: list[threading.Thread] = []
        self.q_map: dict[HttpCollection, Queue] = {}  # the map between http_collection and queue

        self.collections_data: dict[HttpCollection, list[CollectionResponse]] = {}
        self.collections_map: dict[HttpCollection, dict[str, list[CollectionResponse]]] = {}
        self.collections_result: dict[HttpCollection, bool] = {}

    def create_task(self, collection: HttpCollection, thread_group_name: str = None,
                    thread_number: int = 1, loop_count: Optional[int] = 1,
                    duration: Optional[float] = None) -> 'PMeter':
        q = Queue()
        target: ThreadGroup = ThreadGroup(thread_number=thread_number, q=q,
                                          loop_count=loop_count, duration=duration).create(collection)
        self.task_group.append(threading.Thread(target=target, name=thread_group_name))
        self.q_map[collection] = q
        return self

    def run(self) -> 'PMeter':
        # run
        for task_group_thread in self.task_group:
            task_group_thread.start()
            task_group_thread.join()

        # read q in collections_data
        for collection, q in self.q_map.items():
            q_list: list[CollectionResponse] = []
            for _ in range(q.qsize()):
                resp_data: CollectionResponse = q.get()
                q_list.append(resp_data)
            self.collections_data[collection] = q_list

        for collection, q_list in self.collections_data.items():
            _collection: dict[str, list[CollectionResponse]] = {}

            for _resp in q_list:
                _collection.setdefault(_resp.case_name, []).append(_resp)
            self.collections_map[collection] = _collection

        for collection, q_list in self.collections_data.items():
            self.collections_result[collection] = all([_data.success for _data in q_list])

        return self

    def exit(self) -> None:
        result: bool = True
        for _result in self.collections_result.values():
            _result: bool
            result = result and _result
        exit(-1) if not result else None

    def analysis(self):
        for collection, data in self.collections_map.items():
            logger.info("@" + f' Analysis <{collection.name}> '.center(162, '@') + "@")
            self.average(data)

    def average(self, collection_data: dict[str, list[CollectionResponse]]):
        logger.info("|" + f"Average Response Time Table".center(163, '-') + "|")
        logger.info("|" + f"response_time".center(13, '-') +
                    "|" + f"total_count".center(11, '-') +
                    "|" + f"error_count".center(11, '-') +
                    "|" + f"testcase".center(125, '-') + "|")
        for case_name, response_list in collection_data.items():
            total_num: int = len(response_list)
            success_num: int = len([response for response in response_list if response.success])
            success_responses: list[float] = [r.response_time for r in response_list if r.success]
            avg: float = round(sum(success_responses) / success_num, 3) if success_num else 0

            logger.info("|" + f"{avg}ms".ljust(13) +
                        "|" + f"{total_num}".ljust(11) +
                        "|" + f"{total_num - success_num}".ljust(11) +
                        "|" + f"{case_name}".ljust(125) + "|")
        logger.info("|" + f"-".center(163, '-') + "|")


if __name__ == '__main__':
    import yaml
    from pathlib import Path

    _CONFIG_ = Path(__file__).parent.joinpath("config.yaml")

    config: dict = yaml.safe_load(_CONFIG_.read_bytes())
    tsp: float = time.time() * 1000
    pmeter = PMeter()

    for conf in config.get("HttpCollections", []):
        pmeter.create_task(collection=HttpCollection(name=conf["name"], file=conf["file"]),
                           thread_number=conf["thread_number"], loop_count=conf["loop_count"],
                           thread_group_name=conf["thread_group_name"])

    pmeter.run()
    pmeter.analysis()
    cost: float = round(time.time() * 1000 - tsp, 3)
    logger.info(f"Test Completed in {cost}ms!")
    pmeter.exit()
