package sample.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import sample.tools.helper.ImageHelper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClipboardUtils {
    /**
     * 把数据设置到系统剪贴板（复制）
     */
    public static void setClipboardContent(DataFormat dataFormat, Object value) {
        // 获取系统剪贴板
        Clipboard clipboard = getClipboard();
        setClipboardContent(clipboard,dataFormat,value);
    }

    /**
     * 把数据设置到系统剪贴板（复制）
     */
    public static void setClipboardContent(Clipboard clipboard, DataFormat dataFormat, Object value) {
        // 封装文本内容
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.put(dataFormat,value);
        // 把文本内容设置到系统剪贴板
        clipboard.setContent(clipboardContent);
    }

    /**
     * 获取系统剪贴板
     */
    public static Clipboard getClipboard() {
        return Clipboard.getSystemClipboard();
    }

    /**
     * 获取系统剪贴板的数据（粘贴）
     */
    public static <T extends Object> T getClipboardContent(Clipboard clipboard ,DataFormat dataFormat) {
        // 获取剪贴板的数据
        return (T) clipboard.getContent(dataFormat);
    }

    /**
     * 获取系统剪贴板的数据（粘贴）
     */
    public static <T extends Object> T getClipboardContent(DataFormat dataFormat) {
        // 获取系统剪贴板
        Clipboard clipboard = getClipboard();
        // 获取剪贴板的数据
        return (T) getClipboardContent(clipboard,dataFormat);
    }

    /**
     * 添加Clipboard中的内容（一般拖拽和粘贴）
     * @param pane
     */
    public static void addClipboardContent(Pane pane){
        // 获取系统剪贴板
        Clipboard clipboard = getClipboard();
        addClipboardContent(clipboard,pane);
    }
    /**
     * 添加Clipboard中的内容（一般拖拽和粘贴）
     * @param clipboard
     * @param pane 指定容器下增加new HBox()-->new ImageView()
     */
    public static void addClipboardContent(Clipboard clipboard ,Pane pane){
        if(clipboard.hasImage()){
            Image image = clipboard.getImage();
            if(image!=null){
                pane.getChildren().clear();
                pane.getChildren().add(new HBox(new ImageView(image)));
            }
        }else if(clipboard.hasFiles()){
            List<File> files = clipboard.getFiles();
            if(files.size()>0) {
                HBox hBox = new HBox();
                for (File file : files) {
                    Image image = ImageHelper.image(file.getAbsolutePath());
                    hBox.getChildren().add(new ImageView(image));
                }
                pane.getChildren().clear();
                pane.getChildren().add(hBox);
            }
        }else if(clipboard.hasString()){
            Image image = ImageHelper.image(clipboard.getString());
            if(image!=null){
                pane.getChildren().clear();
                pane.getChildren().add(new HBox(new ImageView(image)));
            }else{
                pane.getChildren().clear();
                HBox hBox = new HBox(new Label(clipboard.getString()));
                hBox.setStyle("-fx-background-color:white");
                pane.getChildren().add(hBox);
            }
        }else if(clipboard.hasUrl()){
            Image image = ImageHelper.image(clipboard.getUrl());
            if(image!=null){
                pane.getChildren().clear();
                pane.getChildren().add(new HBox(new ImageView(image)));
            }

        }
    }

    /**
     * 设置拖动检测到的节点
     * @param nodes
     */
    public static void setDragDetectedNode(Node... nodes){
        for (Node node: nodes) {
            node.setOnDragDetected(mouseEvent -> {
                Node nodeSource = (Node) mouseEvent.getSource();
                Dragboard dragboard = nodeSource.startDragAndDrop(TransferMode.COPY_OR_MOVE);
                ClipboardContent content = new ClipboardContent();
                WritableImage wi = new WritableImage((int)(nodeSource.getBoundsInLocal().getWidth()),(int)(nodeSource.getBoundsInLocal().getHeight()));
                nodeSource.snapshot(new SnapshotParameters(),wi);
                //保存图片
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(wi,null);
                File file = new File("snapshot.png");
                try {
                    ImageHelper.writeImage(bufferedImage,file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                content.put(DataFormat.IMAGE, ImageHelper.image(file.getAbsolutePath()));
                dragboard.setContent(content);
                //设置拖拽预览图
                dragboard.setDragView(wi,mouseEvent.getX(),mouseEvent.getY());
                //移除图片
                file.delete();
            });
        }
    }

    /**
     * 设置拖放窗格
     * @param panes
     */
    public static void setDragDroppedPane(Pane... panes){
        for (Pane pane: panes) {
            pane.setOnDragEntered(dragEvent -> pane.setBorder(new Border(new BorderStroke(Paint.valueOf("#0091ea"), BorderStrokeStyle.SOLID,new CornerRadii(0),new BorderWidths(5)))));
            pane.setOnDragExited(dragEvent -> pane.setBorder(null));
            pane.setOnDragOver(dragEvent -> dragEvent.acceptTransferModes(dragEvent.getTransferMode()));
            pane.setOnDragDropped(dragEvent -> {
                Dragboard dragboard = dragEvent.getDragboard();
                ClipboardUtils.addClipboardContent(dragboard,pane);
            });
        }
    }
}
