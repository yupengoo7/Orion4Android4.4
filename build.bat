@echo off
chcp 65001 >nul

:: LunaTV Android 构建脚本 (Windows)
:: 提供多种构建方式

setlocal EnableDelayedExpansion

:: 设置项目目录
set "PROJECT_DIR=%~dp0"
cd /d "%PROJECT_DIR%"

:: 显示帮助信息
echo.
echo ============================================
echo    LunaTV Android 构建脚本
echo ============================================
echo.

:: 检查参数
if "%~1"=="" goto :menu
if "%~1"=="help" goto :help
if "%~1"=="docker" goto :docker_build
if "%~1"=="clean" goto :clean_build
if "%~1"=="setup" goto :check_deps

echo 未知选项: %~1
goto :help

:menu
echo 请选择操作:
echo.
echo  [1] Docker 构建 (推荐)^<- 最简单，无需配置环境
echo  [2] 清理构建缓存
echo  [3] 检查环境依赖
echo  [4] 显示帮助
echo  [0] 退出
echo.
set /p choice="请输入选项 (0-4): "

if "%choice%"=="1" goto :docker_build
if "%choice%"=="2" goto :clean_build
if "%choice%"=="3" goto :check_deps
if "%choice%"=="4" goto :help
if "%choice%"=="0" goto :end

echo 无效的选项！
goto :menu

:docker_build
echo.
echo [INFO] 正在使用 Docker 构建...
echo.

:: 检查 Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker 未安装！
    echo.
    echo 请安装 Docker Desktop:
    echo https://www.docker.com/products/docker-desktop
    echo.
    pause
    goto :end
)

:: 检查 Docker Compose
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Compose 未安装！
    echo.
    echo Docker Desktop for Windows 已包含 Docker Compose
    echo.
    pause
    goto :end
)

:: 创建输出目录
if not exist output mkdir output

echo [INFO] 开始构建 Docker 镜像...
docker-compose build

if errorlevel 1 (
    echo [ERROR] Docker 镜像构建失败！
    pause
    goto :end
)

echo.
echo [INFO] 开始构建 APK...
docker-compose up builder

if errorlevel 1 (
    echo [ERROR] APK 构建失败！
    pause
    goto :end
)

echo.
echo ============================================
echo [SUCCESS] 构建成功！
echo ============================================
echo.
echo APK 文件位置:
echo   output\apk\debug\app-debug.apk
echo.

if exist "output\apk\debug\app-debug.apk" (
    echo 文件大小:
    dir "output\apk\debug\app-debug.apk" | findstr "app-debug.apk"
)

echo.
echo 安装到电视盒子:
echo   adb install output\apk\debug\app-debug.apk
echo.

:: 清理容器
docker-compose down >nul 2>&1

pause
goto :end

:clean_build
echo.
echo [INFO] 清理构建缓存...

if exist "app\build" (
    rmdir /s /q "app\build"
    echo [OK] 已删除 build 目录
)

if exist "output" (
    rmdir /s /q "output"
    echo [OK] 已删除 output 目录
)

echo [OK] 清理完成！
pause
goto :end

:check_deps
echo.
echo [INFO] 检查环境依赖...
echo.

:: 检查 Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [X] Java 未安装
    echo     请下载: https://adoptopenjdk.net/
) else (
    echo [OK] Java 已安装
)

:: 检查 Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo [X] Docker 未安装
    echo     请下载: https://www.docker.com/products/docker-desktop
) else (
    for /f "tokens=*" %%a in ('docker --version') do echo [OK] Docker: %%a
)

:: 检查 Docker Compose
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [X] Docker Compose 未安装
) else (
    for /f "tokens=*" %%a in ('docker-compose --version') do echo [OK] %%a
)

echo.
echo [INFO] 环境检查完成！
if errorlevel 1 (
    echo.
    echo 提示: 使用 Docker 构建无需安装 Java
)
pause
goto :end

:help
echo.
echo 用法: build.bat [选项]
echo.
echo 选项:
echo   docker    使用 Docker 构建 APK
echo   clean     清理构建缓存
echo   setup     检查环境依赖
echo   help      显示帮助信息
echo.
echo 示例:
echo   build.bat docker     构建 APK
echo   build.bat clean      清理缓存
echo.
echo 或者直接运行 build.bat 进入交互菜单
echo.
pause
goto :end

:end
endlocal
exit /b 0