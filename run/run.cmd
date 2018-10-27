echo off
if "%1" == "" goto no_param
if "%2" == "" goto no_param

java -jar testreport-1.0.jar %1 %2 %3
goto exit

:no_param
echo Parameter expected
 
:exit
pause



