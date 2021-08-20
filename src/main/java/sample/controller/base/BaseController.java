package sample.controller.base;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.event.BaseEvent;
import sample.event.FxEventBus;
import sample.tools.PreferencesTools;
import sample.tools.dialog.Message;
import sample.tools.dialog.Notification;
import sample.utils.javafx.FxIntent;
import sample.utils.javafx.FxStyleUtils;
import sample.utils.javafx.JFXUtils;
import sample.view.JFXDecorator;

/**
 * @author mzl
 * @date Create in  2019/6/21 0021 16:41
 * @description
 */
public abstract class BaseController implements Initializable, EventHandler<BaseEvent> {
    public final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Stage primaryStage;
    private Parent root;
    public static boolean ELEMENT_STYLE = false;

    public BaseController() {
    }

    private FxIntent intent;

    public FxIntent getIntent() {
        return intent;
    }

    public void setIntent(FxIntent intent) {
        //默认初始化事件处理程序
        FxEventBus.getDefault().addEventHandler(BaseEvent.BASE_EVENT, this);
        this.intent = intent;
        primaryStage = intent.getPrimaryStage();
//        ControllersManagement.putController(this);
        primaryStage.setOnHiding(this::onHiding);
        primaryStage.setOnShowing(this::onShowing);
        primaryStage.setTitle(initTitle());
        root = intent.getRoot();
        FxStyleUtils.setBase(root,PreferencesTools.getFxBasePref());
        String iconName = initIcon();
        if (iconName != null) {
            Image icon = JFXUtils.getImg(iconName);
            if(icon!=null&&primaryStage.getStyle()!= StageStyle.TRANSPARENT){
                JFXDecorator decorator = (JFXDecorator) root;
                ImageView imageView=new ImageView(icon);
                imageView.setFitWidth(18);
                imageView.setPreserveRatio(true);
                decorator.setIcon(imageView);
            }
        }
    }

    @Override
    public void handle(BaseEvent event) {
        onEvent(event);
    }

    /**
     * 控制器事件回调方法，子类可以覆盖实现
     * @param event
     */
    public void onEvent(BaseEvent event){}

    /**
     * 在窗口关闭之后调用，子类可以覆盖实现
     */
    public void onHiding(WindowEvent event){
//        ControllersManagement.removeController(this.getClass());
        //默认移除事件处理程序
        FxEventBus.getDefault().removeEventHandler(BaseEvent.BASE_EVENT, this);
    }
    /**
     * 在窗口关闭之后调用，子类可以覆盖实现
     */
    public void onShowing(WindowEvent event){}

    /**
     * 初始化窗体的标题
     * @return 字符串
     */
    public abstract String initTitle();
    /**
     * 初始化的窗体图标
     * @return 字符串
     */
    public abstract String initIcon();

    public  <T extends Node> T findView(Node node,String id){
        return (T)node.lookup(id);
    }

    /**
     * @return 视图根对象
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * 显示一则默认类型默认延迟的消息
     *
     * @param message 通知信息
     */
    protected void showMessage(String message) {
        showMessage(message, Message.Type.INFO);
    }

    /**
     * 显示一则指定类型默认延迟的消息
     *
     * @param message 通知信息
     */
    protected void showMessage(String message, Message.Type type) {
        showMessage(message, type, Message.DEFAULT_DELAY);
    }

    /**
     * 显示一则默认类型指定延迟的消息
     *
     * @param message 通知信息
     */
    protected void showMessage(String message, long delay) {
        showMessage(message, Message.Type.INFO, delay);
    }

    /**
     * 显示一则指定类型指定延迟的消息
     *
     * @param message 通知信息
     */
    protected void showMessage(String message, Message.Type type, long delay) {
        Parent root = getRoot();
        if (!(root instanceof Pane)) {
            System.err.println("owner 必须是 Pane 或其子类");
            return;
        }
        Message.show((Pane) root, message, type, delay);
    }

    /**
     * 显示一则默认类型的通知， 用户手动关闭
     *
     * @param message 通知信息
     */
    protected void showNotification(String message) {
        showNotification(message, Notification.Type.INFO);
    }

    /**
     * 显示一则指定类型的通知，用户手动关闭
     *
     * @param message 通知信息
     * @param type    通知类型
     */
    protected void showNotification(String message, Notification.Type type) {
        showNotification(message, type, 0);
    }

    /**
     * 显示一则指定类型的通知，自动关闭，默认显示一秒
     *
     * @param message 通知信息
     */
    protected void showNotificationAutoClose(String message) {
        showNotificationAutoClose(message, Notification.Type.INFO);
    }

    protected void showNotificationAutoClose(String message, Notification.Type type) {
        showNotification(message, type, Notification.DEFAULT_DELAY);
    }

    /**
     * 显示一则指定类型的通知，自动关闭，指定显示时间
     *
     * @param message      通知信息
     * @param type         通知类型
     * @param milliseconds 延迟时间 毫秒
     */
    protected void showNotification(String message, Notification.Type type, long milliseconds) {
        Parent root = getRoot();
        if (!(root instanceof Pane)) {
            System.err.println("owner 必须是 Pane 或其子类");
            return;
        }
        Notification.showAutoClose((Pane) root, message, type, milliseconds);
    }
}
