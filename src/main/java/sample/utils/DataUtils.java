package sample.utils;

public class DataUtils {
    /**
     * 间隔多少指定分隔字符串
     *
     * @param str 要修改的字符
     * @param num 间隔多少位
     * @param chr 指定分隔字符串
     * @return 字符串
     */
    public static String separatedByChr(String str, int num, String chr) {
        return str.replaceAll("(\\w{" + num + "})(?=.)", "$1" + chr);
    }
}
