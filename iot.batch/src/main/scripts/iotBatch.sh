#!/bin/bash
PWD=`pwd`
#echo $PWD
cd /cygdrive/c/GitHub/iot/iot/iot.batch
mvn clean install
cd /cygdrive/c/GitHub/iot/iot/iot.batch/target 
#java -jar iotBatch.jar updateWorkingday -d 1
#java -jar iotBatch.jar updateCalendar -d 0
#java -jar iotBatch.jar updateTemperature -d 1
java -jar iotBatch.jar updateAll
cd $PWD