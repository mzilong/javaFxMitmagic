package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;
import sample.controller.annotation.FxmlPath;
import sample.controller.base.BaseController;
import sample.locale.ControlResources;
import sample.tools.dialog.ProgressTask;
import sample.utils.ThreadPoolUtils;
import sample.utils.javafx.FxIntent;
import sample.utils.javafx.JFXUtils;

import java.net.URL;
import java.util.ResourceBundle;

@FxmlPath("/sample/fxml/")
public class WelcomeController extends BaseController{

    public Label lab;
    public ProgressIndicator progressIndicator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public String initTitle() {
        return ControlResources.getString("Title");
    }

    @Override
    public String initIcon() {
        return null;
    }

    @Override
    public void onShowing(WindowEvent event) {
        super.onShowing(event);
        lab.setOnMouseEntered((MouseEvent e) -> {
            lab.setFont(new Font(20));
        });

        lab.setOnMouseExited((MouseEvent e) -> {
            lab.setFont(new Font(12));
        });

        runLoadData();
    }
    ProgressTask task;
    private void runLoadData(){
        task = new ProgressTask() {
            @Override
            protected void execute() {
                int max = 100;
                for (int i = 1; i <= max; i++) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        if (isCancelled()) {
                            updateMessage("Cancelled");
                            break;
                        }

                    }
                    updateProgress(i, max);
                }
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage("Done!");
                JFXUtils.runUiThread(() -> {
                    getIntent().closePrimaryStage();
                    FxIntent intent = new FxIntent(MainController.class);
                    intent.start();
                });
            }
        };
        progressIndicator.progressProperty().bind(task.progressProperty());
        ThreadPoolUtils.runDelayTime(task,1000);
    }

    @FXML
    public void onLabelClick() {
    }
}
