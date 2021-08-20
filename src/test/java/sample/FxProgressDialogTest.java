package sample;

import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.tools.dialog.FxProgressDialog;
import sample.tools.dialog.ProgressTask;

import static sample.tools.helper.LayoutHelper.button;
import static sample.tools.helper.LayoutHelper.hbox;

public class FxProgressDialogTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Node hbox= hbox(50, 10, Pos.CENTER,
                button("展示数字进度", () -> startProgress1(primaryStage, false)),
                button("展示百分比进度", () -> startProgress1(primaryStage, true)),
                button("不展示进度", () -> startProgress2(primaryStage))
        );
        JFXDecorator jfxDecorator = new JFXDecorator(primaryStage,hbox);
        primaryStage.setScene(new Scene(jfxDecorator));
        primaryStage.show();
    }

    private void startProgress2(Stage primaryStage) {
        ProgressTask task = new ProgressTask() {
            @Override
            protected void execute(){
                // 只要不调用 updateProgress()，就不会展示进度
                try {
                    Thread.sleep(3000);
                    System.out.println("执行完毕。");
                } catch (InterruptedException e) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                    }
                }
            }
        };

        FxProgressDialog dialog = FxProgressDialog.create(primaryStage, task, "请稍候...");
        dialog.show();
    }

    private void startProgress1(Stage primaryStage, boolean showPercentage) {

        ProgressTask progressTask = new ProgressTask() {
            @Override
            protected void execute() {
                int current = 0, max = 100;
                while (current < max) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        break;
                    }
                    updateProgress(current, max);
                    current += 1;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        if (isCancelled()) {
                            updateMessage("Cancelled");
                            break;
                        }
                    }
                }
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("failed!");
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                updateMessage("Cancelled!");
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage("Succeeded!");
            }

        };

        progressTask.setOnCancelled(event -> System.out.println("用户取消了"));

        FxProgressDialog fxProgressDialog =
                FxProgressDialog.create(primaryStage, progressTask, "正在执行某个重要任务...");

        fxProgressDialog.setShowAsPercentage(showPercentage);
        fxProgressDialog.show();
    }
}