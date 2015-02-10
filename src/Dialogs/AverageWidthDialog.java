package com.sdl.ImageProcessor.Dialogs;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class AverageWidthDialog extends JDialog {
    private JLabel widthLabel = new JLabel("Width ");
    private JTextField widthText = new JTextField(10);
    private JButton button = new JButton("OK");

    public AverageWidthDialog(JFrame parent) {
        super(parent, "Choose average square width", true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(widthLabel);
        contentPane.add(widthText);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(button);
        pack();
        setVisible(true);
    }
    public int getSquareWidth() {
        return Integer.parseInt(widthText.getText());
    }
}