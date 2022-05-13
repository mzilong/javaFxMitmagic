package sample.utils.javafx;

import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.*;
import sample.JFXResources;
import sample.locale.ControlResources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author mzl
 * @date Create in  2019/6/5 0005 14:01
 * @description
 */
public class JFXUtils {

    /**
     * 设置每次点击链接之后，链接不会变成灰色以及点击之后执行的操作
     *
     * @param hyperlink hyperlink
     * @param hander    点击监听器，点击hyperlink之后进行的操作
     */
    public static void setLinkAction(Hyperlink hyperlink, LinkActionHander hander) {

        hyperlink.setBorder(Border.EMPTY);
        hyperlink.setOnMouseClicked(event -> {
            hander.setAction();
            hyperlink.setVisited(false);
        });
    }

    /**
     * 设置链接可以自动跳转资源管理器，浏览器或者打开QQ添加好友界面（保证hyperlink的文字是正确的目录路径，网址，QQ号）
     * QQ添加好友测试不通过，还有bug
     *
     * @param hyperlink hyperlink
     */
    public static void setLinkAutoAction(Hyperlink hyperlink) {
        String text = hyperlink.getText();
        //使用默认的浏览器打开
        if (text.contains("www") || text.contains("com") || text.contains(".")) {
            try {
                java.awt.Desktop.getDesktop().browse(new URI(text));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else if (text.contains(File.separator)) {
            try {
                java.awt.Desktop.getDesktop().open(new File(text));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //打开QQ
            try {
                java.awt.Desktop.getDesktop().browse(new URI("http://wpa.qq.com/msgrd?v=3&uin=" + text + "&site=qq&menu=yes"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭当前窗口
     *
     * @param control 一个控件
     */
    public static void closeWindow(Control control) {
        Stage stage = (Stage) control.getScene().getWindow();
        stage.close();
    }

    /**
     * 获得图片路径
     *
     * @param fileName 图片名+扩展名
     * @return 图片image路径
     */
    public static URL getImgPath(String fileName) {
        return JFXResources.getResource("img/" + fileName);
    }

    /**
     * 获得图片文件
     *
     * @param fileName 图片名+扩展名
     * @return 图片image
     */
    public static Image getImg(String fileName) {
        InputStream resourceAsStream = JFXResources.getResourceAsStream("img/" + fileName);
        if (resourceAsStream != null) {
            return new Image(resourceAsStream);
        }
        return null;
    }


    /**
     * 显示toast
     * @param pane 控件所在的画板（AnchorPane, BorderPane, DialogPane, FlowPane, GridPane, HBox, PopupControl.CSSBridge, StackPane, TextFlow, TilePane, VBox）
     * @param text 显示文字
     */
    public static void showToast(Pane pane, String text) {
        JFXSnackbar bar = new JFXSnackbar(pane);
        bar.enqueue(new JFXSnackbar.SnackbarEvent(new Text(text)));
    }

    /**
     * 设置控件的背景颜色
     * @param region 控件
     * @param fill 填充颜色
     */
    public static void setBackground(Region region, Paint fill) {
        setBackground(region,fill,CornerRadii.EMPTY, Insets.EMPTY);
    }
    public static void setBackground(Region region, Paint fill, CornerRadii radii, Insets insets) {
        region.setBackground(new Background(new BackgroundFill(fill,radii,insets)));
    }
    /**
     * 获得fxml文件路径
     *
     * @param fileName 文件名
     * @return
     */
    public static URL getFxmlPath(String fileName) {
        return JFXResources.getResource("fxml/" + fileName + ".fxml");
    }

    /**
     * 获得输出流
     *
     * @param fileName
     * @return
     */
    public static InputStream getFxmlFile(String fileName) {
        return JFXResources.getResourceAsStream("fxml/" + fileName + ".fxml");
    }

    /**
     * 获得当前jar包所在的文件夹
     * @return 路径
     */
    public static String getCurrentPath() {
        String decode = null;
        decode = URLDecoder.decode(JFXUtils.class.getProtectionDomain()
                .getCodeSource().getLocation().getFile(), StandardCharsets.UTF_8);

        if (decode != null) {
            return new File(decode).getParent();
        } else {
            return null;
        }

    }

    public interface LinkActionHander {
        void setAction();
    }

    /**
     * 添加系统托盘
     * @param tooltip 图标移上提示
     * @param fileName 图标路径
     * @param primaryStage 主窗体
     */
    public static void addSystemTray(String tooltip, String fileName, Stage primaryStage){
        //检查系统是否支持托盘
        if (!java.awt.SystemTray.isSupported()) {
            //系统托盘不支持
            System.out.println("系统托盘不支持");
            return;
        }

        java.awt.SystemTray systemTray = java.awt.SystemTray.getSystemTray();
        //此处不能选择ico格式的图片,要使用16*16的png格式的图片
        java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().getImage(getImgPath(fileName));
        java.awt.PopupMenu popupMenu = new java.awt.PopupMenu();
        java.awt.MenuItem showItem = new java.awt.MenuItem(ControlResources.getString("Show"));
        java.awt.MenuItem exitItem = new java.awt.MenuItem(ControlResources.getString("Exit"));
        popupMenu.add(showItem);
        popupMenu.add(exitItem);

        java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image,tooltip,popupMenu);
        try {
            systemTray.add(trayIcon);
        } catch (java.awt.AWTException e) {
            e.printStackTrace();
        }

        showItem.addActionListener(e ->
            runUiThread(() -> {
                if(primaryStage.isIconified()) {
                    //还原最小化
                    primaryStage.setIconified(false);
                }

                if(!primaryStage.isShowing()){
                    primaryStage.show();
                }
                //打开多个软件的时候不处于前面显示的时候，移到最前显示;
                primaryStage.toFront();
        }));

        exitItem.addActionListener(e -> {
            Platform.setImplicitExit(true);
            runUiThread(() -> primaryStage.close());
            systemTray.remove(trayIcon);
            Platform.exit();
        });
    }

    public static void openBrowse(String url){
        try {
            java.awt.Desktop.getDesktop().browse(new URI(url));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回给定颜色的十六进制web字符串,例如 (#302015)
     *
     * @param color 给定的颜色
     * @return 返回给定颜色的十六进制web字符串,例如 (#302015)
     */
    public static String colorToWebColor(final Color color) {
        return color.toString().replace("0x","#");
    }


    /**
     * 获取屏幕尺寸
     * @param scaleWidth  宽度比
     * @param scaleHeight 高度比
     * @return 屏幕尺寸
     */
    public static double[] getScreenSizeByScale(double scaleWidth, double scaleHeight) {
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.width * scaleWidth;
        double screenHeight = screenSize.height * scaleHeight;
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        if (screenWidth > bounds.getWidth()) {//解决屏幕缩放问题
            screenWidth = bounds.getWidth();
        }
        if (screenHeight > bounds.getHeight()) {//解决屏幕缩放问题
            screenHeight = bounds.getHeight();
        }
        return new double[]{screenWidth, screenHeight};
    }

    /**
     * 如果当前线程属于 UI 线程，则执行 runnable，否则调用 Platform.runLater() 来执行 runnable。
     * 这样能保证 runnable 是在 UI 线程上执行。
     *
     * @param runnable 需要在 UI 线程执行的任务
     */
    public static void runUiThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    /**
     * 在调用线程JFX并等待一个Runnable，直到它完成。
     * 类似SwingUtilities.invokeAndWait。
     * 这种方法是不打算从FAT调用，但是当这种情况发生时同步执行的可运行
     *
     * @param runnable 必须在JFX应用程序线程上执行的Runnable。
     * @throws RuntimeException which wraps a possible interruptedException or ExecutionException
     */
    public static void runAndWait(final Runnable runnable) {
        // running this from the FAT
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }

        // run from a separate thread
        try {
            FutureTask<Void> future = new FutureTask<>(runnable, null);
            Platform.runLater(future);
            future.get();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 在JFX线程中调用Callable，并等待其完成。
     * 类似于SwingUtilities.invokeAndWait。
     * 不能从FAT调用此方法，但是发生这种情况时，可调用对象将同步执行。
     *
     * @param callable 必须在JFX应用程序线程上执行的Callable。
     * @return the result of callable.call();
     * @throws RuntimeException which wraps a possible Exception, InterruptedException or ExecutionException
     */
    public static <V> V runAndWait(final Callable<V> callable) {
        // running this from the FAT
        if (Platform.isFxApplicationThread()) {
            try {
                return callable.call();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // run from a separate thread
        try {
            FutureTask<V> future = new FutureTask<>(callable);
            Platform.runLater(future);
            return future.get();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // 给窗体设置全局图标
    public static void setupIcon(Stage stage,Image appIcon) {
        if(appIcon!=null && !appIcon.isError()){
            stage.getIcons().clear();
            stage.getIcons().add(appIcon);
        }else{
            stage.initStyle(StageStyle.UTILITY);
        }
    }

    /**
     * 选择系统文件夹路径
     * @param ownerWindow
     * @param filePath
     * @return
     */
    public static File selectSystemPath(Window ownerWindow,String title,String filePath) {
        if(filePath==null){
            filePath = "";
        }
        File oldDir = new File(filePath);
        return selectSystemPath(ownerWindow,title,oldDir);
    }
    public static File selectSystemPath(Window ownerWindow,String title,File oldDir) {
        DirectoryChooser chooser = new DirectoryChooser();
        if(title!=null&&title.length()>0){
            chooser.setTitle(title);
        }
        if (oldDir!=null&&oldDir.exists()) {
            chooser.setInitialDirectory(oldDir);
        }
        File dir = chooser.showDialog(ownerWindow);
        return dir;
    }


    /**
     * 设置一个或多个组件的大小和背景颜色
     * @param prefWidth 用于设置组件的宽
     * @param prefHeight 用于设置组件的长
     * @param backgroundColor 用于设置的背景颜色
     * @param regions 一个或多个组件，如果没有，那么该语句相当于不存在
     */
    public void setCommpentSizeAndColor(double prefWidth,double prefHeight,Color backgroundColor, Region...regions){
        setCommpentSize(prefWidth,prefHeight,regions);
        setCommpentColor(backgroundColor,regions);
    }
    /**
     * 设置一个或多个组件的大小
     * @param prefWidth 用于设置组件的宽
     * @param prefHeight 用于设置组件的长
     * @param regions 一个或多个组件，如果没有，那么该语句相当于不存在
     */
    public void setCommpentSize(double prefWidth,double prefHeight, Region...regions){
        for(int i = 0; i < regions.length; i++){
            if(regions[i] instanceof Label){
                Label label = (Label) regions[i];
                //设置字体居中
                label.setAlignment(Pos.CENTER);
            } else if(regions[i] instanceof Button) {
                regions[i].setPrefSize(prefWidth, prefHeight);
            }
        }
    }
    /**
     * 设置一个或多个组件的背景颜色
     * @param backgroundColor 用于设置的背景颜色
     * @param regions 一个或多个组件，如果没有，那么该语句相当于不存在
     */
    public void setCommpentColor(Color backgroundColor, Region...regions){
        for(int i = 0; i < regions.length; i++){
            regions[i].setBackground(new Background(new BackgroundFill(backgroundColor,null,null)));
        }
    }

    /**
     * 获取节点的用户数据
     * @param node 节点
     * @param key 关键字
     * @param <T> 类型
     * @return 类型
     */
    public static <T> T getUserData(Node node, String key) {
        return node.getUserData() == null ? null :
                !(node.getUserData() instanceof Map) ? null :
                        (T) ((Map<String, Object>) node.getUserData()).get(key);
    }

    /**
     * 设置节点的用户数据
     * @param node 节点
     * @param key 关键字
     * @param value 值
     */
    public static void setUserData(Node node, String key, Object value) {
        Map<String, Object> map = (Map<String, Object>) node.getUserData();
        if (map == null) {
            map = new HashMap<>();
            node.setUserData(map);
        }
        map.put(key, value);
    }

    /**
     * 获取节点的用户数据
     * @param window 窗口
     * @param key 关键字
     * @param <T> 类型
     * @return 类型
     */
    public static <T> T getUserData(Window window, String key) {
        return window.getUserData() == null ? null :
                !(window.getUserData() instanceof Map) ? null :
                        (T) ((Map<String, Object>) window.getUserData()).get(key);
    }

    /**
     * 设置节点的用户数据
     * @param window 窗口
     * @param key 关键字
     * @param value 值
     */
    public static void setUserData(Window window, String key, Object value) {
        Map<String, Object> map = (Map<String, Object>) window.getUserData();
        if (map == null) {
            map = new HashMap<>();
            window.setUserData(map);
        }
        map.put(key, value);
    }

    public static <T> T getParent(Class<?> viewType) {
        URL url= null;
        try {
            url = FxmlUtils.createFxmlPath(viewType);
        } catch (NoSuchFileException e) {
            e.printStackTrace();
        }
        return getParent(url);
    }

    public static <T> T getParent(URL url) {
        FXMLLoader loader = FxmlUtils.loadFxmlFromResource(url, ControlResources.getResourceBundle(null));
        return loader.getRoot();
    }

    public static <T> T getParent(String url) {
        FXMLLoader loader = FxmlUtils.loadFxmlFromResource(url,ControlResources.getResourceBundle(null));
        return loader.getRoot();
    }
}
