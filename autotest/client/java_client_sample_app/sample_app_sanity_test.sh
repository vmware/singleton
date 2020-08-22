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
client_jar=$base_dir/build/libs/*.jar
echo "test sample-client-app"
cd $base_dir/sample-client-app && mkdir lib
cp $client_jar $base_dir/sample-client-app/lib
gradle build
java -jar build/libs/*.jar zh-Hans
if [ ! -d "$base_dir/sample-client-app/sample-app-with-shared-lib" ]; then
    echo "sample-app-with-shared-lib not found, skip test."
    exit 0
fi
echo "test sample-app-with-shared-lib"
cd $base_dir/sample-client-app/sample-shared-library && mkdir lib
cp $client_jar $base_dir/sample-client-app/sample-shared-library/lib
gradle build
cd $base_dir/sample-client-app/sample-app-with-shared-lib && mkdir lib
cp $base_dir/sample-client-app/sample-shared-library/build/libs/*.jar $client_jar ./lib
gradle build
java -jar build/libs/*.jar
