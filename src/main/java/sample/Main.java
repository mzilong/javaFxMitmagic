package sample;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.PropertyConfigurator;
import sample.controller.WelcomeController;
import sample.utils.javafx.FxIntent;

/**
 * JavaFX Main
 */
public class Main extends FxApplication {
    @Override
    public void initFx() {
//        setDefaultLocale(Locale.US);
//        setHighContrastTheme(HighContrastTheme.YELLOWONBLACK);
//        setApplicationStylesheet(Application.STYLESHEET_CASPIAN);
        PropertyConfigurator.configure(JFXResources.getResource("config/log4j.properties"));
    }

    @Override
    public void startFx(Stage primaryStage) {
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