#!/bin/bash

# LunaTV Android 构建脚本
# 提供多种构建方式

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 项目目录
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

# 显示帮助信息
show_help() {
    echo -e "${BLUE}LunaTV Android 构建脚本${NC}"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  debug       构建 Debug APK (默认)"
    echo "  release     构建 Release APK"
    echo "  clean       清理构建缓存"
    echo "  rebuild     清理并重新构建"
    echo "  docker      使用 Docker 构建"
    echo "  setup       检查并安装依赖"
    echo "  help        显示帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 debug           # 构建 Debug APK"
    echo "  $0 release         # 构建 Release APK"
    echo "  $0 docker          # 使用 Docker 构建"
}

# 检查依赖
check_dependencies() {
    echo -e "${BLUE}检查依赖...${NC}"
    
    # 检查 Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
        echo -e "${GREEN}✓ Java 已安装: $JAVA_VERSION${NC}"
    else
        echo -e "${RED}✗ Java 未安装${NC}"
        echo "请安装 Java 8: https://adoptopenjdk.net/"
        exit 1
    fi
    
    # 检查 Android SDK
    if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
        echo -e "${YELLOW}⚠ Android SDK 环境变量未设置${NC}"
        echo "建议设置 ANDROID_HOME 环境变量"
    else
        echo -e "${GREEN}✓ Android SDK 已配置${NC}"
    fi
    
    # 检查 Docker
    if command -v docker &> /dev/null; then
        echo -e "${GREEN}✓ Docker 已安装${NC}"
        HAS_DOCKER=true
    else
        echo -e "${YELLOW}⚠ Docker 未安装${NC}"
        HAS_DOCKER=false
    fi
}

# 构建 Debug APK
build_debug() {
    echo -e "${BLUE}开始构建 Debug APK...${NC}"
    ./gradlew assembleDebug --no-daemon
    
    if [ $? -eq 0 ]; then
        APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
        if [ -f "$APK_PATH" ]; then
            echo -e "${GREEN}✓ 构建成功!${NC}"
            echo -e "${GREEN}APK 位置: $PROJECT_DIR/$APK_PATH${NC}"
            ls -lh "$APK_PATH"
        fi
    else
        echo -e "${RED}✗ 构建失败${NC}"
        exit 1
    fi
}

# 构建 Release APK
build_release() {
    echo -e "${BLUE}开始构建 Release APK...${NC}"
    echo -e "${YELLOW}注意: Release 构建需要签名密钥${NC}"
    
    ./gradlew assembleRelease --no-daemon
    
    if [ $? -eq 0 ]; then
        APK_PATH="app/build/outputs/apk/release/app-release-unsigned.apk"
        if [ -f "$APK_PATH" ]; then
            echo -e "${GREEN}✓ 构建成功!${NC}"
            echo -e "${YELLOW}注意: 这是一个未签名的 APK，需要签名后才能安装${NC}"
            echo -e "${GREEN}APK 位置: $PROJECT_DIR/$APK_PATH${NC}"
            ls -lh "$APK_PATH"
        fi
    else
        echo -e "${RED}✗ 构建失败${NC}"
        exit 1
    fi
}

# 清理构建缓存
clean_build() {
    echo -e "${BLUE}清理构建缓存...${NC}"
    ./gradlew clean --no-daemon
    echo -e "${GREEN}✓ 清理完成${NC}"
}

# 使用 Docker 构建
build_with_docker() {
    echo -e "${BLUE}使用 Docker 构建...${NC}"
    
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}✗ Docker 未安装${NC}"
        echo "请先安装 Docker: https://docs.docker.com/get-docker/"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        echo -e "${RED}✗ Docker Compose 未安装${NC}"
        echo "请先安装 Docker Compose: https://docs.docker.com/compose/install/"
        exit 1
    fi
    
    # 创建输出目录
    mkdir -p output
    
    echo -e "${BLUE}构建 Docker 镜像...${NC}"
    docker-compose build
    
    echo -e "${BLUE}运行构建...${NC}"
    docker-compose up builder
    
    if [ $? -eq 0 ]; then
        APK_PATH="output/apk/debug/app-debug.apk"
        if [ -f "$APK_PATH" ]; then
            echo -e "${GREEN}✓ Docker 构建成功!${NC}"
            echo -e "${GREEN}APK 位置: $PROJECT_DIR/$APK_PATH${NC}"
            ls -lh "$APK_PATH"
        fi
    else
        echo -e "${RED}✗ Docker 构建失败${NC}"
        exit 1
    fi
    
    # 清理容器
    docker-compose down
}

# 快速 Docker 构建（使用缓存）
build_with_docker_fast() {
    echo -e "${BLUE}使用 Docker 快速构建（使用离线模式）...${NC}"
    
    mkdir -p output
    docker-compose up build-fast
    
    if [ $? -eq 0 ]; then
        APK_PATH="output/apk/debug/app-debug.apk"
        if [ -f "$APK_PATH" ]; then
            echo -e "${GREEN}✓ 快速构建成功!${NC}"
            echo -e "${GREEN}APK 位置: $PROJECT_DIR/$APK_PATH${NC}"
        fi
    fi
    
    docker-compose down
}

# 主函数
main() {
    case "${1:-debug}" in
        debug)
            check_dependencies
            build_debug
            ;;
        release)
            check_dependencies
            build_release
            ;;
        clean)
            clean_build
            ;;
        rebuild)
            clean_build
            build_debug
            ;;
        docker)
            build_with_docker
            ;;
        docker-fast)
            build_with_docker_fast
            ;;
        setup)
            check_dependencies
            echo -e "${GREEN}✓ 环境检查完成${NC}"
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            echo -e "${RED}未知选项: $1${NC}"
            show_help
            exit 1
            ;;
    esac
}

# 运行主函数
main "$@"