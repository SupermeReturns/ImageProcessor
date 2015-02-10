package com.sdl.ImageProcessor.Frames;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.sdl.ImageProcessor.Processors.RotateProcessor;
import com.sdl.ImageProcessor.Dialogs.GeneralDialog;

public class RotateFrame extends ResultFrame implements AdjustmentListener{
    private JCheckBoxMenuItem cbm;

    public RotateFrame(Image img, BasicFrame p) {
        this("Rotate Image", img, p);
    }
    public RotateFrame(String text, Image img, BasicFrame p) {
        super(text, img, p);
        setTitle("Rotate Image");
 
        JMenuBar bar = getJMenuBar();
        JMenu pMenu = new JMenu("操作");
        cbm = new JCheckBoxMenuItem("Keep Size", true);
        JMenuItem clockwise90 =new JMenuItem("顺时针90度");
        JMenuItem cclockwise90 =new JMenuItem("逆时针90度");
        JMenuItem rotate180 =new JMenuItem("旋转180度");
        JMenuItem userDifine =new JMenuItem("自定义");

        bar.add(pMenu);
        pMenu.add(cbm);
        pMenu.add(clockwise90);
        pMenu.add(cclockwise90);
        pMenu.add(rotate180);
        pMenu.add(userDifine);

        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 50, 10, 0, 110);
        scrollBar.addAdjustmentListener(this);
        Container contentPane = getContentPane();  
        contentPane.add(scrollBar,BorderLayout.SOUTH);

        clockwise90.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    resultImage = RotateProcessor.process(originalImage, 90, cbm.isSelected());
                    label.setIcon(new ImageIcon(resultImage));
                    RotateFrame.this.pack();
                }
        });

        cclockwise90.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    resultImage = RotateProcessor.process(originalImage, -90, cbm.isSelected());
                    label.setIcon(new ImageIcon(resultImage));
                    RotateFrame.this.pack();
                }
        });

        rotate180.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    resultImage = RotateProcessor.process(originalImage, 180, cbm.isSelected());
                    label.setIcon(new ImageIcon(resultImage));
                    RotateFrame.this.pack();
                }
        });

        userDifine.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    String arr[] = {"角度"};
                    GeneralDialog gd = new GeneralDialog(RotateFrame.this, arr);
                    double ang = Double.parseDouble(gd.get(0));
                    resultImage = RotateProcessor.process(resultImage, ang, cbm.isSelected());
                    label.setIcon(new ImageIcon(resultImage));
                    RotateFrame.this.pack();
                }
        });

        pack();
        setVisible(true);
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {  
        resultImage = RotateProcessor.process(originalImage, (e.getValue() - 50) / 50.0 * 90, cbm.isSelected());
        label.setIcon(new ImageIcon(resultImage));
        RotateFrame.this.pack();
    }
}
