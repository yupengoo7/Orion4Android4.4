# LunaTV Android 4.4 项目文件清单

## 项目统计
- **总文件数**: 46 个
- **Java 源代码**: 14 个文件
- **布局文件**: 7 个文件
- **资源文件**: 14 个文件
- **配置文件**: 6 个文件

## 文件结构

### 构建配置 (5 文件)
```
LunaTV-Android44/
├── build.gradle                    # 项目级构建配置
├── settings.gradle                 # 项目设置
├── gradlew                         # Gradle wrapper (Unix)
├── gradlew.bat                     # Gradle wrapper (Windows)
└── gradle/wrapper/gradle-wrapper.properties  # Wrapper 配置
```

### 应用模块 (41 文件)

#### 构建配置
```
app/
├── build.gradle                    # 应用构建配置
├── proguard-rules.pro              # ProGuard 规则
└── src/main/
    └── AndroidManifest.xml         # 应用清单
```

#### Java 源代码 (14 文件)
```
java/com/lunatv/
├── LunaTVApp.java                  # 应用入口类
├── activities/                     # Activity 类 (6 文件)
│   ├── LoginActivity.java          # 登录配置页
│   ├── MainActivity.java           # 主页面
│   ├── DetailActivity.java         # 详情页
│   ├── PlayerActivity.java         # 播放器页
│   ├── SearchActivity.java         # 搜索页
│   └── SettingsActivity.java       # 设置页
├── adapters/                       # 适配器 (3 文件)
│   ├── CardPresenter.java          # 卡片展示器
│   ├── EpisodeAdapter.java         # 剧集列表适配器
│   └── SearchHistoryAdapter.java   # 搜索历史适配器
├── api/                            # API 层 (1 文件)
│   └── LunaTVApi.java              # LunaTV API 客户端
├── models/                         # 数据模型 (3 文件)
│   ├── Video.java                  # 视频模型
│   ├── Favorite.java               # 收藏模型
│   └── PlayRecord.java             # 播放记录模型
└── utils/                          # 工具类 (1 文件)
    └── Preferences.java            # 配置存储工具
```

#### 布局文件 (7 文件)
```
res/layout/
├── activity_login.xml              # 登录页面布局
├── activity_main.xml               # 主页面布局
├── activity_detail.xml             # 详情页布局
├── activity_player.xml             # 播放器页面布局
├── activity_search.xml             # 搜索页布局
├── activity_settings.xml           # 设置页布局
├── item_video_card.xml             # 视频卡片布局
├── item_episode.xml                # 剧集按钮布局
├── item_search_history.xml         # 搜索历史项布局
└── title_view.xml                  # 标题栏布局
```

#### Drawable 资源 (10 文件)
```
res/drawable/
├── ic_launcher.xml                 # 应用图标
├── banner.xml                      # 应用横幅
├── ic_back.xml                     # 返回图标
├── ic_play.xml                     # 播放图标
├── ic_pause.xml                    # 暂停图标
├── ic_arrow_right.xml              # 右箭头图标
├── edit_text_background.xml        # 输入框背景
├── button_background.xml           # 按钮背景
├── card_background.xml             # 卡片背景
├── progress_drawable.xml           # 进度条样式
├── progress_thumb.xml              # 进度条滑块
├── progress_horizontal.xml         # 水平进度条
└── gradient_overlay.xml            # 渐变遮罩
```

#### Values 资源 (4 文件)
```
res/values/
├── strings.xml                     # 字符串资源
├── colors.xml                      # 颜色资源
├── styles.xml                      # 样式资源
└── dimens.xml                      # 尺寸资源
```

#### Drawable 密度适配 (4 目录)
```
res/
├── drawable-hdpi/                  # 高密度图片
├── drawable-mdpi/                  # 中密度图片
├── drawable-xhdpi/                 # 超高密度图片
├── drawable-xxhdpi/                # 超超高密度图片
└── drawable-xxxhdpi/               # 超超超高密度图片
```

## 功能模块

### 1. 登录模块 ✅
- 服务器地址配置
- 用户名/密码登录
- Cookie 持久化存储
- TLS 1.2 兼容性处理

### 2. 主页模块 ✅
- Leanback 风格浏览界面
- 热门推荐
- 最近观看
- 我的收藏
- 方形 300x300px 卡片布局
- TV 遥控器导航支持

### 3. 详情模块 ✅
- 海报展示
- 视频信息（标题、简介、导演、演员）
- 选集列表
- 收藏/取消收藏
- 播放按钮

### 4. 播放器模块 ✅
- Android VideoView 播放
- M3U8/MP4 格式支持
- 进度条控制
- 快进/快退
- 片头片尾跳过
- 自动保存播放进度
- 遥控器按键控制

### 5. 搜索模块 ✅
- 关键词搜索
- 搜索历史保存
- 历史记录点击搜索
- 搜索历史清除

### 6. 设置模块 ✅
- 片头跳过开关
- 片尾跳过开关
- 片头跳过时间设置
- 片尾跳过时间设置
- 退出登录功能

### 7. 数据同步模块 ✅
- 收藏列表同步
- 播放记录同步
- 搜索历史本地存储

## 技术特点

### Android 4.4 兼容性
- 最低 SDK: API 19 (Android 4.4 KitKat)
- 目标 SDK: API 21 (Android 5.0)
- 使用 VideoView 替代 ExoPlayer 1.x
- OkHttp 3.12.13 (最后一个支持 API 9+)
- Retrofit 2.6.4 (最后一个支持 API 19+)
- Google Play Services 启用 TLS 1.2

### TV 适配
- Leanback 支持库
- TV 遥控器按键处理
- 焦点导航
- 卡片放大效果
- D-Pad 支持

## 构建说明

### 环境要求
- Android Studio 3.6+
- JDK 8
- Android SDK API 19-28

### 构建步骤
1. 使用 Android Studio 打开项目
2. 等待 Gradle 同步
3. Build -> Build APK
4. 或使用命令行: `./gradlew assembleDebug`

### 安装
```bash
# 通过 ADB
adb install app/build/outputs/apk/debug/app-debug.apk

# 或通过 U 盘复制到电视盒子安装
```

## 注意事项

1. **必须手动构建 APK** - 当前环境无法直接生成 APK 文件
2. **需要 Android Studio** - 建议使用 Android Studio 3.6 或更高版本
3. **首次构建需要时间** - Gradle 需要下载依赖库
4. **Android 4.4 限制** - 部分视频格式可能需要额外解码器支持

## 后续优化建议

1. 添加加载占位图
2. 优化图片加载缓存
3. 添加错误重试机制
4. 支持更多视频格式
5. 添加播放速度控制
6. 支持字幕选择