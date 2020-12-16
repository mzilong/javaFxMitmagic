package sample.view;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.*;
import netscape.javascript.JSObject;

public class Browser extends Region {
 
    private final HBox toolBar;

    final WebView webView = new WebView();
    final WebEngine webEngine = webView.getEngine();
    final Button toggleHelpTopics = new Button("Toggle Help Topics");
    final Button btnPrint = new Button("Print");
    final ComboBox comboBox = new ComboBox();

    public Browser() {
        //apply the styles
        getStyleClass().add("browser");
                
        comboBox.setPrefWidth(60);
 
        // create the toolbar
        toolBar = new HBox();
        toolBar.setAlignment(Pos.CENTER);
        toolBar.getStyleClass().add("browser-toolbar");
        toolBar.getChildren().add(comboBox);
        toolBar.getChildren().add(btnPrint);
        toolBar.getChildren().add(createSpacer());
        toolBar.getChildren().add(toggleHelpTopics);
        //set action for the button
        toggleHelpTopics.setOnAction((ActionEvent t) -> {
            webEngine.executeScript("addActionHandler('000000000020')");
        });
        webView.setFontScale(1);
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("温馨提示");
                alert.setContentText(event.getData());
                alert.showAndWait();
            }
        });

        //process history
        final WebHistory history = webEngine.getHistory();
        history.getEntries().addListener(
            (ListChangeListener.Change<? extends WebHistory.Entry> c) -> {
                c.next();
                c.getRemoved().stream().forEach((e) -> {
                    comboBox.getItems().remove(e.getUrl());
                });
                c.getAddedSubList().stream().forEach((e) -> {
                    comboBox.getItems().add(e.getUrl());
                });
        });
 
        //set the behavior for the history combobox               
        comboBox.setOnAction((Event ev) -> {
            int offset
                    = comboBox.getSelectionModel().getSelectedIndex()
                    - history.getCurrentIndex();
            history.go(offset);
        });
 
        // process page loading
        webEngine.getLoadWorker().stateProperty().addListener(
            (ObservableValue<? extends State> ov, State oldState,
             State newState) -> {
                    if (newState == State.SUCCEEDED) {
                        JSObject win = (JSObject) webEngine.executeScript("window");
                        win.setMember("app", new JavaApp());
                    }
        });

        //processing print job
        btnPrint.setOnAction((ActionEvent e) -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                webEngine.print(job);
                job.endJob();
            }
        });
 
        //add components
        getChildren().add(toolBar);
        getChildren().add(webView);
    }

    public void load(String url) {
        webEngine.load(url);
    }

    /**
     * JavaScript interface object
     */
    public class JavaApp {
        public void exit() {
            Platform.exit();
        }
    }
 
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        double tbHeight = toolBar.prefHeight(w);
        layoutInArea(webView,0,0,w,h-tbHeight,0, HPos.CENTER, VPos.CENTER);
        layoutInArea(toolBar,0,h-tbHeight,w,tbHeight,0,HPos.CENTER,VPos.CENTER);
    }
 
    @Override
    protected double computePrefWidth(double height) {
        return 900;
    }
 
    @Override
    protected double computePrefHeight(double width) {
        return 600;
    }
}