@echo off

set "BIN_HOME=%~dp0"
cd /d "%BIN_HOME%\.."
set "APP_HOME=%cd%"
set "FASTBOOT_CP=.;%APP_HOME%\cfg;%APP_HOME%\lib-ext\*;%APP_HOME%\lib\*"

REM cd %APP_HOME%
cd %BIN_HOME%

if "%1" == "" (
	echo "****************************************************************************************************"
    echo "*  Fastboot Usage                                                                                  *"
    echo "*                                                                                                  *"
    echo "*  Parameters:                                                                                     *"
    echo "*                                                                                                  *"
    echo "*  fastboot.bat -start    : Starts the application                                                 *"
    echo "*  fastboot.bat -stats    : Get application state and queues list                                  *"
    echo "*  fastboot.bat -refresh  : Reload the application configuration from SAP and restart the queue    *"
    echo "*  fastboot.bat -version  : Get application version                                                *"
    echo "*  fastboot.bat -usrdump  : Generate the userdump on the log folder                                *"
    echo "*  fastboot.bat -heapdump : Generate the heapdump on the log folder                                *"
    echo "*  fastboot.bat -traceon [DEBUG,INFO,WARN,ERROR,FATAL,INFO,TRACE] : Update traces severity         *"
    echo "*  fastboot.bat -traceoff : Deactivate the traces                                                  *"
    echo "*  fastboot.bat -listmac  : List the machine mac address                                           *"
    echo "*  fastboot.bat -stop     : Stops application                                                      *"
    echo "****************************************************************************************************"
)

if "%1" == "-start" (
    echo "Starting application"
    java -Xms2048m -Xmx2048m -Dapp.dir=%APP_HOME% -classpath %FASTBOOT_CP% it.bamboolab.fastboot.core.Fastboot
)

if "%1" == "-listmac" (
    echo "Calling Fastboot JMX client"
	java -classpath %FASTBOOT_CP% it.bamboolab.fastboot.core.Client %*
)

if "%1" == "-version" (
    echo "Calling Fastboot JMX client"
	java -classpath %FASTBOOT_CP% it.bamboolab.fastboot.core.Client %*
)

if "%1" == "-newkey" (
    echo "Calling Fastboot JMX client"
	java -classpath %FASTBOOT_CP% it.bamboolab.fastboot.core.Client %*
)
