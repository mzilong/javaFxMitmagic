package sample.utils.javafx;

import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.JFXRippler;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.JFXResources;
import sample.controller.base.BaseController;
import sample.locale.ControlResources;
import sample.view.JFXDecorator;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author mzl
 * @date Create in  2020年8月5日15:40:01
 * @description 传递参数
 */
public class FxIntent {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Parent root;
    private Stage primaryStage;
    private URL fxmlLocation;
    private ResourceBundle resourceBundle;
    private double width = -1;
    private double height = -1;
    private StageStyle stageStyle;
    private Map<String, Object> conveyData = new HashMap<>();

    public Map<String, Object> getConveyData() {
        return conveyData;
    }

    public void setConveyData(Map<String, Object> conveyData) {
        this.conveyData = conveyData;
    }
    public void addData(String key, List<?> data) {
        conveyData.put(key, data);
    }
    public void addData(String key, Object data) {
        conveyData.put(key, data);
    }
    public <T> T getData(String key) {
        return (T) conveyData.get(key);
    }
    public <T> List<T> getDataList(String key, Class<T> clazz) {
        return (List<T>) conveyData.get(key);
    }
    private Class<? extends BaseController> bClass;

    public FxIntent(Class<? extends BaseController> bClass) {
        this(bClass,-1,-1,StageStyle.DECORATED);
    }

    public FxIntent(Class<? extends BaseController> bClass, StageStyle stageStyle) {
        this(bClass,-1,-1,stageStyle);
    }

    public FxIntent(Class<? extends BaseController> bClass, double width, double height) {
        this(bClass,width,height,StageStyle.DECORATED);
    }

    public FxIntent(Class<? extends BaseController> bClass, double width, double height, StageStyle stageStyle) {
        this.bClass = bClass;
        this.width = width;
        this.height = height;
        this.stageStyle = stageStyle;
    }

    public FxIntent() {}


