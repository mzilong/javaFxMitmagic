package sample.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import javafx.util.converter.FloatStringConverter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.BreadCrumbBar;
import sample.animation.AnimationType;
import sample.animation.AnimationUtils;
import sample.controller.annotation.FxmlPath;
import sample.controller.base.BaseController;
import sample.event.BaseEvent;
import sample.locale.ControlResources;
import sample.model.BaseItem;
import sample.model.FontItem;
import sample.tools.BaseGlobal;
import sample.tools.CRC16M;
import sample.tools.PreferencesTools;
import sample.tools.dialog.DialogBuilder;
import sample.tools.dialog.Message;
import sample.utils.DataUtils;
import sample.utils.SerialPortParameter;
import sample.utils.SerialPortTool;
import sample.utils.ThreadPoolUtils;
import sample.utils.javafx.FxIntent;
import sample.utils.javafx.FxStyleUtils;
import sample.utils.javafx.JFXUtils;
import sample.view.Icon;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Pattern;

/**
 * @author: mzl
 * Description:MainController
 * Data: 2020/8/18 16:29
 */

@FxmlPath("fxml")
public class MainController extends BaseController {

    public ColorPicker colorPicker;
    public BreadCrumbBar<Object> breadCrumbBar;
    public TreeView<BaseItem<Byte>> treeView;
    public ComboBox<FontItem> cbFont;
    public ComboBox<BaseItem<Locale>> cbLanguage;

    public StackPane container;
    public AnchorPane parentContainer, parentResult,
            parentParentNode, parentNodeManagement, parentChildNode,parentCurrentAndVoltageCalibration,parentMacConfig;

    public ComboBox<String> cbPortName;
    public ComboBox<Integer> cbBaudRate;
    public ComboBox<Integer> cbDataBits;
    public ComboBox<Float> cbStopBits;
    public ComboBox<String> cbParity;
    public ComboBox<String> cbFlowcontrol;
    public ComboBox<Integer> cbRequestTime;
    public ComboBox<Integer> cbRequestCount;
    public ComboBox<Integer> cbRequestInterval;

    public TextArea textArea,textAreaShow,textAreaRS;
    public Button btnSend;
    public Button btnClear;
    public Button btnCopy;
    public Button btnPaste;
    public Button btnSelectAll;
    public Button btnClearAll;

    public Button btnConfigurePort;
    public Button btnStart;
    public Button btnStop;
    public Button btnOpenSerialPort;
    public Button btnOpenCalculator;
    public Button btnOpenIcon;
    public TextField tfDestinationAddress;
    public TextField tfSourceAddress;
    public TextField tfParentNode;
    public TextArea labResult;
    public TextArea labParentNodeResult;
    public TextArea labNodeResult;
    public TextArea labChlidNodeResult;
    public TextField tfStartNum,tfPageNum;
    public GridPane gpChildNode;
    public VBox vboxMain;
    public Button btnOpenAscii;
    public TextField tfACurrentNum,tfBCurrentNum,tfCCurrentNum,tfNCurrentNum;
    public TextField tfAVoltageNum,tfBVoltageNum,tfCVoltageNum;
    public TextField tfPassword;
    public TextArea labCalibrationResult;
    public TextArea labMacResult;
    public TextField tfMacPassword,tfMac;
    public TextField tfACurrentLsat,tfBCurrentLsat,tfCCurrentLsat,tfNCurrentLsat;
    public TextField tfAVoltageLsat,tfBVoltageLsat,tfCVoltageLsat;
    public Button btnCurrentCalculation,btnVoltageCalculation;
    public ComboBox<BaseItem<String>> cbSensorType;
    public Label lbSensorTypeTip;


    private SerialPortParameter serialPortParameter;
    private SerialPort serialPort;

