 package com.sdl.ImageProcessor.Processors;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

/**
 *  一个<code>RotateProcessor</code>用于旋转图片
 * 可以选择旋转之后，图片大小不变或随图片旋转角度发生变化
 */
public class RotateProcessor extends ImageProcessor{

    /**
     * 以一定角度旋转旋转图片
     * @img 被旋转的图片
     * @angle 旋转的角度(0表示不旋转，180旋转半圈，可以为负，正数为顺时针旋转，负数为逆时针旋转)
     * @keepSize 是否保持原图大小，默认为false
     * @return 旋转之后的图片
     */ 
    public static Image process(Image img, double angle, boolean keepSize) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[] arrayR = new int[width*height];
        int[] arrayG = new int[width*height];
        int[] arrayB = new int[width*height];
        ImageProcessor.getRGBChannels(img, arrayR, arrayG, arrayB);

        // 计算旋转角度，转换为0-360的正数
        double ang = Math.floor(angle) % 360 + (angle - Math.floor(angle));
        if (ang < 0) {
            ang = 360 + ang;
        }

        // 计算旋转后的图片大小,并新建数组保存旋转后的数据
        double pivot_h = height / 2.0;
        double pivot_w = width / 2.0;

        Point p1 = rotatePoint(new Point(0, 0), new Point(pivot_h, pivot_w), -ang);
        Point p2 = rotatePoint(new Point(height, 0), new Point(pivot_h, pivot_w), -ang);

        int new_height, new_width;
        if (((ang>=0)&(ang<90))|((ang>=180)&(ang<270))) {            // 低的点决定高度，高的点决定宽度
            new_height = (int)(Math.ceil(Math.abs(p1.h - pivot_h)) * 2 + 1);
            new_width = (int)(Math.ceil(Math.abs(p2.w - pivot_w)) * 2 + 1);
        } else {
            new_height = (int)(Math.ceil(Math.abs(p2.h - pivot_h)) * 2 + 1);
            new_width = (int)(Math.ceil(Math.abs(p1.w - pivot_w)) * 2 + 1);
        }

        int[] newArrayR = new int[new_width*new_height];
        int[] newArrayG = new int[new_width*new_height];
        int[] newArrayB = new int[new_width*new_height];

        // 计算新图片中的每个像素值，使用插值的方法
        double rAng = Math.toRadians(ang);
        double cosAng = Math.cos(rAng);
        double sinAng = Math.sin(rAng);
        double relative_target_h, relative_target_w, relative_new_h, relative_new_w, new_w, new_h, portion_h, portion_w;
        int left_down_h, left_down_w, left_up_h, left_up_w, right_down_h, right_down_w, right_up_h, right_up_w;

        double  new_pivot_h = new_height / 2.0;
        double new_pivot_w = new_width/ 2.0;

        for (int h = 0; h < new_height; h++) {
            for (int w = 0; w < new_width; w++) {
                // 计算映射的坐标值
                relative_target_h = h - new_pivot_h;
                relative_target_w = w - new_pivot_w;
                relative_new_h = relative_target_h * cosAng - relative_target_w * sinAng;
                relative_new_w = relative_target_w * cosAng + relative_target_h * sinAng;
                new_w = relative_new_w + pivot_w;
                new_h = relative_new_h + pivot_h;

                // 判断是否超出边界
                if ((new_h < 0)|(new_w < 0)|(new_h >= height-1)|(new_w >= width-1))
                    continue;


                // 寻找最临近4个点
                portion_h = new_h - Math.floor(new_h);
                portion_w = new_w - Math.floor(new_w);

                if (new_h == (int)new_h) {               // 如遇到整数，就把它当作小数处理，再寻找周围的四个点
                    new_h += 0.1;
                }
                if (new_w == (int)new_w) {
                    new_w += 0.1;
                }

                left_down_h = (int)Math.floor(new_h);
                left_down_w = (int)Math.floor(new_w);
                left_up_h = (int)Math.floor(new_h);
                left_up_w = (int)Math.ceil(new_w);
                right_down_h = (int)Math.ceil(new_h);
                right_down_w = (int)Math.floor(new_w);
                right_up_h = (int)Math.ceil(new_h);
                right_up_w = (int)Math.ceil(new_w);

                int left_down = left_down_h*width+left_down_w;
                int left_up = left_up_h*width+left_up_w;
                int right_down = right_down_h*width+right_down_w;
                int right_up = right_up_h*width+right_up_w;

                // 计算R通道
                newArrayR[h*new_width+w] = (int)((arrayR[left_down]*portion_w+arrayR[left_up]*(1-portion_w))*portion_h+(arrayR[right_down]*portion_w + arrayR[right_up]*(1-portion_w))*(1-portion_h));

                // 计算G通道
                newArrayG[h*new_width+w] = (int)((arrayG[left_down]*portion_w+arrayG[left_up]*(1-portion_w))*portion_h+(arrayG[right_down]*portion_w + arrayG[right_up]*(1-portion_w))*(1-portion_h));

                // 计算B通道
                newArrayB[h*new_width+w] = (int)((arrayB[left_down]*portion_w+arrayB[left_up]*(1-portion_w))*portion_h+(arrayB[right_down]*portion_w + arrayB[right_up]*(1-portion_w))*(1-portion_h));
            }
        }

