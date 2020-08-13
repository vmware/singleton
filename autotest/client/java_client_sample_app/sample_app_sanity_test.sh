#!/bin/bash

check_result() {
    if [[ $? == 0 ]]; then
        echo sample app run without error.
    else
        echo sample app exit with error code $?.
    fi
}

trap check_result EXIT
base_dir=$1
cd $base_dir/sample-client-app && mkdir lib
cp $base_dir/build/libs/*.jar vipconfig.properties $base_dir/sample-client-app/lib
gradle build
java -jar build/libs/*.jar zh-Hans
