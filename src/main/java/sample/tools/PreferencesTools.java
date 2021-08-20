package sample.tools;

import sample.model.BaseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;
/**
 * 存储工具类
 * @author: mzl
 * Description:PreferencesTools
 * Data: 2021/8/19 16:29
 */
public class PreferencesTools {

    public static Preferences preferences = Preferences.userRoot();

    public static List<BaseItem<Locale>> LANGUAGES = new ArrayList<>();
    static {
        LANGUAGES.add(new BaseItem<>(0, "中文", Locale.CHINA));
        LANGUAGES.add(new BaseItem<>(1, "English", Locale.ENGLISH));
    }


    public static String getFxBasePref() {
        if(preferences!=null){
            return preferences.get("-fx-base","#0091ea");
        }

        return "#0091ea";
    }

    public static void setFxBasePref(String value) {
        preferences.put("-fx-base",value);
    }

    public static String getLanguage(){
        if(preferences!=null){
            return preferences.get("language", Locale.CHINA.getLanguage());
        }
        return LANGUAGES.get(0).value.getLanguage();
    }

    public static void setLanguage(Locale locale) {
        preferences.put("language",locale.getLanguage());
        setDefaultLocale(locale);
    }

    public static void initDefaultLocale(){
        //字体设置
        for (BaseItem<Locale> b: LANGUAGES) {
            if(b.value.getLanguage().equals(getLanguage())){
                setDefaultLocale(b.value);
                break;
            }
        }
    }

    public static void setDefaultLocale(Locale locale){
        Locale.setDefault(locale);
    }
}
