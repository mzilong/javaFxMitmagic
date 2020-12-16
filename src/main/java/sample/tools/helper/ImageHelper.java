package sample.tools.helper;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
/**
 * 图片助手
 * @author mzl
 * @Description ImageHelper
 * @Data  2020/9/4 17:02
 */
public class ImageHelper {

    public static final String CLASSPATH_PREFIX = "classpath:";

    public static final String HTTP_PREFIX = "http:";

    public static final String HTTPS_PREFIX = "https:";

    public static final String FILE_PREFIX = "file:";

    /**
     * 根据 path 得到 image 对象。
     *
     * @param path 如果以 classpath: 开头则从资源中读取，
     *             如果以 http: https: 或 file: 开头则作为 URL 读取，
     *             否则作为本地文件路径读取。
     *
     * @return Image 对象
     */
    public static Image image(String path) {
        try {
            final String lowerCasePath = path.toLowerCase();

            Image image;
            if (lowerCasePath.startsWith(CLASSPATH_PREFIX)) {
                image = new Image(ImageHelper.class.getResourceAsStream(path.substring(CLASSPATH_PREFIX.length())));
            } else if (lowerCasePath.startsWith(HTTP_PREFIX)
                || lowerCasePath.startsWith(HTTPS_PREFIX)
                || lowerCasePath.startsWith(FILE_PREFIX)) {
                image = new Image(path);
            } else {
                image = new Image(Files.newInputStream(Paths.get(path)));
            }
            return image;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取图片BufferedImage
     * @param path 图片路径
     */
    public static BufferedImage getBufferedImage(String path) {
        return getBufferedImage(new File(path));
    }

    public static BufferedImage getBufferedImage(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bufferedImage;
    }

    /**
     * 获取javafx图片
     * @param path 图片路径
     */
    public static Image getFXImage(String path) {
        return getFXImage(new File(path));
    }

    public static Image getFXImage(File file) {
        Image image = null;
        try {
            image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
        } catch (Exception e) {
            image = new Image("file:" + file.getAbsolutePath());
        }
        return image;
    }

    public static Image getFXImage(byte[] bytes) {
        Image image = new Image(new ByteArrayInputStream(bytes));
        return image;
    }


    public static BufferedImage getFXImage(Image img, BufferedImage bimg) {
        return SwingFXUtils.fromFXImage(img, bimg);
    }

    /**
     * 保存图片
     * @param image
     * @param file
     */
    public static void writeImage(Image image, File file) throws Exception{
        writeImage(getFXImage(image, null),file);
    }

    public static void writeImage(BufferedImage bufferedImage, File file) throws IOException {
        String filename = file.getName();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        ImageIO.write(bufferedImage, extension,file);
    }
}
