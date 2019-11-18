#!/bin/bash -ex

base_dir=$1
cd $base_dir/sample-client-app && mkdir lib
cp $base_dir/build/libs/*.jar $base_dir/sample-client-app/lib
# gradle build
# java -jar build/libs/<jar file> <locale>