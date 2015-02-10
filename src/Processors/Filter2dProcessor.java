package com.sdl.ImageProcessor.Processors;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

public class Filter2dProcessor extends ImageProcessor{

    /**
     *  Performs spatial filtering on a gray scale image. 
     * @img image to be equalized
     * @filter a array indicating the weight of an pixel in a window
     * @return the equalized image whose histogram is approximately flat. 
     */ 
    public static Image process(Image img,  int[][] filter) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int filter_width = filter.length;

        // fill the boundries of  the image
        Image newImg =  addBoundriesToImage(img, (filter_width-1)/2, ImageProcessor.BOUNDARY_MIRROR);

        // get patches
        int[][][] patchesR = view_as_window(newImg, filter_width, filter_width, RED);
        int[][][] patchesG = view_as_window(newImg, filter_width, filter_width, GREEN);
        int[][][] patchesB = view_as_window(newImg, filter_width, filter_width, BLUE);

        // calculate the pixels in the result image with patch
        int[] newChannelR = new int[width*height];
        int[] newChannelG = new int[width*height];
        int[] newChannelB = new int[width*height];

        int sumOfFilterArray = 0;
        for (int i = 0; i < filter.length; i++) {
            for (int j = 0; j< filter[i].length; j++) {
                    sumOfFilterArray += filter[i][j];
            }
        }

        // compute the red channel
        for (int w = 0; w < newChannelR.length; w++) {
            int sumOfProducts = 0;
            for (int i = 0; i < filter.length; i++) {
                for (int j = 0; j < filter[i].length; j++) {
                    sumOfProducts += patchesR[w][i][j] * filter[i][j];
                }
            }
            int filterResult = (int)(sumOfProducts/(float)sumOfFilterArray);
            if (filterResult < 0) {
                filterResult = 0;
            } else if (filterResult > 255) {
                filterResult = 255;
            }
            newChannelR[w] = filterResult;
        }
        // compute the green channel
        for (int w = 0; w < newChannelG.length; w++) {
            int sumOfProducts = 0;
            for (int i = 0; i < filter.length; i++) {
                for (int j = 0; j < filter[i].length; j++) {
                    sumOfProducts += patchesG[w][i][j] * filter[i][j];
                }
            }
            int filterResult = (int)(sumOfProducts/(float)sumOfFilterArray);
            if (filterResult < 0) {
                filterResult = 0;
            } else if (filterResult > 255) {
                filterResult = 255;
            }
            newChannelG[w] = filterResult;
        }
        // compute the blue channel
        for (int w = 0; w < newChannelB.length; w++) {
            int sumOfProducts = 0;
            for (int i = 0; i < filter.length; i++) {
                for (int j = 0; j < filter[i].length; j++) {
                    sumOfProducts += patchesB[w][i][j] * filter[i][j];
                }
            }
            int filterResult = (int)(sumOfProducts/(float)sumOfFilterArray);
            if (filterResult < 0) {
                filterResult = 0;
            } else if (filterResult > 255) {
                filterResult = 255;
            }
            newChannelB[w] = filterResult;
        }

        // construct new image with computed arrays
        int[] sRGB = getSingleRGBArray(combineChannels(newChannelR, newChannelG, newChannelB));
        Image result = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, sRGB, 0, width));
        return result;
    }

    public static Image process(Image img,  int type, int filter_width) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        // fill the boundries of  the image
        Image newImg =  addBoundriesToImage(img, (filter_width-1)/2, ImageProcessor.BOUNDARY_MIRROR);

        // get patches
        int[][][] patchesR = view_as_window(newImg, filter_width, filter_width, RED);
        int[][][] patchesG = view_as_window(newImg, filter_width, filter_width, GREEN);
        int[][][] patchesB = view_as_window(newImg, filter_width, filter_width, BLUE);

        // calculate the pixels in the result image with patch
        int[] newChannelR = new int[width*height];
        int[] newChannelG = new int[width*height];
        int[] newChannelB = new int[width*height];

        // compute the red channel
        for (int w = 0; w < newChannelR.length; w++) {
            newChannelR[w] = Filter.process(patchesR[w], type);
        }
        // compute the green channel
        for (int w = 0; w < newChannelG.length; w++) {
            newChannelG[w] = Filter.process(patchesG[w], type);
        }
        // compute the blue channel
        for (int w = 0; w < newChannelB.length; w++) {
            newChannelB[w] = Filter.process(patchesB[w], type);
        }

        // construct new image with computed arrays
        int[] sRGB = getSingleRGBArray(combineChannels(newChannelR, newChannelG, newChannelB));
        Image result = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, sRGB, 0, width));
        return result;
    }

    public static int[] process(int[] arrayC, int array_width, int array_height, int type, int filter_width) {
        if (arrayC.length != array_width * array_height) {
            System.out.println("Error, arrayC width and height does not match");
            return null;
        }

        // fill the boundries of  the image
        int[] newBoundArray = ImageProcessor.addBoundriesToArray(arrayC, array_width, (filter_width-1)/2, ImageProcessor.BOUNDARY_MIRROR);

        // get patches
        int[][][] patches = view_as_window(newBoundArray, array_width+(filter_width-1), array_height+ (filter_width-1), filter_width, filter_width);

        // compute the new arrays
        int[] result = new int[arrayC.length];
        for (int w =0; w < result.length; w++) {
            result[w] = Filter.process(patches[w], type);
        }
        return result;
    }
    /**
     * Extract all patches of a gray scale image.
     * @img image to be extract
     * @patch_width width of the patch
     * @patch_height height of the patch
     * @channel the color channel to extract
     * @return a list of 2-D patches (a 3-D)
     */ 
    public static int[][][] view_as_window(Image img, int patch_width, int patch_height, int channel) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] hist = new int[256];
        int[] tRGB = getTriaxialRGBArray(changeToBufferedImage(img), width, height);
        int[] singleChannel = getSingleChannel(tRGB, channel);

        // construct a 3d array which stores all the patches;
        int[][][] result = new int[(width-patch_width + 1) * (height - patch_height +1 )][patch_height][patch_width];

        // fill this 3d array
        for (int y = 0; y < height - patch_height + 1; y++) {
            for (int x = 0; x < width - patch_width + 1; x++) {
                for (int h = 0; h < patch_height; h++) {
                    for (int w = 0; w < patch_width; w++) {
                        result[y*(width - patch_width + 1) + x][h][w] = singleChannel[(y+h)*width + (x+w)];
                    }
                }
            }
        }

        return result;
    }

    public static int[][][] view_as_window(int[] arrayC, int array_width, int array_height,  int patch_width, int patch_height) {
        // construct a 3d array which stores all the patches;
        int[][][] result = new int[(array_width - patch_width + 1) * (array_height - patch_height +1 )][patch_height][patch_width];

        // fill this 3d array
        for (int y = 0; y < array_height - patch_height + 1; y++) {
            for (int x = 0; x < array_width - patch_width + 1; x++) {
                for (int h = 0; h < patch_height; h++) {
                    for (int w = 0; w < patch_width; w++) {
                        result[y*(array_width - patch_width + 1) + x][h][w] = arrayC[(y+h)*array_width + (x+w)];
                    }
                }
            }
        }

        return result;
    }

}