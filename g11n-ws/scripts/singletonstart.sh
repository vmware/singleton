#Copyright 2019-2022 VMware, Inc.
#SPDX-License-Identifier: EPL-2.0
#!/bin/bash
basepath=$(cd `dirname $0`; pwd)
echo "begin to running singletonstart.sh shell in path: $basepath"
APP_NAME="$1"
if [ -z "$APP_NAME" ]; then
APP_NAME=`ls | grep "^singleton" | grep "\.jar$"`
fi
echo "current app name is $APP_NAME !"
function do_start(){
#check the process is running
local pcmd="ps -ef | grep \"java -jar $APP_NAME\" | grep -v grep | awk '{print \$2}'"
local pid=$(eval $pcmd)
echo "pid value is $pid"
if [ -z "$pid" ]; then
   nohup java -jar $APP_NAME  >/dev/null 2>&1 &
   pid=$(eval $pcmd)
   echo "$APP_NAME start successfully. pid = $pid"
else
   echo "$APP_NAME is already running. pid = $pid"
fi
}

do_start