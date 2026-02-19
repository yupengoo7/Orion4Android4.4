#!/bin/bash

# 修复后的 Gradle Wrapper 启动脚本

# 设置 APP_HOME
cd "$(dirname "$0")"
APP_HOME=$(pwd)

# 设置 CLASSPATH
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# 确定 Java 命令
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# 执行 Gradle
exec "$JAVACMD" \
  -cp "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"