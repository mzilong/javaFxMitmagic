package sample.utils;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * Created by Administrator on 2019/3/18 0018.
 * 串口工具类
 */
public class SerialPortTool {
    private static final Logger logger = LoggerFactory.getLogger(SerialPortTool.class);//slf4j 日志记录器
    /**
     * 查找电脑上所有可用串行端口
     *
     * @return 可用所有串行端口名称列表，没有时 列表为空
     */
    public static final ArrayList<String> getAvailableSerialPorts() {
        //获得电脑主板当前所有可用串口
        SerialPort[] portList = SerialPort.getCommPorts();
        ArrayList<String> portNameList = new ArrayList<>();

        //将可用串口名添加到 List 列表
        for (SerialPort serialPort:portList) {
            String portName = serialPort.getSystemPortName();//名称如 COM1、COM2....
            portNameList.add(portName);
            System.out.println(portName + " - " + serialPort.getPortDescription());
        }
        return portNameList;
    }

    /**
     * 打开电脑上指定的串口
     *
     * @param portName 端口名称，如 COM1，为 null 时，默认使用电脑中能用的端口中的第一个
     * @param baudrate 波特率(baudrate)，如 9600
     * @param databits 数据位（databits），如 SerialPort.DATABITS_8 = 8
     * @param stopbits 停止位（stopbits），如 SerialPort.STOPBITS_1 = 1
     * @param parity   校验位 (parity)，如 SerialPort.PARITY_NONE = 0
     * @param flowcontrol   流控制 (parity)，如 SerialPort.FLOWCONTROL_NONE = 0
     * @return 打开的串口对象，打开失败时，返回 null
     */
    public static final SerialPort openSerialPort(String portName, int baudrate, int databits, int stopbits, int parity, int flowcontrol) {
        //当没有传入可用的 com 口时，默认使用电脑中可用的 com 口中的第一个
        if (portName == null || "".equals(portName)) {
            List<String> comPortList = getAvailableSerialPorts();
            if (comPortList != null && comPortList.size() > 0) {
                portName = comPortList.get(0);
            }
        }
        logger.info("开始打开串口：portName=" + portName + ",baudrate=" + baudrate + ",databits=" + databits + ",stopbits=" + stopbits + ",parity=" + parity+ ",flowcontrol=" + flowcontrol);

        //通过端口名称识别指定 COM 端口
        SerialPort serialPort = SerialPort.getCommPort(portName);
        serialPort.openPort();
        if(serialPort.isOpen()){
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING,0,0);
            serialPort.setComPortParameters(baudrate,databits,stopbits,parity);
            //设置流控制为不受控制
            serialPort.setFlowControl(flowcontrol);
            logger.info("打开串口 " + portName + " 成功...");
            return serialPort;
        }

        logger.info("打开串口 " + portName + " 失败...");
        return null;
    }

    /**
     * 往串口发送数据
     *
     * @param serialPort 串口对象
     * @param bytes      待发送数据
     */
    public static void sendData(SerialPort serialPort, byte[] bytes) {
        try {
            if (serialPort != null && serialPort.isOpen()) {
                int rs = serialPort.writeBytes(bytes,bytes.length);
                if(rs!=-1){
                    logger.info("往串口 " + serialPort.getSystemPortName() + " 发送数据：" + bytesToHexString(bytes) + " 完成...");
                }else{
                    logger.info("往串口 " + serialPort.getSystemPortName() + " 发送数据：" + bytesToHexString(bytes) + " 失败...");
                }
            } else {
                logger.error("串口没有打开，数据无法发送...");
            }
        } catch (Exception e) {
            logger.error("Exception: "+e.getMessage(),e);
            e.printStackTrace();
        }
    }

    /**
     * 从串口读取数据
     *
     * @param serialPort 要读取的串口
     * @return 读取的数据
     */
    public static byte[] readData(SerialPort serialPort) {
        try {
            //等待100毫秒，以便让数据读取完整
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] bytes = null;
        try{

            int len = serialPort.bytesAvailable();
            bytes = new byte[len];
            serialPort.readBytes(bytes, len);

            logger.info("从串口 " + serialPort.getSystemPortName() + " 接受数据：" + SerialPortTool.bytesToHexString(bytes) + " 完成...");
        } catch (Exception e) {
            logger.debug("串口异常："+e.toString());
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 给串口设置监听
     *
     * @param serialPort serialPort 要读取的串口
     * @param listener   SerialPortEventListener监听对象
     * @throws TooManyListenersException 监听对象太多
     */
    public static void setListenerToSerialPort(SerialPort serialPort, SerialPortDataListener listener) {
        if (serialPort != null) {
            //给串口添加事件监听
            serialPort.addDataListener(listener);
            //设置中断事件
//            serialPort.setBreak();
            //设置或清除DTR位
//            serialPort.setDTR();
            //设置或清除RTS位
//            serialPort.setRTS();
        }
    }

    /**
     * 关闭串口
     *
     * @param serialPort 待关闭的串口对象
     */
    public static void closeSerialPort(SerialPort serialPort) {
        if (serialPort != null) {
//            serialPort.clearBreak();
//            serialPort.clearDTR();
//            serialPort.clearRTS();
            //移除串口有数据监听
            serialPort.removeDataListener();
            serialPort.closePort();
            logger.info("关闭串口 " + serialPort.getSystemPortName());
        }
    }

    /**
     * 16进制字符串转十进制字节数组
     * 这是常用的方法，如某些硬件的通信指令就是提供的16进制字符串，发送时需要转为字节数组再进行发送
     *
     * @param hexStr 16进制字符串，如 "455A432F5600"，每两位对应字节数组中的一个10进制元素
     *               默认会去除参数字符串中的空格，所以参数 "45 5A 43 2F 56 00" 也是可以的
     * @return 十进制字节数组, 如 [69, 90, 67, 47, 86, 0]
     */
    public static byte[] hexStringToBytes(String hexStr) {
        if (hexStr == null || "".equals(hexStr.trim())) {
            return null;
        }
        hexStr = hexStr.replace(" ", "");
        int l = hexStr.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = Integer.valueOf(hexStr.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }

    /**
     * 字节转16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
   /* public static void main(String[] args) {
        //发送16进制数据——实际应用中串口通信传输的数据，大都是 16 进制
        String hexStrCode = "68 AA AA AA AA AA AA 68 13 00 DF 16";
        byte[] bytes = SerialPortTool.hexStringToBytes(hexStrCode);
        SerialPort serialPort = SerialPortTool.openSerialPort(null, SerialPortParameter.BAUDRATE_2400, SerialPortParameter.DATABITS_8, SerialPort.ONE_STOP_BIT, SerialPort.EVEN_PARITY,SerialPort.FLOW_CONTROL_DISABLED);

        SerialPortTool.setListenerToSerialPort(serialPort, new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                switch (serialPortEvent.getEventType()) {
                    case SerialPort.LISTENING_EVENT_DATA_AVAILABLE://有效数据
                        SerialPortTool.readData(serialPort);
                        SerialPortTool.closeSerialPort(serialPort);
                        break;
                }
            }
        });
        SerialPortTool.sendData(serialPort, bytes);
    }*/
}