@echo off
chcp 65001 >nul
TITLE FileHasherZipper

echo -----------------------------------

echo 当前命令行代码页：UTF-8

echo -----------------------------------

:: 指定根目录
set ROOT=%~dp0

echo 正在检测java环境。。。

:: 指定 Java 路径
set JAVA_BIN="%ROOT%runtime\zulu21.44.17-ca-jre21.0.8-win_x64\bin\java.exe"

:: 检查 Java 是否存在
if not exist %JAVA_BIN% (
    echo [错误] 找不到 Java 可执行文件: %JAVA_BIN%
    pause
    exit /b 1
)

echo 当前java版本号：

:: 输出java版本号
%JAVA_BIN% -version

:: 指定 JavaFX 路径
set JAVAFX_LIB="%ROOT%runtime\javafx-sdk-21.0.8\lib"

echo oooooooooo.  oooooo   oooo      ooooo                                .   

echo `888'   `Y8b  `888.   .8'       `888'                              .o8   

echo  888     888   `888. .8'         888          .ooooo.   .oooo.   .o888oo 

echo  888oooo888'    `888.8'          888         d88' `"Y8 `P  )88b    888   

echo  888    `88b     `888'           888         888        .oP"888    888   

echo  888    .88P      888            888       o 888   .o8 d8(  888    888 . 

echo o888bood8P'      o888o          o888ooooood8 `Y8bod8P' `Y888""8o   "888" 

echo 环境检测通过，GUI窗口已启动，可以进行文件压缩工作！

:: 执行 Java 程序
"%JAVA_BIN%" --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -Dfile.encoding=UTF-8 -jar FileHasherZipper.jar

exit
