#!/bin/sh
SCRIPT_COMMAND=$0
WORKING_DIR=$PWD
BIN_DIR="$( cd "$( dirname "$0" )" && pwd )"
APP_DIR="$(dirname "$BIN_DIR")"
CLASSPATH=".:$APP_DIR/lib/*:$APP_DIR/lib-ext/*:$APP_DIR/cfg"

cd "$APP_DIR"


if [ "$#" -ne 1 ]; then

  echo "****************************************************************************************************"
  echo "*  Fastrack Usage                                                                                  *"
  echo "*                                                                                                  *"
  echo "*  Parameters:                                                                                     *"
  echo "*                                                                                                  *"
  echo "*  ./fastrack.sh -start     : Starts the application                                               *"
  echo "*  ./fastrack.sh -stats     : Get application state and queues list                                *"
  echo "*  ./fastrack.sh -refresh   : Reload the application configuration from SAP and restart the queue  *"
  echo "*  ./fastrack.sh -version   : Get application version                                              *"
  echo "*  ./fastrack.sh -usrdump   : Generate the userdump on the log folder                              *"
  echo "*  ./fastrack.sh -heapdump  : Generate the heapdump on the log folder                              *"
  echo "*  ./fastrack.sh -traceon [DEBUG,INFO,WARN,ERROR,FATAL,INFO,TRACE] : Update traces severity        *"
  echo "*  ./fastrack.sh -traceoff  : Deactivate the traces                                                *"
  echo "*  ./fastrack.sh -listmac   : List machine mac address                                             *"
  echo "*  ./fastrack.sh -stop      : Stops application                                                    *"
  echo "****************************************************************************************************"

elif [ "$1" = "-start" ]; then
  echo "Starting application"
  java -Xms2048m -Xmx2048m -Dapp.dir="$APP_DIR" -cp "$CLASSPATH" com.primeur.fastrack.core.Fastrack
elif [ "$1" = "-stats" ] || [ "$1" = "-traceon" ] || [ "$1" = "-traceof" ] || [ "$1" = "-stop" ] || [ "$1" = "-usrdump" ] || [ "$1" = "-heapdump" ] || [ "$1" = "-refresh" ]; then
  echo "Calling Fastrack service"
  java -Dapp.dir="$APP_DIR" -cp "$CLASSPATH" com.primeur.fastrack.rmi.Client "$@"
elif  [ "$1" = "-version" ] || [ "$1" = "-listmac" ]; then
  echo "Calling Fastrack utils"
  java -Dapp.dir="$APP_DIR" -cp "$CLASSPATH" com.primeur.fastrack.core.Client "$@"
fi

