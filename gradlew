#!/bin/bash

# Gradle Wrapper 启动脚本

# 获取脚本所在目录（不使用 cd，避免改变工作目录）
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 设置 CLASSPATH
CLASSPATH="$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar"

# 检查 jar 文件是否存在
if [ ! -f "$CLASSPATH" ]; then
    echo "Error: Could not find $CLASSPATH"
    echo "Current directory: $(pwd)"
    echo "Script directory: $SCRIPT_DIR"
    ls -la "$SCRIPT_DIR/gradle/wrapper/" 2>/dev/null || echo "Wrapper directory not found"
    exit 1
fi

# 确定 Java 命令
if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# 执行 Gradle
exec "$JAVACMD" -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"