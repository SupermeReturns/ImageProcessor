package com.sdl.ImageProcessor.Frames;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class BasicFrame extends JFrame {
    protected BasicFrame parent_;
    protected Image resultImage;
    protected Image originalImage;
    protected JLabel label = new JLabel("This is a new window!", JLabel.CENTER);
    protected JMenu fileMenu;

    public BasicFrame(String text) {
        this(text, (Image)null, (BasicFrame)null);
    }

    public BasicFrame(String text, Image i) {
        this(text, i, (BasicFrame)null);
    }

    public BasicFrame(String text, Image i, BasicFrame p) {
        super(text);

        resultImage = i;
        originalImage = i;
        parent_ = p;

        // 设置菜单项
        fileMenu = new JMenu("File");
        JMenuItem openItem =new JMenuItem("Open");
        JMenuItem saveItem =new JMenuItem("Save");
        JMenuItem exitItem =new JMenuItem("Exit");
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        openItem.addActionListener(new OpenHandler());
        saveItem.addActionListener(new SaveHandler());
        exitItem.addActionListener(
            new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);
        bar.add(fileMenu);

        // 显示图片       
        if (originalImage != null) {
            setLabelImage(originalImage);
        }
        Container contentPane = getContentPane();  
        contentPane.add(label);

        // 设置窗口大小，显示，退出操作
        setMinimumSize(new Dimension(500, 300));
        pack();
        setVisible( true );
        setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
    }

    public void setLabelImage(Image i) {
        if (i == null) {
            return;
        }
        resultImage = i;
        label.setIcon(new ImageIcon(resultImage));
    }

    public void setLabelText(String text) {
        label.setText(text);
    }

    class OpenHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser jc = new JFileChooser();
            int rVal = jc.showOpenDialog(BasicFrame.this);  //显示打开文件的对话框
            if(rVal == JFileChooser.APPROVE_OPTION) {
                String dir=jc.getCurrentDirectory().toString();
                String file=jc.getSelectedFile().getName();
                String prefix=file.substring(file.lastIndexOf(".")+1);
                try {
                    File input = new File(dir, file);
                    originalImage = ImageIO.read(input);
                    resultImage = originalImage;
                    label.setText(null);
                    label.setIcon(new ImageIcon(resultImage));
                    pack();
                } catch (Exception ex) {
                    System.out.println("Unknown image format:" + prefix);
                }
            }
        }
    }

    class SaveHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser jc = new JFileChooser();
            int rVal = jc.showSaveDialog(BasicFrame.this);  ////显示保存文件的对话框
            if(rVal == JFileChooser.APPROVE_OPTION) {
                String dir=jc.getCurrentDirectory().toString();
                String file=jc.getSelectedFile().getName();
                String prefix=file.substring(file.lastIndexOf(".")+1);
                try {
                    BufferedImage outputImage = new BufferedImage(resultImage.getWidth(null), resultImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
                    Graphics2D opImageContext = outputImage.createGraphics();
                    opImageContext.drawImage(resultImage, 0, 0, null);

                    File output = new File(dir, file);
                    ImageIO.write(outputImage, prefix, output);
                } catch (Exception ex) {
                    System.out.println("Unknown image formate:" + prefix);            
                }
            }
        }
    }

}