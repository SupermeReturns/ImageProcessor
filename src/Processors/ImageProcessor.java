/* 
 * AP(r) Computer Science GridWorld Case Study:
 * Copyright(c) 2002-2006 College Entrance Examination Board 
 * (http://www.collegeboard.com).
 *
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * @Date 2014/08/10
 * @Author SunDongliang
 * @Tool Sublime_text3
 */
package com.sdl.ImageProcessor.Processors;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

/**
 * A <code>ImageProcessor</code> is a class that process Image.
 * It can extract the red, green, blue channels and gray scale image of a given Image instance
 */
public class ImageProcessor{

    // color code
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int BLUE = 3;
    public static final int GRAY = 4;
    public static final int BAW = 5;

    // boundary adding mode
    public static final int BOUNDARY_ZERO = 0;
    public static final int BOUNDARY_MIRROR = 6;

    /**
     * change a Image into a bufferedImage
     * @param reference of Image instance to be changed
     * @return reference of BufferedImage
     */
    protected static BufferedImage changeToBufferedImage(Image sourceImage){
        int width = sourceImage.getWidth(null);
        int height = sourceImage.getHeight(null);
        BufferedImage buffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);    
        Graphics2D opImageContext = buffImage.createGraphics();
        opImageContext.drawImage(sourceImage, 0, 0, null);
        return buffImage;
    }

    protected static Image changeToImage(BufferedImage bi) {
        int width = bi.getWidth(null);
        int height = bi.getHeight(null);
        int[] sRGB = bi.getRGB(0, 0, width, height, null, 0, width);
        Image result = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, sRGB, 0, width));
        return result;
    }
    /**
     * get triaxial RGB array from a buffImage.
     * @param buffImage the reference of the BUfferedImage
     * @param width  is the width of buffImage
     * @param height is the height of buffImage
     * @return the triaxial RGB array of the bufferedImage
     */    
    protected static int[] getTriaxialRGBArray(BufferedImage buffImage, int width, int height){
        int singleRGBArray[] = new int [ width * height ];
        int triaxialRGBArray[] = new int [ width * height * 3 ];
        singleRGBArray = buffImage.getRGB(0, 0, width, height, null, 0, width);
        int index = 0;

        // use every value in singleRGBArray to create three TiraxialRGBArray values.
        for(int i=0; i<singleRGBArray.length; i++)
        {
            triaxialRGBArray[index] = (int) ((singleRGBArray[i] & 0x00FF0000) >> 16);
            triaxialRGBArray[index+1] = (int) ((singleRGBArray[i] & 0x0000FF00) >> 8);
            triaxialRGBArray[index+2] = (int) (singleRGBArray[i] & 0x000000FF);
            index = index + 3;
        }
        return triaxialRGBArray;
    }

    /**
     * change a triaxial RGB array into a single RGBArray
     * @param the triaxial RGB array to be transformed
     * @return a single RGB array
     */    
    protected static int[] getSingleRGBArray(int[] triaxialRGBArray){
        int singleRGBArray[] = new int [ triaxialRGBArray.length / 3 ];
        int index = 0;

        // use every three TiraxialRGBArray valuesto create one value in singleRGBArray .
        for(int i=0; i<singleRGBArray.length; i++)
        {
            singleRGBArray[i] = (255 & 0xff) << 24 | ( ( (int) triaxialRGBArray[index] & 0xff) << 16) | ( ( (int) triaxialRGBArray[index + 1] & 0xff) << 8) | (int) triaxialRGBArray[index+2] & 0xff;
            index = index + 3;
        }    
        return singleRGBArray;
    }

    /**
     * add boundries to four edges of an image
     * @param img
     * the triaxial RGB array to be transformed
     * @param boundry_width
     * the width of the added boundry in pixels
     * @param value
     * the value you want to fill in newly added pixels
     * @return a single RGB array
     */    
    protected static Image addBoundriesToImage(Image img, int boundry_width, int mode) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int new_width = width+2*boundry_width;
        int new_height = height+2*boundry_width;

        int[] triaxialRGB = getTriaxialRGBArray(changeToBufferedImage(img), width, height);

        int[] redChannel = getSingleChannel(triaxialRGB, RED);
        int[] greenChannel = getSingleChannel(triaxialRGB, GREEN);
        int[] blueChannel = getSingleChannel(triaxialRGB, BLUE);

        int[] newRedChannel = addBoundriesToArray(redChannel, width, boundry_width, mode);
        int[] newGreenChannel = addBoundriesToArray(greenChannel, width, boundry_width, mode);
        int[] newBlueChannel = addBoundriesToArray(blueChannel, width, boundry_width, mode);

        int[] newTriaxialRGB = combineChannels(newRedChannel, newGreenChannel, newBlueChannel);
        int[] newSingleRGB = getSingleRGBArray(newTriaxialRGB);

        Image result = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(new_width, new_height, newSingleRGB, 0, new_width));
        return result;
    }


    /**
     * add boundries to an image array
     * @singleChannel singe channel array of an image
     * @original_width original width of the image
     * @boundry_width boundry width(in pixels)
     * @value the value of added pixel
     * @return pixel array with boundries on four side
     */
    public static int[] addBoundriesToArray(int[] singleChannel, int original_width, int boundry_width, int mode) {
        int original_height = singleChannel.length/original_width;
        int new_height = original_height + 2*boundry_width;
        int new_width = original_width + 2*boundry_width;
        int[] result = new int[new_height* new_width];

        // fill the original array into the center
        for (int i = 0; i < original_height; i++) {
            for (int j = 0; j < original_width; j++) {
                result[(i+boundry_width)*new_width+ (j+boundry_width)] = singleChannel[i * original_width + j];
            }
        }
        switch(mode) {
            case BOUNDARY_ZERO: {
                    // fill the edges
                    for (int i=0; i < boundry_width; i++) { // edge of up and down
                        for (int j = 0; j < new_width; j++) {
                            result[i * new_width + j] = 0;
                            result[(new_height - 1 - i) * new_width + j] = 0;
                        }
                    }
                    for (int i = 0; i< boundry_width; i++) {
                        for (int j = 0; j < original_height; j++) { // edge of left and right
                            result[ (boundry_width+j) * new_width + i ] = 0;
                            result[ (boundry_width+j) * new_width + (new_width- 1 - i) ] = 0;                
                        }
                    }
                }
                break;
            case BOUNDARY_MIRROR: {
                    // 1.填上下左右四个边
                    for (int h = 0; h < boundry_width; h++) {                                                                // 填充上边
                        for (int w = boundry_width; w < new_width - boundry_width; w++) {
                            int mirror_h = 2*boundry_width - 1- h;
                            int mirror_w = w;
                            result[h*new_width+w] = result[mirror_h*new_width+mirror_w];
                        }
                    }
                    for (int h = boundry_width + original_height; h < new_height; h++) {                  // 填充下边
                        for (int w = boundry_width; w < new_width - boundry_width; w++) {
                            int mirror_h = 2*(boundry_width + original_height) - 1 - h;
                            int mirror_w = w;
                            result[h*new_width+w] = result[mirror_h*new_width+mirror_w];
                        }
                    }
                    for (int h = boundry_width; h < boundry_width + original_height; h++) {                  // 填充左边
                        for (int w = 0; w < boundry_width; w++) {
                            int mirror_h = h;
                            int mirror_w = 2*boundry_width - 1 - w;
                            result[h*new_width+w] = result[mirror_h*new_width+mirror_w];
                        }
                    }
                    for (int h = boundry_width; h < boundry_width + original_height; h++) {                  // 填充右边
                        for (int w = boundry_width + original_width; w < new_width; w++) {
                            int mirror_h = h;
                            int mirror_w = 2*(boundry_width+original_width) - 1 - w;
                            result[h*new_width+w] = result[mirror_h*new_width+mirror_w];
                        }
                    }
                    // 2.填四个角
                    for (int h = 0; h < boundry_width; h++) {                                                              // 填充左上角
                        for (int w = 0; w < boundry_width; w++) {
                            int mirror_h = 2*boundry_width - 1- h;
                            int mirror_w = 2*boundry_width - 1 - w;
                            result[h*new_width+w] = result[mirror_h*new_width+mirror_w];                        
                        }
                    }
                    for (int h = 0; h < boundry_width; h++) {                                                              // 填充右上角
                        for (int w = boundry_width + original_width; w < new_width; w++) {
                            int mirror_h = 2*boundry_width - 1- h;
                            int mirror_w = 2*(boundry_width+original_width) - 1 - w;
                            result[h*new_width+w] = result[mirror_h*new_width+mirror_w];                        
                        }
                    }
                    for (int h = boundry_width + original_height; h < new_height; h++) {                // 填充左下角
                        for (int w = 0; w < boundry_width; w++) {
                            int mirror_h = 2*(boundry_width + original_height) - 1 - h;
                            int mirror_w = 2*boundry_width - 1 - w;
                            result[h*new_width+w] = result[mirror_h*new_width+mirror_w];                        
                        }
                    }
                    for (int h = boundry_width + original_height; h < new_height; h++) {                // 填充右下角
                        for (int w = boundry_width + original_width; w < new_width; w++) {
                            int mirror_h = 2*(boundry_width + original_height) - 1 - h;
                            int mirror_w = 2*(boundry_width+original_width) - 1 - w;
                            result[h*new_width+w] = result[mirror_h*new_width+mirror_w];                        
                        }
                    }
                }
                break;
            default: {
                System.out.println("Invalid boundary adding mode");
                return null;
            }
        }

        return result;
    }

    /**
     * extract single channel from an triaxial rgb array
     * @param the channel you want to extract
     * @return extracted array with only an channel
     */
    protected static int[] getSingleChannel(int[] triaxialRGBArray, int channel) {
        int[] singleChannel = new int[triaxialRGBArray.length/3];
        switch (channel) {
            case RED:
                for (int i = 0; i < singleChannel.length; i++) {
                    singleChannel[i] = triaxialRGBArray[i*3];
                }
                break;
            case GREEN:
                for (int i = 0; i < singleChannel.length; i++) {
                    singleChannel[i] = triaxialRGBArray[i*3 + 1];
                }
                break;
            case BLUE:
                for (int i = 0; i < singleChannel.length; i++) {
                    singleChannel[i] = triaxialRGBArray[i*3 + 2];
                }
                break;
            case GRAY:
                for(int i=0; i<singleChannel.length; i++){
                    int scale = (int)(0.299 *  triaxialRGBArray[i] + 0.587 * triaxialRGBArray[i+1] + 0.114 * triaxialRGBArray[i+2]);
                    singleChannel[i] = scale;
                }
                break;
            default:
                System.out.println("Invaild Channel Code, valid option should be : RED, GREEN, BLUE");
        }
        return singleChannel;
    }

    protected static int[] getSingleChannel(Image sourceImage, int channel) {
                int width = sourceImage.getWidth(null);
                int height = sourceImage.getHeight(null);
                int[]  result = getSingleChannel(getTriaxialRGBArray(changeToBufferedImage(sourceImage), width, height), channel);
                return result;
    }

    public static int getRGBChannels(Image sourceImage, int[] arrayR, int[] arrayG, int[] arrayB) {
                if ((arrayR.length != arrayG.length) || (arrayG.length != arrayB.length)) {
                    System.out.println("Error, arrayR, arrayG, arrayB should be the same size");
                    return 1;
                }

                int width = sourceImage.getWidth(null);
                int height = sourceImage.getHeight(null);
                int[] trialArray = getTriaxialRGBArray(changeToBufferedImage(sourceImage), width, height);
                if (arrayR.length*3 != trialArray.length) {
                    System.out.println("Error, length of arrayR does not match the image ");
                    return 1;
                }

                for (int i = 0; i < arrayR.length; i++) {
                    arrayR[i] = trialArray[i*3];
                    arrayG[i] = trialArray[i*3+1];
                    arrayB[i] = trialArray[i*3+2];
                }
                return 0;
    }

    /**
     * combine 3 channels of array into one(which should be the same size)
     * @param red
     * the array of red channels
     * @param green
     * the array of green channels
     * @param red
     * the array of blue channels
     * @return the combined array of three channels
     */
    protected static int[] combineChannels(int[] red, int[] green, int[] blue) {
        int[] result = new int[red.length*3];
        for (int i = 0; i < red.length; i++) {
            result[i*3] = red[i];
            result[i*3+1] = green[i];
            result[i*3+2] = blue[i];
        }
        return result;
    }

    public static Image getImageFrom3Channels(int[] red, int[] green, int[] blue, int width, int height) {
        int[] combinedChannels = combineChannels(red, green, blue);
        int[] sRGB = ImageProcessor.getSingleRGBArray(combinedChannels);
        Image result = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, sRGB, 0, width));
        return result;
    }

    public static Image getImageFrom1Channel(int[] channel, int width, int height) {
        int[] combinedChannels = new int[3*channel.length];
        for (int i =0; i < channel.length; i++) {
            combinedChannels[i*3] = channel[i];
            combinedChannels[i*3+1] = channel[i];
            combinedChannels[i*3+2] = channel[i];
        }
        int[] sRGB = ImageProcessor.getSingleRGBArray(combinedChannels);
        Image result = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, sRGB, 0, width));
        return result;
    }    
    /**
     * processChannel method takes care of the process of extracting channels
     * it can extract red, green, blue channels and gray scale image
     * depending on the color parameter.
     * @param color it decides which channel to extract
     * @param sourceImage the reference of Image to be extracted
     * @return Image with a certain channel
     */
    private static Image processChannel(int color, Image sourceImage) {
        int width = sourceImage.getWidth(null);
        int height = sourceImage.getHeight(null);
        BufferedImage buffImage = changeToBufferedImage(sourceImage);
        int triaxialRGBArray[];
        int singleRGBArray[];
        triaxialRGBArray = getTriaxialRGBArray(buffImage, width, height);

        // use different values for RGB depending on color value
        switch( color ) {
            // red channel :(R, G, B) => (R,0,0)
            case RED:
                for(int i=0; i<triaxialRGBArray.length; i=i+3){
                    triaxialRGBArray[i+1] = 0;
                    triaxialRGBArray[i+2] = 0;
                }            
                break;
            // green channel : (R, G, B) => (0, G,0)
            case GREEN:
                for(int i=0; i<triaxialRGBArray.length; i=i+3){
                    triaxialRGBArray[i] = 0;
                    triaxialRGBArray[i+2] = 0;
                }
                break;
            // blue channel: (R, G, B) => (0, 0, B)
            case BLUE:
                for(int i=0; i<triaxialRGBArray.length; i=i+3){
                    triaxialRGBArray[i] = 0;
                    triaxialRGBArray[i+1] = 0;
                }            
                break;
            // gray scale image: I = 0.299 * R + 0.587 * G + 0.114 * B
            // (R, G, B) => (I, I, I)
            case GRAY:
                for(int i=0; i<triaxialRGBArray.length; i=i+3){
                    int scale = (int)(0.299 *  triaxialRGBArray[i] + 0.587 * triaxialRGBArray[i+1] + 0.114 * triaxialRGBArray[i+2]);
                    triaxialRGBArray[i] = scale;            
                    triaxialRGBArray[i+1] = scale;
                    triaxialRGBArray[i+2] = scale;
                }
                break;
            case BAW:
                for(int i=0; i<triaxialRGBArray.length; i=i+3){
                    int scale;
                    if (triaxialRGBArray[i]>127)
                        scale = 255;
                    else
                        scale = 0;
                    triaxialRGBArray[i] = scale;            
                    triaxialRGBArray[i+1] = scale;
                    triaxialRGBArray[i+2] = scale;
                }
                break;      
            // invalid color code
            default:
                System.out.println("Invalid color code: " + color);
                return (Image)null;
        }

        singleRGBArray = getSingleRGBArray(triaxialRGBArray);
        buffImage.setRGB(0, 0, width, height, singleRGBArray, 0, width);
        return buffImage;
    }

    /**
     * It extract the red channel of the sourceImage
     * In fact, it calls processChannel method to do its job
     * @param the reference of Image to extract
     * @return Image instance of the red channel
     */
    public  static Image showChanelR(Image sourceImage) {
        return processChannel(RED, sourceImage);
    }

    /**
     * It extract the green channel of the sourceImage
     * In fact, it calls processChannel method to do its job 
     * @param the reference of Image to extract
     * @return Image instance of the green channel
     */
    public  static Image showChanelG(Image sourceImage) {
        return processChannel(GREEN, sourceImage);
    }

    /**
     * It extract the blue channel of the sourceImage
     * In fact, it calls processChannel method to do its job
     * @param the reference of Image to extract
     * @return Image instance of the blue channel
     */
    public static Image showChanelB(Image sourceImage) {
        return processChannel(BLUE, sourceImage);
    }

    /**
     * It extract the gray scale image of the sourceImage
     * In fact, it calls processChannel method to do its job
     * @param the reference of Image to extract
     * @return Image instance of the green scale image
     */
    public static Image showGray(Image sourceImage) {
        return processChannel(GRAY, sourceImage);
    }

    /**
     * It takes a image and turn it into black and white image
     * In fact, it calls processChannel method to do its job
     * @param the reference of Image to extract
     * @return Image instance of the black and white image
     */
    public static Image showBAW(Image sourceImage) {
        return processChannel(BAW, sourceImage);
    }

    /**
     * Takes a gray image and a target number of gray levels as input, and 
     * generates the quantized image as output
     * In fact, it calls processChannel method to do its job
     * @param Image the reference of Image to extract
     * @param level a target number of gray levels
     * @return Image instance of the quantized image
     */
    public static Image quantize(Image sourceImage, int level) {
        int width = sourceImage.getWidth(null);
        int height = sourceImage.getHeight(null);
        BufferedImage buffImage = changeToBufferedImage(sourceImage);
        int triaxialRGBArray[];
        int singleRGBArray[];
        triaxialRGBArray = getTriaxialRGBArray(buffImage, width, height);

        float scale = 255 / (level - 1);
        for(int i=0; i<triaxialRGBArray.length; i=i+3){
            int v = (int )((int)(triaxialRGBArray[i] / scale + 0.5) * scale+ 0.5);
            triaxialRGBArray[i] = v;
            triaxialRGBArray[i+1] = v;
            triaxialRGBArray[i+2] = v;
        }

        singleRGBArray = getSingleRGBArray(triaxialRGBArray);
        buffImage.setRGB(0, 0, width, height, singleRGBArray, 0, width);
        return buffImage;
    }

    /**
     * Takes a gray image and a target size as input, and 
     * generates the scaled image as output
     * @param Image the reference of Image to extract
     * @param width width of output image
     * @param height height of output image
     * @return Image instance of the scaled image
     */
    public static  Image scale(Image sourceImage, int targetWidth, int targetHeight) {
        int sourceWidth = sourceImage.getWidth(null);
        int sourceHeight = sourceImage.getHeight(null);

        BufferedImage buffImage = changeToBufferedImage(sourceImage);
        int singleRGBArray[] = buffImage.getRGB(0, 0, sourceWidth, sourceHeight, null, 0, sourceWidth);
        singleRGBArray = resizeBilinear(singleRGBArray, sourceWidth, sourceHeight, targetWidth, targetHeight);
        Image result = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(targetWidth, targetHeight, singleRGBArray, 0, targetWidth));
        return result;
    }

    private static int[] resizeBilinear(int[] pixels, int w, int h, int w2, int h2) {
        int[] temp = new int[w2*h2] ;
        int a, b, c, d, x, y, index ;
        float x_ratio = ((float)(w-1))/w2;
        float y_ratio = ((float)(h-1))/h2;
        float x_diff, y_diff, blue, red, green ;
        int offset = 0 ;
        for (int i=0;i<h2;i++) {
            for (int j=0;j<w2;j++) {
                x = (int)(x_ratio * j) ;
                y = (int)(y_ratio * i) ;
                x_diff = (x_ratio * j) - x ;
                y_diff = (y_ratio * i) - y ;
                index = (y*w+x) ;                
                a = pixels[index] ;
                b = pixels[index+1] ;
                c = pixels[index+w] ;
                d = pixels[index+w+1] ;

                // blue element
                // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                blue = (a&0xff)*(1-x_diff)*(1-y_diff) + (b&0xff)*(x_diff)*(1-y_diff) +
                       (c&0xff)*(y_diff)*(1-x_diff)   + (d&0xff)*(x_diff*y_diff);

                // green element
                // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                green = ((a>>8)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>8)&0xff)*(x_diff)*(1-y_diff) +
                        ((c>>8)&0xff)*(y_diff)*(1-x_diff)   + ((d>>8)&0xff)*(x_diff*y_diff);

                // red element
                // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                red = ((a>>16)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>16)&0xff)*(x_diff)*(1-y_diff) +
                      ((c>>16)&0xff)*(y_diff)*(1-x_diff)   + ((d>>16)&0xff)*(x_diff*y_diff);

                temp[offset++] = 
                        0xff000000 | // hardcode alpha
                        ((((int)red)<<16)&0xff0000) |
                        ((((int)green)<<8)&0xff00) |
                        ((int)blue) ;
            }
        }
        return temp ;
    }

    public static int[] scaling(double[] spec){
        int[] pArray = new int[spec.length];
        double max, min;
        max = spec[0];
        min = spec[0];
        for (int i = 0; i < spec.length; i++) {
            if (spec[i] > max)
                max = spec[i];
            if (spec[i] < min)
                min = spec[i];
        }
        double len = max - min;

        for (int i = 0; i < spec.length; i++) {
            int s = (int)((spec[i]-min)/len*255);
            if (s>255)
                s = 255;
            if (s<0)
                s = 0;
            pArray[i] = s;
        }
        return pArray;
    }
}
