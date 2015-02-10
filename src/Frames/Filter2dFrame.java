package com.sdl.ImageProcessor.Frames;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.sdl.ImageProcessor.Processors.Filter2dProcessor;
import com.sdl.ImageProcessor.Dialogs.AverageWidthDialog;
import com.sdl.ImageProcessor.Processors.Filter;

public class Filter2dFrame extends ResultFrame{
    private static final int[][] LapArray = {
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

    public Filter2dFrame(Image img, BasicFrame p) {
        this("Spatial Filtering", img, p);
    }

    public Filter2dFrame(String text, Image img, BasicFrame p) {
        super(text, img, p);
        setTitle("Spatial Filtering");
 
        JMenuBar bar = getJMenuBar();
        JMenu filtersMenu = new JMenu("Filters");
        JMenu avMenu = new JMenu("Average Filters");
        JMenuItem averageItem =new JMenuItem("Common Average Filter");
        JMenuItem amfItem =new JMenuItem("ArithmeticMeanFiltering");
        JMenuItem gmfItem =new JMenuItem("GemetricMeanFiltering");

        JMenuItem laplacianItem =new JMenuItem("Laplacian filter ");
        JMenu sobelMenu = new JMenu("Sobel Filters");
        JMenuItem pItem =new JMenuItem("Portrait");
        JMenuItem lItem =new JMenuItem("Landscape");

        JMenuItem hfItem =new JMenuItem("HarmonicFiltering");
        JMenuItem chfItem =new JMenuItem("ContraHarmonicFiltering");

        JMenu staMenu = new JMenu("Statistical  Filters");
        JMenuItem miItem =new JMenuItem("MinFiltering");
        JMenuItem maItem =new JMenuItem("MaxFiltering");
        JMenuItem meItem =new JMenuItem("MedianFiltering");

        hfItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AverageWidthDialog awd = new AverageWidthDialog(Filter2dFrame.this);
                    int w = awd.getSquareWidth();
                    resultImage = Filter2dProcessor.process(resultImage,Filter.HARMONIC, w);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });

        chfItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AverageWidthDialog awd = new AverageWidthDialog(Filter2dFrame.this);
                    int w = awd.getSquareWidth();
                    resultImage = Filter2dProcessor.process(resultImage,Filter.CONTRA_HARMONIC, w);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });

        miItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AverageWidthDialog awd = new AverageWidthDialog(Filter2dFrame.this);
                    int w = awd.getSquareWidth();
                    resultImage = Filter2dProcessor.process(resultImage,Filter.MIN, w);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });
        maItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AverageWidthDialog awd = new AverageWidthDialog(Filter2dFrame.this);
                    int w = awd.getSquareWidth();
                    resultImage = Filter2dProcessor.process(resultImage,Filter.MAX, w);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });
        meItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AverageWidthDialog awd = new AverageWidthDialog(Filter2dFrame.this);
                    int w = awd.getSquareWidth();
                    resultImage = Filter2dProcessor.process(resultImage,Filter.MEDIAN, w);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });
        amfItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AverageWidthDialog awd = new AverageWidthDialog(Filter2dFrame.this);
                    int w = awd.getSquareWidth();
                    resultImage = Filter2dProcessor.process(resultImage,Filter.ARITHMETIC_MEAN, w);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });
        gmfItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AverageWidthDialog awd = new AverageWidthDialog(Filter2dFrame.this);
                    int w = awd.getSquareWidth();
                    resultImage = Filter2dProcessor.process(resultImage,Filter.GEMETRIC_MEAN, w);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });

        averageItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AverageWidthDialog awd = new AverageWidthDialog(Filter2dFrame.this);
                    int w = awd.getSquareWidth();
                    int[][] averageArray = new int[w][w];
                    for (int i = 0; i < w; i++) {
                        for (int j = 0; j < w; j++) {
                            averageArray[i][j] = 1;
                        }
                    }
                    resultImage = Filter2dProcessor.process(resultImage,averageArray);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });
        laplacianItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    resultImage = Filter2dProcessor.process(resultImage,LapArray);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });
        pItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    resultImage = Filter2dProcessor.process(resultImage, sobelPArray);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });
        lItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    resultImage = Filter2dProcessor.process(resultImage,sobelLArray);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });

        avMenu.add(averageItem);
        avMenu.add(amfItem);
        avMenu.add(gmfItem);

        sobelMenu.add(pItem);
        sobelMenu.add(lItem);

        staMenu.add(miItem);
        staMenu.add(maItem);
        staMenu.add(meItem);

        filtersMenu.add(avMenu);
        filtersMenu.add(laplacianItem);
        filtersMenu.add(sobelMenu);
        filtersMenu.add(staMenu);
        filtersMenu.add(hfItem);
        filtersMenu.add(chfItem);

        bar.add(filtersMenu);

        JMenuItem Item =new JMenuItem("");
        pack();
    }
}