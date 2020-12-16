package sample.utils.javafx;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import sample.JFXResources;
import sample.controller.annotation.FxmlPath;

import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.Optional;
import java.util.ResourceBundle;

public class FxmlUtils {

    private static final String DIAGONAL = "/";
    private static final String SUFFIX = ".fxml";

    /**
     * This method is used to create a String with the path to the FXML file for
     * a given View class.
     *
     * This is done by taking the package of the view class (if any) and replace
     * "." with "/". After that the Name of the class and the file ending
     * ".fxml" is appended.
     *
     * If the View class is annotated with @FxmlPath then the String path supplied
     * in the annotation  value will be used.
     *<br>
     * Example: sample.controller.MainController as Controller class will be transformed to
     * "/sample/controller/main.fxml"
     *<br>
     * Example 2: fxml (located in the default resources package) will be transformed to
     * "fxml/Main.fxml"
     *<br>
     * Example 3: /sample/fxml (located in the default complete package) will be transformed to
     * "/sample/fxml/main.fxml"
     *
     * @param viewType
     *            the view class type.
     * @return the path to the fxml file as string.
     */
    public static URL createFxmlPath(Class<?> viewType) throws NoSuchFileException {
        final StringBuilder pathBuilder = new StringBuilder();

        final FxmlPath pathAnnotation = viewType.getDeclaredAnnotation(FxmlPath.class);
        Optional<FxmlPath> optionalFxmlPath= Optional.ofNullable(pathAnnotation);
        final String pathName = optionalFxmlPath.map(FxmlPath::value).map(String::trim).orElse("");

        if (pathName.isEmpty()) {
            if (viewType.getPackage() != null) {
                pathBuilder.append(DIAGONAL);
                pathBuilder.append(viewType.getPackageName().replaceAll("\\.", DIAGONAL));
                pathBuilder.append(DIAGONAL);
            }
            pathBuilder.append(viewType.getSimpleName().replace("Controller",SUFFIX));
        } else {
            pathBuilder.append(pathName.replaceAll("\\\\", DIAGONAL));
            if(!pathName.endsWith(SUFFIX)){
                if(!pathName.endsWith(DIAGONAL)){
                    pathBuilder.append(DIAGONAL);
                }
                pathBuilder.append(viewType.getSimpleName().replace("Controller",SUFFIX));
            }
        }

        URL fxmlLocation = JFXResources.getResource(pathBuilder.toString().toLowerCase());

        if(fxmlLocation==null){
            throw new NoSuchFileException(pathBuilder.toString().toLowerCase());
        }

        return fxmlLocation;
    }

    public static FXMLLoader loadFxmlFromResource(String resourcePath) {
        return loadFxmlFromResource(resourcePath, null);
    }

    public static FXMLLoader loadFxmlFromResource(String resourcePath, ResourceBundle resourceBundle) {
        return loadFxmlFromResource(JFXResources.getResource(resourcePath),resourceBundle);
    }

    public static FXMLLoader loadFxmlFromResource(URL resourceURL, ResourceBundle resourceBundle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            fxmlLoader.setLocation(resourceURL);
            fxmlLoader.setResources(resourceBundle);
            fxmlLoader.load();
            return fxmlLoader;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
