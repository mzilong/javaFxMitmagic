package sample.tools.dialog;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import sample.utils.javafx.FxmlUtils;
import sample.utils.javafx.JFXUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义对话框
 *
 * @param <T> 对话框的 Controller 类型
 */
public class FxDialog<T> {

    private boolean modal = true;

    private boolean resizable = false;

    private double prefWidth;

    private double prefHeight;

    private boolean closeable = true;

    private Window owner;

    private String bodyFxmlPath;

    private Parent body;

    private String title;

    private Image icon;

    private ButtonType[] buttonTypes;

    private Map<ButtonType, BiConsumer<ActionEvent, Stage>> buttonHandlers = new HashMap<>();

    private Consumer<Stage> withStage;

    private ResourceBundle resourceBundle;

    public FxDialog<T> setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public FxDialog<T> setPrefHeight(double prefHeight) {
        this.prefHeight = prefHeight;
        return this;
    }

    public FxDialog<T> setPrefWidth(double prefWidth) {
        this.prefWidth = prefWidth;
        return this;
    }

    public FxDialog<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public FxDialog<T> setIcon(Image icon) {
        this.icon = icon;
        return this;
    }

    public FxDialog<T> setOwner(Window owner) {
        this.owner = owner;
        return this;
    }

    public FxDialog<T> setBody(Parent body) {
        this.body = body;
        return this;
    }

    public FxDialog<T> setBodyFxml(String bodyFxmlPath) {
        this.bodyFxmlPath = bodyFxmlPath;
        return this;
    }

    public FxDialog<T> setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        return this;
    }

    public FxDialog<T> setButtonTypes(ButtonType... buttonTypes) {
        this.buttonTypes = buttonTypes;
        return this;
    }

    public FxDialog<T> setModal(boolean modal) {
        this.modal = modal;
        return this;
    }

    public FxDialog<T> setCloseable(boolean closeable) {
        this.closeable = closeable;
        return this;
    }

    public FxDialog<T> withStage(Consumer<Stage> withStage) {
        this.withStage = withStage;
        return this;
    }

    public FxDialog<T> setButtonHandler(ButtonType buttonType, BiConsumer<ActionEvent, Stage> buttonHandler) {
        this.buttonHandlers.put(buttonType, buttonHandler);
        return this;
    }

    public T show() {

        if (this.bodyFxmlPath != null) {
            FXMLLoader fxmlLoader = FxmlUtils.loadFxmlFromResource(this.bodyFxmlPath, resourceBundle);

            Stage stage = createStage(fxmlLoader.getRoot());
            stage.show();
            return fxmlLoader.getController();
        }

        if (this.body != null) {
            Stage stage = createStage(this.body);
            stage.show();
            return null;
        }

        throw new RuntimeException( "bodyFxmlPath 和 body 不能都为空");
    }

    public T showAndWait() {

        if (this.bodyFxmlPath != null) {
            FXMLLoader fxmlLoader = FxmlUtils.loadFxmlFromResource(this.bodyFxmlPath, resourceBundle);

            Stage stage = createStage(fxmlLoader.getRoot());
            stage.showAndWait();
            return fxmlLoader.getController();
        }

        if (this.body != null) {
            Stage stage = createStage(this.body);
            stage.showAndWait();
            return null;
        }

        throw new RuntimeException("bodyFxmlPath 和 body 不能都为空");
    }
    double x;
    double y;
    private Stage createStage(Parent content) {
        VBox dialogContainer = new VBox(content);
        VBox.setVgrow(content, Priority.ALWAYS);

        dialogContainer.setPadding(new Insets(5));
        dialogContainer.setSpacing(5);

        Stage stage = new Stage();
        if (this.buttonTypes!=null&&this.buttonTypes.length>0){
            dialogContainer.getChildren().add(new Separator());
            dialogContainer.getChildren().add(buttonsPanel(stage));
        }
        Scene scene = new Scene(dialogContainer);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(this.resizable);

        JFXUtils.setupIcon(stage,icon);

        if (this.modal) {
            if (this.owner != null) {
                stage.initOwner(this.owner);
                stage.initModality(Modality.WINDOW_MODAL);
                adjustPosition(stage, owner);
            } else {
                stage.initModality(Modality.APPLICATION_MODAL);
            }
        }

        if (this.prefWidth > 0) {
            stage.setWidth(this.prefWidth);
        }

        if (this.prefHeight > 0) {
            stage.setHeight(this.prefHeight);
        }

        if (!this.closeable) {
            stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, Event::consume);
//            stage.initStyle(StageStyle.UNDECORATED);
//            scene.setOnMousePressed(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    x = event.getScreenX() - stage.getX();
//                    y = event.getScreenY() - stage.getY();
//                }
//            });
//            scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    stage.setX(event.getScreenX() - x);
//                    stage.setY(event.getScreenY() - y);
//                }
//            });
        }

        if (this.withStage != null) {
            this.withStage.accept(stage);
        }

        return stage;
    }

    private ButtonBar buttonsPanel(Stage stage) {
        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(
            Stream.of(this.buttonTypes)
                .map(buttonType -> createButton(buttonType, stage))
                .collect(Collectors.toList())
        );
        return buttonBar;
    }

    private Button createButton(ButtonType buttonType, Stage stage) {
        final Button button = new Button(buttonType.getText());
        final ButtonData buttonData = buttonType.getButtonData();
        ButtonBar.setButtonData(button, buttonData);
        button.setDefaultButton(buttonData.isDefaultButton());
        button.setCancelButton(buttonData.isCancelButton());
        button.setOnAction(event -> {
            BiConsumer<ActionEvent, Stage> handler = this.buttonHandlers.get(buttonType);
            if (handler != null) {
                handler.accept(event, stage);
            }
        });
        return button;
    }

    private void adjustPosition(Window dialog, Window owner) {
        dialog.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            dialog.setX(Math.max(0, owner.getX() + owner.getWidth() / 2 - dialog.getWidth() / 2));
            dialog.setY(Math.max(0, owner.getY() + owner.getHeight() / 2 - dialog.getHeight() / 2));
        });
    }
}
