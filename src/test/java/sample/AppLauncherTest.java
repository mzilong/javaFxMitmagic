package sample;
/**
 * 打包运行
 * 错误: 缺少 JavaFX 运行时组件, 需要使用该组件来运行此应用程序。
 * 原因：模块化 Java 程序与非模块化 Java 程序的启动方式有所不同
 * 解决：单独创建一个启动类
 */
public class AppLauncherTest {
    public static void main(String[] args) {
        JavaFXTest.main(args);
    }
}