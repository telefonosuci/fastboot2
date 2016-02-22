#!/bin/sh
SCRIPT_COMMAND=$0
WORKING_DIR=$PWD
BIN_DIR="$( cd "$( dirname "$0" )" && pwd )"
APP_DIR="$(dirname "$BIN_DIR")"
CLASSPATH=".:$APP_DIR/lib/*:$APP_DIR/lib-ext/*:$APP_DIR/cfg"

cd "$APP_DIR"


if [ "$#" -ne 1 ]; then

  echo "****************************************************************************************************"
  echo "*  fastboot Usage                                                                                  *"
  echo "*                                                                                                  *"
  echo "*  Parameters:                                                                                     *"
  echo "*                                                                                                  *"
  echo "*  ./fastboot.sh -start     : Starts the application                                               *"
  echo "*  ./fastboot.sh -stats     : Get application state and queues list                                *"
  echo "*  ./fastboot.sh -refresh   : Reload the application configuration from SAP and restart the queue  *"
  echo "*  ./fastboot.sh -version   : Get application version                                              *"
  echo "*  ./fastboot.sh -usrdump   : Generate the userdump on the log folder                              *"
  echo "*  ./fastboot.sh -heapdump  : Generate the heapdump on the log folder                              *"
  echo "*  ./fastboot.sh -traceon [DEBUG,INFO,WARN,ERROR,FATAL,INFO,TRACE] : Update traces severity        *"
  echo "*  ./fastboot.sh -traceoff  : Deactivate the traces                                                *"
  echo "*  ./fastboot.sh -listmac   : List machine mac address                                             *"
  echo "*  ./fastboot.sh -stop      : Stops application                                                    *"
  echo "****************************************************************************************************"

elif [ "$1" = "-start" ]; then
  echo "Starting application"
  java -Xms2048m -Xmx2048m -Dapp.dir="$APP_DIR" -cp "$CLASSPATH" com.bamboolab.fastboot.core.fastboot
elif [ "$1" = "-stats" ] || [ "$1" = "-traceon" ] || [ "$1" = "-traceof" ] || [ "$1" = "-stop" ] || [ "$1" = "-usrdump" ] || [ "$1" = "-heapdump" ] || [ "$1" = "-refresh" ]; then
  echo "Calling fastboot service"
  java -Dapp.dir="$APP_DIR" -cp "$CLASSPATH" com.bamboolab.fastboot.rmi.Client "$@"
elif  [ "$1" = "-version" ] || [ "$1" = "-listmac" ]; then
  echo "Calling fastboot utils"
  java -Dapp.dir="$APP_DIR" -cp "$CLASSPATH" com.bamboolab.fastboot.core.Client "$@"
fi

