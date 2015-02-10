package com.sdl.ImageProcessor.Frames;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Image;

public class EditFrame extends BasicFrame {

    public EditFrame(String text) {
        this(text, (Image)null, (BasicFrame)null);
    }

    public EditFrame(String text, Image i) {
        this(text, i, (BasicFrame)null);
    }

    public EditFrame(String text, Image i, BasicFrame p) {
        super(text, i, p);

        // 设置菜单项
        JMenuItem originalItem =new JMenuItem("Original Image");
        fileMenu.add(originalItem, 0);

        originalItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    resultImage = originalImage;
                    setLabelImage(resultImage);    
                }
        });

        pack();
    }
}