module sample {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
    requires com.jfoenix;
    requires java.desktop;
    requires java.datatransfer;
    requires java.prefs;
    requires javafx.swing;
    requires javafx.web;
    requires jdk.jsobject;
    requires org.slf4j;
    requires log4j;
    opens sample.controller to javafx.fxml;
    opens sample.tools to javafx.base;
    exports sample.tools to javafx.base;
    exports sample.controller to javafx.fxml;
    exports sample.view to javafx.fxml;
    exports sample to javafx.graphics,org.apache.commons.io;

}