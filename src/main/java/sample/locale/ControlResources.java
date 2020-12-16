package sample.locale;

import java.util.ResourceBundle;

public final class ControlResources {

    private static final String BASE_NAME = "sample/locale/control";

    public static String getString(String key) {
        return getString(BASE_NAME,key);
    }

    public static String getString(String baseName, String key) {
        try {
            String strResource= getResourceBundle(baseName).getString(key);
            return strResource;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static ResourceBundle getResourceBundle(String baseName) {
        return ResourceBundle.getBundle(baseName!=null&&baseName.length()>0?baseName:BASE_NAME);
    }
}
