@echo off
chcp 65001 >nul
TITLE FileHasherZipper
setlocal enabledelayedexpansion

:: 循环
:LOOP_START
cls  :: 清屏使界面更清晰

echo -----------------------------------

echo 当前命令行代码页：UTF-8

echo -----------------------------------

:: 指定根目录
set ROOT=%~dp0

echo 正在检测java环境。。。

:: 重置目标路径变量
set "TARGET_PATH="

:: 指定 Java 路径
set JAVA_BIN="%ROOT%zulu21.44.17-ca-jre21.0.8-win_x64\bin\java.exe"

:: 检查 Java 是否存在
if not exist %JAVA_BIN% (
    echo [错误] 找不到 Java 可执行文件: %JAVA_BIN%
    pause
    exit /b 1
)

echo 当前java版本号：

:: 输出java版本号
%JAVA_BIN% -version

echo oooooooooo.  oooooo   oooo      ooooo                                .   

echo `888'   `Y8b  `888.   .8'       `888'                              .o8   

echo  888     888   `888. .8'         888          .ooooo.   .oooo.   .o888oo 

echo  888oooo888'    `888.8'          888         d88' `"Y8 `P  )88b    888   

echo  888    `88b     `888'           888         888        .oP"888    888   

echo  888    .88P      888            888       o 888   .o8 d8(  888    888 . 

echo o888bood8P'      o888o          o888ooooood8 `Y8bod8P' `Y888""8o   "888" 

echo 环境检测通过，可以进行文件压缩工作！

:: 禁用延迟变量扩展
setlocal disabledelayedexpansion

:: 用户输入路径
set /p TARGET_PATH=请输入需要压缩的文件或文件夹的绝对路径（可以将文件或文件夹拖入命令行快捷获取路径）: 

echo 正在执行压缩操作。。。

echo 请稍后

:: 执行 Java 程序
"%JAVA_BIN%" -Dfile.encoding=UTF-8 -jar FileHasherZipper.jar %TARGET_PATH%

:: 错误检查
if errorlevel 1 (
    echo [错误] 程序执行失败，请检查路径或程序逻辑。
    pause
) else (
    echo Tips：如果路径中有中文或其他特殊字符会导致7zip输出的日志中出现乱码路径，不影响使用

    echo 操作成功完成！
)

:: 用户选择逻辑
:RETRY
choice /C YN /M "是否继续压缩其他文件？(Y继续/N退出)" 

if %errorlevel% equ 1 goto LOOP_START  :: 选Y则跳回循环开始

if %errorlevel% equ 2 exit  :: 选N则退出

:: 错误处理
goto RETRY  :: 防止无效输入
