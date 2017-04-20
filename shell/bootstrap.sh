#!/usr/bin/env sh

#######################################################################################
# orctom@gmail.com
#
# <app_name>
#   ├── app
#   │   ├── <app_name>-<version>.jar
#   │   └── lib
#   │       ├── guava-19.0.jar
#   │       ├── slf4j-api-1.7.21.jar
#   │       └── ...
#   ├── conf
#   │   └── env-xxx.properties (optional, environment related config files)
#   └── logs
#       └── biz.log
#######################################################################################


#######################################################################################
###############          Change Following Variables as Needed           ###############
#######################################################################################
# Java Home
JAVA_HOME=/usr/lib/jvm/java-8-oracle

####### Select a Collector #######
# Default Collector
JAVA_OPTS="-server -Xms1G -Xmx1G"

# CMS Collector
#JAVA_OPTS="-server -Xms6G -Xmx6G -Xmn2G -XX:SurvivorRatio=2 -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark -XX:CMSInitiatingOccupancyFraction=65 -XX:+ParallelRefProcEnabled"

# G1 Collector
#JAVA_OPTS="-server -Xms6G -Xmx6G -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled"

#######################################################################################
###    Do NOT change Following Variables, unless you know what you are changing     ###
#######################################################################################

# Base Directory Path 
pushd `dirname $0` > /dev/null
BASEDIR=`pwd`
popd > /dev/null

# Script that will be executed on OutOfMemory error, no whitespaces or quotes allowed here
OOM_SCRIPT=${BASEDIR}/oom.sh

# GC logging, OOM action...
JAVA_OPTS="$JAVA_OPTS -verbose:gc -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -XX:+PrintTenuringDistribution -Xloggc:../logs/gc-`date +%F_%H-%M-%S`.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M -Dsun.net.inetaddr.ttl=30 -Djava.security.egd=file:/dev/./urandom -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=../logs/dump-`date +%F_%H-%M-%S`.hprof -XX:OnOutOfMemoryError=${OOM_SCRIPT}"

###############  values  ###############
work_dir=${BASEDIR}/app
pid_file=${BASEDIR}/pid
app_name=${BASEDIR}

###############  colors  ###############
if [[ ! "$TERM" = "dumb" ]]; then
  fg_red=$(tput setaf 1)     ; fg_green=$(tput setaf 2)  ; fg_blue=$(tput setaf 4)
  fg_magenta=$(tput setaf 5) ; fg_yellow=$(tput setaf 3) ; fg_cyan=$(tput setaf 6)
  fg_white=$(tput setaf 7)   ; fg_black=$(tput setaf 0)
  bg_red=$(tput setab 1)     ; bg_green=$(tput setab 2)  ; bg_blue=$(tput setab 4)
  bg_magenta=$(tput setab 5) ; bg_yellow=$(tput setab 3) ; bg_cyan=$(tput setab 6)
  bg_white=$(tput setab 7)   ; bg_black=$(tput setab 0)
  reset_color=$(tput sgr 0)
fi

###############  functions  ###############
start() {
  echo "starting..."
  if is_running; then
    echo "${app_name} is ${fg_blue}already running.${reset_color}"
    exit 0
  fi
  echo "starting jar in ${work_dir}..."
  cd ${work_dir}
  nohup ${JAVA_HOME}/bin/java ${JAVA_OPTS} -jar *.jar >/dev/null &
  echo $! > ${pid_file}
  sleep 1
  status
}

stop() {
  echo "stopping..."
  if is_running; then
    kill `cat ${pid_file}`
    sleep 2
    if is_running; then
      echo "Failed to stop, ${app_name} is still running"
    else
      echo "Successfully stopped ${app_name}"
      rm -f ${pid_file}
    fi
  else
    echo "${app_name} ${fg_blue}was NOT running.${reset_color}"
  fi
}

force_stop() {
  echo "force stopping..."
  if is_running; then
    kill -9 `cat ${pid_file}`
    sleep 2
    if is_running; then
      echo "Failed to stop, ${app_name} is still running"
    else
      echo "Successfully stopped ${app_name}"
      rm -f ${pid_file}
    fi
  else
    echo "${app_name} ${fg_blue}was NOT running.${reset_color}"
  fi
}

status() {
  if is_running; then
    echo "${app_name} is ${fg_blue}running${reset_color}, pid: ${fg_blue}`cat ${pid_file}`${reset_color}"
  else
    echo "${app_name} is ${fg_blue}stopped.${reset_color}"
  fi
}

is_running() {
  if [ ! -e "${pid_file}" ]; then
    echo "${fg_cyan}  pid file NOT exist: '${pid_file}' ${reset_color}"
    return 1
  fi
  pid=`cat ${pid_file}`
  if [ ! -e "/proc/${pid}" ]; then
    echo "${fg_cyan}  process directory NOT exist: '/proc/${pid}' ${reset_color}"
    return 1
  fi
  if [ "java" != "`cat /proc/${pid}/comm`" ]; then
    echo "${fg_cyan}  '${pid}' is not a java process. but a '`cat /proc/${pid}/comm`' ${reset_color}"
    return 1
  fi

  return 0
}

redeploy_app() {
  cd ${BASEDIR}
  if [ -e app ]; then
    today=`date +%Y%m%d-%H%M%S`
    mv app app-${today}
    echo "Backup app to app-${today}"
  fi
  echo "Unzipping *.zip to app"
  unzip *.zip -d app
}

verify() {
  if [ -z "$JAVA_HOME" ]; then
    echo "JAVA_HOME is not set."
    exit 1
  fi
  if [ ! -e "$JAVA_HOME/bin/java" ]; then
    echo "$JAVA_HOME is not a valid JDK/JRE."
    exit 1
  fi
}

###############  main  ###############
verify

case $1 in
start)
  start
  ;;
stop)
  stop
  ;;
kill)
  force_stop
  ;;
status)
  status
  ;;
restart)
  stop
  sleep 1
  start
  ;;
redeploy)
  stop
  sleep 1
  redeploy_app
  sleep 1
  start
  ;;
*)
  echo "./bootstrap.sh (start|stop|kill|restart|redeploy|status)"
esac
