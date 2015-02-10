package com.sdl.ImageProcessor.Processors;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

public class EqualizeHistProcessor extends ImageProcessor{

    // size of histogram image
    public static final int HISTOGRAM_SIZE = 280;
    public static final int RGB_SEPERATE_MODE = 0;
    public static final int RGB_AVERAGE_MODE = 1;
    /**
     * applies histogram equalization on a image. 
     * @img1 image to be equalized
     * @return the equalized image whose histogram is approximately flat. 
     */ 
    public static Image process(Image img) {
        return process(img, RGB_SEPERATE_MODE);
    }

    public static Image process(Image img, int type) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int pixelNum = width * height;

        // compute img's histogram and transform it to probalities(0<p<1)
        int[] histR = getHistogramStatics(img, RED);
        int[] histG = getHistogramStatics(img, GREEN);
        int[] histB = getHistogramStatics(img, BLUE);


        int[] transTableR, transTableG, transTableB; // transformation table
        switch(type) {
            case RGB_SEPERATE_MODE: {
                float[] histR_Pr = new float[histR.length];
                float[] histG_Pr = new float[histG.length];
                float[] histB_Pr = new float[histB.length];
                for (int i = 0; i < histR.length; i++) {
                    histR_Pr[i] = histR[i] / (float)pixelNum;
                    histG_Pr[i] = histG[i] / (float)pixelNum;
                    histB_Pr[i] = histB[i] / (float)pixelNum;
                }

                // construct a transform table
                transTableR = new int[256];
                transTableG = new int[256];
                transTableB = new int[256];
                float sumR = 0f;
                float sumG = 0f;
                float sumB = 0f;
                for (int i = 0; i < histR_Pr.length; i++) {
                    sumR += histR_Pr[i];
                    sumG += histG_Pr[i];
                    sumB += histB_Pr[i];
                    transTableR[i] = (int)(255 * sumR);
                    transTableG[i] = (int)(255 * sumG);
                    transTableB[i] = (int)(255 * sumB);
                }
            }
                break;
            case RGB_AVERAGE_MODE:{
                float[] histR_Pr = new float[histR.length];

                for (int i = 0; i < histR.length; i++) {
                    histR[i] = (histR[i] + histG[i] + histB[i]) / 3;
                    histR_Pr[i] = histR[i] / (float)pixelNum;
                }

                // construct a transform table
                transTableR = new int[256];
                float sumR = 0f;
                for (int i = 0; i < histR_Pr.length; i++) {
                    sumR += histR_Pr[i];
                    transTableR[i] = (int)(255 * sumR);
                }
                transTableG = transTableR;
                transTableB = transTableR;
            }
                break;
            default:
                System.out.println("Invaild Type");
                return null;
        }

        // transform every pixel in img with transTable;
        int[] tRGB = getTriaxialRGBArray(changeToBufferedImage(img), width, height);
        for(int i=0; i<tRGB.length; i=i+3) {
            tRGB[i] = transTableR[tRGB[i]];
            tRGB[i + 1] = transTableG[tRGB[i+1]];
            tRGB[i + 2] = transTableB[tRGB[i+2]];
        }

        // construct result image from byte array and return it
        int[] sRGB = getSingleRGBArray(tRGB);
        Image result = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, sRGB, 0, width));
        return result;
    }

    /**
     * compute the histogram statics of an image
     * @img image to be computed
     * @channel choose R/G/B channel of the image ( RED, GREEN, BLUE)
     * @return a array of numbers with index indicating gray level and value the number the gray scale occurs
     */ 
    public static int[] getHistogramStatics(Image img, int channel) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] hist = new int[256];
        int[] tRGB = getTriaxialRGBArray(changeToBufferedImage(img), width, height);
        int[] singleChannel =  getSingleChannel(tRGB, channel);
        for(int i=0; i<singleChannel.length; i++) {
            hist[tRGB[i]]++;
        } 
        return hist;
    }

    /**
     * produce the histogram image of an image
     * @param img
     * image to be computed
     * @param size
     * the size of the histogram
     * @param channel
     * choose R/G/B channel of the image ( RED, GREEN, BLUE)
     * @return the histogram of img
     */ 
    public static Image getHistogramImage(Image img, int channel) {
        BufferedImage histogramImage = new BufferedImage(HISTOGRAM_SIZE, HISTOGRAM_SIZE, BufferedImage.TYPE_4BYTE_ABGR);

        // get the histogram data
        int[] histo = getHistogramStatics(img, channel);

        // find the greatest value and get the rate
        int max = histo[0];
        for (int i = 0; i < histo.length; i++) {
            if (histo[i] > max) {
                max = histo[i];
            }
        }
        float rate = 200.0f/max;
        int offset = 2;  

        // paint the buffered image
        // drawi xy axis
        Graphics2D g2d = histogramImage.createGraphics();  
        g2d.setPaint(Color.BLACK);  
        g2d.fillRect(0, 0, HISTOGRAM_SIZE, HISTOGRAM_SIZE);  
        g2d.setPaint(Color.WHITE);  
        g2d.drawLine(5, 250, 265, 250);  
        g2d.drawLine(5, 250, 5, 5);

        // draw lines for every level
        String label  = "";
        switch (channel) {
            case RED:
                label += "Red Channel Histogram";
                g2d.setPaint(Color.RED); 
                break;
            case GREEN:
                label += "Green ChannelHistogram";
                g2d.setPaint(Color.GREEN); 
                break;
            case BLUE:
                label += "Blue Channel Histogram";
                g2d.setPaint(Color.BLUE); 
                break;
            case GRAY:
                label += "Gray Channel Histogram";
                g2d.setPaint(Color.GRAY); 
                break;
        }
        for(int i=0; i<histo.length; i++) {  
            int frequency = (int)(histo[i] * rate);
            g2d.drawLine(5 + offset + i, 250, 5 + offset + i, 250-frequency);  
        }

        // X Axis Gray intensity  
        g2d.setPaint(Color.RED);  
        g2d.drawString(label, 50, 270); 
        return changeToImage(histogramImage);
    }
}