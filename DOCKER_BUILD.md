# Docker 构建指南

使用 Docker 构建 LunaTV Android APK，无需在本地安装复杂的 Android 开发环境。

## 优势

- ✅ **无需配置环境** - Docker 包含所有必需的依赖
- ✅ **跨平台支持** - 支持 Windows、macOS、Linux
- ✅ **一致性** - 每次构建环境相同，避免"在我机器上能运行"问题
- ✅ **隔离性** - 不影响本地系统环境
- ✅ **可重复** - 随时可以重新构建相同的环境

## 前置要求

### 1. 安装 Docker

**Windows/Mac:**
- 下载 [Docker Desktop](https://www.docker.com/products/docker-desktop)
- 安装并启动 Docker Desktop

**Linux (Ubuntu/Debian):**
```bash
# 安装 Docker
sudo apt-get update
sudo apt-get install -y docker.io docker-compose

# 将用户加入 docker 组（无需 sudo 运行 docker）
sudo usermod -aG docker $USER

# 重新登录或运行
newgrp docker
```

**验证安装:**
```bash
docker --version
docker-compose --version
```

## 使用方法

### 方法一：使用构建脚本（推荐）

```bash
# 进入项目目录
cd LunaTV-Android44

# 使用 Docker 构建 Debug APK
./build.sh docker

# 构建完成后，APK 在 output/apk/debug/ 目录
```

### 方法二：使用 docker-compose 直接构建

```bash
# 进入项目目录
cd LunaTV-Android44

# 构建并运行（首次较慢，需要下载镜像和依赖）
docker-compose up builder

# 构建完成后，APK 在 output/apk/debug/ 目录
```

### 方法三：手动 Docker 命令

```bash
# 构建 Docker 镜像
docker build -t lunatv-android-builder .

# 运行构建容器
docker run --rm \
  -v "$(pwd):/app" \
  -v "$(pwd)/output:/app/app/build/outputs" \
  -v "gradle-cache:/root/.gradle" \
  lunatv-android-builder

# APK 输出到 output/apk/debug/ 目录
```

## 构建选项

### 构建 Debug APK（开发测试用）

```bash
# 使用脚本
./build.sh docker

# 或使用 docker-compose
docker-compose up builder
```

### 构建 Release APK（发布用）

```bash
# 使用 docker-compose
docker-compose up build-release

# APK 在 output/apk/release/ 目录
```

### 快速构建（使用 Gradle 缓存）

```bash
# 如果之前构建过，使用离线模式加快构建
./build.sh docker-fast

# 或使用 docker-compose
docker-compose up build-fast
```

### 清理构建

```bash
# 清理 Gradle 缓存和构建产物
./build.sh clean

# 或
./gradlew clean
```

## 目录说明

```
LunaTV-Android44/
├── Dockerfile              # Docker 镜像定义
├── docker-compose.yml      # Docker Compose 配置
├── build.sh               # 构建脚本
├── output/                # 构建输出目录（自动生成）
│   └── apk/
│       ├── debug/         # Debug APK
│       │   └── app-debug.apk
│       └── release/       # Release APK
│           └── app-release-unsigned.apk
└── ...                    # 项目源代码
```

## 首次构建说明

### 第一次构建会比较慢，因为需要：

1. **下载基础镜像** (~500MB)
   - Ubuntu 20.04 基础镜像
   - OpenJDK 8

2. **下载 Android SDK** (~1GB)
   - Command Line Tools
   - Android API 19, 21, 28
   - Build Tools 28.0.3, 29.0.3

3. **下载 Gradle 和依赖** (~200MB)
   - Gradle 5.6.4
   - 项目依赖库

### 后续构建会快很多，因为：

- Docker 镜像已缓存
- Gradle 依赖已下载
- 使用 docker-compose 会自动重用缓存

## 常见问题

### Q: Docker 构建失败，提示内存不足

**A:** 增加 Docker 内存限制

**Docker Desktop (Windows/Mac):**
1. 打开 Docker Desktop
2. Settings -> Resources -> Advanced
3. 增加 Memory 到 4GB 或更高
4. 点击 Apply & Restart

**Linux:**
```bash
# 创建或编辑 /etc/docker/daemon.json
sudo nano /etc/docker/daemon.json

# 添加以下内容
{
  "default-ulimits": {
    "nofile": {
      "Name": "nofile",
      "Hard": 64000,
      "Soft": 64000
    }
  }
}

# 重启 Docker
sudo systemctl restart docker
```

### Q: 构建时网络超时

**A:** 配置 Docker 使用国内镜像源

**Docker Desktop:**
Settings -> Docker Engine -> 编辑 JSON
```json
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ]
}
```

**Linux:**
```bash
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ]
}
EOF
sudo systemctl restart docker
```

### Q: 如何查看构建日志

**A:**
```bash
# 使用脚本构建时会自动显示日志
./build.sh docker

# 使用 docker-compose
docker-compose up builder

# 查看详细日志
docker-compose logs builder
```

### Q: 如何进入 Docker 容器调试

**A:**
```bash
# 启动交互式容器
docker run -it --rm \
  -v "$(pwd):/app" \
  lunatv-android-builder bash

# 在容器内手动构建
./gradlew assembleDebug --stacktrace
```

### Q: 构建成功但找不到 APK

**A:** 检查输出目录
```bash
# 列出所有输出文件
find output -name "*.apk" -type f

# 或
ls -la output/apk/debug/
```

## 性能优化

### 加速首次构建

1. **使用国内镜像源**（见上文的网络超时解决方案）

2. **预下载 Gradle 分发包**
   ```bash
   # 在项目目录放置 gradle-5.6.4-bin.zip
   # 修改 gradle/wrapper/gradle-wrapper.properties
   # 将 distributionUrl 改为本地路径
   distributionUrl=file:///app/gradle-5.6.4-bin.zip
   ```

3. **使用镜像加速 SDK 下载**
   - 在 Dockerfile 中添加代理配置
   - 或使用预先构建好的基础镜像

### 减小镜像体积

如果需要更小的镜像，可以使用多阶段构建：

```dockerfile
# 第一阶段：构建
FROM openjdk:8-jdk as builder
# ... 构建步骤 ...

# 第二阶段：仅复制 APK
FROM alpine:latest
COPY --from=builder /app/app/build/outputs/apk/debug/app-debug.apk /app-debug.apk
```

## 进阶用法

### 自定义构建参数

```bash
# 使用特定 Gradle 参数
docker run --rm \
  -v "$(pwd):/app" \
  -e GRADLE_OPTS="-Xmx4g -XX:MaxMetaspaceSize=1g" \
  lunatv-android-builder \
  ./gradlew assembleDebug --no-daemon --info
```

### 并行构建多个变体

```bash
# 同时构建 debug 和 release
./build.sh docker &
./build.sh docker-release &
wait
```

### CI/CD 集成

**GitHub Actions:**
```yaml
name: Build APK
on: [push]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build with Docker
        run: |
          docker-compose up builder
      - name: Upload APK
        uses: actions/upload-artifact@v2
        with:
          name: app-debug
          path: output/apk/debug/app-debug.apk
```

**GitLab CI:**
```yaml
build:
  image: docker:latest
  services:
    - docker:dind
  script:
    - docker-compose up builder
  artifacts:
    paths:
      - output/apk/debug/app-debug.apk
```

## 故障排除

如果构建失败，请检查：

1. ✅ Docker 是否正常运行
   ```bash
   docker info
   ```

2. ✅ 是否有足够磁盘空间（至少需要 5GB 空闲空间）
   ```bash
   df -h
   ```

3. ✅ 项目文件是否完整
   ```bash
   ls -la app/src/main/java/com/lunatv/
   ```

4. ✅ Docker 日志
   ```bash
   docker-compose logs
   ```

5. ✅ 尝试清理并重建
   ```bash
   docker-compose down
   docker system prune -f
   ./build.sh docker
   ```

## 更新和维护

### 更新 Android SDK

编辑 Dockerfile，修改 SDK 版本号：
```dockerfile
RUN sdkmanager \
    "platforms;android-30" \
    "build-tools;30.0.3"
```

然后重建镜像：
```bash
docker-compose build --no-cache
```

### 更新 Gradle

修改 `gradle/wrapper/gradle-wrapper.properties`：
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-7.0-bin.zip
```

## 获取帮助

如果遇到问题：

1. 查看详细构建日志
2. 检查 Docker 版本是否为最新
3. 确保项目代码完整
4. 尝试清理 Docker 缓存后重新构建

祝构建顺利！🚀