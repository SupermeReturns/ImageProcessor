package com.sdl.ImageProcessor.Processors;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
public class FadeProcessor extends ImageProcessor{

    /**
     * It takes two input image and a param into a combined image
     * two image have the same size, if not convert the second image to the size of the first
     * @img1 first image
     * @img2 second image
     * @param  between 0 and 1, it means w = p*param + q*(1-param)(p,q,w are pixels in the same position in img1, img2, and output image)
     * @return the combined image
     */ 
    public static Image fade(Image img1, Image img2, float param) {
        int width = img1.getWidth(null);
        int height = img1.getHeight(null);
        if ((width != img2.getWidth(null)) || (height != img2.getHeight(null))) {
            img2 = ImageProcessor.scale(img2, width, height);
        }
        BufferedImage buffImg1 = changeToBufferedImage(img1);
        BufferedImage buffImg2 = changeToBufferedImage(img2);
        int tRGBOfImg1[];
        int tRGBOfImg2[];
        int tRGBOfResult[] = new int[width*height*3];
        int sRGB[];
        tRGBOfImg1 = getTriaxialRGBArray(buffImg1, width, height);
        tRGBOfImg2 = getTriaxialRGBArray(buffImg2, width, height);
        for(int i=0; i<tRGBOfImg1.length; i=i+3){
            tRGBOfResult[i] = (int)(tRGBOfImg1[i] * param + tRGBOfImg2[i]* (1-param));
            tRGBOfResult[i+1] = (int)(tRGBOfImg1[i+1] * param + tRGBOfImg2[i+1]* (1-param));
            tRGBOfResult[i+2] = (int)(tRGBOfImg1[i+2] * param + tRGBOfImg2[i+2]* (1-param));
        }
        sRGB = getSingleRGBArray(tRGBOfResult);
        Image result = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, sRGB, 0, width));
        return result;
    }
}