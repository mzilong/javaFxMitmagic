package sample;

import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FxButtonTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
        FlowPane main = new FlowPane();
        main.setVgap(20);
        main.setHgap(20);

        main.getChildren().add(new Button("Java Button"));
        JFXButton jfoenixButton = new JFXButton("JFoenix Button");
        jfoenixButton.setButtonType(JFXButton.ButtonType.FLAT);
        main.getChildren().add(jfoenixButton);

        JFXButton button = new JFXButton("RAISED BUTTON");
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.getStyleClass().add("custom-jfx-button-raised");
        main.getChildren().add(button);

        JFXButton button1 = new JFXButton("DISABLED");
        button1.setDisable(true);
        main.getChildren().add(button1);

        StackPane pane = new StackPane();
        pane.getChildren().add(main);
        StackPane.setMargin(main, new Insets(100));
        pane.setStyle("-fx-background-color:WHITE");

        final Scene scene = new Scene(pane, 800, 200);
        scene.getStylesheets().addAll(
                JFoenixResources.class.getResource("css/jfoenix-design.css").toExternalForm(),
                Main.class.getResource("/css/jfoenix-main-demo.css").toExternalForm()
                );
        primaryStage.setTitle("JFX Button Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}