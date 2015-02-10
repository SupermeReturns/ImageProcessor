package com.sdl.ImageProcessor.Frames;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JFlashPlayer;

public class DemoFrame extends JFrame {
    JFrame parent;
    JFlashPlayer flashPlayer;

    public DemoFrame(JFrame _parent) {
        super("Demonstration");
        parent = _parent;

        // set up help menu
        JMenu menu = new JMenu("Help");
        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);
        bar.add(menu);

        JMenuItem item = new JMenuItem("Open&Save");
        menu.add(item);
        item.addActionListener(
            new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                flashPlayer.load("resources/open_save.swf");
                flashPlayer.play();
            }
        });

        item = new JMenuItem("algebra transformation");
        menu.add(item);
        item.addActionListener(
            new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                flashPlayer.load("resources/algebra_transformation.swf");
                flashPlayer.play();
            }
        });

        item = new JMenuItem("color transformation");
        menu.add(item);
        item.addActionListener(
            new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                flashPlayer.load("resources/color_transformation.swf");
                flashPlayer.play();
            }
        });

        item = new JMenuItem("Flash Player");
        menu.add(item);
        item.addActionListener(
            new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                flashPlayer.load("resources/flash_player.swf");
                flashPlayer.play();
            }
        });

        // set up flash pannel
        NativeInterface.open();
        flashPlayer = new JFlashPlayer();
        this.add(flashPlayer);
        flashPlayer.setControlBarVisible(true); 
        // NativeInterface.runEventPump();

        pack();
        setSize(600, 400);
        setVisible(true); 
    }
}