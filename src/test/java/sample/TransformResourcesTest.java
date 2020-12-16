package sample;

import sample.locale.TransformResources;

import java.util.Locale;

public class TransformResourcesTest {
    public static void main(String[] args) {
        TransformResources.showLocaleToUnicode(Locale.CHINA,false,false);
        TransformResources.showLocaleToUnicode(Locale.TAIWAN,false,false);
        TransformResources.showLocaleToUnicode(Locale.US,false,false);
        System.out.println("");
        System.out.println("");
        System.out.println("转化如下---------------------------------");
        TransformResources.showLocaleToUnicode(Locale.CHINA,false,true);
        TransformResources.showLocaleToUnicode(Locale.TAIWAN,false,true);
        TransformResources.showLocaleToUnicode(Locale.US,false,false);
    }
}
