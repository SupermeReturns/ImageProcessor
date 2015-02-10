package com.sdl.ImageProcessor.Frames;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Image;

public class ResultFrame extends EditFrame {
    public ResultFrame(String text) {
        this(text, (Image)null, (BasicFrame)null);
    }

    public ResultFrame(String text, Image i) {
        this(text, i, (BasicFrame)null);
    }

    public ResultFrame(String text, Image i, BasicFrame p) {
        super(text, i, p);
        // 设置菜单项
        JMenuItem applyItem =new JMenuItem("Apply");
        fileMenu.add(applyItem, 0);

        applyItem.addActionListener(
            new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                parent_.setLabelImage(resultImage);
                parent_.pack();
                dispose();
            }
        });

        setLabelText(null);
        pack();
    }
}