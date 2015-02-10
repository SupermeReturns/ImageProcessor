package com.sdl.ImageProcessor.Processors;

import java.util.Arrays;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

public class FreqFilter2dProcessor extends ImageProcessor{
    public static Image process(Image img,  double[][] filter) {
        return process(img, filter, true);
    }

    /**
     *  Performs spatial filtering on a gray scale image in the frequency domain using fourier transformation. 
     * @param img image to be equalized
     * @param filter a array indicating the filter
     * @param flags whether use fast fourier transformation or not
     * @return the filtered image
     */ 
    public static Image process(Image img,  double[][] filter, boolean flags) {
        // zero-fill to make filter and img have the same size
        int imgW = img.getWidth(null);
        int imgH = img.getHeight(null);        
        int filterH = filter.length;
        int filterW = filter[0].length;

        int totalW = imgW + filterW - 1;
        int totalH = imgH + filterH -1;

        double[][] imgReal = new double[totalH][totalW];
        double[][] imgImg = new double[totalH][totalW];

        double[][] filterReal = new double[totalH][totalW];
        double[][] filterImg = new double[totalH][totalW];

        int[] temp = ImageProcessor.getSingleChannel(img, ImageProcessor.RED);
        for (int i = 0; i < imgH; i++) {
            for (int j = 0; j < imgW; j++) {
                imgReal[i][j] = (double)temp[i*imgW + j];
            }
        }
        for (int i = 0; i < filterH; i++) {
            for (int j = 0; j < filterW; j++) {
                filterReal[i][j] = filter[i][j];
            }
        }

        // apply fourier transformation 
        computeDft2d(imgReal, imgImg);
        computeDft2d(filterReal, filterImg);

        // dot multiply
        for (int i = 0; i < totalH; i++) {
            for (int j = 0; j < totalW; j++) {
                imgReal[i][j] = ((imgReal[i][j] * filterReal[i][j]  - imgImg[i][j]  * filterImg[i][j]))/totalW;
                imgImg[i][j]  = ((imgReal[i][j]  * filterImg[i][j]  + imgImg[i][j]  * filterReal[i][j]))/totalW;
            }
        }

        // apply inverse fourier transformation
        computeiDft2d(imgReal, imgImg);

        // scaling
        double max, min;
        max = imgReal[0][0];
        min = imgReal[0][0];
        for (int y = 0; y < totalH; y++) {
            for (int x = 0; x < totalW; x++) {
                if (imgReal[y][x] > max)
                    max = imgReal[y][x];
                if (imgReal[y][x] < min)
                    min = imgReal[y][x];                
            }
        }

        double len = max - min;
        for (int y = 0; y < totalH; y++) {
            for (int x = 0; x < totalW; x++) {
                imgReal[y][x] = (imgReal[y][x]-min)/len*255;        
            }
        }

        // erase black side lines and produce result image

        int[] singleChannel = new int[imgW * imgH];
        int originalX = (filterW - 1) / 2;
        int originalY = (filterH - 1) / 2;
        for (int i = 0; i < imgH; i++) {
            for (int j = 0; j < imgW; j++) {
                 int s = (int)imgReal[originalY+i][originalX + j];
                if (s>255)
                    s = 255;
                if (s<0)
                    s = 0;
                singleChannel[i*imgW+ j] = s;
            }
        }

        return ImageProcessor.getImageFrom1Channel(singleChannel, imgW, imgH);
    }
    /**
     *  perform fourier transformation or inverse fourier transformation on a image
     * @param img image to be transformed
     * @param flags true for fourier transformation and false for inverse transformation
     * @return the transformed image
     */ 
    public static Image dft2d(Image sourceImage, boolean flags) {
        int width = sourceImage.getWidth(null);
        int height = sourceImage.getHeight(null);

        double[][] realPart = get2dArray(ImageProcessor.getSingleChannel(sourceImage, ImageProcessor.RED), width, height);
        double[][] imagPart = new double[height][width];

        int[] singleChannel = new int[width*height];
        if (flags) {                                           // forward transformation
            // centering
            for (int y=0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    realPart[y][x] = realPart[y][x]*Math.pow(-1,x+y);
                }
            }

            // apply transformation
            computeDft2d(realPart, imagPart);
            double[] spec = new double[width*height];
            for (int y=0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    spec[y*width+x] = Math.sqrt(Math.pow(realPart[y][x],2)+Math.pow(imagPart[y][x],2));
                }
            }

