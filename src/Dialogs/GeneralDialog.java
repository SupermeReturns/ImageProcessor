package com.sdl.ImageProcessor.Dialogs;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class GeneralDialog extends JDialog {
    private JTextField[] tfs;
    private JButton button = new JButton("OK");

    public GeneralDialog(JFrame parent, String[] arr) {   
        super(parent, "Please Input Parameters", true);

        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());
        tfs = new JTextField[arr.length];
        for (int i = 0; i < tfs.length; i++) {
            contentPane.add(new JLabel(arr[i]));
            tfs[i] = new JTextField(10);
            contentPane.add(tfs[i]);
        }
        contentPane.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        pack();
        setVisible(true);
    }

    public String get(int index) {
        return tfs[index].getText();
    }
}
