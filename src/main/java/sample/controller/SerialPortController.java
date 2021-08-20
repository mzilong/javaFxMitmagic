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
import org.apache.commons.lang3.StringUtils;
import sample.controller.annotation.FxmlPath;
import sample.controller.base.BaseController;
import sample.event.BaseEvent;
import sample.event.FxEventBus;
import sample.locale.ControlResources;
import sample.tools.CRC16M;
import sample.utils.DataUtils;
import sample.utils.SerialPortParameter;
import sample.utils.SerialPortTool;
import sample.utils.ThreadPoolUtils;
import sample.utils.javafx.JFXUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @author: mzl
 * Description:ChildController
 * Data: 2020/8/18 16:29
 */

@FxmlPath("fxml/serialport.fxml")
public class SerialPortController extends BaseController {

    public ComboBox<String> cbCheck;
    public ComboBox<Integer> cbRequestTime;
    public TextArea textAreaShow;

    public ComboBox<String> cbPortName;
    public ComboBox<Integer> cbBaudRate;
    public ComboBox<Integer> cbDataBits;
    public ComboBox<Float> cbStopBits;
    public ComboBox<Integer> cbParity;
    public ComboBox<String> cbFlowcontrol;
    public Button btnConfigurePort;

    public Button btnClearAll;
    public Button btnSend;
    public Button btnClear;
    public Button btnCopy;
    public Button btnPaste;
    public Button btnSelectAll;
    public TextArea textArea;

    private ObservableList<String> observableList ;
    private String curCheck;
    private int timeout = 3*1000;

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

    private void initPortName(boolean isSet){
        ArrayList<String> aspList = SerialPortTool.getAvailableSerialPorts();
        observableList = FXCollections.observableList(aspList);//转化为可观察的list，支持改变监听
        if(aspList.size() > 0) {
            cbPortName.setItems(observableList);
            if (isSet){
                serialPortParameter.setPortName(aspList.get(0));
                cbPortName.setValue(serialPortParameter.getPortName());
            }
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

        cbPortName.setOnMouseClicked(mouseEvent -> {
            initPortName(false);
        });

        cbRequestTime.getItems().addAll(1,2,3,6,10,15,20,60);
        cbRequestTime.setValue(3);

        cbCheck.getItems().addAll(SerialPortParameter.CRC_NONE,SerialPortParameter.CHECKSUM_8,SerialPortParameter.CRC_16_MODBUS);

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
    }

    private SerialPortParameter serialPortParameter;
    private SerialPort serialPort;
    private boolean isReceive;
    ScheduledThreadPoolExecutor stpExecutor;

    private Runnable task= () -> {
        isReceive = false;
        setScrollToBottom(textAreaShow.getText() +ControlResources.getString("Receive")+"：" + ControlResources.getString("CommunicationTimeout") +"\n");
    };
    private void sendData() {
        if(serialPort==null){
            setScrollToBottom(textAreaShow.getText() +ControlResources.getString("OpenTheSerialPort.Tip") +"\n");
            return;
        }
        if(isReceive){
            return;
        }
        //发送16进制数据——实际应用中串口通信传输的数据，大都是 16 进制
        String hexStrCode = textArea.getText().replace("\n","");
        if(hexStrCode == null || hexStrCode.length() == 0){
            setScrollToBottom(textAreaShow.getText() + ControlResources.getString("SendContent.Tip") +"\n");
            return;
        }
        isReceive = true;
        byte[] bytes = SerialPortTool.hexStringToBytes(hexStrCode);
        textArea.setText(DataUtils.separatedByChr(DataUtils.bytesToHexString(bytes).toUpperCase(),2," "));
        if(StringUtils.isNotBlank(curCheck)){
            if(curCheck.equals(SerialPortParameter.CHECKSUM_8)){
                bytes = CRC16M.checkSum(bytes,bytes.length);
            }else if(curCheck.equals(SerialPortParameter.CRC_16_MODBUS)){
                bytes = CRC16M.updateCheckCode(bytes,bytes.length);
            }
        }
        hexStrCode =  DataUtils.separatedByChr(Objects.requireNonNull(SerialPortTool.bytesToHexString(bytes)).toUpperCase(),2," ");
        SerialPortTool.sendData(serialPort, bytes);
        setScrollToBottom(textAreaShow.getText() + ControlResources.getString("Send")+"："+ hexStrCode  + "\n");
        stpExecutor = ThreadPoolUtils.runDelayTime(task,timeout);
    }

    @FXML
    public void onButtonClick(ActionEvent actionEvent) {
        if(actionEvent.getTarget().equals(btnClearAll)){
            textAreaShow.clear();
            textArea.clear();
        }else if(actionEvent.getTarget().equals(btnClear)){
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
        }else  if(actionEvent.getTarget().equals(cbCheck)){
            curCheck = cbCheck.getSelectionModel().getSelectedItem();
        }else  if(actionEvent.getTarget().equals(cbRequestTime)){
            String timeStr = cbRequestTime.getSelectionModel().getSelectedItem()+"";
            if(StringUtils.isNotBlank(timeStr)){
                timeout = Integer.parseInt(timeStr)*1000;
            }else{
                timeout = 3000;
            }
        }
    }
    private void closeSerialPort(){
        stopData();
        SerialPortTool.closeSerialPort(serialPort);
        serialPort = null;
        btnConfigurePort.setText(ControlResources.getString("ConfigureThePort"));
        setScrollToBottom(textAreaShow.getText() + ControlResources.getString("CloseTheSerialPort")+"\n");
    }
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
//                    if(!isReceive){
//                        return;
//                    }
                    isReceive = true;
                    byte[] copyValue = SerialPortTool.readData(serialPort);
                    stopData();
                    JFXUtils.runUiThread(() -> setScrollToBottom(textAreaShow.getText()+ControlResources.getString("Receive") + "：" + DataUtils.separatedByChr(Objects.requireNonNull(SerialPortTool.bytesToHexString(copyValue)).toUpperCase(), 2, " ") + "\n"));
                }
            }
        });
        JFXUtils.runUiThread(() -> {
            if (serialPort != null) {
                btnConfigurePort.setText(ControlResources.getString("CloseThePort"));
                setScrollToBottom(textAreaShow.getText() + ControlResources.getString("OpenSerialPort") +"【" + serialPortParameter.getPortName() + "】"+ControlResources.getString("Success") +"\n");
            } else {
                setScrollToBottom(textAreaShow.getText() +ControlResources.getString("OpenSerialPort") + "【" + serialPortParameter.getPortName() + "】"+ControlResources.getString("Fail") +"\n");
            }
        });
    }
    private void stopData() {
        isReceive = false;
        stpExecutor.shutdownNow();
    }
    private void setScrollToBottom(String txt){
        textAreaShow.setText(txt);
        textAreaShow.setScrollTop(Double.MAX_VALUE);
    }
}
