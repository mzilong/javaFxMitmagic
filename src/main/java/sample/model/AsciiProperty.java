package sample.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class AsciiProperty {
    /** 十进制 **/
    private final SimpleIntegerProperty decimal = new SimpleIntegerProperty(this, "decimal");
    /** 二进制 **/
    private final SimpleStringProperty binary= new SimpleStringProperty(this, "binary");
    /** 十六进制 **/
    private final SimpleStringProperty hex= new SimpleStringProperty(this, "hex");
    /** html **/
    private final SimpleStringProperty html= new SimpleStringProperty(this, "html");
    /** 字符图形 **/
    private final SimpleStringProperty charGraphic= new SimpleStringProperty(this, "charGraphic");
    /** 解析 **/
    private final SimpleStringProperty parsing= new SimpleStringProperty(this, "parsing");

    public AsciiProperty(int decimal, String binary, String hex, String html, String charGraphic, String parsing) {
        setDecimal(decimal);
        setBinary(binary);
        setHex(hex);
        setHtml(html);
        setCharGraphic(charGraphic);
        setParsing(parsing);
    }

    public int getDecimal() {
        return decimal.get();
    }

    public SimpleIntegerProperty decimalProperty() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal.set(decimal);
    }

    public String getBinary() {
        return binary.get();
    }

    public SimpleStringProperty binaryProperty() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary.set(binary);
    }

    public String getHex() {
        return hex.get();
    }

    public SimpleStringProperty hexProperty() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex.set(hex);
    }

    public String getHtml() {
        return html.get();
    }

    public SimpleStringProperty htmlProperty() {
        return html;
    }

    public void setHtml(String html) {
        this.html.set(html);
    }

    public String getCharGraphic() {
        return charGraphic.get();
    }

    public SimpleStringProperty charGraphicProperty() {
        return charGraphic;
    }

    public void setCharGraphic(String charGraphic) {
        this.charGraphic.set(charGraphic);
    }

    public String getParsing() {
        return parsing.get();
    }

    public SimpleStringProperty parsingProperty() {
        return parsing;
    }

    public void setParsing(String parsing) {
        this.parsing.set(parsing);
    }
}