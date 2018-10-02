#!/bin/bash

baseUrl="http://s3.amazonaws.com/eegeo-static/"

srcPackageName="sdk.package.android.cpp11.c++_static.tar.gz"
destPackageName="./sdk.package.tar.gz"
includeDestination="./sdk/src/main/cpp/libs/eegeo"
sdkDestination="sdk.package.android"

echo "Fetching eeGeo sdk..."
rm -f ./$destPackageName
rm -rf $includeDestination

src_url=$(echo $baseUrl$srcPackageName | sed "s:+:%2B:g")

curl $src_url > ./$destPackageName

statuscode=$?
if [ $statuscode -ne 0 ] ; then
    echo "Failed to download sdk package ${baseUrl}${srcPackageName}" >&2
    exit $statuscode
fi    

if [ ! -d `dirname "$includeDestination"` ]; then
    mkdir -p `dirname "$includeDestination"`
fi

tar -zxvf $destPackageName

if [ $? -ne 0 ] ; then
    echo "Failed to unzip sdk package ${destPackageName}"
    exit 1    
fi

rm -f ./$destPackageName
platformVersion=`cat ./$sdkDestination/version.txt`
echo "Platform version --> $platformVersion"
echo mv ./$sdkDestination $includeDestination
mv ./$sdkDestination $includeDestination
