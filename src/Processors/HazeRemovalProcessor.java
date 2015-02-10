package com.sdl.ImageProcessor.Processors;

import java.util.Arrays;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

class PNode {
    public int x;
    public int y;
    public int value;
    public PNode head;
    public PNode tail;
    public PNode(int x_, int y_, int v_, PNode h_, PNode t_) {
        x = x_;
        y = y_;
        value = v_;
        head = h_;
        tail = t_;
    }
}

public class HazeRemovalProcessor extends ImageProcessor{
    public static final int DARK_CHANNEL_PATCH_SIZE = 15;
    public static final double TOP_PERCENTAGE = 0.1;
    public static final double W0 = 0.95;
    public static final double T0 = 0.1;

    public static Image process(Image img) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        // 1.获取R，G，B三个通道各自的patches
        int[] arrayR = new int[width * height];
        int[] arrayG = new int[width * height];
        int[] arrayB = new int[width * height];
        if (ImageProcessor.getRGBChannels(img, arrayR, arrayG, arrayB) != 0) {
            System.out.println("Error, something is wrong with the image");
            return null;
        }

        // 2.获取dark channel
        int[] darkChannel = calcDarkChannel(arrayR, arrayG, arrayB, width, height);
        if (darkChannel == null) {
            System.out.println("something is wrong in calcDarkChannel"); 
            return null;           
        }

        // 3.获取Atmosphere light
        PNode node = findTopPercentage(TOP_PERCENTAGE, darkChannel, width, height); // 1.获取前百分之一的像素
        int lightR = findMaxInChannel(node, arrayR, width, height); // 2.在原图中找2中算出的那些像素点的位置中强度最大的点的强度作为atmosphere light返回
        int lightG = findMaxInChannel(node, arrayG, width, height);
        int lightB = findMaxInChannel(node, arrayB, width, height);

        // 4.获取transmission rate table
        double[] transTable = getTransTable(arrayR, arrayG, arrayB, lightR, lightG, lightB, width, height);

        // 5.获取目标图片中每一个像素
        for (int h = 0; h < height; h++) {                                                                                                                  // 计算R通道
            for (int w = 0; w < width; w++) {
                arrayR[h*width+w] = (int)((arrayR[h*width+w] - lightR) / Math.max(T0, transTable[h*width+w]) + lightR);
            }
        }
        for (int h = 0; h < height; h++) {                                                                                                                  // 计算G通道
            for (int w = 0; w < width; w++) {
                arrayG[h*width+w] = (int)((arrayG[h*width+w] - lightG) / Math.max(T0, transTable[h*width+w]) + lightG);
            }
        }
        for (int h = 0; h < height; h++) {                                                                                                                  // 计算B通道
            for (int w = 0; w < width; w++) {
                arrayB[h*width+w] = (int)((arrayB[h*width+w] - lightB) / Math.max(T0, transTable[h*width+w]) + lightB);
            }
        }

