 package com.sdl.ImageProcessor.Processors;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

/**
 *  一个<code>GTransProcessor</code>用于实现灰度变换
 * 可以传递不同的参数，进行不同的灰度变换
 */
public class GTransProcessor extends ImageProcessor{

    public static final int GRAY_LEVEL_SLICING = 0;
    public static final int GRAY_LEVEL_SLICING_BINARY = 1;
    public static final int POWER_LAW = 2;
    public static final int LOG = 3;
    public static final int NEGATIVE = 4;

    public static Image process(Image img, int k,int param1, int param2) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] rArray = new int[width*height];
        int[] gArray = new int[width*height];
        int[] bArray = new int[width*height];
        ImageProcessor.getRGBChannels(img, rArray, gArray, bArray);

        if (k == GRAY_LEVEL_SLICING) {
            rArray = GLS(rArray, param1, param2);
            gArray = GLS(gArray, param1, param2);
            bArray = GLS(bArray, param1, param2);
        } else if (k == GRAY_LEVEL_SLICING_BINARY) {
            rArray = GLSB(rArray, param1, param2);
            gArray = GLSB(gArray, param1, param2);
            bArray = GLSB(bArray, param1, param2);
        } else {
            System.out.println("Wrong Parameters");
            return null;
        }

        return ImageProcessor.getImageFrom3Channels(rArray, gArray, bArray, width, height);
    }
    public static Image process(Image img, int k, double param1) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] rArray = new int[width*height];
        int[] gArray = new int[width*height];
        int[] bArray = new int[width*height];
        ImageProcessor.getRGBChannels(img, rArray, gArray, bArray);

        if (k == POWER_LAW) {
            rArray = PowerLaw(rArray, param1);
            gArray = PowerLaw(gArray, param1);
            bArray = PowerLaw(bArray, param1);
        } else {
            System.out.println("Wrong Parameters");
            return null;
        }

        return ImageProcessor.getImageFrom3Channels(rArray, gArray, bArray, width, height);
    }
    public static Image process(Image img, int k) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] rArray = new int[width*height];
        int[] gArray = new int[width*height];
        int[] bArray = new int[width*height];
        ImageProcessor.getRGBChannels(img, rArray, gArray, bArray);

        if (k == LOG) {
            rArray = Log(rArray);
            gArray = Log(gArray);
            bArray = Log(bArray);
        } else if (k == NEGATIVE) {
            rArray = Negative(rArray);
            gArray = Negative(gArray);
            bArray = Negative(bArray);
        } else {
            System.out.println("Wrong Parameters");
            return null;
        }

        return ImageProcessor.getImageFrom3Channels(rArray, gArray, bArray, width, height);
    }

    protected static int[] GLS(int[] pArray, int param1, int param2) {
        for(int i = 0; i < pArray.length; i++) {
            if ((pArray[i] >= param1) & (pArray[i] <= param2)) {
                pArray[i] = 255;
            }
        }
        return pArray;
    }

    protected static int[] GLSB(int[] pArray, int param1, int param2) {
        for(int i = 0; i < pArray.length; i++) {
            if ((pArray[i] >= param1) & (pArray[i] <= param2)) {
                pArray[i] = 255;
            } else {
                pArray[i] = 0;
            }
        }
        return pArray;
    }

    protected static int[] PowerLaw(int[] pArray, double gamma) {
        double[] spec = new double[pArray.length];
        for(int i = 0; i < pArray.length; i++) {
            spec[i] = Math.pow(pArray[i], gamma);
        }

        return ImageProcessor.scaling(spec);       
    }

    protected static int[] Log(int[] pArray) {
        double[] spec = new double[pArray.length];
        for(int i = 0; i < pArray.length; i++) {
            spec[i] = Math.log(pArray[i]+1);
        }

        return ImageProcessor.scaling(spec);
    }

    protected static int[] Negative(int[] pArray) {
        for(int i = 0; i < pArray.length; i++) {
            pArray[i] = 255 - pArray[i];
        }
        return pArray;
    }

}
