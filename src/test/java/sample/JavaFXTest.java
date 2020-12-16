package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.utils.javafx.JFXUtils;

public class JavaFXTest extends Application {

    private double x = 0.00;
    private double y = 0.00;
    private double width = 0.00;
    private double height = 0.00;
    private boolean isMax = false;
    private boolean isTop;// 是否处于下边界调整窗口状态
    private boolean isBottom;// 是否处于下边界调整窗口状态
    private boolean isLeft;// 是否处于左边界调整窗口状态
    private boolean isRight;// 是否处于右边界调整窗口状态
    private boolean isLeftBottom;// 是否处于右下角调整窗口状态
    private boolean isRightBottom;// 是否处于右下角调整窗口状态
    private boolean isRightTop;// 是否处于下边界调整窗口状态
    private boolean isLeftTop;// 是否处于下边界调整窗口状态
    private double RESIZE_WIDTH = 5.00;
    private double MIN_WIDTH = 400.00;
    private double MIN_HEIGHT = 300.00;
    private double xOffset = 0, yOffset = 0;//自定义dialog移动横纵坐标
    private double initX, initY, initWidth = -1, initHeight = -1,initStageX = -1, initStageY = -1;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        BorderPane root = new BorderPane();
        GridPane gpTitle = new GridPane();
        gpTitle.setAlignment(Pos.CENTER_LEFT);
        gpTitle.setPadding(new Insets(10));
        Label lbTitle = new Label("自定义窗口");
        lbTitle.setGraphic(new ImageView(JFXUtils.getImg("ic_launcher_16x16.png")));
        Button btnMin = new Button("—");
        Button btnMax = new Button("口");
        Button btnClose = new Button("X");
        gpTitle.add(lbTitle, 0 , 0);
        gpTitle.add(btnMin, 1, 0);
        gpTitle.add(btnMax, 2, 0);
        gpTitle.add(btnClose, 3, 0);
        GridPane.setHgrow(lbTitle, Priority.ALWAYS);
        root.setTop(gpTitle);
        Label lbTips = new Label("Welcome to learn javafx!!!");
        lbTips.setFont(Font.font(20));
        lbTips.setTextFill(Paint.valueOf("red"));
        root.setCenter(lbTips);
        root.setStyle("-fx-background-color: white ;-fx-border-color: rgb(128,128,64); -fx-border-width: 1;");
        gpTitle.setStyle("-fx-background-color: rgb(58.0,154.0,242.0);");
        
