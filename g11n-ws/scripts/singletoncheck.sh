#Copyright 2019-2022 VMware, Inc.
#SPDX-License-Identifier: EPL-2.0
#!/bin/bash
basepath=$(cd `dirname $0`; pwd)
echo "the singleton shell path: $basepath"
APP_NAME="$1"

if [ -z "$APP_NAME"]; then
APP_NAME=` ls | grep "^singleton" | grep "\.jar$"`
fi
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

#keep app alive
function keepappalive(){
        local alivePid=$(is_exist)
        if [ $alivePid -gt 0 ]; then
        while [ "1" = "1" ]
        do
                alivePid=$(is_exist)
                if [ $alivePid -eq 0 ]; then
                        echo "$APP_NAME is stop running. and begin to start"
                        nohup java -jar $APP_NAME  >singleton.log 2>&1 &
                        alivePid=$(is_exist)
                        echo "$APP_NAME on running status. pid = $alivePid"
                else
                        echo "$APP_NAME on running status. pid = $alivePid"
						sleep 5
                fi

        done
else
        echo "$APP_NAME is not started"
fi
}
keepappalive