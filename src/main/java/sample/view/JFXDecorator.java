/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package sample.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.svg.SVGGlyph;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Window Decorator allow to resize/move its content Note: the default close button will call stage.close() which will
 * only close the current stage. it will not close the java application, however it can be customized by calling {@link
 * #setOnCloseButtonAction(Runnable)}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXDecorator extends VBox {
    private Stage primaryStage;
    private double initXOffset = 0, initYOffset = 0, initX, initY;
    private double lastStageWidth = -1, lastStageHeight = -1;
    private boolean isTopMaximized,isBottomMaximized,isLeftMaximized,isRightMaximized;
    private final DecimalFormat df = new DecimalFormat("#.00");

    private boolean allowMove = false;
    private boolean isDragging = false;
    private Timeline windowDecoratorAnimation;
    private StackPane contentPlaceHolder = new StackPane();
    private HBox buttonsContainer;

    private ObjectProperty<Runnable> onCloseButtonAction = new SimpleObjectProperty<>(() ->
        primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));

    private OnMouseMoveEdgeListener onMouseMoveEdgeListener;
    public interface OnMouseMoveEdgeListener {
        void onMouseMoveEdge(Side side, MouseEvent mouseEvent);
    }
    /**
     * Set the mouse to move the window to the edge callback interface
     *
     * @param onMouseMoveEdgeListener
     */
    public void setOnMouseMoveEdge(OnMouseMoveEdgeListener onMouseMoveEdgeListener) {
        this.onMouseMoveEdgeListener = onMouseMoveEdgeListener;
    }

    private BooleanProperty customMaximize = new SimpleBooleanProperty(false);
    private boolean maximized = false;
    private BoundingBox initBox;
    private BoundingBox originalBox;
    private BoundingBox maximizedBox;

    protected JFXButton btnMax;
    protected JFXButton btnFull;
    protected JFXButton btnClose;
    protected JFXButton btnMin;

    protected MenuItem menuItemFull,menuItemMax,menuItemMin,menuItemClose;
    protected MenuButton menuButton;

    protected StringProperty title = new SimpleStringProperty();
    protected Text text;
    protected Node graphic;
    protected HBox graphicContainer;

    /**
     * Create a window decorator for the specified node with the options:
     * - full screen
     * - maximize
     * - minimize
     *
     * @param stage the primary stage used by the application
     * @param node  the node to be decorated
     */
    public JFXDecorator(Stage stage, Node node) {
        this(stage, node, true, true, true);
    }

    private final SimpleDoubleProperty stageX = new SimpleDoubleProperty(-1);
    private final SimpleDoubleProperty stageY = new SimpleDoubleProperty(-1);
    private final SimpleDoubleProperty stageWidth = new SimpleDoubleProperty(-1);
    private final SimpleDoubleProperty stageHeight = new SimpleDoubleProperty(-1);

    public double getStageX() {
        return stageX.get();
    }

    public SimpleDoubleProperty stageXProperty() {
        return stageX;
    }

    public void setStageX(double stageX) {
        this.stageX.set(stageX);
    }

    public double getStageY() {
        return stageY.get();
    }

    public SimpleDoubleProperty stageYProperty() {
        return stageY;
    }

    public void setStageY(double stageY) {
        this.stageY.set(stageY);
    }

    public double getStageWidth() {
        return stageWidth.get();
    }

    public SimpleDoubleProperty stageWidthProperty() {
        return stageWidth;
    }

    public void setStageWidth(double stageWidth) {
        this.stageWidth.set(stageWidth);
    }

    public double getStageHeight() {
        return stageHeight.get();
    }

    public SimpleDoubleProperty stageHeightProperty() {
        return stageHeight;
    }

    public void setStageHeight(double stageHeight) {
        this.stageHeight.set(stageHeight);
    }

    /**
     * Create a window decorator for the specified node with the options:
     * - full screen
     * - maximize
     * - minimize
     *
     * @param stage      the primary stage used by the application
     * @param node       the node to be decorated
     * @param fullScreen indicates whether to show full screen option or not
     * @param max        indicates whether to show maximize option or not
     * @param min        indicates whether to show minimize option or not
     */
    public JFXDecorator(Stage stage, Node node, boolean fullScreen, boolean max, boolean min) {
        this(stage,node,fullScreen,max,min,null,null,null,null,null);
    }

    /**
     * Create a window decorator for the specified node with the options:
     * - full screen
     * - maximize
     * - minimize
     *
     * @param stage      the primary stage used by the application
     * @param node       the node to be decorated
     * @param fullScreen indicates whether to show full screen option or not
     * @param max        indicates whether to show maximize option or not
     * @param min        indicates whether to show minimize option or not
     * @param resFullScreen         ResourceBundle--fullscreen
     * @param resMinimize           ResourceBundle--minimize
     * @param resMaximize           ResourceBundle--maximize
     * @param resRestore           ResourceBundle--restore
     * @param resClose              ResourceBundle--close
     */
    public JFXDecorator(Stage stage, Node node, boolean fullScreen, boolean max, boolean min,String resFullScreen, String resMinimize, String resMaximize, String resRestore, String resClose) {
        this.resFullScreen = resFullScreen;
        this.resMinimize = resMinimize;
        this.resRestore = resRestore;
        this.resMaximize = resMaximize;
        this.resClose = resClose;

        primaryStage = stage;
        // Note that setting the style to TRANSPARENT is causing performance
        // degradation, as an alternative we set it to UNDECORATED instead.
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        setPickOnBounds(false);
        this.getStyleClass().addAll("jfx-decorator");

        stageX.addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                primaryStage.setX(newValue.doubleValue());
            }
        });
        stageY.addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                primaryStage.setY(newValue.doubleValue());
            }
        });
        stageWidth.addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                primaryStage.setWidth(newValue.doubleValue());
            }
        });
        stageHeight.addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                primaryStage.setHeight(newValue.doubleValue());
            }
        });

        initMaximizedBox();
        initializeButtons();
        initializeContainers(node, fullScreen, max, min);

        primaryStage.fullScreenProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                // remove border
                this.getStyleClass().remove("resize-border");
                /*
                 *  note the border property MUST NOT be bound to another property
                 *  when going full screen mode, thus the binding will be lost if exisited
                 */
                this.borderProperty().unbind();
                this.setBorder(Border.EMPTY);
                if (windowDecoratorAnimation != null) {
                    windowDecoratorAnimation.stop();
                }
                windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                    new KeyValue(buttonsContainer.translateYProperty(),
                        -buttonsContainer.getHeight(),
                        Interpolator.EASE_BOTH)));
                windowDecoratorAnimation.setOnFinished((finish) -> {
                    this.getChildren().remove(buttonsContainer);
                });
                windowDecoratorAnimation.play();
            } else {
                // add border
                if (windowDecoratorAnimation != null) {
                    if (windowDecoratorAnimation.getStatus() == Animation.Status.RUNNING) {
                        windowDecoratorAnimation.stop();
                    } else {
                        this.getChildren().add(0, buttonsContainer);
                    }
                }
                buttonsContainer.setTranslateY(-buttonsContainer.getHeight());
                windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                    new KeyValue(buttonsContainer.translateYProperty(),
                        0,
                        Interpolator.EASE_BOTH)));
                windowDecoratorAnimation.setOnFinished((finish) -> {
                    this.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(4, 4, 4, 4))));
                    this.getStyleClass().add("resize-border");
                });
                windowDecoratorAnimation.play();
            }
        });

        // show the drag cursor on the borders
        this.setOnMouseMoved((mouseEvent) -> {
            if(isCustomMaximize()) {
                showDragCursorOnBorders(mouseEvent);
            }
        });
        this.setOnMousePressed((mouseEvent) -> {
            if(isCustomMaximize()){
                updateInitMouseValues(mouseEvent);
            }
        });
        // handle drag events on the decorator pane
        this.setOnMouseDragged((mouseEvent) -> {
            if(isCustomMaximize()){
                handleDragEventOnDecoratorPane(mouseEvent);
            }
        });
        this.setOnMouseReleased((mouseEvent) -> {
            if(isCustomMaximize()){
                handleReleasedEventOnDecoratorPane(mouseEvent);
            }
        });

        buttonsContainer.setOnMousePressed( (mouseEvent) -> updateInitMouseValues(mouseEvent));
        buttonsContainer.setOnMouseEntered((enter) ->{
            if (!isDragging) {
                buttonsContainer.setCursor(Cursor.MOVE);
                allowMove = true;
            }
        });
        buttonsContainer.setOnMouseExited((enter) -> {
            if (!isDragging) {
                buttonsContainer.setCursor(Cursor.DEFAULT);
                allowMove = false;
            }
        });
        buttonsContainer.setOnMouseDragged((enter) -> {
            if (!isDragging) {
                showDragCursorOnScreen(enter);
            }
        });
    }

    private String resFullScreen,resMinimize,resRestore,resMaximize,resClose;
    private void initializeButtons() {
        SVGGlyph full = new SVGGlyph(0,
            "FULLSCREEN",
            "M598 214h212v212h-84v-128h-128v-84zM726 726v-128h84v212h-212v-84h128zM214 426v-212h212v84h-128v128h-84zM298 598v128h128v84h-212v-212h84z",
            Color.WHITE);
        full.setSize(16, 16);
        SVGGlyph minus = new SVGGlyph(0,
            "MINUS",
            "M804.571 420.571v109.714q0 22.857-16 38.857t-38.857 16h-694.857q-22.857 0-38.857-16t-16-38.857v-109.714q0-22.857 16-38.857t38.857-16h694.857q22.857 0 38.857 16t16 38.857z",
            Color.WHITE);
        minus.setSize(12, 2);
        minus.setTranslateY(4);
        SVGGlyph resizeMax = new SVGGlyph(0,
            "RESIZE_MAX",
            "M726 810v-596h-428v596h428zM726 44q34 0 59 25t25 59v768q0 34-25 60t-59 26h-428q-34 0-59-26t-25-60v-768q0-34 25-60t59-26z",
            Color.WHITE);
        resizeMax.setSize(12, 12);
        SVGGlyph resizeMin = new SVGGlyph(0,
            "RESIZE_MIN",
            "M80.842 943.158v-377.264h565.894v377.264h-565.894zM0 404.21v619.79h727.578v-619.79h-727.578zM377.264 161.684h565.894v377.264h-134.736v80.842h215.578v-619.79h-727.578v323.37h80.842v-161.686z",
            Color.WHITE);
        resizeMin.setSize(12, 12);
        SVGGlyph close = new SVGGlyph(0,
            "CLOSE",
            "M810 274l-238 238 238 238-60 60-238-238-238 238-60-60 238-238-238-238 60-60 238 238 238-238z",
            Color.WHITE);
        close.setSize(12, 12);

        //MenuIcon
        SVGGlyph full2 = new SVGGlyph(1,
                "FULLSCREEN2",
                "M598 214h212v212h-84v-128h-128v-84zM726 726v-128h84v212h-212v-84h128zM214 426v-212h212v84h-128v128h-84zM298 598v128h128v84h-212v-212h84z",
                Color.BLACK);
        full2.setSize(10, 10);
        SVGGlyph minus2 = new SVGGlyph(1,
                "MINUS2",
                "M804.571 420.571v109.714q0 22.857-16 38.857t-38.857 16h-694.857q-22.857 0-38.857-16t-16-38.857v-109.714q0-22.857 16-38.857t38.857-16h694.857q22.857 0 38.857 16t16 38.857z",
                Color.BLACK);
        minus2.setSize(10, 2);
        minus2.setTranslateY(5);
        SVGGlyph resizeMax2 = new SVGGlyph(1,
                "RESIZE_MAX2",
                "M726 810v-596h-428v596h428zM726 44q34 0 59 25t25 59v768q0 34-25 60t-59 26h-428q-34 0-59-26t-25-60v-768q0-34 25-60t59-26z",
                Color.BLACK);
        resizeMax2.setSize(10,10);
        SVGGlyph resizeMin2 = new SVGGlyph(1,
                "RESIZE_MIN2",
                "M80.842 943.158v-377.264h565.894v377.264h-565.894zM0 404.21v619.79h727.578v-619.79h-727.578zM377.264 161.684h565.894v377.264h-134.736v80.842h215.578v-619.79h-727.578v323.37h80.842v-161.686z",
                Color.BLACK);
        resizeMin2.setSize(10, 10);
        SVGGlyph close2 = new SVGGlyph(1,
                "CLOSE2",
                "M810 274l-238 238 238 238-60 60-238-238-238 238-60-60 238-238-238-238 60-60 238 238 238-238z",
                Color.BLACK);
        close2.setSize(10, 10);



        btnFull = new JFXButton();
        btnFull.getStyleClass().add("jfx-decorator-button");
        btnFull.setCursor(Cursor.HAND);
        btnFull.setOnAction((action) -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));
        btnFull.setGraphic(full);
        btnFull.setRipplerFill(Color.WHITE);

        btnClose = new JFXButton();
        btnClose.getStyleClass().add("jfx-decorator-button");
        btnClose.setCursor(Cursor.HAND);
        btnClose.setOnAction((action) -> onCloseButtonAction.get().run());
        btnClose.setGraphic(close);
        btnClose.setRipplerFill(Color.RED);

        btnMin = new JFXButton();
        btnMin.getStyleClass().add("jfx-decorator-button");
        btnMin.setCursor(Cursor.HAND);
        btnMin.setOnAction((action) -> primaryStage.setIconified(true));
        btnMin.setGraphic(minus);
        btnMin.setRipplerFill(Color.WHITE);

        btnMax = new JFXButton();
        btnMax.getStyleClass().add("jfx-decorator-button");
        btnMax.setCursor(Cursor.HAND);
        btnMax.setRipplerFill(Color.WHITE);
        btnMax.setOnAction((action) -> maximize(resizeMin, resizeMax, resizeMin2, resizeMax2));
        btnMax.setGraphic(resizeMax);
        if(resMaximize!=null) {
            btnMax.setTooltip(new Tooltip(resMaximize));
        }

        //Menu
        menuItemFull = new MenuItem(resFullScreen,full2);
        menuItemFull.onActionProperty().bind(btnFull.onActionProperty());
        menuItemFull.getStyleClass().add("jfx-decorator-menuitem");
        menuItemMax = new MenuItem(resMaximize,resizeMax2);
        menuItemMax.onActionProperty().bind(btnMax.onActionProperty());
        menuItemMax.getStyleClass().add("jfx-decorator-menuitem");
        menuItemMin = new MenuItem(resMinimize,minus2);
        menuItemMin.onActionProperty().bind(btnMin.onActionProperty());
        menuItemMin.getStyleClass().add("jfx-decorator-menuitem");
        menuItemClose = new MenuItem(resClose,close2);
        menuItemClose.getStyleClass().add("jfx-decorator-menuitem");
        menuItemClose.onActionProperty().bind(btnClose.onActionProperty());
    }

    private void initializeContainers(Node node, boolean fullScreen, boolean max, boolean min) {
        buttonsContainer = new HBox();
        buttonsContainer.getStyleClass().add("jfx-decorator-buttons-container");
        buttonsContainer.setBackground(new Background(new BackgroundFill(Color.BLACK,
                CornerRadii.EMPTY,
                Insets.EMPTY)));
        // BINDING
        buttonsContainer.setPadding(new Insets(4));
        buttonsContainer.setAlignment(Pos.CENTER_RIGHT);

        SVGGlyph svgGlyph = new SVGGlyph("M512 682c-128.024-99.309-255.626-199.040-384-298l384-298 384 298c-128.375 98.958-255.974 198.693-384 298zM512 792l314-246 70 54-384 298-384-298 70-54z",Color.WHITE);
        svgGlyph.setSize(12,12);
        menuButton = new MenuButton("",svgGlyph);
        menuButton.getStyleClass().clear();
        menuButton.getStyleClass().addAll("jfx-decorator-menu");

        // customize decorator buttons
        List<JFXButton> btns = new ArrayList<>();
        if (fullScreen) {
            if(resFullScreen!=null) {
                menuButton.getItems().add(menuItemFull);
            }
            btns.add(btnFull);
            HBox.setMargin(btnFull, new Insets(0, 30, 0, 0));
        }
        if (min) {
            if(resMinimize!=null){
                menuButton.getItems().add(menuItemMin);
            }
            btns.add(btnMin);
        }
        if (max) {
            if(resMaximize!=null) {
                menuButton.getItems().add(menuItemMax);
            }
            btns.add(btnMax);
            // maximize/restore the window on header double click
            buttonsContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
                if (mouseEvent.getClickCount() == 2) {
                    btnMax.fire();
                }
            });
        }
        if(resClose!=null) {
            menuButton.getItems().add(new SeparatorMenuItem());
            menuButton.getItems().add(menuItemClose);
        }
        btns.add(btnClose);

        text = new Text();
        text.getStyleClass().addAll("jfx-decorator-text", "title", "jfx-decorator-title");
        text.setFill(Color.WHITE);
        text.textProperty().bind(title); //binds the Text's text to title
        title.bind(primaryStage.titleProperty()); //binds title to the primaryStage's title



        graphicContainer = new HBox();
        graphicContainer.setPickOnBounds(false);
        graphicContainer.setAlignment(Pos.CENTER_LEFT);
        graphicContainer.getChildren().setAll(menuButton);

        HBox graphicTextContainer = new HBox(graphicContainer, text);
        graphicTextContainer.getStyleClass().add("jfx-decorator-title-container");
        graphicTextContainer.setAlignment(Pos.CENTER_LEFT);
        graphicTextContainer.setPickOnBounds(false);
        HBox.setHgrow(graphicTextContainer, Priority.ALWAYS);
        HBox.setMargin(graphicTextContainer, new Insets(0, 4, 0, 4));
        HBox.setMargin(graphicContainer, new Insets(0, 4, 0, 0));

        buttonsContainer.getChildren().setAll(graphicTextContainer);
        buttonsContainer.getChildren().addAll(btns);
        buttonsContainer.setMinWidth(180);
        contentPlaceHolder.getStyleClass().add("jfx-decorator-content-container");
        contentPlaceHolder.setMinSize(0, 0);
        StackPane clippedContainer = new StackPane(node);
        contentPlaceHolder.getChildren().add(clippedContainer);
        ((Region) node).setMinSize(0, 0);
        VBox.setVgrow(contentPlaceHolder, Priority.ALWAYS);
        this.getStyleClass().add("resize-border");
        this.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(4, 4, 4, 4))));
        // BINDING

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(clippedContainer.widthProperty());
        clip.heightProperty().bind(clippedContainer.heightProperty());
        clippedContainer.setClip(clip);

        this.getChildren().addAll(buttonsContainer, contentPlaceHolder);
    }

    /**
     * disable icon menu
     * @param tf
     */
    public void disableMenu(boolean tf){
        if(tf){
            menuButton.setOnShown(event -> menuButton.hide());
        }else{
            menuButton.setOnShown(null);
        }
    }

    private void maximize(SVGGlyph resizeMin, SVGGlyph resizeMax,SVGGlyph resizeMin2, SVGGlyph resizeMax2) {
        if (!isCustomMaximize()) {
            if(!primaryStage.isMaximized()){
                originalBox = new BoundingBox(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight());
            }
            primaryStage.setMaximized(!primaryStage.isMaximized());
            maximized = primaryStage.isMaximized();
            if (primaryStage.isMaximized()) {
                menuItemMax.setGraphic(resizeMin2);
                btnMax.setGraphic(resizeMin);
                if(resRestore !=null) {
                    menuItemMax.setText(resRestore);
                    btnMax.setTooltip(new Tooltip(resRestore));
                }
            } else {
                menuItemMax.setGraphic(resizeMax2);
                btnMax.setGraphic(resizeMax);
                if(resMaximize!=null) {
                    menuItemMax.setText(resMaximize);
                    btnMax.setTooltip(new Tooltip(resMaximize));
                }
            }
        } else {
            //When the mouse reaches the upper edge of the screen to trigger the restoration,
            // set the y-axis to center and restore the height, otherwise it will trigger the zoom size
            if(isBottomMaximized){
                double _y = (maximizedBox.getHeight()- initBox.getHeight())/2;
                setStageHeight(initBox.getHeight());
                setStageY(_y<0?0:_y);
                isBottomMaximized = false;
                return;
            }

            if (!maximized) {
                // store original bounds
                originalBox = new BoundingBox(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight());
                initMaximizedBox();
                // maximized the stage
                setStageX(maximizedBox.getMinX());
                setStageY(maximizedBox.getMinY());
                setStageWidth(maximizedBox.getWidth());
                setStageHeight(maximizedBox.getHeight());
                menuItemMax.setGraphic(resizeMin2);
                btnMax.setGraphic(resizeMin);
                if(resRestore !=null) {
                    menuItemMax.setText(resRestore);
                    btnMax.setTooltip(new Tooltip(resRestore));
                }
            } else {
                // restore stage to its original size
                setStageX(originalBox.getMinX());
                setStageY(originalBox.getMinY());
                setStageWidth(originalBox.getWidth());
                setStageHeight(originalBox.getHeight());
                menuItemMax.setGraphic(resizeMax2);
                btnMax.setGraphic(resizeMax);
                if(resMaximize!=null) {
                    menuItemMax.setText(resMaximize);
                    btnMax.setTooltip(new Tooltip(resMaximize));
                }

                //When the mouse reaches the upper edge of the screen to trigger the restoration,
                // set the y-axis to center and restore the height, otherwise it will trigger the zoom size
                if(isTopMaximized){
                    double _y = (maximizedBox.getHeight()- lastStageHeight)/2;
                    setStageHeight(lastStageHeight);
                    setStageY(_y<0?0:_y);
                    isTopMaximized = false;
                }
            }
            maximized = !maximized;
        }
    }

    private void showDragCursorOnScreen(MouseEvent mouseEvent) {
        if (primaryStage.isMaximized() || primaryStage.isFullScreen() || maximized || isTopMaximized) {
            btnMax.fire();
            initXOffset = mouseEvent.getSceneX() * originalBox.getWidth() / maximizedBox.getWidth();
            setPrimaryStageX(mouseEvent.getScreenX() - initXOffset);
            return;
        }

        if(isLeftMaximized || isRightMaximized || isBottomMaximized){
            if(isBottomMaximized){
                if(allowMove){
                    initXOffset = mouseEvent.getSceneX();
                }
                initYOffset = mouseEvent.getSceneY();
                setPrimaryStageY(initYOffset);
            }
            setPrimaryStageHeight(lastStageHeight);
            initXOffset = initXOffset / primaryStage.getWidth() * lastStageWidth;
            setPrimaryStageX(mouseEvent.getScreenX() - initXOffset);
            setPrimaryStageWidth(lastStageWidth);
            return;
        }

        setPrimaryStageX(mouseEvent.getScreenX() - initXOffset);
        setPrimaryStageY(mouseEvent.getScreenY() - initYOffset);
    }

    /**
     * The primary Stage triggers the mouse release event to process the zoom window size and position
     * @param mouseEvent
     */
    private void handleReleasedEventOnDecoratorPane(MouseEvent mouseEvent) {
        if(!isDragging){
            return;
        }
        isDragging = false;
        if(isLeftMaximized){
            if(setPrimaryStageHeight(maximizedBox.getHeight())){
                setPrimaryStageY(0);
            }
            if(setPrimaryStageWidth(maximizedBox.getWidth()/2)){
                setPrimaryStageX(0);
            }
        }else if(isRightMaximized){
            if(setPrimaryStageHeight(maximizedBox.getHeight())){
                setPrimaryStageY(0);
            }
            if(setPrimaryStageWidth(maximizedBox.getWidth()/2)){
                setPrimaryStageX(maximizedBox.getWidth()/2);
            }
        }else if(isBottomMaximized){
            if(setPrimaryStageHeight(maximizedBox.getHeight())){
                setPrimaryStageY(0);
            }
            if(setPrimaryStageWidth(primaryStage.getWidth())){
            }
            if(!allowMove){
                //Restore the last recorded height to the height before scaling, used to drag and restore
                lastStageHeight = initBox.getHeight();
            }
        }else if(isTopMaximized){
            lastStageWidth = primaryStage.getWidth();
            lastStageHeight = primaryStage.getHeight();
            btnMax.fire();
        }

        if(onMouseMoveEdgeListener !=null){
            onMouseMoveEdgeListener.onMouseMoveEdge(null,mouseEvent);
        }
    }

    /**
     * Display edge corresponding gestures (Scene.X-Y judgment)
     * @param mouseEvent
     */
    private void showDragCursorOnBorders(MouseEvent mouseEvent) {
        // maximized mode does not support resize
        if (primaryStage.isMaximized() || primaryStage.isFullScreen() || maximized || !primaryStage.isResizable()) {
            this.setCursor(Cursor.DEFAULT);
            return;
        }

        double x = Math.ceil(Double.parseDouble(df.format(mouseEvent.getSceneX())));
        double y = mouseEvent.getSceneY();
        double width = this.getWidth();
        double height = this.getHeight();
        double rightInset = this.snappedRightInset()/2;
        double leftInset = this.snappedLeftInset()/2;
        double topInset = this.snappedTopInset()/2;
        double bottomInset = this.snappedBottomInset()/2;
        double right = Math.ceil(width - rightInset);
        double bottom = Math.ceil(height - bottomInset);

        Cursor cursorType = Cursor.DEFAULT;
        if (this.getBorder() != null && this.getBorder().getStrokes().size() > 0) {
            if(y < topInset){
                if(x < leftInset){
                    cursorType = Cursor.NW_RESIZE;
                } else if (x > right) {
                    cursorType = Cursor.NE_RESIZE;
                } else {
                    cursorType = Cursor.N_RESIZE;
                }
            } else if (y > bottom) {
                if(x < leftInset){
                    cursorType = Cursor.SW_RESIZE;
                } else if (x > right) {
                    cursorType = Cursor.SE_RESIZE;
                } else {
                    cursorType = Cursor.S_RESIZE;
                }
            } else if(x < leftInset){
                if(allowMove){
                    buttonsContainer.setCursor(Cursor.W_RESIZE);
                }
                cursorType = Cursor.W_RESIZE;
            } else if (x > right) {
                cursorType = Cursor.E_RESIZE;
            }else{
                if(allowMove){
                    buttonsContainer.setCursor(Cursor.MOVE);
                }
            }
        }
        this.setCursor(cursorType);
//        System.out.println("x:"+x+",y="+y+",w="+width+",h="+height+",r="+right+",b="+bottom+"-------->"+this.getCursor().toString());
    }

    /**
     * The primary Stage triggers the mouse drag event to process the window size and position
     * @param mouseEvent
     */
    private void handleDragEventOnDecoratorPane(MouseEvent mouseEvent) {
        this.isDragging = true;
        Cursor cursor = this.getCursor();
        mouseEvent.consume();
        if (!mouseEvent.isPrimaryButtonDown() || (initXOffset == -1 && initYOffset == -1)) {
            return;
        }
        /*
         * Long press generates drag event!
         */
        if (primaryStage.isFullScreen() || mouseEvent.isStillSincePress() || primaryStage.isMaximized() || maximized) {
            return;
        }
        if(mouseEvent.getScreenX()==0){
            if(allowMove){
                //Move the mouse to the left edge of the screen and widen to half
                isLeftMaximized = true;
                if(onMouseMoveEdgeListener !=null){
                    onMouseMoveEdgeListener.onMouseMoveEdge(Side.LEFT,mouseEvent);
                }
            }
        }else if(mouseEvent.getScreenX()>= maximizedBox.getWidth()-1){
            if(allowMove){
                //Move the mouse to the right edge of the screen and widen to half
                isRightMaximized = true;
                if(onMouseMoveEdgeListener !=null){
                    onMouseMoveEdgeListener.onMouseMoveEdge(Side.RIGHT,mouseEvent);
                }
            }
        }else if(mouseEvent.getScreenY()==0){
            if(!allowMove){
                //Move the mouse to the upper edge of the screen and pull it up
                isBottomMaximized = true;
                if(onMouseMoveEdgeListener !=null){
                    onMouseMoveEdgeListener.onMouseMoveEdge(Side.BOTTOM,mouseEvent);
                }
            }else{
                //Move the mouse to the upper edge of the screen and place it in the largest window
                isTopMaximized = true;
                if(onMouseMoveEdgeListener !=null){
                    onMouseMoveEdgeListener.onMouseMoveEdge(Side.TOP,mouseEvent);
                }
            }
        }else if(mouseEvent.getScreenY()>= maximizedBox.getHeight()-1){
            //Move the mouse to the bottom edge of the screen and pull it up
            isBottomMaximized = true;
            if(onMouseMoveEdgeListener !=null){
                onMouseMoveEdgeListener.onMouseMoveEdge(Side.BOTTOM,mouseEvent);
            }
        }else{
            //restore the mouse to the edge of the screen record
            if(isTopMaximized || isLeftMaximized || isRightMaximized || isBottomMaximized){
                isTopMaximized = isBottomMaximized = isLeftMaximized = isRightMaximized = false;
                if(onMouseMoveEdgeListener !=null){
                    onMouseMoveEdgeListener.onMouseMoveEdge(null,mouseEvent);
                }
            }
        }

        double newX = mouseEvent.getScreenX();
        double newY = mouseEvent.getScreenY();

        double deltax = newX - initX;
        double deltay = newY - initY;

        if (Cursor.E_RESIZE.equals(cursor)) {
            setPrimaryStageWidth(initBox.getWidth() + deltax);
        } else if (Cursor.NE_RESIZE.equals(cursor)) {
            if (setPrimaryStageHeight(initBox.getHeight() - deltay)) {
                setPrimaryStageY(initBox.getMinY() + deltay);
            }
            setPrimaryStageWidth(initBox.getWidth() + deltax);
        } else if (Cursor.SE_RESIZE.equals(cursor)) {
            setPrimaryStageWidth(initBox.getWidth() + deltax);
            setPrimaryStageHeight(initBox.getHeight() + deltay);
        } else if (Cursor.S_RESIZE.equals(cursor)) {
            setPrimaryStageHeight(initBox.getHeight() + deltay);
        } else if (Cursor.W_RESIZE.equals(cursor)) {
            if (setPrimaryStageWidth(initBox.getWidth() - deltax)) {
                setPrimaryStageX(initBox.getMinX() + deltax);
            }
        } else if (Cursor.SW_RESIZE.equals(cursor)) {
            if (setPrimaryStageWidth(initBox.getWidth() - deltax)) {
                setPrimaryStageX(initBox.getMinX() + deltax);
            }
            setPrimaryStageHeight(initBox.getHeight() + deltay);
        } else if (Cursor.NW_RESIZE.equals(cursor)) {
            if (setPrimaryStageWidth(initBox.getWidth() - deltax)) {
                setPrimaryStageX(initBox.getMinX() + deltax);
            }
            if (setPrimaryStageHeight(initBox.getHeight() - deltay)) {
                setPrimaryStageY(initBox.getMinY() + deltay);
            }
        } else if (Cursor.N_RESIZE.equals(cursor)) {
            if (setPrimaryStageHeight(initBox.getHeight() - deltay)) {
                setPrimaryStageY(initBox.getMinY() + deltay);
            }
        } else if (allowMove) {
            setPrimaryStageX(mouseEvent.getScreenX() - initXOffset);
            setPrimaryStageY(mouseEvent.getScreenY() - initYOffset);
        }
    }

    private void initMaximizedBox(){
        Rectangle2D bounds;
        ObservableList<Screen> screen = Screen.getScreensForRectangle(primaryStage.getX(),
                primaryStage.getY(),
                primaryStage.getWidth(),
                primaryStage.getHeight());
        if(screen!=null&&screen.size()>0){
            // get the max stage bounds
            bounds = screen.get(0).getVisualBounds();
        }else{
            bounds= Screen.getPrimary().getVisualBounds();
        }
        // get the max stage bounds
        maximizedBox = new BoundingBox(bounds.getMinX(),
                bounds.getMinY(),
                bounds.getWidth(),
                bounds.getHeight());
    }
    private void updateInitMouseValues(MouseEvent mouseEvent) {
        if (primaryStage.isMaximized()||maximized) {
            return;
        }
        mouseEvent.consume();

        initBox = new BoundingBox(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight());
        initX = mouseEvent.getScreenX();
        initY = mouseEvent.getScreenY();
        initXOffset = mouseEvent.getSceneX();
        initYOffset = mouseEvent.getSceneY();
    }

    private boolean setPrimaryStageWidth(double width) {
        width = Double.parseDouble(df.format(width));
        lastStageWidth = primaryStage.getWidth();
        double minWidth = buttonsContainer.getMinWidth() + this.snappedLeftInset() + this.snappedRightInset();
        if (width >= primaryStage.getMinWidth() && width >= minWidth) {
            setStageWidth(width);
        } else if (width >= primaryStage.getMinWidth() && width <= minWidth) {
            setStageWidth(minWidth);
        }else {
            return false;
        }
        return true;
    }

    private boolean setPrimaryStageHeight(double height) {
        height = Double.parseDouble(df.format(height));
        double minHeight = buttonsContainer.getHeight() + this.snappedTopInset() + this.snappedBottomInset();
        lastStageHeight = primaryStage.getHeight();
        if (height >= primaryStage.getMinHeight() && height >= minHeight) {
            setStageHeight(height);
        } else if (height >= primaryStage.getMinHeight() && height <= minHeight) {
            setStageHeight(minHeight);
        }else{
            return false;
        }
        return true;
    }

    private void setPrimaryStageX(double x) {
        x = Double.parseDouble(df.format(x));
        double minWidth = buttonsContainer.getMinWidth() + this.snappedLeftInset() + this.snappedRightInset();
        double minX = initX + initBox.getWidth() -minWidth;
        if((x>=minX||primaryStage.getWidth()==minWidth)&&!allowMove){
            setStageX(initX + initBox.getWidth() -minWidth - initXOffset);
        }else{
            setStageX(x);
        }
    }

    private void setPrimaryStageY(double y) {
        y = Double.parseDouble(df.format(y));
        double minHeight = buttonsContainer.getHeight() + this.snappedTopInset() + this.snappedBottomInset();
        if((y>= initY + initBox.getHeight() -minHeight||primaryStage.getHeight()==minHeight)&&!allowMove){
            setStageY(initY + initBox.getHeight() -minHeight - initYOffset);
        }else{
            setStageY(y);
        }
    }

    /**
     * set a speficed runnable when clicking on the close button
     *
     * @param onCloseButtonAction runnable to be executed
     */
    public void setOnCloseButtonAction(Runnable onCloseButtonAction) {
        this.onCloseButtonAction.set(onCloseButtonAction);
    }

    /**
     * this property is used to replace JavaFX maximization
     * with a custom one that prevents hiding windows taskbar when
     * the JFXDecorator is maximized.
     *
     * @return customMaximizeProperty whether to use custom maximization or not.
     */
    public final BooleanProperty customMaximizeProperty() {
        return this.customMaximize;
    }

    /**
     * @return whether customMaximizeProperty is active or not
     */
    public final boolean isCustomMaximize() {
        return this.customMaximizeProperty().get();
    }

    /**
     * set customMaximize property
     *
     * @param customMaximize
     */
    public final void setCustomMaximize(final boolean customMaximize) {
        this.customMaximizeProperty().set(customMaximize);
    }

    /**
     * @param maximized
     */
    public void setMaximized(boolean maximized) {
        if (this.maximized != maximized) {
            Platform.runLater(() -> {
                btnMax.fire();
            });
        }
    }

    /**
     * will change the decorator content
     *
     * @param content
     */
    public void setContent(Node content) {
        this.contentPlaceHolder.getChildren().setAll(content);
    }

    /**
     * will set the title
     *
     * @param text
     * @deprecated Use {@link JFXDecorator#setTitle(String)} instead.
     */
    public void setText(String text) {
        setTitle(text);
    }

    /**
     * will get the title
     *
     * @deprecated Use {@link JFXDecorator#setTitle(String)} instead.
     */
    public String getText() {
        return getTitle();
    }

    public String getTitle() {
        return title.get();
    }

    /**
     * By default this title property is bound to the primaryStage's title property.
     * <p>
     * To change it to something else, use <pre>
     *     {@code jfxDecorator.titleProperty().unbind();}</pre> first.
     */
    public StringProperty titleProperty() {
        return title;
    }

    /**
     * If you want the {@code primaryStage}'s title and the {@code JFXDecorator}'s title to be different, then
     * go ahead and use this method.
     * <p>
     * By default, this title property is bound to the {@code primaryStage}'s title property-so merely setting the
     * {@code primaryStage}'s title, will set the {@code JFXDecorator}'s title.
     */
    public void setTitle(String title) {
        this.title.unbind();
        this.title.set(title);
    }

    public void setGraphic(Node node) {
        if (graphic != null) {
            graphicContainer.getChildren().remove(graphic);
        }
        if (node != null) {
            graphicContainer.getChildren().add(0, node);
        }
        graphic = node;
    }

    public Node getGraphic() {
        return graphic;
    }

    public void setIcon(Node node) {
        if(menuButton!=null){
            menuButton.setGraphic(node);
        }
    }
}
