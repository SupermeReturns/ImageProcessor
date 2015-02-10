package com.sdl.ImageProcessor.Frames;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JFlashPlayer;

public class FlashFrame extends JFrame {
    JFrame parent;
    JFlashPlayer flashPlayer;
    public FlashFrame(JFrame _parent) {
        super("Java Flash Player");
        parent = _parent;


        // TEST
        System.out.println("1");
        // TEST

        // set up File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem =new JMenuItem("Open");
        JMenuItem exitItem =new JMenuItem("Exit");
        fileMenu.add(openItem);
        fileMenu.add(exitItem);
        openItem.addActionListener(new OpenHandler());
        exitItem.addActionListener(
            new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });


        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);
        bar.add(fileMenu);

        // TEST
        System.out.println("3");
        // TEST

        // set up flash panel
        NativeInterface.open();
        System.out.println("4");
        flashPlayer = new JFlashPlayer();
        flashPlayer.setControlBarVisible(true); 
        System.out.println("5");
        this.add(flashPlayer);
        System.out.println("6");
        //NativeInterface.runEventPump();


        // TEST
        System.out.println("7");
        // TEST        

        pack();
        setSize(800, 600);
        setVisible(true);
    }
    
    class OpenHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser jc = new JFileChooser();
            // 去掉显示所有文件这个过滤器。
            // jc.setAcceptAllFileFilterUsed(false);
            // 只显示swf后缀的文件
            jc.addChoosableFileFilter(new MyFileFilter("swf", "Flash Files"));   

            int rVal = jc.showOpenDialog(FlashFrame.this);  //显示打开文件的对话框
            if(rVal == JFileChooser.APPROVE_OPTION) {
                String dir=jc.getCurrentDirectory().toString();
                String file=jc.getSelectedFile().getName();
                String prefix=file.substring(file.lastIndexOf(".")+1);
                try {
                    flashPlayer.load(dir+System.getProperty("file.separator")+file);
                    // flashPlayer.play();
                    pack();
                } catch (Exception ex) {
                    System.out.println("Damaged Flash Animation:" + file);
                }
            }
        }
    }
}
