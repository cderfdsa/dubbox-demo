#!/bin/sh
#linux下启动java程序的通用脚本。即可以作为开机自启动service脚本被调用，
#也可以作为启动java程序的独立脚本来使用。

#
#警告!!!：该脚本stop部分使用系统kill命令来强制终止指定的java程序进程。
#在杀死进程前，未作任何条件检查。在某些情况下，如程序正在进行文件或数据库写操作，
#可能会造成数据丢失或数据不完整。如果必须要考虑到这类情况，则需要改写此脚本，
#增加在执行kill命令前的一系列检查。
#

###################################
# 以下这些注释设置可以被chkconfig命令读取
# chkconfig: - 99 50
# description: Java程序启动脚本
# processname: test
# config: 如果需要的话，可以配置
###################################
#
###################################
#环境变量及程序执行参数
#需要根据实际环境以及Java程序名称来修改这些参数
###################################

#执行程序启动所使用的系统用户，考虑到安全，推荐不使用root帐号
RUNNING_USER=game

DATE=$(date +%Y%m%d)

#当前目录
cur_dir=$(cd "$(dirname "$0")"; pwd)

#Java程序所在的目录（classes的上一级目录）
APP_HOME="$cur_dir"

echo $DATE $APP_HOME

# 应用名称，在maven打包时会被替换成pom中设置的值
APP_NAME=${appName}
#update

#java虚拟机启动参数
JAVA_OPTS="-XX:ErrorFile=./hs_err_pid.log -ms256m -mx256m -Xmn128m -Dspath=$cur_dir -XX:PermSize=128m -XX:MaxPermSize=128m"

#初始化psid变量（全局）
psid=0


###################################
#(函数)判断程序是否已启动
#
#说明：
#使用JDK自带的JPS命令及grep命令组合，准确查找pid
#jps 加 l 参数，表示显示java的完整包路径
#使用awk，分割出pid ($1部分)，及Java程序名称($2部分)
###################################
checkpid() {
   #javaps=`$JAVA_HOME/bin/jps -lv | grep spath=$cur_dir`
   javaps=`jps -lv | grep spath=$cur_dir`

   # -n 表示检测字符串长度是否为0，不为0返回 true
   if [ -n "$javaps" ]; then
      psid=`echo $javaps | awk '{print $1}'`
   else
      psid=0
   fi
}

###################################
#(函数)启动程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示程序已启动
#3. 如果程序没有被启动，则执行启动命令行
#4. 启动命令执行后，再次调用checkpid函数
#5. 如果步骤4的结果能够确认程序的pid,则打印[OK]，否则打印[Failed]
#注意：echo -n 表示打印字符后，不换行
#注意: "nohup 某命令 >/dev/null 2>&1 &" 的用法
###################################
start() {
   checkpid

   if [ $psid -ne 0 ]; then
      echo "================================"
      echo "warn: $APP_NAME already started! (pid=$psid)"
      echo "================================"
   else
      echo -n "Starting $APP_NAME ..."
      nohup java $JAVA_OPTS -jar $APP_NAME.jar > $APP_HOME/log/$DATE.log 2>&1 &
      #su - $RUNNING_USER -c "$JAVA_CMD"
      #$JAVA_CMD
      checkpid
      if [ $psid -ne 0 ]; then
         echo "(pid=$psid) [OK]"
      else
         echo "[Failed]"
      fi
   fi
}

###################################
#(函数)停止程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则开始执行停止，否则，提示程序未运行
#3. 使用kill -9 pid命令进行强制杀死进程
#4. 执行kill命令行紧接其后，马上查看上一句命令的返回值: $?
#5. 如果步骤4的结果$?等于0,则打印[OK]，否则打印[Failed]
#6. 为了防止java程序被启动多次，这里增加反复检查进程，反复杀死的处理（递归调用stop）。
#注意：echo -n 表示打印字符后，不换行
#注意: 在shell编程中，"$?" 表示上一句命令或者一个函数的返回值
###################################
stop() {
   checkpid

   if [ $psid -ne 0 ]; then
      echo -n "Stopping $APP_NAME ...(pid=$psid) "
      kill -9 $psid

      #su - $RUNNING_USER -c "kill -9 $psid"
      # $? 表示显示最后命令的退出状态。0表示没有错误，其他任何值表明有错误
      if [ $? -eq 0 ]; then
         echo "[OK]"
      else
         echo "[Failed]"
      fi

      checkpid
      if [ $psid -ne 0 ]; then
         stop
      fi
   else
      echo "================================"
      echo "warn: $APP_NAME is not running"
      echo "================================"
   fi
}

###################################
#(函数)检查程序运行状态
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示正在运行并表示出pid
#3. 否则，提示程序未运行
###################################
status() {
   checkpid

   if [ $psid -ne 0 ];  then
      echo "$APP_NAME is running! (pid=$psid)"
   else
      echo "$APP_NAME is not running"
   fi
}

###################################
#(函数)打印系统环境参数
###################################
info() {
   echo "System Information:"
   echo "****************************"
   echo `head -n 1 /etc/issue`
   echo `uname -a`
   echo
   echo `java -version`
   echo
   echo "APP_HOME=$APP_HOME"
   echo "APP_NAME=$APP_NAME"
   echo "****************************"
}

update() {
        java -classpath $CLASSPATH $APP_UPDATE
}
install() {
        java -classpath $CLASSPATH $APP_INSTALL
}
cleanWorld() {
        java -classpath $CLASSPATH $APP_CLEAN_WORLD
}
updateFile(){
      mv upgrade/game.sql  upgrade/game-$DATE.sql
}
###################################
#读取脚本的第一个参数($1)，进行判断
#参数取值范围：{start|stop|restart|status|info|update|install}
#如参数不在指定范围之内，则打印帮助信息
###################################
case "$1" in
   'start')
      start
      ;;
   'stop')
     stop
     ;;
   'restart')
     stop
     start
     ;;
   'status')
     status
     ;;
   'info')
     info
     ;;
   'update')
     update
     updateFile
     ;;
    'install')
     install
     ;;
    'updateFile')
     updateFile
     ;;
    'cleanWorld')
     cleanWorld
     ;;
  *)
     echo "Usage: $0 {start|stop|restart|status|info|update|updateFile|cleanWorld}"
     exit 1
esac
exit 0
