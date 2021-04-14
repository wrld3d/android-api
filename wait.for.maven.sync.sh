#!/bin/bash

usage() {
    echo "Usage: $0 <version_number> <retry_duration> <retry_count>"
    echo "eg. sh wait.for.maven.sync.sh 0.0.1335 60s 30"
    exit 1;
}

currentVersion=$1
retryDuration=$2
maxRetryCount=$3

if [ -z "${currentVersion}" ]; then
    usage
    exit 1
fi

if [ -z "${maxRetryCount}" ]; then
    usage
    exit 1
fi

if [ -z "${retryDuration}" ]; then
    usage
    exit 1
fi

# Constants
mavenData="https://repo1.maven.org/maven2/com/wrld3d/wrld-android-sdk/maven-metadata.xml"


currentTryCount=1

while [ $currentTryCount -le $maxRetryCount ]
do
    result=$(curl -s $mavenData | grep "<version>$currentVersion</version>")
    if [ $result ]; then
        break
    fi

    echo "Version not found. Retrying..."
    (( currentTryCount++ ))
    sleep $retryDuration
done

if [ $currentTryCount -gt $maxRetryCount ]; then
    echo "Failed to find version $currentVersion after $maxRetryCount retries."
    exit 1
fi

echo "Version Found."
exit 0
