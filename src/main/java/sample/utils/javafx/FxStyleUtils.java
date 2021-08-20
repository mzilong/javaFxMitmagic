package sample.utils.javafx;

import javafx.scene.Node;
import javafx.scene.Parent;

public class FxStyleUtils {

    public static void setBase(Node node,String val){
        setStyle(node,"-fx-base",val);
    }

    public static void setColorLabelVisible(Node node,boolean val){
        setStyle(node,"-fx-color-label-visible",val);
    }

    public static void setFont(Node node,String val){
        setStyle(node,"-fx-font",val);
    }
    public static void removeFont(Node node){
        removeStyle(node,"-fx-font");
    }

    public static void setTextFill(Parent node,String val){
        setStyle(node,"-fx-text-fill",val);
    }
    public static void setFontSize(Node node, int val){
        setStyle(node,"-fx-font-size",val);
    }

    public static void setBackgroundColor(Node node,String val){
        setStyle(node,"-fx-background-color",val);
    }

    public static void setStyle(Node node,String key,Object val){
        if(node==null){return;}
        removeStyle(node,key);
        node.setStyle(String.format("%s%s: %s;",node.getStyle(),key,val));
    }

    public static void removeStyle(Node node,String key){
        if(node==null||key==null||key.length()==0){return;}
        String[] styleArr = node.getStyle().split(";");
        String styleStr = "";
        for (String str:styleArr) {
            if(str.length()>0&&str.indexOf(key)==-1){
                styleStr+= str+";";
            }
        }
        node.setStyle(styleStr);
    }
}