    public void start() {
        try {
            resourceBundle = ControlResources.getResourceBundle(null);
            fxmlLocation = FxmlUtils.createFxmlPath(bClass);
            //这里初始化会调用实现#Initializable接口的initialize方法
            FXMLLoader loader = FxmlUtils.loadFxmlFromResource(fxmlLocation,resourceBundle);
            root = loader.getRoot();
            if(primaryStage==null){
                primaryStage = new Stage();
            }
            JFXDecorator decorator;
            primaryStage.initStyle(stageStyle);
            if(stageStyle == StageStyle.UTILITY){
                primaryStage.initModality(Modality.APPLICATION_MODAL);
                decorator = new JFXDecorator(primaryStage, root,false,false,false);
                root = decorator;
            }else if(stageStyle == StageStyle.TRANSPARENT){
            }else{
                decorator = new JFXDecorator(primaryStage, root,true,true,true,
                            resourceBundle.getString("JFXDecorator.FullScreen"),
                            resourceBundle.getString("JFXDecorator.Minimize"),
                            resourceBundle.getString("JFXDecorator.Maximize"),
                            resourceBundle.getString("JFXDecorator.Restore"),
                            resourceBundle.getString("JFXDecorator.Close"));
                decorator.setCustomMaximize(true);
                decorator.setOnMouseMoveEdge((side, mouseEvent) -> {
                    showRectangleStage(side,mouseEvent);
                    showPointStage(side,mouseEvent);
                });
                root = decorator;
            }
            JFXUtils.setupIcon(primaryStage,JFXUtils.getImg("ic_launcher_512x512.png"));
            BaseController controller = loader.getController();
            controller.setIntent(this);
            Scene scene= new Scene(root,width,height, Color.TRANSPARENT);
            scene.getStylesheets().addAll(JFoenixResources.load("css/jfoenix-fonts.css").toExternalForm(),
                    JFoenixResources.load("css/jfoenix-design.css").toExternalForm(),
                    JFXResources.getResource("css/jfoenix-main.css").toExternalForm());
            if (BaseController.ELEMENT_STYLE) {
                scene.getStylesheets().add(JFXResources.getResource("css/element-ui.css").toExternalForm());
            }
            scene.getStylesheets().add(JFXResources.getResource("css/icon.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("打开["+bClass.getSimpleName()+"]");
    }

    private Stage rectangleStage;
    private Timeline timeline;
    /**
     * 用于显示放大预览的透明矩形窗口
     * @param side 方位枚举
     * @param mouseEvent 鼠标事件
     */
    private void showRectangleStage(Side side, MouseEvent mouseEvent){
        if(side==null){
            if(rectangleStage !=null&& rectangleStage.isShowing()) {
                if(timeline!=null){
                    timeline.stop();
                }
                timeline = new Timeline(
                        new KeyFrame(Duration.millis(0),
                                new KeyValue(rectangleStage.opacityProperty(), 1, Interpolator.LINEAR)
                        ),
                        new KeyFrame(Duration.millis(200),
                                new KeyValue(rectangleStage.opacityProperty(), 0, Interpolator.LINEAR)
                        ));
                timeline.play();
                ScheduledThreadPoolExecutor stpExecutor = new ScheduledThreadPoolExecutor(1);
                stpExecutor.schedule(() -> {
                    timeline = null;
                    Platform.runLater(() -> {
                        rectangleStage.close();
                        rectangleStage = null;
                    });
                },200, TimeUnit.MILLISECONDS);
            }
            return;
        }
        if(rectangleStage !=null&& rectangleStage.isShowing()){
            return;
        }
        rectangleStage = new Stage();
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setBackground(null);
        Rectangle rectangle= new Rectangle();
        rectangle.setFill(Color.valueOf("#ffffff10"));
        rectangle.setStroke(Color.valueOf("#ffffff30"));

        Node mousePane = (Node) mouseEvent.getTarget();
        Scene scene = mousePane.getScene();
        Stage primaryStage = (Stage) scene.getWindow();
        anchorPane.getChildren().addAll(rectangle);
        Rectangle2D vBounds = Screen.getPrimary().getVisualBounds();
        rectangleStage.initStyle(StageStyle.TRANSPARENT);
        rectangleStage.setScene(new Scene(anchorPane,vBounds.getWidth(),vBounds.getHeight(),Color.TRANSPARENT));
        rectangleStage.initOwner(primaryStage);
        rectangleStage.show();
        primaryStage.toFront();

        double margin = 10;
        double x = 0;
        double y = 0;
        double w = vBounds.getWidth();
        double h = vBounds.getHeight();
        if(side==Side.TOP){
            x = 0;
        }else if(side==Side.LEFT){
            w = w/2;
            x = 0;
        }else if(side==Side.RIGHT){
            w = w/2;
            x= w;
        }else if(side==Side.BOTTOM){
            w = primaryStage.getWidth();
            x = mouseEvent.getScreenX()-mouseEvent.getSceneX();
        }
        w -= margin*2;
        h -= margin*2;
        y += margin;
        x += margin;
        if(timeline!=null){
            timeline.stop();
        }
        timeline=new Timeline(
                new KeyFrame(Duration.millis(0),
                        new KeyValue(rectangle.xProperty(), mouseEvent.getScreenX() , Interpolator.LINEAR),
                        new KeyValue(rectangle.yProperty(), mouseEvent.getScreenY() , Interpolator.LINEAR),
                        new KeyValue(rectangle.opacityProperty(), 0, Interpolator.LINEAR),
                        new KeyValue(rectangle.widthProperty(), 0, Interpolator.LINEAR),
                        new KeyValue(rectangle.heightProperty(), 0, Interpolator.LINEAR)
                ),
                new KeyFrame(Duration.millis(150),
                        new KeyValue(rectangle.xProperty(), x, Interpolator.LINEAR),
                        new KeyValue(rectangle.yProperty(), y, Interpolator.LINEAR),
                        new KeyValue(rectangle.opacityProperty(), 1, Interpolator.LINEAR),
                        new KeyValue(rectangle.widthProperty(), w, Interpolator.LINEAR),
                        new KeyValue(rectangle.heightProperty(), h, Interpolator.LINEAR)
                )
        );
        timeline.play();
    }

    private Stage pointStage;
    /**
     * 用于显示放大预览的透明点
     * @param side 方位枚举
     * @param mouseEvent 鼠标事件
     */
    private void showPointStage(Side side,MouseEvent mouseEvent){
        if(side==null){
            if(pointStage !=null&& pointStage.isShowing()){
                pointStage.close();
                pointStage = null;
            }
            return;
        }

        if(pointStage !=null&& pointStage.isShowing()){
            return;
        }
        Node mousePane= (Node) mouseEvent.getTarget();
        Stage primaryStage = (Stage) mousePane.getScene().getWindow();
        double rw = 50;
        JFXRippler buttonRippler = new JFXRippler(new Rectangle(rw,rw,Color.TRANSPARENT));
        buttonRippler.setMaskType(JFXRippler.RipplerMask.CIRCLE);
        buttonRippler.setRipplerFill(Color.WHITE);
        buttonRippler.resize(rw, rw);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setBackground(null);
        anchorPane.getChildren().addAll(buttonRippler);
        pointStage = new Stage();
        pointStage.initStyle(StageStyle.TRANSPARENT);
        pointStage.setScene(new Scene(anchorPane,rw,rw,Color.TRANSPARENT));
        pointStage.initOwner(primaryStage);
        pointStage.show();
        pointStage.setX(mouseEvent.getScreenX()-rw/2);
        pointStage.setY(mouseEvent.getScreenY()-rw/2);
        Runnable releaseManualRippler = buttonRippler.createManualRipple();
        if (releaseManualRippler != null) {
            releaseManualRippler.run();
        }
        pointStage.toFront();
    }

    public void closePrimaryStage(){
        if(primaryStage!=null){
            primaryStage.close();
            logger.info("关闭["+bClass.getSimpleName()+"]");
        }
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public URL getFxmlLocation() {
        return fxmlLocation;
    }

    public void setFxmlLocation(URL fxmlLocation) {
        this.fxmlLocation = fxmlLocation;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
