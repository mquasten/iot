
echo start iot-batch at: %DATE% %TIME%  >>  iot.log 2>>&1

rem start mongod -dbpath "C:\mongodata"

rem sleep 5

rem java -jar iotBatch.jar updateIP   >>  iot.log 2>>&1

rem java -jar iotBatch.jar updateAll  >>  iot.log 2>>&1

java -jar iotBatch.jar updateAll 

rem shutdown /s /f /t 00 >>  iot.log 2>>&1