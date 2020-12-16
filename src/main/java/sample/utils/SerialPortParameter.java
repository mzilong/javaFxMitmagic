package sample.utils;

import com.fazecast.jSerialComm.SerialPort;

/**
 * @name SerialPortParameter
 * @author mzl.
 * @date 2019/6/26.
 * @version 1.0
 * @Description 串口参数
 */
public final class SerialPortParameter {
    public static final int BAUDRATE_150 = 150;
    public static final int BAUDRATE_300 = 300;
    public static final int BAUDRATE_600 = 600;
    public static final int BAUDRATE_1200 = 1200;
    public static final int BAUDRATE_2400 = 2400;
    public static final int BAUDRATE_4800 = 4800;
    public static final int BAUDRATE_9600 = 9600;
    public static final int BAUDRATE_19200 = 19200;
    public static final int BAUDRATE_38400 = 38400;
    public static final int BAUDRATE_57600 = 57600;
    public static final int BAUDRATE_115200 = 115200;
    public static final int BAUDRATE_128000 = 128000;
    public static final int BAUDRATE_256000 = 256000;


    public static final int DATABITS_5 = 5;
    public static final int DATABITS_6 = 6;
    public static final int DATABITS_7 = 7;
    public static final int DATABITS_8 = 8;

    public static final float ONE_STOP_BIT = 1;
    public static final float ONE_POINT_FIVE_STOP_BITS = 1.5f;
    public static final float TWO_STOP_BITS = 2;

    public static final String PARITY_NONE = "None";
    public static final String PARITY_ODD = "Odd";
    public static final String PARITY_EVEN = "Even";
    public static final String PARITY_MARK = "Mark";
    public static final String PARITY_SPACE = "Space";

    public static final String FLOW_CONTROL_DISABLED = "无";
    public static final String FLOW_CONTROL_RTS_ENABLED = "RTS";
    public static final String FLOW_CONTROL_CTS_ENABLED = "CTS";
    public static final String FLOW_CONTROL_DSR_ENABLED = "DSR";
    public static final String FLOW_CONTROL_DTR_ENABLED = "DTR";
    public static final String FLOW_CONTROL_XONXOFF_IN_ENABLED = "XONXOFF_IN";
    public static final String FLOW_CONTROL_XONXOFF_OUT_ENABLED = "XONXOFF_OUT";

    /**
     * 串口名称(COM0、COM1、COM2等等)
     */
    private String portName;
    /**
     * 波特率
     * 默认：115200
     */
    private int baudRate;
    /**
     * 数据位 默认8位
     * 可以设置的值：SerialPort.DATABITS_5、SerialPort.DATABITS_6、SerialPort.DATABITS_7、SerialPort.DATABITS_8
     * 默认：SerialPort.DATABITS_8
     */
    private int dataBits;
    /**
     * 停止位
     * 可以设置的值：SerialPort.STOPBITS_1、SerialPort.STOPBITS_2、SerialPort.STOPBITS_1_5
     * 默认：SerialPort.STOPBITS_1
     */
    private int stopBits;
    /**
     * 校验位
     * 可以设置的值：SerialPort.NONE、SerialPort.ODD、SerialPort.EVEN、SerialPort.MARK、SerialPort.SPACE
     * 默认：SerialPort.NONE
     */
    private int parity;
    /**
     * 流控制
     * 可以设置的值：SerialPort.FLOWCONTROL_NONE、SerialPort.FLOWCONTROL_RTSCTS_IN、SerialPort.FLOWCONTROL_RTSCTS_OUT、SerialPort.FLOWCONTROL_XONXOFF_IN、SerialPort.FLOWCONTROL_XONXOFF_OUT
     * 默认：SerialPort.NONE
     */
    private int flowcontrol;

    /**
     * 1 载波检测 CD (Carrier Detect)
     */
    private boolean CD;
    /**
     * 2 接收数据 RXD（Received Data）
     */
    private boolean RXD;
    /**
     * 3 发送数据 TXD（Transmit Data）
     */
    private boolean TXD;
    /**
     * 4 数据终端就绪 DTR (Data Terminal Ready)
     */
    private boolean DTR;
    /**
     * 5 信号接地 SG（Signal Ground）
     */
    private boolean SG;
    /**
     * 6 数据集就绪 DSR (Data Set Ready)
     */
    private boolean DSR;
    /**
     * 7 请求发送 RTS (Request To Send)
     */
    private boolean RTS;
    /**
     * 8 请求发送 CTS (Request To Send)
     */
    private boolean CTS;
    /**
     * 9 振铃提示 RI (Ring Indicator)
     */
    private boolean RI;



    public SerialPortParameter() {
    }

    public SerialPortParameter(String portName) {
        this.portName = portName;
        this.baudRate = SerialPortParameter.BAUDRATE_2400;
        this.dataBits = SerialPortParameter.DATABITS_8;
        this.stopBits = SerialPort.ONE_STOP_BIT;
        this.parity = SerialPort.EVEN_PARITY;
        this.flowcontrol = SerialPort.FLOW_CONTROL_DISABLED;
    }

