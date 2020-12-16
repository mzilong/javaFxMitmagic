package sample.controller.base;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.event.BaseEvent;
import sample.event.FxEventBus;
import sample.utils.javafx.FxIntent;
import sample.utils.javafx.FxStyleUtils;
import sample.utils.javafx.JFXUtils;
import sample.view.JFXDecorator;

import java.util.prefs.Preferences;

/**
 * @author mzl
 * @date Create in  2019/6/21 0021 16:41
 * @description
 */
public abstract class BaseController implements Initializable, EventHandler<BaseEvent> {
    public final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Stage primaryStage;
    public Preferences preferences;

    public BaseController() {
        this.preferences = Preferences.userRoot();
    }

    public String getFxBasePref() {
        return preferences.get("-fx-base","#0091ea");
    }

    public void setFxBasePref(String value) {
        preferences.put("-fx-base",value);
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
        Parent root = intent.getRoot();
        FxStyleUtils.setBase(root,getFxBasePref());
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
        this.preferences = null;
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
}
