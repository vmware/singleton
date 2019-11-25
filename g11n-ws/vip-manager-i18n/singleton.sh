#!/bin/bash
basepath=$(cd `dirname $0`; pwd)
echo "the singleton shell path: $basepath"

APP_NAME=vip-manager-i18n-0.1.0.jar

#Introduce usage
function usage(){
  echo "Usage:sh singleton.sh [start|stop|status]"
}

#check the process is running
function is_exist(){
  local pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}'`
#not existed return ,existed return 0     
  if [ -z $pid ]; then
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
     nohup java -jar $APP_NAME  >/dev/null 2>&1 &
     keepappalive
  else
     echo "$APP_NAME is already running. pid = $startedpid"
  fi
}

function checkalive(){
local flag="check"
echo $flag > check.bat
while [ "$flag" == "check" ]
do
alivePid=$(is_exist)
if [ $alivePid -eq 0 ]; then
     echo "$APP_NAME is stop running. and begin to start"
     nohup java -jar $APP_NAME  >/dev/null 2>&1 &
	 alivePid=$(is_exist)
     echo "$APP_NAME on running status. pid = $alivePid"
  else
   sleep 5
   echo "$APP_NAME on running status. pid = $alivePid"
fi
 while read line
          do
            flag=${line}
         break
 done < check.bat  
done
echo "stopChecked" > check.bat
}

#keep app alive
function keepappalive(){
local alivePid=$(is_exist)
if [ $alivePid -gt 0 ]; then
   checkalive
else 
  echo "$APP_NAME start failure"
fi
}

#method of stop
function stopapp(){
  local stopPid=$(is_exist)
  if [ $stopPid -eq 0 ]; then
    echo "$APP_NAME is not running"
  else
    echo "stop">check.bat
	echo "$APP_NAME begin to stopping and Pid = $stopPid"
    sleep 5
	local killFlag="stop"
	local expire=1
	while [ "$killFlag" != "stopChecked" ] && [ $expire -lt 30 ]
    do
	 ##  echo $expire
	   while read line
          do
            killFlag=${line}
         break
       done < check.bat
	   
		sleep 2
 ##		echo $killFlag
		let expire=$expire+1
	done 
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
  "status")
    status
        ;;
  *)
    usage
        ;;
esac