    private int timeout = 3000;
    private BaseItem<Byte> curBaseItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serialPortParameter = new SerialPortParameter("");
        //这里一定要有判断，不然会有空指针异常
//        String title = ReflectUtils.reflect(this).method("initTitle").get();
    }

    @Override
    public String initTitle() {
        return ControlResources.getString("Title") + BaseGlobal.versions;
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
                }, "").setNegativeBtn(ControlResources.getString("Dialog.cancel.button"), "").addListener(pane ->
            FxStyleUtils.setBase(pane, JFXUtils.colorToWebColor(Color.valueOf(color).brighter()))
        ).create();
    }

    @Override
    public void onEvent(BaseEvent event) {
        Object obj = event.getData();
        System.out.println("BaseEvent.MainController:" + obj);
    }

    @Override
    public void onShowing(WindowEvent event) {
        super.onShowing(event);
        initContainer();
        initSerialPortParameter();
        Stage primaryStage = getIntent().getPrimaryStage();
        primaryStage.setOnCloseRequest(this::onCloseRequest);
        //监听最大化
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
        });

        //添加系统托盘
        JFXUtils.addSystemTray(initTitle(), "ic_launcher_16x16.png", primaryStage);

        //给Scene添加快捷键
        ObservableMap<KeyCombination, Runnable> observableMap = getIntent().getPrimaryStage().getScene().getAccelerators();
        observableMap.put(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN), () -> btnConfigurePort.fire());

        //主题颜色设置
        colorPicker.setValue(Color.valueOf(PreferencesTools.getFxBasePref()));
        FxStyleUtils.setColorLabelVisible(colorPicker, false);
        colorPicker.getCustomColors().addAll(
                Color.web("#0091ea"),
                Color.web("#3C3F41")
        );

        //导航条
        TreeItem<Object> model = BreadCrumbBar.buildTreeModel(ControlResources.getString("Function"));
        breadCrumbBar.setSelectedCrumb(model);
        breadCrumbBar.setAutoNavigationEnabled(false);
        vboxMain.getChildren().remove(breadCrumbBar);
        //左侧功能
        TreeItem<BaseItem<Byte>> rootTreeItem = new TreeItem<>(new BaseItem<>(0,ControlResources.getString("Function"), (byte)0x00),new Icon("el-icon-s-grid"));
        TreeItem<BaseItem<Byte>> nodeTreeItem = new TreeItem<>(new BaseItem<>(-1,ControlResources.getString("ChildNodeManagement"), (byte)0x0F),new Icon("el-icon-edit-outline"));
        rootTreeItem.getChildren().addAll(
                new TreeItem<>(new BaseItem<>(1,ControlResources.getString("Current"), (byte)0x01)),
                new TreeItem<>(new BaseItem<>(2,ControlResources.getString("Voltage"), (byte)0x02)),
                new TreeItem<>(new BaseItem<>(3,ControlResources.getString("LeakageCurrent"), (byte)0x03)),
                new TreeItem<>(new BaseItem<>(4,ControlResources.getString("WetTemperature"), (byte)0x04)),
                new TreeItem<>(new BaseItem<>(5,ControlResources.getString("ReadTheStationTopology"), (byte)0x05)),
                new TreeItem<>(new BaseItem<>(7,ControlResources.getString("SendingStationAreaTopology"), (byte)0x07)),
                new TreeItem<>(new BaseItem<>(8,ControlResources.getString("ActivePower"), (byte)0x08)),
                new TreeItem<>(new BaseItem<>(9,ControlResources.getString("ReactivePower"), (byte)0x09)),
                new TreeItem<>(new BaseItem<>(10,ControlResources.getString("InspectingPower"), (byte)0x0A)),
                new TreeItem<>(new BaseItem<>(11,ControlResources.getString("PowerFactor"), (byte)0x0B)),
                new TreeItem<>(new BaseItem<>(12,ControlResources.getString("DeviceInformation"), (byte)0x0C)),
                new TreeItem<>(new BaseItem<>(13,ControlResources.getString("ParentNode"), (byte)0x0D)),
                new TreeItem<>(new BaseItem<>(14,ControlResources.getString("CableTemperature"), (byte)0x0E)),
                new TreeItem<>(new BaseItem<>(18,ControlResources.getString("CurrentAndVoltageCalibration"), (byte)0x12)),
                new TreeItem<>(new BaseItem<>(6,ControlResources.getString("ResetTheMeteringModule"), (byte)0x06)),
//                new TreeItem<>(new BaseItem<>(19,ControlResources.getString("Configure.Mac"), (byte)0x13)),
                nodeTreeItem

        );
        nodeTreeItem.getChildren().addAll(
                new TreeItem<>(new BaseItem<>(15,ControlResources.getString("ChildNodeQuery"), (byte)0x0F),new Icon("el-icon-search")),
                new TreeItem<>(new BaseItem<>(16,ControlResources.getString("ChildNodeAdd"), (byte)0x10),new Icon("el-icon-plus")),
                new TreeItem<>(new BaseItem<>(17,ControlResources.getString("ChildNodeDelete"), (byte)0x11),new Icon("el-icon-delete"))
        );

        rootTreeItem.setExpanded(true);
        nodeTreeItem.setExpanded(true);
        treeView.setEditable(false);
        treeView.setRoot(rootTreeItem);
        treeView.setCellFactory(TextFieldTreeCell.forTreeView(new StringConverter<>() {
            @Override
            public String toString(BaseItem<Byte> integerBaseItem) {
                return integerBaseItem.name;
            }

            @Override
            public BaseItem<Byte> fromString(String s) {
                return null;
            }
        }));
        curBaseItem = treeView.getTreeItem(0).getValue();
        treeView.setOnMouseClicked(mouseEvent -> {
            if(isReceive){
                return;
            }
            TreeItem<BaseItem<Byte>> item = treeView.getSelectionModel().getSelectedItem();
            if(item==null) {
                return;
            }
            int level = treeView.getTreeItemLevel(item);
            BaseItem<Byte> baseItem = item.getValue();
            if(baseItem.id==-1){
                return;
            }
            //设置导航条
            List<String> str = new ArrayList<>();
            str.add(baseItem.name);
            for (int i = 0; i < level; i++) {
                item = item.getParent();
                str.add(item.getValue().name);
            }
            Collections.reverse(str);
            TreeItem<Object> model1 =  BreadCrumbBar.buildTreeModel(str.toArray());
            breadCrumbBar.setSelectedCrumb(model1);
            if (baseItem.id==0) {
                parentContainer.toFront();
            } else if (baseItem.id==13) {
                parentParentNode.toFront();
                labParentNodeResult.setText("");
            } else if (baseItem.id==15) {
                parentNodeManagement.toFront();
                labNodeResult.setText("");
            } else if (baseItem.id==16||baseItem.id==17) {
                parentChildNode.toFront();
                labChlidNodeResult.setText("");
            } else if (baseItem.id==18) {
                parentCurrentAndVoltageCalibration.toFront();
                labCalibrationResult.setText("");
                if(isNewCurrent){
                    if(currentList.size()==3){
                        //三相
                        tfACurrentLsat.setText(currentList.get(0).toString());
                        tfBCurrentLsat.setText(currentList.get(1).toString());
                        tfCCurrentLsat.setText(currentList.get(2).toString());
                    }else{
                        //单相
                        tfACurrentLsat.setText("0");
                        tfBCurrentLsat.setText("0");
                        tfCCurrentLsat.setText(currentList.get(0).toString());
                    }
                    tfNCurrentLsat.setText("0");
                }else{
                    tfACurrentLsat.setText("");
                    tfBCurrentLsat.setText("");
                    tfCCurrentLsat.setText("");
                    tfNCurrentLsat.setText("");
                }
                tfACurrentNum.setText("0");
                tfBCurrentNum.setText("0");
                tfCCurrentNum.setText("0");
                tfNCurrentNum.setText("0");
                if(isNewVoltage){
                    if(currentList.size()==3){
                        //三相
                        tfAVoltageLsat.setText(voltageList.get(0).toString());
                        tfBVoltageLsat.setText(voltageList.get(1).toString());
                        tfCVoltageLsat.setText(voltageList.get(2).toString());
                    }else{
                        //单相
                        tfAVoltageLsat.setText("0");
                        tfBVoltageLsat.setText("0");
                        tfCVoltageLsat.setText(voltageList.get(0).toString());
                    }
                }else{
                    tfAVoltageLsat.setText("");
                    tfBVoltageLsat.setText("");
                    tfCVoltageLsat.setText("");
                }
                tfAVoltageNum.setText("0");
                tfBVoltageNum.setText("0");
                tfCVoltageNum.setText("0");
            }  else if (baseItem.id==19) {
                parentMacConfig.toFront();
                labMacResult.setText("");
            } else {
                parentResult.toFront();
                labResult.setText("");
            }
            curBaseItem = baseItem;
        });

        //字体设置
        List<FontItem> list = new ArrayList<>();
        list.add(new FontItem("System Default", null, 0));
        list.add(new FontItem("Mac (13px)", "Lucida Grande", 13));
        list.add(new FontItem("Windows 100% (12px)", "Segoe UI", 12));
        list.add(new FontItem("Windows 125% (15px)", "Segoe UI", 15));
        list.add(new FontItem("Windows 150% (18px)", "Segoe UI", 18));
        list.add(new FontItem("Embedded Touch (22px)", "Arial", 22));
        list.add(new FontItem("Linux (13px)", "Lucida Sans", 13));
        list.add(new FontItem("Embedded Small (9px)", "Arial", 9));
        cbFont.getItems().addAll(list);
        cbFont.setConverter(new StringConverter<>() {
            @Override
            public String toString(FontItem fontItem) {
                return fontItem.name;
            }

            @Override
            public FontItem fromString(String s) {
                return null;
            }
        });
        cbFont.setValue(list.get(0));

        //字体设置
        cbLanguage.getItems().addAll(PreferencesTools.LANGUAGES);
        cbLanguage.setConverter(new StringConverter<>() {
            @Override
            public String toString(BaseItem<Locale> baseItem) {
                return baseItem.name;
            }

            @Override
            public BaseItem<Locale> fromString(String s) {
                return null;
            }
        });

        for (BaseItem<Locale> b: PreferencesTools.LANGUAGES) {
            if(b.value.getLanguage().equals(PreferencesTools.getLanguage())){
                cbLanguage.setValue(b);
                break;
            }
        }
        addTextLimiter(tfSourceAddress,2,12);
        addTextLimiter(tfDestinationAddress,2,12);
        addTextLimiter(tfParentNode,2,12);
        addTextLimiter(tfPassword,2,3);
        addTextLimiter(tfMacPassword,2,3);
        addTextLimiter(tfMac,2,12);
        addTextLimiter(tfStartNum,1,2);
        addTextLimiter(tfPageNum,1,2);

        addTextLimiter(tfACurrentNum,3,4);
        addTextLimiter(tfBCurrentNum,3,4);
        addTextLimiter(tfCCurrentNum,3,4);
        addTextLimiter(tfNCurrentNum,3,4);
        addTextLimiter(tfAVoltageNum,3,4);
        addTextLimiter(tfBVoltageNum,3,4);
        addTextLimiter(tfCVoltageNum,3,4);

        addTextLimiter(tfACurrentLsat,4,-1);
        addTextLimiter(tfBCurrentLsat,4,-1);
        addTextLimiter(tfCCurrentLsat,4,-1);
        addTextLimiter(tfNCurrentLsat,4,-1);
        addTextLimiter(tfAVoltageLsat,4,-1);
        addTextLimiter(tfBVoltageLsat,4,-1);
        addTextLimiter(tfCVoltageLsat,4,-1);
    }

    public void addTextLimiter(final TextField tf, int patternType, final int maxLength){
        String matches = null;
        if(patternType == 1){
            matches = "[0-9]+";
        }else if(patternType == 2){
            matches = "[A-Fa-f0-9]+";
        }else if(patternType == 3){
            matches = "(\\-)?(\\d)*";
        }else if(patternType == 4){
            matches = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,4})?$";
        }
        String finalMatches = matches;
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if(newValue!=null&&newValue.length()>0&&finalMatches!=null){
                boolean isExist = false;
                if(Pattern.compile(finalMatches).matcher(newValue).matches()){
                    isExist = true;
                }
                if(!isExist){
                    tf.setText(oldValue);
                }
            }
            if(newValue!=null&&newValue.length()>0&&maxLength!=-1){
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }

    private void initContainer(){
        parentContainer = JFXUtils.getParent("fxml/fun/container.fxml");
        parentResult = JFXUtils.getParent("fxml/fun/result.fxml");
        parentParentNode = JFXUtils.getParent("fxml/fun/parent_node.fxml");
        parentNodeManagement = JFXUtils.getParent("fxml/fun/child_node_management.fxml");
        parentChildNode = JFXUtils.getParent("fxml/fun/child_node.fxml");
        parentCurrentAndVoltageCalibration = JFXUtils.getParent("fxml/fun/current_and_voltage_calibration.fxml");
        parentMacConfig = JFXUtils.getParent("fxml/fun/mac_config.fxml");

        container.getChildren().addAll(parentResult,parentParentNode,parentNodeManagement, parentChildNode,parentCurrentAndVoltageCalibration,parentMacConfig,parentContainer);

        btnSend = findView(parentContainer,"#btnSend");
        btnSend.setOnAction(this::onButtonClick);
        textArea = findView(parentContainer,"#textArea");
        textAreaShow = findView(parentContainer,"#textAreaShow");
        textAreaRS = findView(parentContainer,"#textAreaRS");
        labResult = findView(parentResult,"#labResult");
        ((VBox) findView(parentResult,"#vboxResult")).getChildren().addAll(
                setNodeTitleBorder(findView(parentResult,"#apResult"),ControlResources.getString("ReceiveData")));

        tfParentNode = findView(parentParentNode,"#tfParentNode");
        labParentNodeResult = findView(parentParentNode,"#labParentNodeResult");
        ((VBox) findView(parentParentNode,"#vboxParentNode")).getChildren().addAll(
                setNodeTitleBorder(findView(parentParentNode,"#hboxParentNode"),ControlResources.getString("DataField")),
                setNodeTitleBorder(findView(parentParentNode,"#apParentNodeResult"),ControlResources.getString("ReceiveData")));

        tfStartNum = findView(parentNodeManagement,"#tfStartNum");
        tfPageNum = findView(parentNodeManagement,"#tfPageNum");
        labNodeResult = findView(parentNodeManagement,"#labNodeResult");
        ((VBox) findView(parentNodeManagement,"#vboxNode")).getChildren().addAll(
                setNodeTitleBorder(findView(parentNodeManagement,"#apNode"),ControlResources.getString("DataField")),
                setNodeTitleBorder(findView(parentNodeManagement,"#apNodeResult"),ControlResources.getString("ReceiveData")));

        gpChildNode = findView(parentChildNode,"#gpChildNode");
        gpChildNode.getChildren().clear();
        addNode();

        labChlidNodeResult = findView(parentChildNode,"#labChlidNodeResult");
        ((VBox) findView(parentChildNode,"#vboxChildNode")).getChildren().addAll(
                setNodeTitleBorder(findView(parentChildNode,"#hboxChildNode"),ControlResources.getString("DataField")),
                setNodeTitleBorder(findView(parentChildNode,"#apChildNodeResult"),ControlResources.getString("ReceiveData")));

        tfPassword = findView(parentCurrentAndVoltageCalibration,"#tfPassword");
        tfACurrentNum = findView(parentCurrentAndVoltageCalibration,"#tfACurrentNum");
        tfBCurrentNum = findView(parentCurrentAndVoltageCalibration,"#tfBCurrentNum");
        tfCCurrentNum = findView(parentCurrentAndVoltageCalibration,"#tfCCurrentNum");
        tfNCurrentNum = findView(parentCurrentAndVoltageCalibration,"#tfNCurrentNum");
        tfAVoltageNum = findView(parentCurrentAndVoltageCalibration,"#tfAVoltageNum");
        tfBVoltageNum = findView(parentCurrentAndVoltageCalibration,"#tfBVoltageNum");
        tfCVoltageNum = findView(parentCurrentAndVoltageCalibration,"#tfCVoltageNum");
        tfACurrentLsat = findView(parentCurrentAndVoltageCalibration,"#tfACurrentLsat");
        tfBCurrentLsat = findView(parentCurrentAndVoltageCalibration,"#tfBCurrentLsat");
        tfCCurrentLsat = findView(parentCurrentAndVoltageCalibration,"#tfCCurrentLsat");
        tfNCurrentLsat = findView(parentCurrentAndVoltageCalibration,"#tfNCurrentLsat");
        tfAVoltageLsat = findView(parentCurrentAndVoltageCalibration,"#tfAVoltageLsat");
        tfBVoltageLsat = findView(parentCurrentAndVoltageCalibration,"#tfBVoltageLsat");
        tfCVoltageLsat = findView(parentCurrentAndVoltageCalibration,"#tfCVoltageLsat");
        btnCurrentCalculation = findView(parentCurrentAndVoltageCalibration,"#btnCurrentCalculation");
        btnVoltageCalculation = findView(parentCurrentAndVoltageCalibration,"#btnVoltageCalculation");
        cbSensorType = findView(parentCurrentAndVoltageCalibration,"#cbSensorType");
        lbSensorTypeTip = findView(parentCurrentAndVoltageCalibration,"#lbSensorTypeTip");
        tfCVoltageLsat = findView(parentCurrentAndVoltageCalibration,"#tfCVoltageLsat");
        labCalibrationResult = findView(parentCurrentAndVoltageCalibration,"#labCalibrationResult");
        ((VBox) findView(parentCurrentAndVoltageCalibration,"#vboxCalibration")).getChildren().addAll(
                setNodeTitleBorder(findView(parentCurrentAndVoltageCalibration,"#apCalibration"),ControlResources.getString("DataField")),
                setNodeTitleBorder(findView(parentCurrentAndVoltageCalibration,"#apCalibrationResult"),ControlResources.getString("ReceiveData")));

        tfMacPassword = findView(parentMacConfig,"#tfMacPassword");
        tfMac = findView(parentMacConfig,"#tfMac");
        labMacResult = findView(parentMacConfig,"#labMacResult");
        ((VBox) findView(parentMacConfig,"#vboxMac")).getChildren().addAll(
                setNodeTitleBorder(findView(parentMacConfig,"#apMac"),ControlResources.getString("DataField")),
                setNodeTitleBorder(findView(parentMacConfig,"#apMacResult"),ControlResources.getString("ReceiveData")));
    }

    private void addNode(){
        int rowCount = gpChildNode.getChildren().size()/3;
        if(rowCount>=16){
            return;
        }
        Label label=new Label(ControlResources.getString("ChildNode.Addr")+rowCount);
        label.setGraphic(new Icon("el-icon-s-help"));
        label.setId("labChildNode"+rowCount);
        TextField textField = new TextField();
        textField.setId("tfChildNode"+rowCount);
        String btnStr = rowCount==0?ControlResources.getString("Add"):ControlResources.getString("Delete");
        Button button = new Button(btnStr);
        button.setId("btnChildNode"+rowCount);
        GridPane.setMargin(button, new Insets(0.0D, 10.0D, 0.0D, 0.0D));
        button.setOnAction(event -> {
            Button target = (Button) event.getTarget();
            if(target.getText().equals(ControlResources.getString("Add"))){
                addNode();
            }else{
                gpChildNode.getChildren().removeAll(
                    target,
                        label,
                        textField
                );
            }
        });
        GridPane.setConstraints(label, rowCount>=8?3:0,rowCount%8);
        GridPane.setConstraints(textField, rowCount>=8?4:1,rowCount%8);
        GridPane.setConstraints(button, rowCount>=8?5:2,rowCount%8);
        gpChildNode.getChildren().addAll(label,textField,button);
        addTextLimiter(textField,0,12);
    }

    private void initPortName(boolean isSet){
        ArrayList<String> aspList = SerialPortTool.getAvailableSerialPorts();
        ObservableList<String> observableList = FXCollections.observableList(aspList);//转化为可观察的list，支持改变监听
        if(aspList.size() > 0) {
            cbPortName.setItems(observableList);
            if (isSet){
                serialPortParameter.setPortName(aspList.get(0));
                cbPortName.setValue(serialPortParameter.getPortName());
            }
        }
    }
    private void initSerialPortParameter(){
        initPortName(true);
        cbBaudRate.setEditable(true);
        cbBaudRate.getItems().addAll(SerialPortParameter.BAUDRATE_150,SerialPortParameter.BAUDRATE_300,SerialPortParameter.BAUDRATE_600
                ,SerialPortParameter.BAUDRATE_1200,SerialPortParameter.BAUDRATE_2400,SerialPortParameter.BAUDRATE_4800
                ,SerialPortParameter.BAUDRATE_9600,SerialPortParameter.BAUDRATE_19200,SerialPortParameter.BAUDRATE_38400,SerialPortParameter.BAUDRATE_57600
                ,SerialPortParameter.BAUDRATE_128000,SerialPortParameter.BAUDRATE_115200,SerialPortParameter.BAUDRATE_256000);
        cbBaudRate.setValue(serialPortParameter.getBaudRate());
        cbDataBits.getItems().addAll(SerialPortParameter.DATABITS_5,SerialPortParameter.DATABITS_6,SerialPortParameter.DATABITS_7,SerialPortParameter.DATABITS_8);
        cbDataBits.setValue(serialPortParameter.getDataBits());
        cbStopBits.getItems().addAll(SerialPortParameter.ONE_STOP_BIT,SerialPortParameter.ONE_POINT_FIVE_STOP_BITS,SerialPortParameter.TWO_STOP_BITS);
        cbStopBits.setValue((float) serialPortParameter.getStopBits());
        cbStopBits.setConverter(new FloatStringConverter(){
            @Override
            public String toString(Float f) {
                String stopBits = f+"";
                if(f!= SerialPortParameter.ONE_POINT_FIVE_STOP_BITS){
                    stopBits  = f.intValue()+"";
                }
                return stopBits;
            }
        });
        cbParity.getItems().addAll(SerialPortParameter.PARITY_NONE,SerialPortParameter.PARITY_ODD,SerialPortParameter.PARITY_EVEN,SerialPortParameter.PARITY_MARK,SerialPortParameter.PARITY_SPACE);
//        cbParity.setCellFactory(new Callback<>() {
//            @Override
//            public ListCell<String> call(ListView<String> stringListView) {
//                final ListCell<String> cell = new ListCell<>() {
//                    @Override
//                    public void updateItem(String item, boolean empty) {
//                        super.updateItem(item, empty);
//                        serialPortParameter.setParityStr(item);
//                    }
//                };
//                return cell;
//            }
//        });
        cbParity.setValue(serialPortParameter.getParityStr());
        cbFlowcontrol.getItems().addAll(SerialPortParameter.FLOW_CONTROL_DISABLED,SerialPortParameter.FLOW_CONTROL_DSR_ENABLED
                ,SerialPortParameter.FLOW_CONTROL_CTS_ENABLED,SerialPortParameter.FLOW_CONTROL_DTR_ENABLED,SerialPortParameter.FLOW_CONTROL_RTS_ENABLED
                ,SerialPortParameter.FLOW_CONTROL_XONXOFF_IN_ENABLED,SerialPortParameter.FLOW_CONTROL_XONXOFF_OUT_ENABLED);
        cbFlowcontrol.setValue(serialPortParameter.getFlowcontrolStr());

        cbRequestTime.getItems().addAll(1,2,3,6,10,15,20,60);
        cbRequestTime.setValue(3);

        cbRequestCount.getItems().addAll(1,2,3,6,10,15,50,100);
        cbRequestCount.setValue(1);

        cbRequestInterval.getItems().addAll(100,500,1000,60000,900000,3600000,7200000);
        cbRequestInterval.setValue(100);

        List<BaseItem<String>> sensorTypeList= new LinkedList<>();
        sensorTypeList.add(new BaseItem<>(0, ControlResources.getString("SensorType.UniphaseMeterBox"), ControlResources.getString("SensorType.MeterBox.Tip")));
        sensorTypeList.add(new BaseItem<>(1,ControlResources.getString("SensorType.TriphaseMeterBox"),ControlResources.getString("SensorType.MeterBox.Tip")));
        sensorTypeList.add(new BaseItem<>(2,ControlResources.getString("SensorType.BranchBox"),ControlResources.getString("SensorType.BranchBox.Tip")));
        sensorTypeList.add(new BaseItem<>(3,ControlResources.getString("SensorType.DistributionTransformerBox"),ControlResources.getString("SensorType.DistributionTransformerBox.Tip")));
        cbSensorType.getItems().addAll(sensorTypeList);
        cbSensorType.setValue(sensorTypeList.get(0));
        cbSensorType.setConverter(new StringConverter<>() {

            @Override
            public String toString(BaseItem<String> stringBaseItem) {
                return stringBaseItem.name;
            }

            @Override
            public BaseItem<String> fromString(String s) {
                for (BaseItem<String> stringBaseItem:sensorTypeList){
                    if(s.contains(stringBaseItem.name)){
                        return stringBaseItem;
                    }
                }
                return null;
            }
        });
        cbPortName.setOnMouseClicked(mouseEvent -> initPortName(false));
    }

    public void openSerialPort() {
        FxIntent intent = new FxIntent(SerialPortController.class, StageStyle.UTILITY);
        intent.addData("data", "我是传过来的数据");
        intent.start();
    }
    public void openCalculator() {
        try {
            Runtime.getRuntime().exec("cmd /c start C:\\WINDOWS\\system32\\calc.exe");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openIconManagement() {
        FxIntent intent = new FxIntent(IconManagementController.class, StageStyle.UTILITY);
        intent.addData("data", "我是传过来的数据");
        intent.start();
    }
    public void openAsciiManagement() {
        FxIntent intent = new FxIntent(AsciiCodeManagementController.class, StageStyle.UTILITY);
        intent.addData("data", "我是传过来的数据");
        intent.start();
    }

    public void  selectLocale(Locale locale){
        PreferencesTools.setLanguage(locale);
        JFXUtils.runUiThread(() -> getIntent().closePrimaryStage());
        FxIntent intent = new FxIntent(MainController.class);
        intent.start();

    }

    public void selectBgColor() {
        String color = JFXUtils.colorToWebColor(colorPicker.getValue());
        PreferencesTools.setFxBasePref(color);
        FxStyleUtils.setBase(getIntent().getRoot(), color);
    }

    public void onChange(ActionEvent actionEvent) {
        if (actionEvent.getTarget().equals(cbLanguage)) {
            BaseItem<Locale> fontItem = cbLanguage.getSelectionModel().getSelectedItem();
            selectLocale(fontItem.value);
        } else if (actionEvent.getTarget().equals(cbFont)) {
            FontItem fontItem = cbFont.getSelectionModel().getSelectedItem();
            if (fontItem.fontSize != 0) {
                FxStyleUtils.setFont(getIntent().getRoot(), fontItem.fontSize + "px \"" + fontItem.fontName + "\"");
            } else {
                FxStyleUtils.removeFont(getIntent().getRoot());
            }
        } else if(actionEvent.getTarget().equals(cbPortName)){
            serialPortParameter.setPortName(cbPortName.getSelectionModel().getSelectedItem());
        }else  if(actionEvent.getTarget().equals(cbBaudRate)){
            serialPortParameter.setBaudRate(Integer.parseInt(cbBaudRate.getSelectionModel().getSelectedItem()+""));
        }else  if(actionEvent.getTarget().equals(cbDataBits)){
            serialPortParameter.setDataBits(cbDataBits.getSelectionModel().getSelectedItem());
        }else  if(actionEvent.getTarget().equals(cbStopBits)){
            float stopBits =cbStopBits.getSelectionModel().getSelectedItem();
            if(stopBits== SerialPortParameter.ONE_POINT_FIVE_STOP_BITS){
                stopBits  = SerialPort.TWO_STOP_BITS;
            }
            serialPortParameter.setStopBits((int) stopBits);
        }else  if(actionEvent.getTarget().equals(cbParity)){
            serialPortParameter.setParityStr(cbParity.getSelectionModel().getSelectedItem());
        }else  if(actionEvent.getTarget().equals(cbFlowcontrol)){
            serialPortParameter.setFlowcontrolStr(cbFlowcontrol.getSelectionModel().getSelectedItem());
        }else  if(actionEvent.getTarget().equals(cbRequestTime)){
            String timeStr = cbRequestTime.getSelectionModel().getSelectedItem()+"";
            if(StringUtils.isNotBlank(timeStr)){
                timeout = Integer.parseInt(timeStr)*1000;
            }else{
                timeout = 3000;
            }
        }else  if(actionEvent.getTarget().equals(cbRequestCount)){
            initAllcount();
        }else  if(actionEvent.getTarget().equals(cbRequestInterval)){
            String allIntervalStr = cbRequestInterval.getSelectionModel().getSelectedItem()+"";
            if(StringUtils.isNotBlank(allIntervalStr)&&Integer.parseInt(allIntervalStr)>0){
                allInterval = Integer.parseInt(allIntervalStr);
            }else{
                allInterval = 100;
                cbRequestInterval.setValue(allInterval);
            }
        }else  if(actionEvent.getTarget().equals(cbSensorType)){
            BaseItem<String> sensorTypeItem= cbSensorType.getSelectionModel().getSelectedItem();
            sensorType = sensorTypeItem.id;
            lbSensorTypeTip.setText(sensorTypeItem.value);
        }
    }
    private int sensorType;
    private int allInterval= 100;

    private final Runnable taskCount = () -> JFXUtils.runUiThread(this::sendData);

    private int allcount= 1;
    private ScheduledThreadPoolExecutor allcountThreadPool;
    private void countingCycle(){
        if(allcountThreadPool!=null){
            allcountThreadPool.shutdownNow();
            allcountThreadPool = null;
        }
        allcount--;
        if(allcount>0){
            allcountThreadPool = ThreadPoolUtils.runDelayTime(taskCount, allInterval);
        }
    }
    private void initAllcount(){
        String allcountStr = cbRequestCount.getSelectionModel().getSelectedItem()+"";
        if(StringUtils.isNotBlank(allcountStr)&&Integer.parseInt(allcountStr)>0){
            allcount = Integer.parseInt(allcountStr);
        }else{
            allcount = 1;
            cbRequestCount.setValue(allcount);
        }
    }
    public void onButtonClick(ActionEvent actionEvent) {
        if(actionEvent.getTarget().equals(btnClearAll)){
            textAreaShow.clear();
            textAreaRS.clear();
            textArea.clear();
        }else if(actionEvent.getTarget().equals(btnClear)){
            textAreaShow.clear();
            textAreaRS.clear();
        }else if(actionEvent.getTarget().equals(btnCopy)){
            textAreaShow.copy();
        }else if(actionEvent.getTarget().equals(btnPaste)){
            textAreaShow.paste();
        }else if(actionEvent.getTarget().equals(btnSelectAll)){
            textAreaShow.selectAll();
        }else if(actionEvent.getTarget().equals(btnConfigurePort)){
            configurePort();
        }else if(actionEvent.getTarget().equals(btnStart)||actionEvent.getTarget().equals(btnSend)){
            if(isReceive){
                return;
            }
            initAllcount();
            sendData();
        }else if(actionEvent.getTarget().equals(btnStop)){
            allcount = 0;
            stopData();
        }else if(actionEvent.getTarget().equals(btnOpenSerialPort)){
            closeSerialPort();
            openSerialPort();
        }else if(actionEvent.getTarget().equals(btnOpenCalculator)){
            openCalculator();
        }else if(actionEvent.getTarget().equals(btnOpenIcon)){
            openIconManagement();
        }else if(actionEvent.getTarget().equals(btnOpenAscii)){
            openAsciiManagement();
        }else if(actionEvent.getTarget().equals(btnCurrentCalculation)){
            currentAndVoltageCalibration(0);
        }else if(actionEvent.getTarget().equals(btnVoltageCalculation)){
            currentAndVoltageCalibration(1);
        }
    }
    private void currentAndVoltageCalibration(int type){
        if(type==0){
            if(StringUtils.isEmpty(tfACurrentLsat.getText())){
                AnimationUtils.createTransition(tfACurrentLsat, AnimationType.SHAKE).play();
                tfACurrentLsat.requestFocus();
                return;
            }
            if(StringUtils.isEmpty(tfBCurrentLsat.getText())){
                AnimationUtils.createTransition(tfBCurrentLsat, AnimationType.SHAKE).play();
                tfBCurrentLsat.requestFocus();
                return;
            }
            if(StringUtils.isEmpty(tfCCurrentLsat.getText())){
                AnimationUtils.createTransition(tfCCurrentLsat, AnimationType.SHAKE).play();
                tfCCurrentLsat.requestFocus();
                return;
            }
            if(StringUtils.isEmpty(tfNCurrentLsat.getText())){
                AnimationUtils.createTransition(tfNCurrentLsat, AnimationType.SHAKE).play();
                tfNCurrentLsat.requestFocus();
                return;
            }
            float aCurrentLast = Float.parseFloat(tfACurrentLsat.getText());
            float bCurrentLast = Float.parseFloat(tfBCurrentLsat.getText());
            float cCurrentLast = Float.parseFloat(tfCCurrentLsat.getText());
            float nCurrentLast = Float.parseFloat(tfNCurrentLsat.getText());
            long aCurrentNum = 0,bCurrentNum = 0,cCurrentNum = 0,nCurrentNum = 0;
            if(sensorType==0){
                cCurrentNum = Math.round((cCurrentLast - 50) * 1000 * 0.03298);
                nCurrentNum = 0;
            }else if(sensorType==1){
                aCurrentNum = Math.round((aCurrentLast - 50) * 1000 * 0.034);
                bCurrentNum = Math.round((bCurrentLast - 50) * 1000 * 0.034);
                cCurrentNum = Math.round((cCurrentLast - 50) * 1000 * 0.034);
                nCurrentNum = 0;
            }else if(sensorType==2){
                aCurrentNum = Math.round((aCurrentLast - 400) * 1000 * 0.00425);
                bCurrentNum = Math.round((bCurrentLast - 400) * 1000 * 0.00425);
                cCurrentNum = Math.round((cCurrentLast - 400) * 1000 * 0.00425);
                nCurrentNum = 0;
            }else if(sensorType==3){
                aCurrentNum = Math.round((aCurrentLast - 800) * 1000 * 0.002125);
                bCurrentNum = Math.round((bCurrentLast - 800) * 1000 * 0.002125);
                cCurrentNum = Math.round((cCurrentLast - 800) * 1000 * 0.002125);
                nCurrentNum = 0;
            }
            tfACurrentNum.setText(String.valueOf(aCurrentNum));
            tfBCurrentNum.setText(String.valueOf(bCurrentNum));
            tfCCurrentNum.setText(String.valueOf(cCurrentNum));
            tfNCurrentNum.setText(String.valueOf(nCurrentNum));
        }else if(type==1){
            if(StringUtils.isEmpty(tfAVoltageLsat.getText())){
                AnimationUtils.createTransition(tfAVoltageLsat, AnimationType.SHAKE).play();
                tfAVoltageLsat.requestFocus();
                return;
            }
            if(StringUtils.isEmpty(tfBVoltageLsat.getText())){
                AnimationUtils.createTransition(tfBVoltageLsat, AnimationType.SHAKE).play();
                tfBVoltageLsat.requestFocus();
                return;
            }
            if(StringUtils.isEmpty(tfCVoltageLsat.getText())){
                AnimationUtils.createTransition(tfCVoltageLsat, AnimationType.SHAKE).play();
                tfCVoltageLsat.requestFocus();
                return;
            }
            float aVoltageLast = Float.parseFloat(tfAVoltageLsat.getText());
            float bVoltageLast = Float.parseFloat(tfBVoltageLsat.getText());
            float cVoltageLast = Float.parseFloat(tfCVoltageLsat.getText());
            long aVoltageNum = 0,bVoltageNum = 0,cVoltageNum = 0;
            if(sensorType==0){
                cVoltageNum = Math.round((cVoltageLast - 220) * 10 * 0.3591);
            }else if(sensorType==1||sensorType==2||sensorType==3){
                aVoltageNum = Math.round((aVoltageLast - 220) * 10 * 0.6895);
                bVoltageNum = Math.round((bVoltageLast - 220) * 10 * 0.6895);
                cVoltageNum = Math.round((cVoltageLast - 220) * 10 * 0.6895);
            }
            tfAVoltageNum.setText(String.valueOf(aVoltageNum));
            tfBVoltageNum.setText(String.valueOf(bVoltageNum));
            tfCVoltageNum.setText(String.valueOf(cVoltageNum));

        }
    }
    private void closeSerialPort(){
        stopData();
        SerialPortTool.closeSerialPort(serialPort);
        serialPort = null;
        btnConfigurePort.setText(ControlResources.getString("ConfigureThePort"));
        setScrollToBottom(textAreaShow.getText() + ControlResources.getString("CloseTheSerialPort")+"\n");
    }
    long lastTime = 0;
    byte[] recvBytesType;
    private void configurePort() {
        if(serialPort!=null){
            closeSerialPort();
            return;
        }
        serialPort = SerialPortTool.openSerialPort(serialPortParameter.getPortName(), serialPortParameter.getBaudRate(), serialPortParameter.getDataBits(), serialPortParameter.getStopBits(), serialPortParameter.getParity(),serialPortParameter.getFlowcontrol());
        SerialPortTool.setListenerToSerialPort(serialPort, new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                if (serialPortEvent.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {//有效数据
                    byte[] recvBytes = SerialPortTool.readData(serialPort);
                    if(!isReceive){
                        return;
                    }

                    if(System.nanoTime()-lastTime<= 200_000_000){
                        System.out.println(System.nanoTime()+","+lastTime+","+(System.nanoTime()-lastTime));
                        byte[] recvBytesNew = new byte[recvBytes.length+recvBytesType.length];
                        System.arraycopy(recvBytesType,0,recvBytesNew,0,recvBytesType.length);
                        System.arraycopy(recvBytes,0,recvBytesNew,recvBytesType.length,recvBytes.length);
                        recvBytes = recvBytesNew;
                    }
                    recvBytesType = recvBytes;
                    lastTime = System.nanoTime();
                    String recMsg = getRecvMsg(recvBytes);
                    if(recMsg.length()>0) {
                        if(ControlResources.getString("Success").equals(recMsg)&&curBaseItem.id==5){
                            isReceive = true;
                        }else{
                            stopData();
                            byte[] finalRecvBytes = recvBytes;
                            JFXUtils.runUiThread(() ->{
                                if(curBaseItem.id==13){
                                    labParentNodeResult.setText(recMsg);
                                }else if(curBaseItem.id==15){
                                    labNodeResult.setText(recMsg);
                                }else if (curBaseItem.id==16||curBaseItem.id==17) {
                                    labChlidNodeResult.setText(recMsg);
                                }else if(curBaseItem.id==18){
                                    labCalibrationResult.setText(recMsg);

                                }else if(curBaseItem.id==19){
                                    labMacResult.setText(recMsg);
                                }else{
                                    labResult.setText(recMsg);
                                }
                                setScrollToBottom(textAreaShow.getText() +ControlResources.getString("Receive")+ "：" +
                                        DataUtils.separatedByChr(Objects.requireNonNull(SerialPortTool.bytesToHexString(finalRecvBytes)).toUpperCase(), 2, " ") + "\n");
                                textAreaRS.appendText("\n=============="+curBaseItem.name+"结果["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"]==============\n");
                                textAreaRS.appendText(recMsg);
                                countingCycle();
                            });
                        }
                    }
                }
            }
        });
        JFXUtils.runUiThread(() -> {
            if (serialPort != null) {
                btnConfigurePort.setText(ControlResources.getString("CloseThePort"));
                setScrollToBottom(textAreaShow.getText() + ControlResources.getString("OpenSerialPort") +
                        "【" + serialPortParameter.getPortName() + "】"+ControlResources.getString("Success") +"\n");
            } else {
                if(curBaseItem.id!=0){
                    showMessage(ControlResources.getString("OpenSerialPort") + "【" + serialPortParameter.getPortName() + "】"+ControlResources.getString("Fail"));
                }
                setScrollToBottom(textAreaShow.getText() +ControlResources.getString("OpenSerialPort") +
                        "【" + serialPortParameter.getPortName() + "】"+ControlResources.getString("Fail") +"\n");
            }
        });
    }
    private String getRecvMsg(byte[] recvBytes){
        StringBuilder msg = new StringBuilder();
        int length = recvBytes.length;
        int dataLen;
        int otherLen = 5;
        try {
            for (int i = 0; i<length; i++){
                if(recvBytes[i]==ADDR){
                    if((i+2)<length){
                        dataLen = Integer.parseInt(DataUtils.bytesToHexString(recvBytes[i+2]),16);
                        if(i+dataLen+otherLen<=length){
                            byte[] recvBytesTemp = new byte[dataLen+otherLen];
                            System.arraycopy(recvBytes,i,recvBytesTemp,0,recvBytesTemp.length);

                            //通信方式
                            byte comMode = (byte) ((recvBytesTemp[1]&0x20)>>5);
                            //后续帧标志
//                            byte sfFlag = (byte) ((recvBytesTemp[1]&0x40)>>6);
                            //功能码
                            byte fcode = (byte) (recvBytesTemp[1]&0x1f);
                            boolean isSrcAddr = true;

                            if(comMode==(byte)0x01) {
                                byte[] desAddrTemp = new byte[6];
                                if(recvBytesTemp.length-(otherLen-2)>=desAddrTemp.length) {
                                    System.arraycopy(recvBytesTemp, otherLen - 2, desAddrTemp, 0, desAddrTemp.length);
                                }
                                //源地址
                                byte[] srcAddr = DataUtils.strAddrToByteAddr(srcAddrStr);
                                isSrcAddr = Arrays.equals(desAddrTemp, srcAddr);
                            }
                            //CRC16M检验
                            if (CRC16M.checkCode(recvBytesTemp)&&isSrcAddr) {
                                byte[] srcAddrBytes = new byte[6];
                                byte[] desAddrBytes = new byte[6];
                                byte[] dataBytes;
                                if(comMode==(byte)0x01) {
                                    dataBytes = new byte[dataLen - srcAddrBytes.length - desAddrBytes.length];
                                    System.arraycopy(recvBytesTemp, otherLen-2, desAddrBytes, 0, desAddrBytes.length);
                                    ArrayUtils.reverse(desAddrBytes);
                                    System.arraycopy(recvBytesTemp, otherLen-2 + desAddrBytes.length, srcAddrBytes, 0, srcAddrBytes.length);
                                    ArrayUtils.reverse(srcAddrBytes);
                                    System.arraycopy(recvBytesTemp, otherLen-2 + srcAddrBytes.length + desAddrBytes.length, dataBytes, 0, dataBytes.length);
                                }else{
                                    dataBytes = new byte[dataLen];
                                    System.arraycopy(recvBytesTemp, otherLen-2, dataBytes, 0, dataBytes.length);
                                }

                                float num;
                                String unit="";
                                float multiple;
                                List<String> strList = new ArrayList<>();
                                int radix = 10,interval;
                                //功能码判断
                                if(fcode==(byte)0x01||fcode==(byte)0x02||fcode==(byte)0x0B||fcode==(byte)0x03
                                        ||fcode==(byte)0x08||fcode==(byte)0x09||fcode==(byte)0x0A){
                                    if(fcode==(byte)0x01){
                                        multiple= 1000.0f;
                                        interval = 3;
                                        unit = "A";
                                    }else if (fcode == (byte) 0x02){
                                        multiple = 10.0f;
                                        interval = 2;
                                        unit = "V";
                                    }else if(fcode==(byte)0x03){
                                        multiple= 1000.0f;
                                        interval = 3;
                                        unit = "A";
                                    }else if(fcode==(byte)0x08||fcode==(byte)0x09||fcode==(byte)0x0A){
                                        multiple= 1000.0f;
                                        interval = 3;
                                        switch (fcode){
                                            case (byte)0x08: unit = "kW";break;
                                            case (byte)0x09: unit = "kvar"; break;
                                            case (byte)0x0A: unit = "kVA"; break;
                                            default: break;
                                        }
                                    }else {
                                        multiple = 1.0f;
                                        interval = 2;
                                    }

                                    if(cheackPhase(dataBytes,interval)){
                                        strList.add(ControlResources.getString("Present"));
                                    }else{
                                        strList.add(ControlResources.getString("Aphase"));
                                        strList.add(ControlResources.getString("Bphase"));
                                        strList.add(ControlResources.getString("Cphase"));
                                    }

                                    msg = new StringBuilder(formatMsg(dataBytes, strList, unit, radix, multiple, interval,fcode));
                                }else if(fcode==(byte)0x04){
                                    multiple= 10.0f;
                                    unit = "℃";
                                    radix = 16;
                                    byte[] dataTemp = new byte[2];
                                    System.arraycopy(dataBytes,0,dataTemp,0,dataTemp.length);
                                    ArrayUtils.reverse(dataTemp);
                                    int numTemp = DataUtils.byteArrayToInt(dataTemp,dataTemp.length);
                                    if(DataUtils.integerToBinary(numTemp).length()>=16){
                                        num = -Integer.parseInt(Integer.toBinaryString(-numTemp).substring(16), 2)/multiple;
                                    }else{
                                        num = numTemp/multiple;
                                    }
                                    msg.append(ControlResources.getString("Temperature")).append("：").append(num).append(unit).append("\n");
                                    num = Integer.parseInt(DataUtils.bytesToHexString(dataBytes[2]),radix);
                                    unit = "%";
                                    msg.append(ControlResources.getString("Humidity")).append("：").append((int) num).append(unit);
                                }else if(fcode==(byte)0x00||fcode==(byte)0x06||fcode==(byte)0x0D||fcode==(byte)0x05||fcode==(byte)0x12){
                                    msg = new StringBuilder(dataBytes[0] == (byte) 0x00 ? ControlResources.getString("Success") : ControlResources.getString("Fail"));
                                    if(curBaseItem.id==18&&dataBytes[0] == (byte) 0x00){
                                        isNewCurrent = false;
                                        isNewVoltage = false;
                                    }
                                }else if(fcode==(byte)0x0C){
                                    byte[] macAddrBytes = new byte[6];
                                    System.arraycopy(dataBytes,1,macAddrBytes,0,macAddrBytes.length);
                                    ArrayUtils.reverse(macAddrBytes);
                                    byte[] parentAddrBytes = new byte[6];
                                    System.arraycopy(dataBytes,1+macAddrBytes.length,parentAddrBytes,0,parentAddrBytes.length);
                                    ArrayUtils.reverse(parentAddrBytes);
                                    if(comMode==(byte)0x01) {
                                        msg.append(ControlResources.getString("DestinationAddress")).append("：").append(DataUtils.bytesToHexString(desAddrBytes)).append("\n");
                                        msg.append(ControlResources.getString("SourceAddress")).append("：").append(DataUtils.bytesToHexString(srcAddrBytes)).append("\n");
                                    }
                                    msg.append(ControlResources.getString("EquipmentType")).append("：");
                                    switch (dataBytes[0]){
                                        case 0x01:
                                            msg.append(ControlResources.getString("BranchBoxSensor"));
                                            break;
                                        case 0x02:
                                            msg.append(ControlResources.getString("MeterBoxSensor"));
                                            break;
                                        case 0x03:
                                            msg.append(ControlResources.getString("WithTransformerSensor"));
                                            break;
                                        case 0x04:
                                            msg.append(ControlResources.getString("SingleTopologyTransmitSensor"));
                                            break;
                                    }
                                    msg.append("\n");
                                    msg.append(ControlResources.getString("MacAddr")).append("：").append(DataUtils.bytesToHexString(macAddrBytes)).append("\n");
                                    msg.append(ControlResources.getString("ParentNode.Addr")).append("：").append(DataUtils.bytesToHexString(parentAddrBytes)).append("\n");
                                    msg.append(ControlResources.getString("SoftwareVersionNumber")).append("：").append(Integer.parseInt((DataUtils.bytesToHexString(dataBytes[macAddrBytes.length + parentAddrBytes.length + 1])), 16) / 10.0f);
                                }else if(fcode==(byte)0x0E){
                                    multiple= 10.0f;
                                    unit = "℃";
                                    radix = 16;
                                    interval = 2;
                                    if(cheackPhase(dataBytes,interval*2)){
                                        strList.add(ControlResources.getString("FireWire"));
                                    }else{
                                        strList.add(ControlResources.getString("Aphase"));
                                        strList.add(ControlResources.getString("Bphase"));
                                        strList.add(ControlResources.getString("Cphase"));
                                    }
                                    strList.add(ControlResources.getString("ZeroLine"));
                                    byte[] dataTemp = new byte[interval];
                                    int start = dataBytes.length/dataTemp.length-strList.size();
                                    for (int j =0;j<strList.size();j++,start++){
                                        if(dataBytes.length-start*dataTemp.length>=dataTemp.length) {
                                            System.arraycopy(dataBytes, start * dataTemp.length, dataTemp, 0, dataTemp.length);
                                        }
                                        ArrayUtils.reverse(dataTemp);
                                        int numTemp = DataUtils.byteArrayToInt(dataTemp,dataTemp.length);
                                        //正确解析
                                        if(DataUtils.integerToBinary(numTemp).length()>=16){
                                            num = -Integer.parseInt(Integer.toBinaryString(-numTemp).substring(16), 2)/multiple;
                                        }else{
                                            num = numTemp/multiple;
                                        }
                                        //将错就错，对应泰森温度正数部分用16位无符号存储导致多出来的FF*10
//                                        if(numTemp>0x80*10){
//                                            num = (0x100*10-numTemp)/-10f;
//                                        }else{
//                                            num = numTemp/multiple;
//                                        }
                                        msg.append(strList.get(j)).append("：").append(num).append(unit);
                                        if(j!=strList.size()-1){
                                            msg.append("\n");
                                        }
                                    }
                                }else if(fcode==(byte)0x07){
                                    byte[] addrBytes = new byte[6];
                                    byte phase = dataBytes[addrBytes.length];
                                    byte signalStrength = dataBytes[addrBytes.length+1];
                                    System.arraycopy(dataBytes,0,addrBytes,0,addrBytes.length);
                                    ArrayUtils.reverse(addrBytes);
                                    if(comMode==(byte)0x01) {
                                        msg.append(ControlResources.getString("DestinationAddress")).append("：").append(DataUtils.bytesToHexString(desAddrBytes)).append("\n");
                                        msg.append(ControlResources.getString("SourceAddress")).append("：").append(DataUtils.bytesToHexString(srcAddrBytes)).append("\n");
                                    }
                                    msg.append(ControlResources.getString("MeterChildNodeAddr")).append("：").append(DataUtils.bytesToHexString(addrBytes)).append("\n");
                                    msg.append(ControlResources.getString("Phase")).append("：");
                                    switch (phase){
                                        case (byte)0x01: msg.append(ControlResources.getString("Aphase")); break;
                                        case (byte)0x02: msg.append(ControlResources.getString("Bphase")); break;
                                        case (byte)0x03: msg.append(ControlResources.getString("Cphase")); break;
                                        default: msg.append(ControlResources.getString("UnknownPhase")); break;
                                    }
                                    msg.append("\n");
                                    msg.append(ControlResources.getString("SignalStrength")).append("：").append(DataUtils.bytesToHexString(signalStrength)).append("\n");
                                }else if(fcode==(byte)0x0F){
                                    byte[] addrBytes = new byte[6];
                                    int allNum = (dataBytes[1]<<8)|dataBytes[0];
                                    int curNum = dataBytes[2];

                                    if(comMode==(byte)0x01) {
                                        msg.append(ControlResources.getString("DestinationAddress")).append("：").append(DataUtils.bytesToHexString(desAddrBytes)).append("\n");
                                        msg.append(ControlResources.getString("SourceAddress")).append("：").append(DataUtils.bytesToHexString(srcAddrBytes)).append("\n");
                                    }
                                    msg.append(ControlResources.getString("TotalNumberOfAffiliateNodes")).append("：").append(allNum).append("\n");
                                    msg.append(ControlResources.getString("NumberOfAffiliateNodes")).append("：").append(curNum).append("\n");

                                    for (int j = 0; j < curNum; j++) {
                                        System.arraycopy(dataBytes,j*addrBytes.length+3,addrBytes,0,addrBytes.length);
                                        ArrayUtils.reverse(addrBytes);
                                        msg.append(ControlResources.getString("MeterChildNodeAddr")).append(j + 1).append("：").append(DataUtils.bytesToHexString(addrBytes)).append("\n");
                                    }
                                }

                                break;
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return msg.toString();
    }

    /**
     * 检查相位
     * @param dataBytes 数据
     * @param interval 数据间隔
     * @return true:单相，false:三相
     */
    private boolean cheackPhase(byte[] dataBytes, int interval){
        int isPhase = 0;
        byte noValue = (byte) 0xFF;
        int len = dataBytes.length-interval;
        for (int j =0;j<len;j++){
            if(dataBytes[j] == noValue){
                isPhase++;
            }
        }
        return isPhase == len;
    }

    List<Float> voltageList =new LinkedList<>();
    List<Float> currentList =new LinkedList<>();
    private boolean isNewCurrent;
    private boolean isNewVoltage;
    private String formatMsg(byte[] dataBytes, List<String> strList, String unit, int radix, float multiple, int interval, byte fcode){
        StringBuilder msg = new StringBuilder();
        float num;
        byte[] dataTemp = new byte[interval];
        int start = dataBytes.length/dataTemp.length-strList.size();
        if(fcode==0x01){
            currentList.clear();
            isNewCurrent = true;
        } else if(fcode==0x02){
            voltageList.clear();
            isNewVoltage = true;
        }
        for (int j =0;j<strList.size();j++,start++){
            if(dataBytes.length-start*dataTemp.length>=dataTemp.length) {
                System.arraycopy(dataBytes, start * dataTemp.length, dataTemp, 0, dataTemp.length);
            }
            ArrayUtils.reverse(dataTemp);
            num = Integer.parseInt(DataUtils.bytesToHexString(dataTemp),radix)/multiple;
            if(fcode==0x01){
                currentList.add(num);
            } else if(fcode==0x02){
                voltageList.add(num);
            }
            msg.append(strList.get(j)).append("：").append(num).append(unit);
            if(j!=strList.size()-1){
                msg.append("\n");
            }
        }
        return msg.toString();
    }

    private final Runnable task= () -> {
        isReceive = false;
        JFXUtils.runUiThread(() -> {
            if (curBaseItem.id==13) {
                labParentNodeResult.setText(ControlResources.getString("CommunicationTimeout"));
                AnimationUtils.createTransition(labParentNodeResult, AnimationType.SHAKE).play();
            } else if (curBaseItem.id==15) {
                labNodeResult.setText(ControlResources.getString("CommunicationTimeout"));
                AnimationUtils.createTransition(labNodeResult, AnimationType.SHAKE).play();
            } else if (curBaseItem.id==16||curBaseItem.id==17) {
                labChlidNodeResult.setText(ControlResources.getString("CommunicationTimeout"));
                AnimationUtils.createTransition(labChlidNodeResult, AnimationType.SHAKE).play();
            } else if (curBaseItem.id==18) {
                labCalibrationResult.setText(ControlResources.getString("CommunicationTimeout"));
                AnimationUtils.createTransition(labCalibrationResult, AnimationType.SHAKE).play();
            }  else if (curBaseItem.id==19) {
                labMacResult.setText(ControlResources.getString("CommunicationTimeout"));
                AnimationUtils.createTransition(labMacResult, AnimationType.SHAKE).play();
            } else {
                labResult.setText(ControlResources.getString("CommunicationTimeout"));
                AnimationUtils.createTransition(labResult, AnimationType.SHAKE).play();
            }
            setScrollToBottom(textAreaShow.getText() +ControlResources.getString("Receive")+"：" + ControlResources.getString("CommunicationTimeout") +"\n");
            countingCycle();
        });
    };
    private ScheduledThreadPoolExecutor stpExecutor;
    private boolean isReceive;
    private void sendData() {
        if(serialPort==null){
            if(curBaseItem.id!=0){
                showMessage(ControlResources.getString("OpenTheSerialPort.Tip"));
            }
            setScrollToBottom(textAreaShow.getText() +ControlResources.getString("OpenTheSerialPort.Tip") +"\n");
            return;
        }

        if(curBaseItem!=null&&curBaseItem.id!=0){
            srcAddrStr = tfSourceAddress.getText().replace(" ", "").toUpperCase();
            desAddrStr = tfDestinationAddress.getText().replace(" ", "").toUpperCase();

            if(StringUtils.isEmpty(srcAddrStr)||srcAddrStr.length()>12){
                showMessage(ControlResources.getString("SourceAddress.Tip"), Message.Type.ERROR);
                AnimationUtils.createTransition(tfSourceAddress, AnimationType.SHAKE).play();
                tfSourceAddress.requestFocus();
                return;
            }
            if(StringUtils.isNotEmpty(srcAddrStr)) {
                srcAddrStr = DataUtils.formatAddress(srcAddrStr);
                tfSourceAddress.setText(srcAddrStr);
            }
            if(desAddrStr.length()>12){
                showMessage(ControlResources.getString("DestinationAddress.Tip"), Message.Type.ERROR);
                AnimationUtils.createTransition(tfDestinationAddress, AnimationType.SHAKE).play();
                tfDestinationAddress.requestFocus();
                return;
            }
            if(StringUtils.isNotEmpty(desAddrStr)) {
                desAddrStr = DataUtils.formatAddress(desAddrStr);
                tfDestinationAddress.setText(desAddrStr);
            }

            if(curBaseItem.id==13){
                parentAddrStr = tfParentNode.getText().replace(" ", "").toUpperCase();
                if(StringUtils.isEmpty(parentAddrStr)||parentAddrStr.length()>12){
                    showMessage(ControlResources.getString("ParentNode.Addr.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfParentNode, AnimationType.SHAKE).play();
                    tfParentNode.requestFocus();
                    return;
                }
                parentAddrStr = DataUtils.formatAddress(parentAddrStr);
                tfParentNode.setText(parentAddrStr);
                labParentNodeResult.setText("");
            }else if(curBaseItem.id==15){
                startNum = tfStartNum.getText().replace(" ", "").toUpperCase();
                pageNum = tfPageNum.getText().replace(" ", "").toUpperCase();
                if(StringUtils.isEmpty(startNum)){
                    showMessage(ControlResources.getString("ChildNodeQuery.StartNum.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfStartNum, AnimationType.SHAKE).play();
                    tfStartNum.requestFocus();
                    return;
                }
                if(StringUtils.isEmpty(pageNum)||Integer.parseInt(pageNum)>16){
                    showMessage(ControlResources.getString("ChildNodeQuery.PageNum.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfPageNum, AnimationType.SHAKE).play();
                    tfPageNum.requestFocus();
                    return;
                }
                labNodeResult.setText("");
            }else if (curBaseItem.id==16||curBaseItem.id==17) {
                for (int i = 0; i < gpChildNode.getChildren().size()/3; i++) {
                    TextField tfChildNode= findView(gpChildNode,"#tfChildNode"+i);
                    String tfChildNodeStr = tfChildNode.getText().replace(" ", "").toUpperCase();
                    if(StringUtils.isEmpty(tfChildNodeStr)){
                        showMessage(ControlResources.getString("ChildNode.Addr.Tip")+i, Message.Type.ERROR);
                        AnimationUtils.createTransition(tfChildNode, AnimationType.SHAKE).play();
                        tfChildNode.requestFocus();
                        return;
                    }
                    tfChildNodeStr = DataUtils.formatAddress(tfChildNodeStr);
                    tfChildNode.setText(tfChildNodeStr);
                }
                labChlidNodeResult.setText("");
            }else if(curBaseItem.id==18){
                password = tfPassword.getText().replace(" ", "").toUpperCase();
                aCurrentNum = tfACurrentNum.getText().replace(" ", "");
                bCurrentNum = tfBCurrentNum.getText().replace(" ", "");
                cCurrentNum = tfCCurrentNum.getText().replace(" ", "");
                nCurrentNum = tfNCurrentNum.getText().replace(" ", "");
                aVoltageNum = tfAVoltageNum.getText().replace(" ", "");
                bVoltageNum = tfBVoltageNum.getText().replace(" ", "");
                cVoltageNum = tfCVoltageNum.getText().replace(" ", "");
                if(StringUtils.isEmpty(password)||password.length()!=3){
                    showMessage(ControlResources.getString("password.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfPassword, AnimationType.SHAKE).play();
                    tfPassword.requestFocus();
                    return;
                }
                if(StringUtils.isEmpty(aCurrentNum)||Integer.parseInt(aCurrentNum)>127||Integer.parseInt(aCurrentNum)<-128){
                    showMessage(ControlResources.getString("Compensation.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfACurrentNum, AnimationType.SHAKE).play();
                    tfACurrentNum.requestFocus();
                    return;
                }
                if(StringUtils.isEmpty(bCurrentNum)||Integer.parseInt(bCurrentNum)>127||Integer.parseInt(bCurrentNum)<-128){
                    showMessage(ControlResources.getString("Compensation.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfBCurrentNum, AnimationType.SHAKE).play();
                    tfBCurrentNum.requestFocus();
                    return;
                }
                if(StringUtils.isEmpty(cCurrentNum)||Integer.parseInt(cCurrentNum)>127||Integer.parseInt(cCurrentNum)<-128){
                    showMessage(ControlResources.getString("Compensation.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfCCurrentNum, AnimationType.SHAKE).play();
                    tfCCurrentNum.requestFocus();
                    return;
                }
                if(StringUtils.isEmpty(nCurrentNum)||Integer.parseInt(nCurrentNum)>127||Integer.parseInt(nCurrentNum)<-128){
                    showMessage(ControlResources.getString("Compensation.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfNCurrentNum, AnimationType.SHAKE).play();
                    tfNCurrentNum.requestFocus();
                    return;
                }
                if(StringUtils.isEmpty(aVoltageNum)||Integer.parseInt(aVoltageNum)>127||Integer.parseInt(aVoltageNum)<-128){
                    showMessage(ControlResources.getString("Compensation.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfAVoltageNum, AnimationType.SHAKE).play();
                    tfAVoltageNum.requestFocus();
                    return;
                }
                if(StringUtils.isEmpty(bVoltageNum)||Integer.parseInt(bVoltageNum)>127||Integer.parseInt(bVoltageNum)<-128){
                    showMessage(ControlResources.getString("Compensation.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfBVoltageNum, AnimationType.SHAKE).play();
                    tfBVoltageNum.requestFocus();
                    return;
                }
                if(StringUtils.isEmpty(cVoltageNum)||Integer.parseInt(cVoltageNum)>127||Integer.parseInt(cVoltageNum)<-128){
                    showMessage(ControlResources.getString("Compensation.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfCVoltageNum, AnimationType.SHAKE).play();
                    tfCVoltageNum.requestFocus();
                    return;
                }
                labCalibrationResult.setText("");
            }else if (curBaseItem.id==19) {
                password = tfMacPassword.getText().replace(" ", "").toUpperCase();
                macAddrStr = tfMac.getText().replace(" ", "").toUpperCase();

                if(StringUtils.isEmpty(password)||password.length()!=3){
                    showMessage(ControlResources.getString("password.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfMacPassword, AnimationType.SHAKE).play();
                    tfMacPassword.requestFocus();
                    return;
                }
                if(StringUtils.isEmpty(macAddrStr)||macAddrStr.length()>12){
                    showMessage(ControlResources.getString("Mac.Addr.Tip"), Message.Type.ERROR);
                    AnimationUtils.createTransition(tfMac, AnimationType.SHAKE).play();
                    tfMac.requestFocus();
                    return;
                }
                macAddrStr = DataUtils.formatAddress(macAddrStr);
                tfMac.setText(macAddrStr);
                labMacResult.setText("");
            }else{
                if(allcount==1) {
                    labResult.setText("");
                }
            }

            //组报文发送帧
            textArea.setText(sendWriteStr());
        }

        //发送16进制数据——实际应用中串口通信传输的数据，大都是 16 进制
        String hexStrCode = textArea.getText().replace("\n","");
        if(hexStrCode.length() == 0){
            setScrollToBottom(textAreaShow.getText() +ControlResources.getString("SendContent.Tip") +"\n");
            return;
        }
        isReceive = true;
        byte[] bytes = SerialPortTool.hexStringToBytes(hexStrCode);
        hexStrCode =  DataUtils.separatedByChr(Objects.requireNonNull(SerialPortTool.bytesToHexString(bytes)).toUpperCase(),2," ");
        SerialPortTool.sendData(serialPort, bytes);
        textArea.setText(hexStrCode);
        setScrollToBottom(textAreaShow.getText()+ControlResources.getString("Send") + "："+ hexStrCode  + "\n");
        stpExecutor = ThreadPoolUtils.runDelayTime(task,timeout);
    }

    private final byte ADDR= (byte)0x00;
    private String srcAddrStr;
    private String desAddrStr;
    private String parentAddrStr;
    private String macAddrStr;
    private String startNum;
    private String pageNum;

    private String password;
    private String aCurrentNum,bCurrentNum,cCurrentNum,nCurrentNum,aVoltageNum,bVoltageNum,cVoltageNum;

    private String sendWriteStr(){
        byte[] senBytes = new byte[256];
        int count = 3;
        byte com = 0x20;
        if(StringUtils.isNotEmpty(srcAddrStr)&&StringUtils.isNotEmpty(desAddrStr)) {
            //源地址
            byte[] srcAddr = DataUtils.strAddrToByteAddr(srcAddrStr);
            //目的地址
            byte[] desAddr = DataUtils.strAddrToByteAddr(desAddrStr);
            if(desAddr!=null) {
                System.arraycopy(desAddr, 0, senBytes, count, desAddr.length);
                count += 6;
            }
            if(srcAddr!=null) {
                System.arraycopy(srcAddr, 0, senBytes, count, srcAddr.length);
                count += 6;
            }
        }else{
            com = 0x00;
        }

        if(curBaseItem.id==13) {
            byte[] parentAddr = DataUtils.strAddrToByteAddr(parentAddrStr);//父节点地址
            if(parentAddr!=null){
                System.arraycopy(parentAddr,0,senBytes,count,parentAddr.length);//填入父节点地址
                count+=6;
            }
        }else if(curBaseItem.id==15){
            senBytes[count++] = Byte.parseByte(startNum);
            senBytes[count++] = Byte.parseByte(pageNum);
        }else if(curBaseItem.id==16||curBaseItem.id==17){
            for (int i = 0; i < gpChildNode.getChildren().size()/3; i++) {
                TextField tfChildNode= findView(gpChildNode,"#tfChildNode"+i);
                String tfChildNodeStr = tfChildNode.getText().replace(" ", "").toUpperCase();
                //子节点地址
                byte[] childAddr = DataUtils.strAddrToByteAddr(tfChildNodeStr);
                if(childAddr!=null){
                    System.arraycopy(childAddr,0,senBytes,count,childAddr.length);
                    count+=6;
                }
            }
        }else if(curBaseItem.id==18){
            byte[] passwordAddr = DataUtils.stringToByteArray(password);//父节点地址
            senBytes[count++] = passwordAddr[0];
            senBytes[count++] = passwordAddr[1];
            senBytes[count++] = passwordAddr[2];
            senBytes[count++] = Byte.parseByte(aCurrentNum);
            senBytes[count++] = Byte.parseByte(bCurrentNum);
            senBytes[count++] = Byte.parseByte(cCurrentNum);
            senBytes[count++] = Byte.parseByte(nCurrentNum);
            senBytes[count++] = Byte.parseByte(aVoltageNum);
            senBytes[count++] = Byte.parseByte(bVoltageNum);
            senBytes[count++] = Byte.parseByte(cVoltageNum);
        }else if(curBaseItem.id==19) {
            byte[] macAddr = DataUtils.strAddrToByteAddr(macAddrStr);//父节点地址
            if(macAddr!=null){
                System.arraycopy(macAddr,0,senBytes,count,macAddr.length);//填入父节点地址
                count+=6;
            }
        }

        senBytes[0]= ADDR;//地址A
        senBytes[1]= (byte) (curBaseItem.value|com);//功能码FUN
        senBytes[2]= (byte) (count - 3);//数据域-数据长度L
        senBytes = CRC16M.updateCheckCode(senBytes,count);
        return DataUtils.bytesToHexString(senBytes);
    }

    private void stopData() {
        if(allcount<=1) {
            isReceive = false;
        }
        if(stpExecutor!=null){
            stpExecutor.shutdownNow();
            stpExecutor = null;
        }
    }

    private void setScrollToBottom(String txt){
        textAreaShow.setText(txt);
        if(allcount<=1) {
            textAreaShow.setScrollTop(Double.MAX_VALUE);
        }
    }
}
