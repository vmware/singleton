import threading
import time

lock = threading.Lock()
default = ["VMCUI", "VMCUI1"]
li = ["VMCUI", "VMCUI1"]


def get_ele():
    # lock.acquire()
    global li
    try:
        r = li.pop()
    except IndexError:
        li = default.copy()
        r = li.pop()
    # lock.release()
    return r


if __name__ == '__main__':

    # for _ in range(10):
    #     print(get_ele())

    def test():
        case_name = get_ele()
        print(f"666{case_name}^^^")
        time.sleep(0.3)


    tasks = []
    for _ in range(100):
        tasks.append(threading.Thread(target=test))

    for task in tasks:
        task.start()

    for task in tasks:
        task.join()
