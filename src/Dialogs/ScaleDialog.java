package com.sdl.ImageProcessor.Dialogs;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class ScaleDialog extends JDialog {
    private JLabel widthLabel = new JLabel("width: ");
    private JTextField widthText = new JTextField(10);
    private JLabel heightLabel = new JLabel("height: ");
    private JTextField heightText = new JTextField(10);
    private JButton button = new JButton("OK");
    public ScaleDialog(JFrame parent) {
        super(parent, "Quantize image to...", true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(widthLabel);
        contentPane.add(widthText);
        contentPane.add(heightLabel);
        contentPane.add(heightText);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(button);
        pack();
        setVisible(true);
    }
    public int getInputWidth() {
        return Integer.parseInt(widthText.getText());
    }
    public int getInputHeight() {
        return Integer.parseInt(heightText.getText());        
    }
}