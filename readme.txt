方案一：命令行执行
> mvn clean package
-------------打包完成运行

当出现【错误: 缺少 JavaFX 运行时组件, 需要使用该组件来运行此应用程序】再执行以下方式(注：javafx-sdk是自己去网上下载：https://gluonhq.com/products/javafx/)
> set JAVAFX_PATH="D:\myhome\java\javafx-sdk-11.0.2\lib"
> java -Dfile.encoding=GBK --module-path %JAVAFX_PATH% --add-modules javafx.controls,javafx.fxml --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED  -jar D:\work\IdeaProjects\JavaFX\untitled\target\untitled-1.0-SNAPSHOT-shade.jar
-------------成功打开

方案二：界面化操作
直接点击运行AppLauncher.main方法看看能不能正常运行
然后点击Maven Projects中的Lifecycle:clean和Lifecycle:package

方案三：javafx:run和javafx:jlink
移动module-info.java到java下Maven Projects中的javafx:run
使用jlink定制精简jre需要配置module-info.java自定义模块化


不使用AppLauncher.java，直接用Main.java启动需要加以下运行参数
-Dfile.encoding=GBK
--module-path
${JAVAFX_PATH}
--add-modules
javafx.controls,javafx.fxml
--add-exports
javafx.base/com.sun.javafx.event=ALL-UNNAMED

====================================================
idea运行流程说明：运行无法设置字体或者颜色的时候就打开regedit,跳转到【计算机\HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Prefs】-右键设置权限-用户-完全控制

打包流程说明：本项目直接maven package运行打包,然后windows系统就双击项目下output/.iss配置文件打开inno setup生成安装包

注意：本项目需要使用jdk11.0.8这个版本package打包