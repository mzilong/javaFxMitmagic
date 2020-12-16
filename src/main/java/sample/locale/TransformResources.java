package sample.locale;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransformResources {

    /**
     * 汉字转化为Unicode编码
     * @param CN 待转化的中文
     * @return 返回转化之后的unicode编码
     */
    public static String CNToUnicode(String CN) {
        char[] utfBytes = CN.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    /**
     * unicode编码转换为汉字
     * @param unicodeStr 待转化的编码
     * @return 返回转化后的汉子
     */
    public static String UnicodeToCN(String unicodeStr) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(unicodeStr);
        char ch;
        while (matcher.find()) {
            String group = matcher.group(2);
            ch = (char) Integer.parseInt(group, 16);
            String group1 = matcher.group(1);
            unicodeStr = unicodeStr.replace(group1, ch + "");
        }

        return unicodeStr.replace("\\", "").trim();
    }

    /**
     * unicode编码转换为汉字
     * @param unicodeStr 待转化的编码
     * @return 返回转化后的汉子
     */
    public static String UnicodeToCN2(String unicodeStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = unicodeStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = unicodeStr.substring(start + 2, unicodeStr.length());
            } else {
                charStr = unicodeStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    /**
     * 显示系统语言信息
     * @param locale 系统语言
     */
    public static void showLocaleInfo(Locale locale){
        System.out.println("默认语言代码: " + locale.getLanguage());
        System.out.println("默认地区代码: " + locale.getCountry());
        System.out.println("默认语言地区代码: " + locale.toString());
        System.out.println("---------------------------------------");
        System.out.println("默认语言描述: " + locale.getDisplayLanguage());
        System.out.println("默认地区描述: " + locale.getDisplayCountry());
        System.out.println("默认语言,地区描述: " + locale.getDisplayName());
        System.out.println("---------------------------------------");
        System.out.println("在美国默认语言叫: " + locale.getDisplayLanguage(Locale.US));
        System.out.println("在美国默认地区叫: " + locale.getDisplayCountry(Locale.US));
        System.out.println("在美国默认语言,地区叫: " + locale.getDisplayName(Locale.US));
        System.out.println("在日本默认语言代码叫: " + locale.getDisplayLanguage(Locale.JAPAN));
        System.out.println("在日本默认地区代码叫: " + locale.getDisplayCountry(Locale.JAPAN));
        System.out.println("在日本默认语言,地区代码叫: " + locale.getDisplayName(Locale.JAPAN));
        System.out.println("---------------------------------------");
        System.out.println("语言环境三字母缩写: " + locale.getISO3Language());
        System.out.println("国家环境三字母缩写: " + locale.getISO3Country());
        System.out.println("---------------------------------------]");
    }

    /**
     * 显示系统所有语言信息
     */
    public static void showAllLocaleInfo(){
        // 机器已经安装的语言环境数组
        Locale[] allLocale = Locale.getAvailableLocales();
        for (Locale l1: allLocale) {
            showLocaleInfo(l1);
        }
        System.out.println("---------------------------------------");
        // 返回 ISO 3166 中所定义的所有两字母国家代码
        String[] str1 = Locale.getISOCountries();
        for (String str: str1) {
            System.out.println("国家代码: " + str);
        }
        System.out.println("---------------------------------------");
        // 返回 ISO 639 中所定义的所有两字母语言代码
        String[] str2 = Locale.getISOLanguages();
        for (String str: str2) {
            System.out.println("语言代码: " + str);
        }
    }

    /**
     * 显示系统指定语言信息
     * @param locale 系统语言
     */
    public static void updateLocale(Locale locale,boolean isShow){
        // 设置默认语言环境
        setLocale(locale);
        if(isShow) {
            // 获取计算机默认语言环境并显示
            Locale l = Locale.getDefault();
            showLocaleInfo(l);
        }
    }

    /**
     * 设置系统指定语言信息
     * @param locale 系统语言
     */
    public static void setLocale(Locale locale){
        // 设置默认语言环境
        Locale.setDefault(locale);
    }

    /**
     * 用于生成Unicode，并复制到对应系统语言资源文件中
     * @param locale 系统语言
     * @param isShow 是否显示系统语言信息
     * @param isUnicode 是否转化Unicode显示
     */
    public static void showLocaleToUnicode(Locale locale,boolean isShow, boolean isUnicode){
        updateLocale(locale,isShow);
        ResourceBundle resourceBundle =ControlResources.getResourceBundle(null);
        resourceBundle.keySet().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if(isUnicode){
                    System.out.println(s+"="+CNToUnicode(resourceBundle.getString(s)));
                }else{
                    System.out.println(s+"="+resourceBundle.getString(s));
                }
            }
        });
        System.out.println("---------------------------------------]");
    }
}