            // logarism
            for (int i = 0; i < spec.length; i++) {
                spec[i] = Math.log(spec[i]+1);
            }

            // scaling
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
                singleChannel[i] = s;
            }

        } else {                                             // backward transformation
            // first forward transformation
            computeDft2d(realPart, imagPart);

            // back transformation
            computeiDft2d(realPart, imagPart);

            // store results into singleChannel
            double[] arr1d = get1dArray(realPart);
            int s;
            for (int i = 0; i < arr1d.length; i++) {
                s = (int)arr1d[i];
                if (s > 255)
                    s = 255;
                if (s < 0)
                    s = 0;
                singleChannel[i] = s;
            }
        }

        // produce result
        return ImageProcessor.getImageFrom1Channel(singleChannel, width, height);
    }

    /**
     * Computes one dimension discrete Fourier transform (DFT) of the given vector.
     * All the array arguments must have the same length.
     * @param inreal the real part of input
     * @param inrimag the imaginary part of input
     * @param outreal the real part of output
     * @param outimag the imaginary part of output
     */
    public static void computeDft1d(double[] inreal, double[] inimag) {
        int n = inreal.length;
        double[] outreal = new double[n];
        double[] outimag = new double[n];

        for (int k = 0; k < n; k++) {  // For each output element
            double sumreal = 0;
            double sumimag = 0;
            for (int t = 0; t < n; t++) {  // For each input element
                double angle = 2 * Math.PI * t * k / n;
                sumreal +=  inreal[t] * Math.cos(angle) + inimag[t] * Math.sin(angle);
                sumimag += -inreal[t] * Math.sin(angle) + inimag[t] * Math.cos(angle);
            }
            outreal[k] = sumreal;
            outimag[k] = sumimag;
        }

        System.arraycopy(outreal, 0, inreal, 0, n);
        System.arraycopy(outimag, 0, inimag, 0, n);        
    }
    /**
     * The Fast Fourier Transform (generic version, with NO optimizations).
     * @param inputReal
     *            an array of length n, the real part
     * @param inputImag
     *            an array of length n, the imaginary part
     * @param DIRECT
     *            TRUE = direct transform, FALSE = inverse transform
     * @return a new array of length 2n
     */
    public static void computeDft2d(double[][] inreal, double[][] inimag) {
        int height = inreal.length;
        int width = inreal[0].length;

        // transform rows
        for (int y = 0; y < height; y++) {
            computeDft1d(inreal[y], inimag[y]);
        }

        // transform columns
        for (int x = 0; x < width; x++) {
            double[] realCol = new double[height];
            double[] imagCol = new double[height];

            // copy that column
            for (int y = 0; y < height; y++) {
                realCol[y] = inreal[y][x];
                imagCol[y] = inimag[y][x];
            }

            // apply 1d fourier transformation
            computeDft1d(realCol, imagCol);

            // copy results back
            for (int y = 0; y < height; y++) {
                inreal[y][x] = realCol[y];
                inimag[y][x] = imagCol[y];
            }
        }
    }

    public static void computeiDft2d(double[][] inreal, double[][] inimag) {
        int height = inreal.length;
        int width = inreal[0].length;

        // get conjugates
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                inimag[y][x] = -inimag[y][x];
            }
        }

        // use forward transformation to implement inverse transformation
        computeDft2d(inreal, inimag);

        // get conjugates and time M*N
        int total = width * height;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                inreal[y][x] = inreal[y][x] / total;
                inimag[y][x] = -inimag[y][x] / total;
            }
        }
    }

    public static double[] get1dArray(double[][] arr) {
        int height = arr.length;
        int width = arr[0].length;
        double[] result = new double[width*height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[y * width + x] = arr[y][x];
            }
        }
        return result;
    }

    public static double[][] get2dArray(int[] arr, int width, int height) {
        double[][] result = new double[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[y][x] = arr[y * width + x];
            }
        }
        return result;
    }
}


