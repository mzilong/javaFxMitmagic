package sample;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.PropertyConfigurator;
import sample.controller.WelcomeController;
import sample.tools.PreferencesTools;
import sample.utils.javafx.FxIntent;

/**
 * JavaFX Main
 */
public class Main extends FxApplication {
    @Override
    public void initFx() {
//        setDefaultLocale(Locale.ENGLISH);
//        setHighContrastTheme(HighContrastTheme.YELLOWONBLACK);
//        setApplicationStylesheet(Application.STYLESHEET_CASPIAN);
//        setElementStyleEnable(true);
        PropertyConfigurator.configure(JFXResources.getResource("config/log4j.properties"));
    }

    @Override
    public void startFx(Stage primaryStage) {
        //初始化默认字体
        PreferencesTools.initDefaultLocale();

        FxIntent intent = new FxIntent(WelcomeController.class, StageStyle.TRANSPARENT);
        intent.setPrimaryStage(primaryStage);
        intent.start();
    }

    @Override
    public void stopFx() {
        System.out.println("System.exit(0);");
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}