package sample.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * @create mzl
 * @date 2021/4/14 13:43
 */
public class DataUtils {

    /**
     * 将String转化为byte[]数组(ascii)
     * @param arg
     *            需要转换的String对象
     * @return 转换后的byte[]数组
     */
    public static  byte[] stringToByteArray(String arg) {
        if (arg != null) {
            /* 1.先去除String中的' '，然后将String转换为char数组 */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }

            byte[] byteArray = new byte[length];
            for (int i = 0; i < length; i++) {
                byteArray[i] = (byte)NewArray[i];
            }
            return byteArray;

        }
        return new byte[] {};
    }

    /**
     * 将String转化为byte[]数组(hex)
     *
     * @param string 需要转换的String对象
     * @return 转换后的byte[]数组
     */
    public static byte[] stringToBytes(String string) {
        if (string == null || string.length() <= 0) {
            return null;
        }
        byte[] bys = new byte[string.length() / 2];
        for (int i = 0, j = 0; i < string.length(); i += 2, j++) {
            bys[j] = (byte) Integer.parseInt(string.substring(i, i + 2), 16);
        }
        return bys;
    }

    /**
     * 字节转16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || length <= 0) {
            return null;
        }
        for (int i = 0; i < length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }
    public static String bytesToHexString(byte[] src) {
        return bytesToHexString(src,src.length);
    }
    public static String bytesToHexString(byte src) {
        return bytesToHexString(new byte[]{src},1);
    }

    public static byte[] strAddrToByteAddr(String addr) {
        if (addr.length() != 12) {
            return null;
        }
        return strAddrToBytes(addr);
    }


    public static byte[] strAddrToBytes(String strData) {
        if (strData.length() % 2 != 0) {
            strData += "0";
        }

        int dataLen = strData.length();
        int buffLen = strData.length() / 2;
        byte[] byteData = new byte[buffLen];

        for (int i = 0, j = dataLen - 2; i < buffLen; i++, j -= 2) {//反序
            byteData[i] = (byte) (Integer.parseInt(strData.substring(j, j + 2), 16) & 0xff);
        }

        return byteData;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * byte[]转int
     *
     * @param bytes
     * @param length
     * @return
     */
    public static int byteArrayToInt(byte[] bytes, int length) {
        int value = 0;
        for (int i = 0; i < length; i++) {
            int shift = (length - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;
        }
        return value;
    }

    /**
     * 间隔多少指定分隔字符串
     *
     * @param str 要修改的字符
     * @param num 间隔多少位
     * @param chr 指定分隔字符串
     * @return
     */
    public static String separatedByChr(String str, int num, String chr) {
        return str.replaceAll("(\\w{" + num + "})(?=.)", "$1" + chr);
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    /**
     * 十进制转十六进制字符串
     *
     * @param integer
     * @return
     */
    public static String integerToHex(int integer) {
        return Integer.toHexString((integer & 0x000000ff) | 0xffffff00).substring(6).toUpperCase();
    }

    /**
     * 十进制数据转换为十六进制字符串数
     *
     * @param dec
     * @return
     */
    public static String decToHex(String dec) {
        BigInteger data = new BigInteger(dec, 10);
        return data.toString(16);
    }

    /**
     * 十六进制数据转换为十进制字符串数
     *
     * @param hex
     * @return
     */
    public static String hexToDec(String hex) {
        BigInteger data = new BigInteger(hex, 16);
        return data.toString(10);
    }

    /**
     * 输入一个不超过8位的正的十六进制数字符串，将它转换为正的十进制数后输出。
     * 　　注：十六进制数中的10~15分别用大写的英文字母A、B、C、D、E、F表示。
     * 样例输入
     * FFFF
     * 样例输出
     * 65535
     */
    public static Long hexParseLong(String str) {
        return Long.parseLong(str, 16);
    }

    /**
     * 二进制字符串转十进制
     *
     * @param binary
     * @return
     */
    public static int convertToDecimal(String binary) {
        return Integer.valueOf(binary, 2);
    }

    /**
     * 二进制字符串转十六进制字符串
     *
     * @param bytes
     * @return
     */
    public static String byteStrToHexStr(String bytes) {
        return Integer.toHexString(Integer.valueOf(bytes, 2));
    }

    /**
     * 十进制转二进制
     *
     * @param integer
     * @return
     */
    public static String integerToBinary(int integer) {
        String tempStr = Integer.toBinaryString(integer);
        while (tempStr.length() < 8) {
            tempStr = "0" + tempStr;
        }
        return tempStr;
    }

    /**
     * 二进制转十进制
     *
     * @param str
     * @return
     */
    public static int binaryToInteger(String str) {
        return Integer.parseInt(str, 2);
    }

    /**
     * 十六进制字符串转十进制
     *
     * @param str
     * @return
     */
    public static int hexParseInt(String str) {
        return Integer.parseInt(str, 16);
    }
    /**
     * 十进制字符串转十进制
     *
     * @param str
     * @return
     */
    public static int decParseInt(String str) {
        return Integer.parseInt(str);
    }

    public static String formatAddress(String address) {
        address = address.replace(" ", "");
        String result = null;
        if (address.length() <= 12) {
            String string = "";
            for (int i = 0; i < 12 - address.length(); i++) {
                string = string + "0";
            }
            result = string + address;
        } else {
            result = address.substring(address.length() - 12);
        }

        return result;
    }

    /**
     * int到byte[]
     * @param i
     * @return
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }
    public static String intToAscii(int i) {
        return bytesToAscii(intToByteArray(i),0,4,null);
    }
    public static String intToAscii(int i,String charsetName) {
        return bytesToAscii(intToByteArray(i),0,4,charsetName);
    }
    public static String bytesToAscii(byte[] bytes, int offset, int dateLen,String charsetName) {
        if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
            return null;
        }
        if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
            return null;
        }

        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);
        String asciiStr = null;
        try {
            if(charsetName==null){
                asciiStr = new String(data);
            }else{
                asciiStr = new String(data, charsetName);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return asciiStr;
    }

    /**
     * 指定长度在前面补0
     * @param code
     * @param length
     * @return
     */
    public static String makeUpCode(String code, int length) {
        String makeUpCode = code.replace(" ", "");
        String result = null;
        if (makeUpCode.length() <= length) {
            String string = "";
            for (int i = 0; i < length - makeUpCode.length(); i++) {
                string = string + "0";
            }
            result = string + makeUpCode;
        } else {
            result = makeUpCode.substring(makeUpCode.length() - length);
        }

        return result;
    }
}
