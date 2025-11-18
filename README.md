# 声景校园 (Sound Campus)

一款专为盲人大学生设计的无障碍安卓手机应用，提供校园导航和文字识别功能。

## 功能特性

### 1. 校园导航系统
- 离线地图支持，预存校园主要建筑位置信息
- 语音引导的路径规划和步行导航
- 关键地点标记（教学楼、宿舍、食堂、图书馆、实验室等）
- 实时位置追踪和距离计算
- 转向提示和到达通知

### 2. 文字识别朗读系统
- 实时摄像头文字识别
- 自动语音朗读识别到的文字
- 支持中文识别
- 重复朗读功能
- 支持教室门牌、公告栏、书本等文字识别

### 3. 无障碍交互界面
- 完全适配屏幕阅读器的UI设计
- 大按钮、高对比度配色
- 语音反馈系统
- TTS（Text-to-Speech）集成

## 技术栈

- **平台**: Android (最低版本 API 24, Android 7.0)
- **开发语言**: Java
- **OCR引擎**: Google ML Kit Text Recognition (支持离线)
- **相机**: AndroidX Camera
- **位置服务**: Google Location Services
- **语音合成**: Android TTS Engine
- **数据存储**: SQLite + JSON

## 权限要求

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

## 项目结构

```
app/src/main/java/com/soundcampus/
├── MainActivity.java                 # 主界面
├── navigation/                       # 导航模块
│   ├── NavigationActivity.java      # 导航界面
│   ├── MapManager.java              # 地图管理
│   ├── LocationTracker.java         # 位置追踪
│   └── RouteCalculator.java         # 路线计算
├── ocr/                             # 文字识别模块
│   ├── OcrActivity.java             # 识别界面
│   └── TextRecognizer.java          # 文字识别引擎
├── data/                            # 数据模型
│   ├── CampusLocation.java          # 校园位置
│   ├── Route.java                   # 路线
│   ├── NavigationInstruction.java   # 导航指令
│   └── DatabaseHelper.java          # 数据库助手
└── utils/                           # 工具类
    ├── AccessibilityHelper.java     # 无障碍助手
    ├── PermissionManager.java       # 权限管理
    └── LocationHelper.java          # 位置计算
```

## 数据存储

### SQLite 数据库
- **locations 表**: 存储校园位置信息
  - id, name, latitude, longitude, description, category
- **routes 表**: 存储路线信息
  - start_id, end_id, distance

### 本地文件
- **campus_map.json**: 校园地图详细数据

## 构建项目

1. 克隆项目到本地
2. 使用 Android Studio 打开项目
3. 等待 Gradle 同步完成
4. 连接 Android 设备或启动模拟器
5. 点击 Run 按钮构建并安装应用

```bash
./gradlew assembleDebug
```

## 使用说明

### 导航模式
1. 从主界面点击"校园导航"按钮
2. 从下拉列表选择目的地
3. 点击"开始导航"
4. 应用将提供语音导航指引
5. 到达目的地后会播放确认提示

### 文字识别模式
1. 从主界面点击"文字识别"按钮
2. 将摄像头对准需要识别的文字
3. 点击"拍摄文字"按钮
4. 应用会自动识别并朗读文字
5. 可点击"重复上一次"重新朗读

## 无障碍特性

- 所有UI元素都设置了 `contentDescription`
- 支持 TalkBack 屏幕阅读器
- 高对比度配色方案（黑底白字/黄色强调）
- 大按钮设计（120dp高度）
- 语音反馈系统
- 简化的界面布局

## 开发优先级

- ✅ Phase 1: 基础导航 + 文字识别核心功能
- ⏳ Phase 2: 优化无障碍交互和语音反馈
- ⏳ Phase 3: 添加个性化设置和高级功能

## 特殊考虑

- **电量优化**: 智能管理后台服务
- **隐私保护**: 所有数据本地处理，不上传云端
- **容错机制**: GPS信号弱时的惯性导航补偿
- **离线运行**: 完全无需网络连接

## 测试建议

- 不同光照条件下的文字识别测试
- 多种校园地形下的导航精度测试
- 盲人用户参与的真实环境测试
- 无障碍交互的兼容性测试（TalkBack）

## 依赖库

- AndroidX AppCompat
- Google Material Design
- Google ML Kit Text Recognition
- AndroidX Camera
- Google Play Services Location
- Gson

## 许可证

Copyright 2024 Sound Campus Project

## 联系方式

如有问题或建议，请提交 Issue 或 Pull Request。
