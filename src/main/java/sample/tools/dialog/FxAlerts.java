package sample.tools.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import sample.utils.javafx.JFXUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * 系统对话框封装
 */
public class FxAlerts {

    public static void error(String title, String message, Image icon) {
        alert(Alert.AlertType.ERROR, title, message, icon);
    }

    public static void error(String title, Image icon, Throwable throwable) {
        boolean noMessage = isBlank(throwable.getMessage());
        String message = noMessage ? throwable.toString() : throwable.getMessage();
        String details = getStackTrace(throwable);
        alert(Alert.AlertType.ERROR, title, message, details, icon);
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static void info(String title, String message, Image icon) {
        alert(Alert.AlertType.INFORMATION, title, message, icon);
    }

    public static void warn(String title, String message, Image icon) {
        alert(Alert.AlertType.WARNING, title, message, icon);
    }

    public static boolean confirmOkCancel(String title, Image icon, String message) {
        return _alert(Alert.AlertType.CONFIRMATION, title, message, null, icon, ButtonType.OK, ButtonType.CANCEL) == ButtonType.OK;
    }

    public static boolean confirmYesNo(String title, Image icon, String message) {
        return _alert(Alert.AlertType.CONFIRMATION, title, message, null, icon,  ButtonType.YES, ButtonType.NO) == ButtonType.YES;
    }

    public static void alert(Alert.AlertType alertType, String title, String message, Image icon, ButtonType... buttonTypes) {
        alert(alertType, title, message,null, icon,buttonTypes);
    }

    public static void alert(Alert.AlertType alertType, String title, String message, String details, Image icon){
        alert(alertType, title, message,details, icon,ButtonType.OK);
    }
    private static void alert(Alert.AlertType alertType, String title, String message, String details, Image icon, ButtonType... buttonTypes){
        _alert(alertType, title, message, details, icon, buttonTypes);
    }

    public static int confirmYesNoCancel(String title, Image icon, String message) {
        ButtonType buttonType = _alert(Alert.AlertType.CONFIRMATION, title, message, null, icon, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        return buttonType ==ButtonType.YES?1: buttonType ==ButtonType.NO?0:-1;//1:YES,0:NO,-1:CANCEL
    }

    private static ButtonType _alert(Alert.AlertType alertType, String title, String message, String details, Image icon, ButtonType... buttonTypes){
        try {
            Alert alert = new Alert(alertType, message.trim(), buttonTypes);
            alert.setTitle(title);
            alert.setHeaderText(null);
            if(details!=null){
                TextArea textArea = new TextArea(details);
                textArea.setEditable(false);
                textArea.setWrapText(true);

                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(textArea, 0, 0);

                alert.getDialogPane().setExpandableContent(expContent);
            }
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            JFXUtils.setupIcon(stage,icon);

            Optional<ButtonType> result = alert.showAndWait();
            return result.orElse(ButtonType.CANCEL);
        } catch (Exception e) {
            e.printStackTrace();
            return ButtonType.CANCEL;
        }
    }
}
