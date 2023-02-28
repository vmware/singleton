#Copyright 2019-2022 VMware, Inc.
#SPDX-License-Identifier: EPL-2.0
#!/bin/bash
basepath=$(cd `dirname $0`; pwd)
echo "begin to running singletonstop.sh shell in path: $basepath"
APP_NAME="$1"
CHECK_SHELL_NAME="$2"
if [ -z "$APP_NAME" ]; then
APP_NAME=` ls | grep "^singleton" | grep "\.jar$"`
fi

if [ -z "$CHECK_SHELL_NAME" ]; then
CHECK_SHELL_NAME="singletoncheck.sh"
fi

shellPid=`ps -ef|grep $CHECK_SHELL_NAME|grep -v grep|awk '{print $2}'`
   if [ ! -n "$shellPid" ]; then
          echo "$CHECK_SHELL_NAME script not running!"    
   else
          kill -9 $shellPid
 		  sleep 3
		  echo "$CHECK_SHELL_NAME script has stoped!"    
   fi
function stopApp(){
echo "current app name is $APP_NAME"
local pcmd="ps -ef | grep \"java -jar $APP_NAME\" | grep -v grep | awk '{print \$2}'"
local pid=$(eval $pcmd)
 #not existed return ,existed return 0
 if [ -z $pid ]; then
               echo "$APP_NAME is not running"
 else
               kill -9 $pid
               echo "$APP_NAME stop successfully. Pid is $pid"
 fi
}

stopApp