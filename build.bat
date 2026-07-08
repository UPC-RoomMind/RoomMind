@echo off
:: 改用系统默认GBK编码936，彻底解决中文乱码
chcp 936 >nul
title RoomMind 自动打包脚本

echo ================================================
echo         RoomMind 自动打包脚本
echo ================================================
echo.

set "PROJECT_ROOT=%~dp0"
set "OUTPUT_DIR=%PROJECT_ROOT%build_output"
set "BACKEND_DIR=%PROJECT_ROOT%backend"
:: 若你前端不在frontend，自行修改此处路径
set "FRONTEND_DIR=%PROJECT_ROOT%frontend"

echo [1/4] 创建输出目录...
if exist "%OUTPUT_DIR%" (
    rmdir /s /q "%OUTPUT_DIR%"
)
mkdir "%OUTPUT_DIR%"
echo       成功：输出目录已创建 %OUTPUT_DIR%
echo.

:: 先判断前端目录是否存在，不存在直接跳过前端打包
echo [2/4] 开始打包前端...
if not exist "%FRONTEND_DIR%" (
    echo       提示：未检测到frontend前端目录，跳过前端打包步骤
    echo.
    goto backend_build
)
cd /d "%FRONTEND_DIR%"
echo       正在安装依赖...
call npm install
if %errorlevel% neq 0 (
    echo       失败：前端依赖安装出错！
    pause
    exit /b 1
)
echo       成功：依赖安装完成

echo       正在构建生产版本...
call npm run build
if %errorlevel% neq 0 (
    echo       失败：前端构建打包出错！
    pause
    exit /b 1
)
echo       成功：前端构建完成

echo       复制dist静态文件到输出目录...
xcopy /e /i /y "%FRONTEND_DIR%\dist" "%OUTPUT_DIR%\dist"
echo       成功：前端dist文件复制完成
echo.

:backend_build
echo [3/4] 开始打包后端...
cd /d "%BACKEND_DIR%"
echo       执行Maven清理打包（跳过单元测试）
:: 兼容两种方式：有mvnw优先用，没有则调用系统mvn
if exist "mvnw.cmd" (
    call mvnw.cmd clean package -DskipTests
) else (
    call mvn clean package -DskipTests
)
if %errorlevel% neq 0 (
    echo       失败：后端Maven打包出错！
    pause
    exit /b 1
)
echo       成功：后端打包完成

echo       复制Jar包至输出目录...
for /f "delims=" %%f in ('dir /b /s "%BACKEND_DIR%\target\*.jar"') do (
    copy "%%f" "%OUTPUT_DIR%\"
    echo       已复制文件：%%~nxf
)
echo.

echo [4/4] 全部打包任务完成！
echo ================================================
echo 打包输出目录：%OUTPUT_DIR%
echo.
echo 目录包含内容：
echo   - *.jar        SpringBoot后端服务包
echo   - dist文件夹   前端静态资源（如有前端）
echo ================================================
echo.
echo 服务器部署步骤：
echo   1. 将build_output整个文件夹上传至Linux服务器
echo   2. 执行启动命令：java -jar xxx.jar
echo   3. 浏览器访问 http://服务器IP:8080
echo ================================================

pause