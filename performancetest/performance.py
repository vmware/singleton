import random
import time
import uuid
import threading
from queue import Queue
from pathlib import Path
from typing import Optional

import yaml
import requests
from loguru import logger

from utils import read_json, TestCase, Parameters

_CONFIG_ = Path(__file__).parent.joinpath("config.yaml")

BASE_URL: str = "https://34.95.120.208"

config: dict = yaml.safe_load(_CONFIG_.read_bytes())

LOCALES = ["de_DE", "fr_FR", "ja_JP", "ru_RU", "zh_CN", "zh_TW"]


class CollectionResponse:
    __slots__ = ["case_name", "success", "status", "response_time", "response_content"]

    def __init__(self):
        self.case_name: str = ''
        self.success: bool = False
        self.status: int = 0  # 0：success， 1：success(warning)，2：fail，3：error
        self.response_time: float = 1000000.0
        self.response_content: dict = {}


class HttpCollection:
    def __init__(self, name: str, file: str, threshold_limit: float):
        self.id: str = str(uuid.uuid4())
        self.name: str = name
        self.http_session = requests.Session()
        self.testcases: list[TestCase] = read_json(file)
        self.total_time: float = 0
        self.threshold_limit: float = threshold_limit
        self.success: bool = True

    def get_case_by_name(self, name: str) -> TestCase:
        for case in self.testcases:
            if case.name == name:
                return case

    @staticmethod
    def validate(response: requests.Response, case: TestCase):
        # assert http status_code == 200
        assert response.status_code == 200, f"Bad Request! status_code: {response.status_code} != 200."

        # assert response return_code
        code: int = response.json().get("response", {}).get("code", -1)
        expected_code: int = case.validators.get("return_code", -100)
        assert code == expected_code, f"Bad Response! return_code: {code} is not expected({expected_code})."

    def execute(self, case: TestCase, q: Queue, p: Parameters):
        resp: CollectionResponse = CollectionResponse()
        resp.case_name = case.name

        _product_name: str = p.get_ele()
        url: str = BASE_URL + case.url.format(productName=_product_name)
        if case.params.get("productName", None):
            case.params["productName"] = _product_name

        if case.params.get("locale", None):
            case.params["locale"] = random.choice(LOCALES)

        if case.params.get("displayLanguage", None):
            case.params["displayLanguage"] = random.choice(LOCALES)

        if case.method == "PUT":
            if case.body.get("data", {}).get("productName", None):
                case.body["data"]["productName"] = _product_name

        try:
            r: requests.Response = self.http_session.request(case.method, url, params=case.params, json=case.body,
                                                             headers=case.headers, verify=False)
        except Exception as e:
            resp.status = 3  # http request error or code error
            logger.critical((f'🆔{case.name}💣ERROR\n'
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
                resp.status = 2  # fail
                resp.response_time = round(r.elapsed.total_seconds() * 1000, 3)
                resp.response_content = r.content
                logger.error((f'🆔{case.name}❌FAIL\n'
                              f'{"*" * 30} request_data {"*" * 30}\n'
                              f'url= {r.request.url}\n'
                              f'json= {case.body}\n'
                              f"response={r.text[0:200]}...\n"
                              f'error_msg= {e}\n'
                              f'{"*" * 74}\n'))

            except Exception as e:
                resp.status = 3  # code error
                logger.critical((f'🆔{case.name}💣ERROR\n'
                                 f'{"*" * 30} request_data {"*" * 30}\n'
                                 f'url= {r.request.url}\n'
                                 f'json= {case.body}\n'
                                 f"response={r.text[0:200]}...\n"
                                 f'error_msg= {e}\n'
                                 f'{"*" * 74}\n'))

            else:
                resp.response_time = round(r.elapsed.total_seconds() * 1000, 3)
                resp.response_content = r.json()
                resp.success = True

                response_time_threshold: float = case.validators.get("response_time_threshold", 0)
                if resp.response_time > response_time_threshold:
                    resp.status = 1
                    logger.warning(
                        f'🆔{case.name}💢WARN🕜:{"%.3f" % resp.response_time}ms! 🔗content-size: {len(r.content)} bytes')
                else:
                    resp.status = 0
                    logger.debug(
                        f'🆔{case.name}✅PASS🕜:{"%.3f" % resp.response_time}ms! 🔗content-size: {len(r.content)} bytes')

        q.put(resp)

    def __call__(self, index: int, q: Queue, loop_count: Optional[int] = None, duration: Optional[float] = None):
        """
        There are durations, which are executed through durations. Cycle times fail
        Otherwise, execute the number of loops
        """
        p = Parameters(index)

        if loop_count:
            for case in self.testcases:
                for i in range(loop_count):
                    self.execute(case, q, p)
        elif duration:
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
        logger.debug(f'🔔HttpCollection: <{threading.current_thread().name}> start running!')
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
        self.collections_map: dict[
            HttpCollection, dict[str, list[CollectionResponse]]] = {}

        self.collections_result_map: dict[HttpCollection, dict[str, dict]] = {}

    def create_task(self, collection: HttpCollection, thread_group_name: str = None,
                    thread_number: int = 1, loop_count: Optional[int] = 1,
                    duration: Optional[float] = None) -> 'PMeter':
        q: Queue = Queue()
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

        for collection, case_data in self.collections_map.items():
            _analysis: dict[str, dict] = {}

            threshold_limit: float = collection.threshold_limit
            for case_name, response_list in case_data.items():
                result: bool = True
                total_num: int = len(response_list)
                success_num: int = len([response for response in response_list if response.success])
                warn_num: int = len([response for response in response_list if response.status == 1])
                fail_num: int = len([response for response in response_list if response.status == 2])
                error_num: int = len([response for response in response_list if response.status == 3])
                success_responses: list[float] = [r.response_time for r in response_list if r.success]
                avg: float = round(sum(success_responses) / success_num, 2) if success_num else float(0)
                pass_rate: float = (1 - warn_num / success_num) if success_num else float(0)

                tc: TestCase = collection.get_case_by_name(case_name)
                average_response_time: float = tc.validators.get("average_response_time", float(0))

                if fail_num or error_num:
                    result = False
                if avg > average_response_time:
                    result = False
                if pass_rate < threshold_limit:
                    result = False
                analysis_data: dict = {"Total": total_num, "Success": success_num, "Warn": warn_num, "Fail": fail_num,
                                       "Error": error_num,
                                       "AverageTime": avg, "PassRate": pass_rate, "threshold_limit": threshold_limit,
                                       "average_response_time": average_response_time, "result": result}
                _analysis[case_name] = analysis_data
                collection.success = collection.success and result
            self.collections_result_map[collection] = _analysis

        return self

    def exit(self):
        exit_code = -1
        for collection in self.collections_result_map.keys():
            if not collection.success:
                exit(exit_code)

    def analysis(self):
        for collection, data in self.collections_result_map.items():
            logger.info("@" + f' Analysis <{collection.name}> '.center(168, '@') + "@")
            self.average(collection, data)

    @staticmethod
    def average(collection: HttpCollection, collection_data: dict[str, dict]):
        logger.info("|" + f"Average Response Time Table".center(169, '-') + "|")
        logger.info("|" + f"➕".center(1, '-') +
                    "|" + f"Total".center(5, '-') +
                    "|" + f"Warn".center(4, '-') +
                    "|" + f"Fail".center(4, '-') +
                    "|" + f"Error".center(5, '-') +
                    "|" + f"AverageTime".center(11, '-') +
                    "|" + f"Threshold".center(9, '-') +
                    "|" + f"Testcase".center(122, '-') + "|")
        for case_name, response_dict in collection_data.items():
            success: str = "🟩" if response_dict["result"] else "🟥"
            total_num: int = response_dict["Total"]
            warn_num: int = response_dict["Warn"]
            fail_num: int = response_dict["Fail"]
            error_num: int = response_dict["Error"]
            avg: float = response_dict["AverageTime"]
            pass_rate: str = f'{round(response_dict["PassRate"] * 100, 2)}%'

            logger.info("|" + f"{success}".ljust(1) +
                        "|" + f"{total_num}".ljust(5) +
                        "|" + f"{warn_num}".ljust(4) +
                        "|" + f"{fail_num}".ljust(4) +
                        "|" + f"{error_num}".ljust(5) +
                        "|" + f"{avg}ms".ljust(11) +
                        "|" + f"{pass_rate}".ljust(9) +
                        "|" + f"{case_name}".ljust(122) + "|")
        logger.info("|" + f"-".center(169, '-') + "|")


if __name__ == '__main__':

    tsp: float = time.time() * 1000
    pmeter = PMeter()

    for conf in config.get("HttpCollections", []):
        pmeter.create_task(
            collection=HttpCollection(name=conf["name"], file=conf["file"], threshold_limit=conf["threshold_limit"]),
            thread_number=conf["thread_number"], loop_count=conf["loop_count"],
            thread_group_name=conf["thread_group_name"])
    time.sleep(5)  # wait cpu down when docker start
    pmeter.run()
    pmeter.analysis()
    cost: float = round(time.time() * 1000 - tsp, 3)
    logger.info(f"Test Completed in 🕑{cost}ms!")
    pmeter.exit()
