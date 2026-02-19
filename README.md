# LunaTV Android 4.4 电视客户端

基于原生 Android 开发的 LunaTV 电视客户端，支持 Android 4.4 (API 19) 及以上版本。

## 功能特性

- ✅ **服务器配置** - 支持自定义 LunaTV 服务器地址、用户名和密码
- ✅ **视频搜索** - 支持关键词搜索，搜索历史保存
- ✅ **视频播放** - 支持 M3U8/MP4 格式视频播放
- ✅ **选集播放** - 支持多集视频切换
- ✅ **收藏功能** - 收藏视频并同步到服务器
- ✅ **播放记录** - 自动保存播放进度，支持断点续播
- ✅ **片头片尾跳过** - 可配置片头片尾跳过时间
- ✅ **遥控器支持** - 完整支持电视遥控器操作

## 技术栈

- **开发语言**: Java 7
- **最低 SDK**: API 19 (Android 4.4)
- **目标 SDK**: API 21 (Android 5.0)
- **视频播放**: Android VideoView (原生 MediaPlayer)
- **网络请求**: OkHttp 3.12.13 + Retrofit 2.6.4
- **图片加载**: Picasso 2.71828
- **UI 框架**: Android Leanback (Android TV 支持库)

## 项目结构

```
LunaTV-Android44/
├── app/
│   ├── src/main/
│   │   ├── java/com/lunatv/
│   │   │   ├── LunaTVApp.java              # 应用入口
│   │   │   ├── activities/
│   │   │   │   ├── LoginActivity.java      # 登录配置页
│   │   │   │   ├── MainActivity.java       # 主页面
│   │   │   │   ├── DetailActivity.java     # 详情页
│   │   │   │   ├── PlayerActivity.java     # 播放器页
│   │   │   │   ├── SearchActivity.java     # 搜索页
│   │   │   │   └── SettingsActivity.java   # 设置页
│   │   │   ├── adapters/
│   │   │   │   ├── CardPresenter.java      # 卡片展示器
│   │   │   │   ├── EpisodeAdapter.java     # 剧集列表适配器
│   │   │   │   └── SearchHistoryAdapter.java # 搜索历史适配器
│   │   │   ├── api/
│   │   │   │   └── LunaTVApi.java          # API 客户端
│   │   │   ├── models/
│   │   │   │   ├── Video.java              # 视频模型
│   │   │   │   ├── Favorite.java           # 收藏模型
│   │   │   │   └── PlayRecord.java         # 播放记录模型
│   │   │   └── utils/
│   │   │       └── Preferences.java        # 配置存储
│   │   └── res/
│   │       ├── layout/                     # 布局文件
│   │       ├── drawable/                   # 图片资源
│   │       └── values/                     # 字符串、颜色、样式
│   └── build.gradle                        # 应用构建配置
├── build.gradle                            # 项目构建配置
├── settings.gradle
└── gradle/wrapper/gradle-wrapper.properties
```

## 构建步骤

### 环境要求

- Android Studio 3.6 或更高版本
- JDK 8
- Android SDK API 19-28

### 构建 APK

1. 打开 Android Studio
2. 选择 "Open an existing Android Studio project"
3. 选择 `LunaTV-Android44` 文件夹
4. 等待 Gradle 同步完成
5. 点击菜单栏 `Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`
6. 生成的 APK 位于 `app/build/outputs/apk/debug/app-debug.apk`

### 构建 Release APK

1. 点击菜单栏 `Build` -> `Generate Signed Bundle / APK...`
2. 选择 `APK`
3. 创建或选择签名密钥
4. 选择 `release` 构建类型
5. 生成的 APK 位于 `app/build/outputs/apk/release/app-release.apk`

### 命令行构建

```bash
# 进入项目目录
cd LunaTV-Android44

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease
```

## 安装到电视盒子

### 方法一：ADB 安装

```bash
# 连接电视盒子
adb connect <电视盒子IP地址>:5555

# 安装 APK
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 方法二：U盘安装

1. 将 APK 文件复制到 U 盘
2. 将 U 盘插入电视盒子
3. 使用文件管理器找到 APK 文件并安装

## 使用说明

### 首次启动

1. 打开应用后进入登录配置页
2. 输入 LunaTV 服务器地址（例如：`http://192.168.1.100:3000`）
3. 输入用户名和密码
4. 点击登录按钮

### 主页面

- **热门推荐** - 显示推荐视频
- **最近观看** - 显示播放历史
- **我的收藏** - 显示收藏的视频
- 使用遥控器方向键导航，确定键进入详情

### 详情页

- 显示视频详细信息（海报、简介、导演、演员等）
- 点击"播放"按钮开始播放
- 点击"收藏"按钮添加/取消收藏
- 选集区域选择要播放的集数

### 播放器

- **遥控器操作**:
  - 方向键左右：快进/快退 10 秒
  - 确定键：播放/暂停
  - 菜单键/上下键：显示/隐藏控制栏
  - 返回键：退出播放
- **控制栏**:
  - 拖动进度条跳转
  - 片头片尾跳过按钮
  - 显示当前播放时间和总时长

### 搜索

- 点击主页面的搜索按钮进入搜索页
- 输入关键词后按遥控器确定键搜索
- 搜索历史会自动保存，点击历史记录可快速搜索

### 设置

- 在主页面点击设置按钮进入设置页
- 可配置片头片尾跳过功能
- 可调整片头片尾跳过时间
- 可退出登录并清除所有数据

## 注意事项

### Android 4.4 兼容性

- 应用使用 Android VideoView 播放视频，支持 M3U8 和 MP4 格式
- 对于某些视频格式可能需要电视盒子支持相应的解码器
- 如果播放失败，请检查电视盒子是否安装了必要的解码器

### 网络要求

- 需要连接到 LunaTV 服务器
- 视频播放需要良好的网络连接
- 建议在 WiFi 环境下使用

### TLS 1.2 支持

- Android 4.4 默认不支持 TLS 1.2
- 应用会自动尝试启用 TLS 1.2（需要 Google Play Services）
- 如果服务器使用 HTTPS，建议使用 Android 5.0+ 设备获得最佳体验

## 故障排除

### 无法连接到服务器

- 检查服务器地址是否正确
- 确保电视盒子和服务器在同一网络
- 检查服务器是否正常运行

### 播放失败

- 检查视频链接是否有效
- 确保电视盒子支持视频格式
- 检查网络连接是否稳定

### 登录失败

- 检查用户名和密码是否正确
- 确保服务器配置了正确的 PASSWORD 环境变量
- 查看服务器日志获取详细信息

## 开发计划

- [ ] 添加豆瓣热门数据展示
- [ ] 支持分类浏览
- [ ] 添加播放速度调节
- [ ] 支持字幕选择
- [ ] 添加画中画模式
- [ ] 支持语音搜索

## 许可证

MIT License

## 致谢

- [LunaTV](https://github.com/MoonTechLab/LunaTV) - 后端服务
- [OrionTV](https://github.com/orion-lib/OrionTV) - 参考设计

## 联系方式

如有问题或建议，请提交 Issue 或 Pull Request。