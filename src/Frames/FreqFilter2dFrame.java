package com.sdl.ImageProcessor.Frames;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.sdl.ImageProcessor.Processors.FreqFilter2dProcessor;
import com.sdl.ImageProcessor.Dialogs.AverageWidthDialog;

public class FreqFilter2dFrame extends ResultFrame{
    private static final double[][] LapArray = {
        {-1, -1, -1},
        {-1,  8, -1},
        {-1, -1, -1}
    };

    public FreqFilter2dFrame(Image img, BasicFrame p) {
        this("Frequency Filtering", img, p);
    }

    public FreqFilter2dFrame(String text, Image img, BasicFrame p) {
        super(text, img, p);
        setTitle("Frequency Filtering");
 
        JMenuBar bar = getJMenuBar();
        JMenu processing = new JMenu("Processing");
        JMenuItem fourier = new JMenuItem("Fourier Spectrum");
        JMenuItem ifourier = new JMenuItem("Inverse Fourier");
        JMenu filtering = new JMenu("Frequency Filters");
        JMenuItem avg =new JMenuItem("Averaging");
        JMenuItem lap =new JMenuItem("Laplacian");

        fourier.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    resultImage = FreqFilter2dProcessor.dft2d(resultImage,true);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });

        ifourier.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    resultImage = FreqFilter2dProcessor.dft2d(originalImage,false) ;
                    label.setIcon(new ImageIcon(resultImage));
                }
        });

        avg.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AverageWidthDialog awd = new AverageWidthDialog(FreqFilter2dFrame.this);
                    int w = awd.getSquareWidth();
                    double[][] averageArray = new double[w][w];
                    for (int i = 0; i < w; i++) {
                        for (int j = 0; j < w; j++) {
                            averageArray[i][j] = 1.0/(w*w);
                        }
                    }
                    resultImage = FreqFilter2dProcessor.process(resultImage,averageArray);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });
        lap.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    resultImage = FreqFilter2dProcessor.process(resultImage,LapArray);
                    label.setIcon(new ImageIcon(resultImage));
                }
        });

        processing.add(fourier);
        processing.add(ifourier);
        processing.add(filtering);
        filtering.add(avg);
        filtering.add(lap);
        bar.add(processing);

        pack();
    }
}