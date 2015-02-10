package com.sdl.ImageProcessor.Processors;

import java.util.Arrays;
import java.awt.Image;

public class Filter {
    public static final int LAPLACIAN = 0;
    public static final int SOBEL_LANDSCAPE = 1; 
    public static final int  SOBEL_PORTRAIT= 2;
    public static final int  HARMONIC = 3;   
    public static final int  CONTRA_HARMONIC = 4;   
    public static final int  MIN = 5;   
    public static final int  MAX = 6;   
    public static final int  MEDIAN = 7;   
    public static final int  ARITHMETIC_MEAN = 8;   
    public static final int  GEMETRIC_MEAN = 9;   

    public static int process(int[][] data, int type) {
        switch (type) {
            case LAPLACIAN:
                return MaskFiltering(data, LAPLACIAN);
            case SOBEL_LANDSCAPE :
                return MaskFiltering(data, SOBEL_LANDSCAPE);
            case SOBEL_PORTRAIT:
                return MaskFiltering(data, SOBEL_PORTRAIT);
            case HARMONIC:
                return HarmonicFiltering(data);
            case CONTRA_HARMONIC:
                return ContraHarmonicFiltering(data, -1.5);
            case MIN:
                return MinFiltering(data);
            case MAX:
                return MaxFiltering(data);
            case MEDIAN:
                return MedianFiltering(data);
            case ARITHMETIC_MEAN:
                return ArithmeticMeanFiltering(data);
            case GEMETRIC_MEAN:
                return GemetricMeanFiltering(data);
            default:
                System.out.println("Invalid Type Pramameter!");
        }
        return 0;
    }
    public static int process(int[][] data, int type, double param1) {
        switch(type) {
            case CONTRA_HARMONIC:
                return ContraHarmonicFiltering(data, param1);
            default:
                System.out.println("Invalid Type Pramameter!");
        }
        return 0;
    }
    private static final int[][] lapArray = {
        {0, 1, 0},
        {1, -4, 1},
        {0, 1, 0}
    };
    private static final int[][] sobelLArray = {
        {-1, -2, -1},
        {0, 0, 0},
        {1, 2, 1}
    };
    private static final int[][] sobelPArray = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };

    public static int MaskFiltering(int[][] data, int type) {
        if ((data.length != 3) && (data[0].length != 3)) {
            System.out.println("Mask filtering data should be a 3*3 array");
            return 0;
        }

        int result = 0;
        switch (type) {
            case LAPLACIAN:
                for (int h = 0; h < 3; h++) {
                    for (int w = 0; w < 3; w++) {
                        result += data[h][w] * lapArray[h][w];
                    }
                }
            case SOBEL_LANDSCAPE:
                for (int h = 0; h < data.length; h++) {
                    for (int w = 0; w < data[h].length; w++) {
                        result += data[h][w] * sobelLArray[h][w];
                    }
                }
            case SOBEL_PORTRAIT:
                for (int h = 0; h < data.length; h++) {
                    for (int w = 0; w < data[h].length; w++) {
                        result += data[h][w] * sobelPArray[h][w];
                    }
                }
        }

        if (result < 0) {
            result = 0;
        } else if (result > 255) {
            result = 255;
        }
        return result;

    }

    public static int HarmonicFiltering(int[][] data) {
        double result = 0;
        for (int h = 0; h < data.length; h++) {
            for (int w = 0; w < data[h].length; w++) {
                result += 1.0 / data[h][w];
            }
        }
        result = data.length * data[0].length / result;
        return (int)result;
    }
    public static int ContraHarmonicFiltering(int[][] data, double q) {
        double numberator = 0;
        double denominator = 0;
        for (int h = 0; h < data.length; h++) {
            for (int w = 0; w < data[h].length; w++) {
                numberator += java.lang.StrictMath.pow(data[h][w], q+1);
                denominator += java.lang.StrictMath.pow(data[h][w], q);
            }
        }
        return (int)(numberator / denominator);
    }
    public static int MinFiltering(int[][] data) {
        int min = data[0][0];
        for (int h = 0; h < data.length; h++) {
            for (int w = 0; w < data[h].length; w++) {
                if (data[h][w] < min) {
                    min = data[h][w];
                }
            }
        }
        return min;
    }
    public static int MaxFiltering(int[][] data) {
        int max = data[0][0];
        for (int h = 0; h < data.length; h++) {
            for (int w = 0; w < data[h].length; w++) {
                if (data[h][w] > max) {
                    max = data[h][w];
                }
            }
        }
        return max;
    }
    public static int MedianFiltering(int[][] data) {
        int[] copy = new int[data.length*data[0].length];
        for (int h = 0; h < data.length; h++) {
            for (int w = 0; w < data[h].length; w++) {
                copy[h*data[0].length + w] = data[h][w];
            }
        }
        Arrays.sort(copy);
        if (copy.length % 2 == 0) {
            return (copy[copy.length] + copy[copy.length+1])/2;
        } else {
            return copy[(copy.length+1)/2];
        }
    }
    public static int ArithmeticMeanFiltering(int[][] data) {
        int sum = 0;
        for (int h = 0; h < data.length; h++) {
            for (int w = 0; w < data[h].length; w++) {
                sum += data[h][w];
            }
        }
        return sum / (data.length*data[0].length);
    }
    public static int GemetricMeanFiltering(int[][] data) {
        double product = 1;
        for (int h = 0; h < data.length; h++) {
            for (int w = 0; w < data[h].length; w++) {
                product *= data[h][w];
            }
        }
        //System.out.println("product:" + product);
        return (int)java.lang.StrictMath.pow(product,1.0  / (data.length*data[0].length));
    }
}

