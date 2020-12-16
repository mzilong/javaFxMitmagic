package sample.utils.javafx;

import javafx.scene.Parent;

public class FxStyleUtils {

    public static void setBase(Parent parent,String val){
        setStyle(parent,"-fx-base",val);
    }

    public static void setColorLabelVisible(Parent parent,boolean val){
        setStyle(parent,"-fx-color-label-visible",val);
    }

    public static void setTextFill(Parent parent,String val){
        setStyle(parent,"-fx-text-fill",val);
    }

    public static void setBackgroundColor(Parent parent,String val){
        setStyle(parent,"-fx-background-color",val);
    }

    public static void setStyle(Parent parent,String key,Object val){
        setStyle(parent,String.format("%s: %s;",key,val));
    }

    public static void setStyle(Parent parent,String keyAndval){
        if(parent==null||keyAndval==null||keyAndval.length()==0){return;}
        parent.setStyle(String.format("%s%s",parent.getStyle(),keyAndval));
    }
}
