package com.sdl.ImageProcessor.Frames;

import java.awt.Image;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.sdl.ImageProcessor.Processors.FadeProcessor;

public class FadeFrame extends ResultFrame implements AdjustmentListener{
    private JScrollBar scrollBar;
    private Image img1_, img2_;

    public FadeFrame(Image img1, Image img2, BasicFrame p) {
        this("Fade Effect", img1, img2, p);
    }

    public FadeFrame(String text, Image img1, Image img2, BasicFrame p) {
        super(text, FadeProcessor.fade(img1, img2, 0.5f), p);
        img1_ = img1;
        img2_ = img2;
        setTitle("Fade Effect");
        scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 50, 10, 0, 110);
        scrollBar.addAdjustmentListener(this);
        Container contentPane = getContentPane();  
        contentPane.add(scrollBar,BorderLayout.SOUTH);
        pack();
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {  
        resultImage = FadeProcessor.fade(img1_, img2_, (100-e.getValue())/100f);
        label.setIcon(new ImageIcon(resultImage));
    }
}