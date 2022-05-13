package sample.controller;

import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.controlsfx.control.BreadCrumbBar;
import sample.JFXResources;
import sample.controller.annotation.FxmlPath;
import sample.controller.base.BaseController;
import sample.event.BaseEvent;
import sample.locale.ControlResources;
import sample.tools.PreferencesTools;
import sample.tools.dialog.DialogBuilder;
import sample.utils.ClipboardUtils;
import sample.utils.ReflectUtils;
import sample.utils.javafx.FxIntent;
import sample.utils.javafx.FxStyleUtils;
import sample.utils.javafx.JFXUtils;
import sample.view.Browser;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author: mzl
 * Description:TestController
 * Data: 2020/8/18 16:29
 */

@FxmlPath("fxml")
public class TestController extends BaseController {

    public AnchorPane tAnchorPane,tAnchorPane2;
    public Browser broeser;
    public ColorPicker colorPicker;
    public BreadCrumbBar breadCrumbBar;
    public TreeView<String> treeView;
    public Menu fontMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //这里一定要有判断，不然会有空指针异常
        String title = ReflectUtils.reflect(this).method("initTitle").get();
    }

    @Override
    public String initTitle(){
        return ControlResources.getString("Title");
    }

    @Override
    public String initIcon() {
        return "ic_launcher.png";
    }

    private void onCloseRequest(Event event) {
        event.consume();
        String color = PreferencesTools.getFxBasePref();
        new DialogBuilder(getIntent().getPrimaryStage()).setTitle(ControlResources.getString("Dialog.Tips"))
                .setMessage(ControlResources.getString("Dialog.Message"))
                .setPositiveBtn(ControlResources.getString("Dialog.yes.button"), () -> {
                    JFXUtils.runUiThread(() -> getIntent().closePrimaryStage());
                    Platform.exit();
                }, "").setNegativeBtn(ControlResources.getString("Dialog.cancel.button"), "").addListener(pane -> {
                    FxStyleUtils.setBase(pane,JFXUtils.colorToWebColor(Color.valueOf(color).brighter()));
                }).create();
    }

    @Override
    public void onEvent(BaseEvent event) {
        Object obj = event.getData();
        System.out.println("BaseEvent.MainController:"+obj);
    }

    @Override
    public void onShowing(WindowEvent event) {
        super.onShowing(event);
        Stage primaryStage = getIntent().getPrimaryStage();
        primaryStage.setOnCloseRequest(windowEvent -> onCloseRequest(windowEvent));
        //监听最大化
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {});

        //添加系统托盘
        JFXUtils.addSystemTray(initTitle(), "ic_launcher_16x16.png",primaryStage);

        //给Scene添加快捷键
        ObservableMap<KeyCombination, Runnable> observableMap= getIntent().getPrimaryStage().getScene().getAccelerators();
        observableMap.put(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN), new Runnable() {
            @Override
            public void run() {
                ClipboardUtils.addClipboardContent(tAnchorPane);
            }
        });
        observableMap.put(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), new Runnable() {
            @Override
            public void run() {
                ClipboardUtils.setClipboardContent(DataFormat.IMAGE,JFXUtils.getImg("ic_launcher.png"));
            }
        });

        ClipboardUtils.setDragDroppedPane(tAnchorPane);
        ClipboardUtils.setDragDetectedNode(broeser);

        broeser.load(JFXResources.getResource("webview/index.html").toExternalForm());
//        broeser.load("http://47.106.241.58:8013/");

        colorPicker.setValue(Color.valueOf(PreferencesTools.getFxBasePref()));
        FxStyleUtils.setColorLabelVisible(colorPicker,false);
        colorPicker.getCustomColors().addAll(
                Color.web("#0091ea"),
                Color.web("#3C3F41")
        );
        TreeItem<String> model = BreadCrumbBar.buildTreeModel("Hello", "World", "This", "is", "cool");
        breadCrumbBar.setSelectedCrumb(model);
        breadCrumbBar.setAutoNavigationEnabled(false);
//        JFXUtils.setBackground(breadCrumbBar,colorPicker.getValue());
        treeView.setEditable(true);
        treeView.setCellFactory(TextFieldTreeCell.forTreeView());
        treeView.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2){
                TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
                System.out.println("Selected Text : " + item.getValue());
            }
        });
        ToggleGroup tg = new ToggleGroup();
        fontMenu.getItems().addAll(
                buildFontRadioMenuItem("System Default", null, 0, tg),
                buildFontRadioMenuItem("Mac (13px)", "Lucida Grande", 13, tg),
                buildFontRadioMenuItem("Windows 100% (12px)", "Segoe UI", 12, tg),
                buildFontRadioMenuItem("Windows 125% (15px)", "Segoe UI", 15, tg),
                buildFontRadioMenuItem("Windows 150% (18px)", "Segoe UI", 18, tg),
                buildFontRadioMenuItem("Linux (13px)", "Lucida Sans", 13, tg),
                buildFontRadioMenuItem("Embedded Touch (22px)", "Arial", 22, tg),
                buildFontRadioMenuItem("Embedded Small (9px)", "Arial", 9, tg)
        );
    }

    public RadioMenuItem buildFontRadioMenuItem(String name, final String in_fontName, final int in_fontSize, ToggleGroup tg) {
        RadioMenuItem radioMenuItem= new RadioMenuItem(name);
        radioMenuItem.setOnAction(event -> {
            if(in_fontSize!=0){
                FxStyleUtils.setFont(getIntent().getRoot(),in_fontSize + "px \"" + in_fontName + "\"");
            }else{
                FxStyleUtils.removeFont(getIntent().getRoot());
            }
        });
        if(in_fontSize!=0){
            radioMenuItem.setStyle("-fx-font: " + in_fontSize + "px \"" + in_fontName + "\";");
        }
        radioMenuItem.setToggleGroup(tg);
        return radioMenuItem;
    }

    public void openSerialPort(ActionEvent actionEvent) {
        FxIntent intent = new FxIntent(SerialPortController.class, StageStyle.UTILITY);
        intent.addData("data", "我是传过来的数据");
        intent.start();
    }

    public void selectBgColor(ActionEvent actionEvent) {
        String color = JFXUtils.colorToWebColor(colorPicker.getValue());
        PreferencesTools.setFxBasePref(color);
        FxStyleUtils.setBase(getIntent().getRoot(),color);
//        JFXUtils.setBackground(breadCrumbBar,colorPicker.getValue());
    }

    public void onExit(ActionEvent actionEvent) {
        JFXUtils.runUiThread(() -> getIntent().closePrimaryStage());
        Platform.exit();
    }
}
