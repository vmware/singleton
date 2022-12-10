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