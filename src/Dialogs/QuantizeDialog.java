package com.sdl.ImageProcessor.Dialogs;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class QuantizeDialog extends JDialog {
    private JLabel levelLabel = new JLabel("Level: ");
    private JTextField levelText = new JTextField(10);
    private JButton button = new JButton("OK");

    public QuantizeDialog(JFrame parent) {
        super(parent, "Quantize image to...", true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(levelLabel);
        contentPane.add(levelText);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(button);
        pack();
        setVisible(true);
    }
    public int getLevel() {
        return Integer.parseInt(levelText.getText());
    }
}