        // 从三个通道中生成新的Image并返回
        Image newImage = ImageProcessor.getImageFrom3Channels(newArrayR, newArrayG, newArrayB, new_width, new_height);

        // 判断是否保持大小
        if (keepSize) {
            // 图片尺寸，使其能够刚好放在原来的框子里面

            double rateW = width/(double)new_width;
            double rateH = height/(double)new_height;
            if(rateW > rateH) {
                newImage = ImageProcessor.scale(newImage, (int)(width*rateH), height);
            } else if (rateW < rateH){
                newImage = ImageProcessor.scale(newImage, width, (int)(new_height*rateW));
            } else {
                newImage = ImageProcessor.scale(newImage, width, height);
            }

            // 填充数据到新的图片数组中
            new_width = newImage.getWidth(null);
            new_height = newImage.getHeight(null);

            arrayR = new int[new_width*new_height];
            arrayG = new int[new_width*new_height];
            arrayB = new int[new_width*new_height];
            ImageProcessor.getRGBChannels(newImage, arrayR, arrayG, arrayB);
            newArrayR = new int[width*height];
            newArrayG = new int[width*height];
            newArrayB = new int[width*height];

            int startW, startH;          // 寻找填充起始点
            if (new_height == height) {
                startH = 0;
            } else {
                startH = (int)Math.floor(pivot_h - new_height/2.0);
            }
            if (new_width == width) {
                startW = 0;
            } else {
                startW = (int)Math.floor(pivot_w - new_width/2.0);
            }

            for (int h = 0; h < new_height; h++) {         // 开始填充
                for (int w = 0; w < new_width; w++) {
                    newArrayR[(h+startH)*width + (w+startW)] = arrayR[h*new_width+w];
                    newArrayG[(h+startH)*width + (w+startW)] = arrayG[h*new_width+w];
                    newArrayB[(h+startH)*width + (w+startW)] = arrayB[h*new_width+w];
                }
            }

            // 重新生成图片
            newImage = ImageProcessor.getImageFrom3Channels(newArrayR, newArrayG, newArrayB, width, height);
        }

        return newImage;
    }

    public static Image process(Image img, double angle) {
        return process(img, angle, true);
    }

    private static Point rotatePoint(Point target, Point pivot, double angle) {
        double rAng = Math.toRadians(angle);
        double cosAng = Math.cos(rAng);
        double sinAng = Math.sin(rAng);

        double relative_target_h = target.h - pivot.h;
        double relative_target_w = target.w - pivot.w;
        double relative_new_h = relative_target_h * cosAng - relative_target_w * sinAng;
        double relative_new_w = relative_target_w * cosAng + relative_target_h * sinAng;
        return new Point(relative_new_h + pivot.h, relative_new_w+pivot.w);
    }
}

class Point {
    public double h;
    public double w;
    public Point(double h_, double w_) {
        h = h_;
        w = w_;
    }
}