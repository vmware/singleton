#Copyright 2019-2022 VMware, Inc.
#SPDX-License-Identifier: EPL-2.0
#!/bin/bash
basepath=$(cd `dirname $0`; pwd)
echo "the singleton shell path: $basepath"
APP_NAME=`ls | grep "^singleton" | grep "\.jar$"`

#Introduce usage
function usage(){
        echo "Usage:sh singletonall.sh [start|stop|check|status]"
}

#check the process is running
function is_exist(){
       local pcmd="ps -ef | grep \"java -jar $APP_NAME\" | grep -v grep | awk '{print \$2}'"
       local pid=$(eval $pcmd)
        #not existed return ,existed return 0
        if [ -z "$pid" ]; then
                echo 0
        else
                echo $pid
        fi
}

#method of start
function startapp(){
        local startedpid=$(is_exist)
        if [ $startedpid -eq 0 ]; then
                echo "begin to start $APP_NAME"
                nohup java -jar $APP_NAME >/dev/null 2>&1 &
				startedpid=$(is_exist)
                echo "$APP_NAME start successfully. pid = $startedpid"
        else
                echo "$APP_NAME is already running. pid = $startedpid"
        fi
}

#keep app alive
function keepappalive(){
        local alivePid=$(is_exist)
        if [ $alivePid -gt 0 ]; then
        while [ "1" = "1" ]
        do
                alivePid=$(is_exist)
                if [ $alivePid -eq 0 ]; then
                        echo "$APP_NAME is stop running. and begin to start"
                        nohup java -jar $APP_NAME >/dev/null 2>&1 &
                        alivePid=$(is_exist)
                        echo "$APP_NAME on running status. pid = $alivePid"
                else
                        echo "$APP_NAME on running status. pid = $alivePid"
						sleep 5
                fi

        done
else
        echo "$APP_NAME is not started !"
fi
}

#method of stop
function stopapp(){
local stopPid=$(is_exist)
if [ $stopPid -eq 0 ]; then
        echo "$APP_NAME is not running"
else
        local shellPid=`ps -ef|grep singletonall.sh |grep "check$"|grep -v grep|awk '{print $2}'`
        if [ ! -n "$shellPid" ]; then
                echo "check $APP_NAME PID script has stopped!"    
        else
                kill -9 $shellPid
				echo "check $APP_NAME PID script stop successfully! Shell PID=$shellPid"
        fi
        sleep 2
        stopPid=$(is_exist)
        kill -9 $stopPid
        echo "$APP_NAME stop successfully. Pid is $stopPid"
fi
}

#print program status
function status(){
local statusPid=$(is_exist)
if [ $statusPid -eq 0 ]; then
        echo "$APP_NAME is NOT running."
else
        echo "$APP_NAME is running. Pid is $statusPid"
fi
}

#base on input call the method
case "$1" in
"start")
startapp
;;
"stop")
stopapp
;;
"check")
keepappalive
;;
"status")
status
;;
*)
usage
;;
esac

