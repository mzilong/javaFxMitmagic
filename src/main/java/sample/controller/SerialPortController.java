package sample.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import sample.animation.AnimationType;
import sample.animation.AnimationUtils;
import sample.controller.annotation.FxmlPath;
import sample.controller.base.BaseController;
import sample.event.BaseEvent;
import sample.event.FxEventBus;
import sample.locale.ControlResources;
import sample.utils.DataUtils;
import sample.utils.SerialPortParameter;
import sample.utils.SerialPortTool;
import sample.utils.javafx.JFXUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 *
 * @author: mzl
 * Description:ChildController
 * Data: 2020/8/18 16:29
 */

@FxmlPath("fxml/serialport.fxml")
public class SerialPortController extends BaseController {

    private ObservableList<String> observableList ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //这里一定要有判断，不然会有空指针异常
        serialPortParameter = new SerialPortParameter("");
    }

    @Override
    public String initTitle() {
        return ControlResources.getString("Title.SerialDebug");
    }

    @Override
    public String initIcon() {
        return "ic_launcher.png";
    }


    private void onCloseRequest(WindowEvent event) {
        if(serialPort!=null){
            SerialPortTool.closeSerialPort(serialPort);
            serialPort = null;
        }
    }

    @Override
    public void onShowing(WindowEvent event) {
        super.onShowing(event);
        //这里一定要有判断，不然会有空指针异常
        if (getIntent() != null) {
            String data = getIntent().getData("data");
            logger.info(data);
        }

        FxEventBus.getDefault().fireEvent(new BaseEvent("不同Controller之间的Event"));

        ArrayList<String> aspList = SerialPortTool.getAvailableSerialPorts();
        observableList = FXCollections.observableList(aspList);//转化为可观察的list，支持改变监听
        if(aspList.size() > 0){
            cbPortName.setItems(observableList);
            serialPortParameter.setPortName(aspList.get(0));
            cbPortName.setValue(serialPortParameter.getPortName());
        }
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
        cbParity.getItems().addAll(SerialPort.NO_PARITY,SerialPort.ODD_PARITY,SerialPort.EVEN_PARITY,SerialPort.MARK_PARITY,SerialPort.SPACE_PARITY);
        cbParity.setConverter(new IntegerStringConverter() {
            @Override
            public String toString(Integer object) {
                return serialPortParameter.getParityStr(object);
            }
        });
        cbParity.setValue(serialPortParameter.getParity());
        cbFlowcontrol.getItems().addAll(SerialPortParameter.FLOW_CONTROL_DISABLED,SerialPortParameter.FLOW_CONTROL_DSR_ENABLED
                ,SerialPortParameter.FLOW_CONTROL_CTS_ENABLED,SerialPortParameter.FLOW_CONTROL_DTR_ENABLED,SerialPortParameter.FLOW_CONTROL_RTS_ENABLED
                ,SerialPortParameter.FLOW_CONTROL_XONXOFF_IN_ENABLED,SerialPortParameter.FLOW_CONTROL_XONXOFF_OUT_ENABLED);
        cbFlowcontrol.setValue(serialPortParameter.getFlowcontrolStr());

        AnimationUtils.createTransition(btnSend, AnimationType.BOUNCE);

        Stage primaryStage = getIntent().getPrimaryStage();
        //监听最大化
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {});

        primaryStage.setOnCloseRequest(windowEvent -> onCloseRequest(windowEvent));

        //给Scene添加快捷键
        ObservableMap<KeyCombination, Runnable> observableMap= getIntent().getPrimaryStage().getScene().getAccelerators();
        observableMap.put(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN), new Runnable() {
            @Override
            public void run() {
                btnConfigurePort.fire();
            }
        });
        observableMap.put(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN), new Runnable() {
            @Override
            public void run() {
                btnSend.fire();
            }
        });
    }

    private SerialPortParameter serialPortParameter;
    private SerialPort serialPort;

    @FXML
    private TextArea textAreaShow;

    @FXML
    private ComboBox<String> cbPortName;
    @FXML
    private ComboBox<Integer> cbBaudRate;
    @FXML
    private ComboBox<Integer> cbDataBits;
    @FXML
    private ComboBox<Float> cbStopBits;
    @FXML
    private ComboBox<Integer> cbParity;
    @FXML
    private ComboBox<String> cbFlowcontrol;

    @FXML
    private Button btnConfigurePort;

    @FXML
    private Button btnSend;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnCopy;
    @FXML
    private Button btnPaste;
    @FXML
    private Button btnSelectAll;

    @FXML
    private TextArea textArea;

    private void sendData() {
        if(serialPort==null){
            setScrollToBottom(textAreaShow.getText() + "请先打开串口！\n");
            return;
        }
        //发送16进制数据——实际应用中串口通信传输的数据，大都是 16 进制
        String hexStrCode = textArea.getText();
        if(hexStrCode == null || hexStrCode.length() == 0){
            setScrollToBottom(textAreaShow.getText() + "请输入发送内容！\n");
            return;
        }
        byte[] bytes = SerialPortTool.hexStringToBytes(hexStrCode);
        hexStrCode =  DataUtils.separatedByChr(Objects.requireNonNull(SerialPortTool.bytesToHexString(bytes)).toUpperCase(),2," ");
        SerialPortTool.sendData(serialPort, bytes);
        textArea.setText(hexStrCode);
        setScrollToBottom(textAreaShow.getText() + "发送："+ hexStrCode  + "\n");
    }
    @FXML
    public void onButtonClick(ActionEvent actionEvent) {
        if(actionEvent.getTarget().equals(btnClear)){
            textAreaShow.clear();
        }else if(actionEvent.getTarget().equals(btnCopy)){
            textAreaShow.copy();
        }else if(actionEvent.getTarget().equals(btnPaste)){
            textAreaShow.paste();
        }else if(actionEvent.getTarget().equals(btnSelectAll)){
            textAreaShow.selectAll();
        }else if(actionEvent.getTarget().equals(btnSend)){
            sendData();
        }else if(actionEvent.getTarget().equals(btnConfigurePort)){
            configurePort();
        }
    }

    @FXML
    public void onChange(ActionEvent actionEvent) {
        if(actionEvent.getTarget().equals(cbPortName)){
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
            serialPortParameter.setParity(cbParity.getSelectionModel().getSelectedItem());
        }else  if(actionEvent.getTarget().equals(cbFlowcontrol)){
            serialPortParameter.setFlowcontrolStr(cbFlowcontrol.getSelectionModel().getSelectedItem());
        }
    }

    private void configurePort() {
        if(serialPort!=null){
            SerialPortTool.closeSerialPort(serialPort);
            serialPort = null;
            btnConfigurePort.setText("配置端口(ctrl+p)");
            setScrollToBottom(textAreaShow.getText() + "关闭串口\n");
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
                    byte[] copyValue = SerialPortTool.readData(serialPort);
                    JFXUtils.runUiThread(() -> setScrollToBottom(textAreaShow.getText() + "接受：" + DataUtils.separatedByChr(Objects.requireNonNull(SerialPortTool.bytesToHexString(copyValue)).toUpperCase(), 2, " ") + "\n"));
                }
            }
        });
        JFXUtils.runUiThread(() -> {
            if (serialPort != null) {
                btnConfigurePort.setText("关闭端口(ctrl+p)");
                setScrollToBottom(textAreaShow.getText() + "打开串口【" + serialPortParameter.getPortName() + "】成功\n");
            } else {
                setScrollToBottom(textAreaShow.getText() + "打开串口【" + serialPortParameter.getPortName() + "】失败\n");
            }
        });
    }

    private void setScrollToBottom(String txt){
        textAreaShow.setText(txt);
        textAreaShow.setScrollTop(Double.MAX_VALUE);
    }
}
