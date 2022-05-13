package sample.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FileUtils;
import sample.JFXResources;
import sample.controller.annotation.FxmlPath;
import sample.controller.base.BaseController;
import sample.locale.ControlResources;
import sample.utils.ClipboardUtils;
import sample.utils.javafx.FxStyleUtils;
import sample.view.Icon;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author: mzl
 * Description:ChildController
 * Data: 2020/8/18 16:29
 */

@FxmlPath("fxml/Icon_management.fxml")
public class IconManagementController extends BaseController {

    public GridPane gpIcon;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //这里一定要有判断，不然会有空指针异常
    }

    @Override
    public String initTitle() {
        return ControlResources.getString("Title.IconManagement");
    }

    @Override
    public String initIcon() {
        return "ic_launcher.png";
    }


    private void onCloseRequest(WindowEvent event) {
    }

    @Override
    public void onShowing(WindowEvent event) {
        super.onShowing(event);
        //这里一定要有判断，不然会有空指针异常
        if (getIntent() != null) {
            String data = getIntent().getData("data");
            logger.info(data);
        }

        Stage primaryStage = getIntent().getPrimaryStage();
        //监听最大化
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {});

        primaryStage.setOnCloseRequest(windowEvent -> onCloseRequest(windowEvent));

        addNode();
    }

    private void addNode(){
        List<String> list = getCssClassTagNames("css/icon.css");
        int interval = 20;
        int count = (int) Math.ceil(Float.valueOf(list.size())/interval);
        for (int row = 0; row < count; row++) {
            for (int column = 0; column < interval; column++) {
                int index = row*interval+column;
                if(index>=list.size()){
                    break;
                }
                String ctName = list.get(index);
                Button button = new Button();
                Icon icon = new Icon(ctName);
                button.setGraphic(icon);
                Tooltip tooltip = new Tooltip();
                tooltip.setText(ctName);
                FxStyleUtils.setFontSize(icon,25);
                button.setTooltip(tooltip);
                GridPane.setConstraints(button,column,row);
                button.setOnMouseClicked(mouseEvent -> {
                    if(mouseEvent.isControlDown()){
                        ClipboardUtils.setClipboardContent(DataFormat.PLAIN_TEXT,ctName);
                    }
                });
                gpIcon.getChildren().add(button);
            }
        }
    }

    private List<String> getCssClassTagNames(String cssPath){
        List<String> list = new ArrayList<>();
        try {
            String string = FileUtils.fileRead(new File(JFXResources.getResource("css/icon.css").toURI()).toString());
            string = string.replace(" {","").replace("}","");
            String[] strArr= string.split("\r\n\r\n\r\n");
            for (int i = 1; i < strArr.length; i++) {
                String temp = strArr[i];
                list.add(temp.split("\r\n")[0].replace(".",""));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
