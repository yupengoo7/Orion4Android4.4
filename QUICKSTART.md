# 快速开始

## 使用 Docker 构建 APK（最简单）

### 1. 安装 Docker

- **Windows/Mac**: 下载 [Docker Desktop](https://www.docker.com/products/docker-desktop)
- **Linux**: `sudo apt-get install docker.io docker-compose`

### 2. 一键构建

```bash
# 进入项目目录
cd LunaTV-Android44

# 使用脚本构建（最简单）
./build.sh docker

# 或者使用 docker-compose
docker-compose up builder
```

### 3. 获取 APK

构建完成后，APK 文件在：
```
output/apk/debug/app-debug.apk
```

### 4. 安装到电视盒子

```bash
adb install output/apk/debug/app-debug.apk
```

---

## 项目结构

```
LunaTV-Android44/
├── app/src/main/           # 源代码
├── Dockerfile              # Docker 构建配置
├── docker-compose.yml      # Docker Compose 配置
├── build.sh               # 构建脚本
└── output/                # 构建输出（自动生成）
    └── apk/debug/         # APK 文件
```

## 功能特性

- ✅ 连接 LunaTV 服务器
- ✅ 视频搜索和播放
- ✅ 收藏和播放记录同步
- ✅ 片头片尾跳过
- ✅ 电视遥控器支持
- ✅ Android 4.4 兼容

## 文档

- `README.md` - 项目详细介绍
- `DOCKER_BUILD.md` - Docker 构建完整指南
- `PROJECT_FILES.md` - 项目文件清单

## 遇到问题？

查看 `DOCKER_BUILD.md` 中的**常见问题**章节。