        // 6.生成目标图片并返回
        return ImageProcessor.getImageFrom3Channels(arrayR, arrayG, arrayB, width, height);
    }

    public static Image getDarkImage(Image img) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        // 1.获取R，G，B三个通道各自的patches
        int[] arrayR = new int[width * height];
        int[] arrayG = new int[width * height];
        int[] arrayB = new int[width * height];
        if (ImageProcessor.getRGBChannels(img, arrayR, arrayG, arrayB) != 0) {
            System.out.println("Error, something is wrong with the image");
            return null;
        }

        // 2.获取dark channel
        int[] darkChannel = calcDarkChannel(arrayR, arrayG, arrayB, width, height);
        if (darkChannel == null) {
            System.out.println("something is wrong in calcDarkChannel"); 
            return null;           
        }
        return ImageProcessor.getImageFrom1Channel(darkChannel, width, height);
    }

    public static Image getTransImage(Image img) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        // 1.获取R，G，B三个通道各自的patches
        int[] arrayR = new int[width * height];
        int[] arrayG = new int[width * height];
        int[] arrayB = new int[width * height];
        if (ImageProcessor.getRGBChannels(img, arrayR, arrayG, arrayB) != 0) {
            System.out.println("Error, something is wrong with the image");
            return null;
        }

        // 2.获取dark channel
        int[] darkChannel = calcDarkChannel(arrayR, arrayG, arrayB, width, height);
        if (darkChannel == null) {
            System.out.println("something is wrong in calcDarkChannel"); 
            return null;           
        }

        // 3.获取Atmosphere light
        PNode node = findTopPercentage(TOP_PERCENTAGE, darkChannel, width, height); // 1.获取前百分之一的像素
        int lightR = findMaxInChannel(node, arrayR, width, height); 
        int lightG = findMaxInChannel(node, arrayG, width, height);
        int lightB = findMaxInChannel(node, arrayB, width, height);

        // 4.获取transmission rate table
        double[] transTable = getTransTable(arrayR, arrayG, arrayB, lightR, lightG, lightB, width, height);

        // 标定
        double max = transTable[0];
        double min = transTable[0];
        for (int i = 0; i < transTable.length; i++) { // 寻找最大值和最小值
            if (max < transTable[i]) {
                max = transTable[i];
            }
            if (min > transTable[i]) {
                min = transTable[i];
            }
        }

        int[] newArray = new int[transTable.length];
        for (int i = 0; i < transTable.length; i++) {
            newArray[i] = (int)((transTable[i]-min)/(max-min)*255);
        }

        return ImageProcessor.getImageFrom1Channel(newArray, width, height);
    }

    private static int[] calcDarkChannel(int[] arrayR, int[] arrayG, int[] arrayB, int array_width, int array_height) {
        if ((arrayR.length != arrayG.length) || (arrayG.length != arrayB.length)) {
            System.out.println("length of R,G,B arrays should be the same");
            return null;
        }

        if (array_width * array_height != arrayR.length) {
            System.out.println("array width and height does not match");            
            return null;
        }

        // 1.获取RGB通道的最小值
        int[] minArray = new int[arrayR.length];
        for (int i = 0; i < minArray.length; i++) {
            minArray[i] = Math.min(Math.min(arrayR[i], arrayG[i]), arrayB[i]);
        }

        // 2.将上一步获取的数组进行最小值滤波
        int[] result = Filter2dProcessor.process(minArray, array_width, array_height, Filter.MIN, DARK_CHANNEL_PATCH_SIZE);

        return result;
    }

    private static double[] getTransTable(int[] arrayR, int[] arrayG, int[] arrayB, int lightR, int lightG, int lightB, int width, int height) {
        int[] newBoundArrayR = ImageProcessor.addBoundriesToArray(arrayR, width, (DARK_CHANNEL_PATCH_SIZE-1)/2, ImageProcessor.BOUNDARY_MIRROR);        // fill the boundries of  the array
        int[] newBoundArrayG = ImageProcessor.addBoundriesToArray(arrayG, width, (DARK_CHANNEL_PATCH_SIZE-1)/2, ImageProcessor.BOUNDARY_MIRROR);
        int[] newBoundArrayB = ImageProcessor.addBoundriesToArray(arrayB, width, (DARK_CHANNEL_PATCH_SIZE-1)/2, ImageProcessor.BOUNDARY_MIRROR);

        int[][][] patchesR = Filter2dProcessor.view_as_window(newBoundArrayR,                                                                                                                 // get patches
                                            width+(DARK_CHANNEL_PATCH_SIZE-1), height+ (DARK_CHANNEL_PATCH_SIZE-1), 
                                                DARK_CHANNEL_PATCH_SIZE, DARK_CHANNEL_PATCH_SIZE);
        int[][][] patchesG = Filter2dProcessor.view_as_window(newBoundArrayG, 
                                            width+(DARK_CHANNEL_PATCH_SIZE-1), height+ (DARK_CHANNEL_PATCH_SIZE-1), 
                                                DARK_CHANNEL_PATCH_SIZE, DARK_CHANNEL_PATCH_SIZE); 
        int[][][] patchesB = Filter2dProcessor.view_as_window(newBoundArrayB, 
                                            width+(DARK_CHANNEL_PATCH_SIZE-1), height+ (DARK_CHANNEL_PATCH_SIZE-1), 
                                                DARK_CHANNEL_PATCH_SIZE, DARK_CHANNEL_PATCH_SIZE); 

        double[] transTable = new double[arrayR.length];                                                                                                            // calculate transmission rate for every pixel
        for (int i = 0; i< transTable.length; i++) {
            double t1 = Filter.MinFiltering(patchesR[i]) / (double)lightR;
            double t2 = Filter.MinFiltering(patchesG[i]) / (double)lightG;
            double t3 = Filter.MinFiltering(patchesB[i]) / (double)lightB;
            transTable[i] = 1- W0 * Math.min(Math.min(t1, t2), t3);
        }
        return transTable;
    }

    private static PNode findTopPercentage(double percentage,  int[] darkChannel, int width, int height) {
        if (width * height != darkChannel.length) {
            System.out.println("width and height does not match");
            return null;
        }

        int limit = (int)(percentage * 0.01 * darkChannel.length);
        int len = 0;
        PNode head = null;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (len < limit) {
                    head = insertNode(head, new PNode(w, h, darkChannel[h*width+w], null, null));
                    len++;
                } else {
                    if (darkChannel[h*width+w] > head.value) {
                        head = insertNode(head, new PNode(w, h, darkChannel[h*width+w], null, null));
                        head = head.tail;
                    }
                }
            }
        }

        return head;
    }

    private static int findMaxInChannel(PNode node, int[] arrayC, int width, int height) {
        if (width * height != arrayC.length) {
            System.out.println("width and height does not match");
            return -1;
        }

        int max = arrayC[width * node.y + node.x];
        PNode p = node;
        while (p.tail!=null) {
            p = p.tail;
            if (max < arrayC[width * p.y + p.x]) {
                max = arrayC[width * p.y + p.x];
            }
        }

        return max;
    }

    private static PNode insertNode(PNode head, PNode node) {
        if (head == null) {
            return node;
        }

        if (head.value >= node.value) {
            head.head = node;
            node.tail = head;
            return node;
        }

        PNode curNode = head;
        while(curNode.tail != null) {
            curNode = curNode.tail;
            if (curNode.value >= node.value) {
                node.head = curNode.head;
                node.tail = curNode;
                curNode.head.tail = node;
                curNode.head = node;
                return head;
            }
        }

        curNode.tail = node;
        node.head = curNode;
        return head;
    }
}

