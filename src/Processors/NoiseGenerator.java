package com.sdl.ImageProcessor.Processors;

import java.awt.Image;

public class NoiseGenerator {

    public static Image addGaussianNoise(Image img, double mean, double variance) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] triaxialRGB = ImageProcessor.getTriaxialRGBArray(ImageProcessor.changeToBufferedImage(img), width, height);

        int[] redChannel = ImageProcessor.getSingleChannel(triaxialRGB, ImageProcessor.RED);

        double x1, x2, x, xn;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                // generate random num :x1, x2
                do
                    x1 = Math.random();
                while (x1 == 0);
                x2 = Math.random();

                // 获取高斯噪声
                x = Math.sqrt(-2*Math.log(x1)) * Math.cos(2*Math.PI*x2);
                xn = mean + Math.sqrt(variance) * x;
                // 加噪
                redChannel[h*width + w] += xn;

                // 标定
                if (redChannel[h*width + w] > 255) {
                    redChannel[h*width + w]  = 255;
                } else if (redChannel[h*width + w]  < 0) {
                    redChannel[h*width + w] = 0;
                }
            }
        }

        return ImageProcessor.getImageFrom1Channel(redChannel, width, height);
    }

    public static Image addSaltAndPepperNoise(Image img, double pepperPrb, double saltPrb) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] triaxialRGB = ImageProcessor.getTriaxialRGBArray(ImageProcessor.changeToBufferedImage(img), width, height);

        int[] redChannel = ImageProcessor.getSingleChannel(triaxialRGB, ImageProcessor.RED);

        int pNum = (int)(width * height * pepperPrb);
        int sNum = (int)(width * height * saltPrb);
        // add pepper noise
        for (int i = 0; i < pNum; i++) {
            int row = (int)(Math.random() * (double)height);
            int col = (int)(Math.random() * (double)width);
            int index = row * width + col;
            redChannel[index] = 0;
        }

        // add salt noise
        for (int i = 0; i < sNum; i++) {
            int row = (int)(Math.random() * (double)height);
            int col = (int)(Math.random() * (double)width);
            int index = row * width + col;
            redChannel[index] = 255;
        }

        return ImageProcessor.getImageFrom1Channel(redChannel, width, height);
    }
}