    public SerialPortParameter(String portName, int baudRate) {
        this.portName = portName;
        this.baudRate = baudRate;
        this.dataBits = SerialPortParameter.DATABITS_8;
        this.stopBits = SerialPort.ONE_STOP_BIT;
        this.parity = SerialPort.EVEN_PARITY;
        this.flowcontrol = SerialPort.FLOW_CONTROL_DISABLED;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public int getParity() {
        return parity;
    }

    public String getParityStr(int parity){
        this.parity = parity;
        return getParityStr();
    }
    public String getParityStr(){
        String parityStr;
        switch (parity){
            case SerialPort.NO_PARITY:
                parityStr = PARITY_NONE;
                break;
            case SerialPort.ODD_PARITY:
                parityStr = PARITY_ODD;
                break;
            case SerialPort.EVEN_PARITY:
                parityStr = PARITY_EVEN;
                break;
            case SerialPort.MARK_PARITY:
                parityStr = PARITY_MARK;
                break;
            case SerialPort.SPACE_PARITY:
                parityStr = PARITY_SPACE;
                break;
            default:
                parityStr = PARITY_NONE;
                break;
        }
        return parityStr;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public int setFlowcontrolStr(String txt){
        switch (txt){
            case FLOW_CONTROL_DISABLED:
                flowcontrol = SerialPort.FLOW_CONTROL_DISABLED;
                break;
            case FLOW_CONTROL_RTS_ENABLED:
                flowcontrol = SerialPort.FLOW_CONTROL_RTS_ENABLED;
                break;
            case FLOW_CONTROL_CTS_ENABLED:
                flowcontrol = SerialPort.FLOW_CONTROL_CTS_ENABLED;
                break;
            case FLOW_CONTROL_DSR_ENABLED:
                flowcontrol = SerialPort.FLOW_CONTROL_DSR_ENABLED;
                break;
            case FLOW_CONTROL_XONXOFF_IN_ENABLED:
                flowcontrol = SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED;
                break;
            case FLOW_CONTROL_XONXOFF_OUT_ENABLED:
                flowcontrol = SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED;
            break;
            default:
                flowcontrol = SerialPort.FLOW_CONTROL_DISABLED;
                break;
        }
        return flowcontrol;
    }

    public int setParityStr(String txt){
        switch (txt){
            case PARITY_NONE:
                parity = SerialPort.NO_PARITY;
                break;
            case PARITY_ODD:
                parity = SerialPort.ODD_PARITY;
                break;
            case PARITY_EVEN:
                parity = SerialPort.EVEN_PARITY;
                break;
            case PARITY_MARK:
                parity = SerialPort.MARK_PARITY;
                break;
            case PARITY_SPACE:
                parity = SerialPort.SPACE_PARITY;
                break;
            default:
                parity = SerialPort.NO_PARITY;
                break;
        }
        return parity;
    }

    public int getFlowcontrol() {
        return flowcontrol;
    }

    public String getFlowcontrolStr() {
        String flowcontrolStr;
        switch (flowcontrol){
            case SerialPort.FLOW_CONTROL_DISABLED:
                flowcontrolStr = FLOW_CONTROL_DISABLED;
                break;
            case SerialPort.FLOW_CONTROL_CTS_ENABLED:
                flowcontrolStr = FLOW_CONTROL_CTS_ENABLED;
                break;
            case SerialPort.FLOW_CONTROL_DSR_ENABLED:
                flowcontrolStr = FLOW_CONTROL_DSR_ENABLED;
                break;
            case SerialPort.FLOW_CONTROL_DTR_ENABLED:
                flowcontrolStr = FLOW_CONTROL_DTR_ENABLED;
                break;
            case SerialPort.FLOW_CONTROL_RTS_ENABLED:
                flowcontrolStr = FLOW_CONTROL_RTS_ENABLED;
                break;
            case SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED:
                flowcontrolStr = FLOW_CONTROL_XONXOFF_IN_ENABLED;
                break;
            case SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED:
                flowcontrolStr = FLOW_CONTROL_XONXOFF_OUT_ENABLED;
                break;
            default:
                flowcontrolStr = FLOW_CONTROL_DISABLED;
                break;
        }
        return flowcontrolStr;
    }

    public void setFlowcontrol(int flowcontrol) {
        this.flowcontrol = flowcontrol;
    }

    public boolean isCD() {
        return CD;
    }

    public void setCD(boolean CD) {
        this.CD = CD;
    }

    public boolean isRXD() {
        return RXD;
    }

    public void setRXD(boolean RXD) {
        this.RXD = RXD;
    }

    public boolean isTXD() {
        return TXD;
    }

    public void setTXD(boolean TXD) {
        this.TXD = TXD;
    }

    public boolean isDTR() {
        return DTR;
    }

    public void setDTR(boolean DTR) {
        this.DTR = DTR;
    }

    public boolean isSG() {
        return SG;
    }

    public void setSG(boolean SG) {
        this.SG = SG;
    }

    public boolean isDSR() {
        return DSR;
    }

    public void setDSR(boolean DSR) {
        this.DSR = DSR;
    }

    public boolean isRTS() {
        return RTS;
    }

    public void setRTS(boolean RTS) {
        this.RTS = RTS;
    }

    public boolean isCTS() {
        return CTS;
    }

    public void setCTS(boolean CTS) {
        this.CTS = CTS;
    }

    public boolean isRI() {
        return RI;
    }

    public void setRI(boolean RI) {
        this.RI = RI;
    }
}