        btnMin.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setIconified(true);
            }
        });
        btnMax.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                Rectangle2D rectangle2d = Screen.getPrimary().getVisualBounds();
                isMax = !isMax;
                if (isMax) {
                    // 最大化
                    primaryStage.setX(rectangle2d.getMinX());
                    primaryStage.setY(rectangle2d.getMinY());
                    primaryStage.setWidth(rectangle2d.getWidth());
                    primaryStage.setHeight(rectangle2d.getHeight());
                } else {
                    // 缩放回原来的大小
                    primaryStage.setX(x);
                    primaryStage.setY(y);
                    primaryStage.setWidth(width);
                    primaryStage.setHeight(height);
                }
            }
        });
        btnClose.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });
        primaryStage.xProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue != null && !isMax) {
                    x = newValue.doubleValue();
                }
            }
        });
        primaryStage.yProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue != null && !isMax) {
                    y = newValue.doubleValue();
                }
            }
        });
        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue != null && !isMax) {
                    width = newValue.doubleValue();
                }
            }
        });
        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue != null && !isMax) {
                    height = newValue.doubleValue();
                }
            }
        });
        
        root.setOnMouseMoved((MouseEvent event) -> {
            event.consume();
            double x = event.getSceneX();
            double y = event.getSceneY();
            double width = primaryStage.getWidth();
            double height = primaryStage.getHeight();
            Cursor cursorType = Cursor.DEFAULT;// 鼠标光标初始为默认类型，若未进入调整窗口状态，保持默认类型
            // 先将所有调整窗口状态重置
            isTop = isBottom = isLeft = isRight = isLeftTop = isRightTop = isLeftBottom = isRightBottom = false;
            if(x <= RESIZE_WIDTH && y <= RESIZE_WIDTH){                  // 左上角调整窗口状态
                isLeftTop = true;
                cursorType = Cursor.NW_RESIZE;
            } else if (x >= width - RESIZE_WIDTH && y <= RESIZE_WIDTH) {        // 右上角调整窗口状态
                isRightTop = true;
                cursorType = Cursor.SW_RESIZE;
            } else if(x <= RESIZE_WIDTH && y >= height - RESIZE_WIDTH){         // 左下角调整窗口状态
                isLeftBottom = true;
                cursorType = Cursor.SW_RESIZE;
            } else if (x >= width - RESIZE_WIDTH && y >= height - RESIZE_WIDTH) { // 右下角调整窗口状态
                isRightBottom = true;
                cursorType = Cursor.SE_RESIZE;
            } else if(y <= RESIZE_WIDTH){                      // 上边界调整窗口状态
                isTop = true;
                cursorType = Cursor.N_RESIZE;
            } else if (y >= height - RESIZE_WIDTH) {    // 下边界调整窗口状态isLeftBottom
                isBottom = true;
                cursorType = Cursor.S_RESIZE;
            } else if(x <= RESIZE_WIDTH){               // 左边界调整窗口状态
                isLeft = true;
                cursorType = Cursor.W_RESIZE;
            } else if (x >= width - RESIZE_WIDTH) {     // 右边界调整窗口状态
                isRight = true;
                cursorType = Cursor.E_RESIZE;
            }
            // 最后改变鼠标光标
            root.setCursor(cursorType);
        });

        root.setOnMouseDragged((MouseEvent event) -> {
            
            //根据鼠标的横纵坐标移动dialog位置
            event.consume();
            if (yOffset != 0 ) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
                if(event.getScreenX()==0){
                    System.out.println("鼠标到最左面了放大");
                }else if(event.getScreenY()==0){
                    System.out.println("鼠标到最上面了放大");
                }else if(event.getScreenX()>= Screen.getPrimary().getVisualBounds().getWidth()-1){
                    System.out.println("鼠标到最右面了放大");
                }else if(event.getScreenY()>= Screen.getPrimary().getVisualBounds().getHeight()-1){
                    System.out.println("鼠标到最底面了放大");
                    primaryStage.setY(Screen.getPrimary().getVisualBounds().getHeight()-yOffset);
                }
            }
            
            double x = event.getSceneX();
            double y = event.getSceneY();
            // 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
            double nextX = primaryStage.getX();
            double nextY = primaryStage.getY();
            double nextWidth = primaryStage.getWidth();
            double nextHeight = primaryStage.getHeight();

            double deltax = x - initX;
            double deltay = y - initY;

            if (isTop || isLeftTop || isRightTop) {
                nextHeight -= deltay;
                nextY += deltay;
            }

            if (isBottom || isLeftBottom || isRightBottom) {
                nextHeight = y;
            }

            if (isLeft || isLeftTop || isLeftBottom) {
                nextWidth -= deltax;
                nextX += deltax;
            }

            if (isRight || isRightTop || isRightBottom) {
                nextWidth = x;
            }

            if (nextWidth <= MIN_WIDTH) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
                nextWidth = MIN_WIDTH;
            }
            if (nextHeight <= MIN_HEIGHT) {// 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
                nextHeight = MIN_HEIGHT;
            }
            // 最后统一改变窗口的x、y坐标和宽度、高度，可以防止刷新频繁出现的屏闪情况
            primaryStage.setX(nextX);
            primaryStage.setY(nextY);
            primaryStage.setWidth(nextWidth);
            primaryStage.setHeight(nextHeight);

        });
        //鼠标点击获取横纵坐标
        root.setOnMousePressed(event -> {
            event.consume();
            xOffset = event.getSceneX();
            if (event.getSceneY() > 46) {
                yOffset = 0;
            } else {
                yOffset = event.getSceneY();
            }

            initWidth = primaryStage.getWidth();
            initHeight = primaryStage.getHeight();

            initStageX = primaryStage.getX();
            initStageY = primaryStage.getY();
            initX = event.getSceneX();//鼠标事件点下位置到屏幕X起点的距离
            initY = event.getSceneY();//鼠标事件点下位置到屏幕Y起点的距离

        });
        //根据鼠标移动的位置改变鼠标的样式
//        root.setOnMouseMoved(event -> {
//          event.consume();
//          if (event.getSceneY() > 46) {
//              root.getStyleClass().removeAll("sursor-move");
//          } else {
//              root.getStyleClass().add("sursor-move");
//          }
//        });

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("自定义窗口");
        primaryStage.getIcons().add(JFXUtils.getImg("ic_launcher_16x16.png"));
        primaryStage.show();
    }

    
    public static void main(String[] args) {
        launch(args);
    }

}