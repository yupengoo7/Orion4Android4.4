# Android 构建环境 Docker 镜像
# 基于 Ubuntu 20.04，包含 OpenJDK 8 和 Android SDK

FROM ubuntu:20.04

# 设置环境变量
ENV DEBIAN_FRONTEND=noninteractive
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_SDK_ROOT=$ANDROID_HOME
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# 安装基础依赖
RUN apt-get update && apt-get install -y \
    openjdk-8-jdk \
    wget \
    unzip \
    git \
    curl \
    locales \
    && rm -rf /var/lib/apt/lists/*

# 设置中文 locale
RUN locale-gen zh_CN.UTF-8
ENV LANG=zh_CN.UTF-8
ENV LANGUAGE=zh_CN:zh
ENV LC_ALL=zh_CN.UTF-8

# 创建 Android SDK 目录
RUN mkdir -p $ANDROID_HOME

# 下载并安装 Android SDK Command Line Tools
RUN cd $ANDROID_HOME && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip && \
    unzip -q commandlinetools-linux-8512546_latest.zip && \
    rm commandlinetools-linux-8512546_latest.zip && \
    mkdir -p cmdline-tools/latest && \
    mv cmdline-tools/bin cmdline-tools/latest/ && \
    mv cmdline-tools/lib cmdline-tools/latest/

# 接受 Android SDK 许可协议
RUN yes | sdkmanager --licenses

# 安装必要的 Android SDK 组件
RUN sdkmanager \
    "platforms;android-28" \
    "platforms;android-21" \
    "platforms;android-19" \
    "build-tools;28.0.3" \
    "build-tools;29.0.3" \
    "extras;android;m2repository" \
    "extras;google;m2repository"

# 安装 Gradle 5.6.4
RUN wget -q https://services.gradle.org/distributions/gradle-5.6.4-bin.zip && \
    unzip -q gradle-5.6.4-bin.zip -d /opt && \
    rm gradle-5.6.4-bin.zip

ENV GRADLE_HOME=/opt/gradle-5.6.4
ENV PATH=$PATH:$GRADLE_HOME/bin

# 设置工作目录
WORKDIR /app

# 复制项目文件
COPY . /app

# 默认命令：构建 Debug APK
CMD ["gradle", "assembleDebug", "--no-daemon", "--stacktrace"]

# 构建说明
# docker build -t lunatv-android-builder .
# docker run -v $(pwd)/app/build/outputs:/outputs lunatv-android-builder
# 或者使用 docker-compose up