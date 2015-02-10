package com.sdl.ImageProcessor.Frames;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import com.sdl.ImageProcessor.Dialogs.*;
import com.sdl.ImageProcessor.Processors.*;

public class MainFrame extends EditFrame{
    public MainFrame() {
        super("Image Processor");
        // Process 菜单项
        JMenu processMenu = new JMenu("Process");

        // Attention!!!!! 这里添加Process菜单项
        JMenuItem showR =new JMenuItem("RED");
        showR.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) { 
                    new ResultFrame("Extracted Red Channel", ImageProcessor.showChanelR(resultImage), MainFrame.this);
                }
        });
        JMenuItem showG =new JMenuItem("GREEN");
        showG.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) { 
                    new ResultFrame("Extracted Green Channel", ImageProcessor.showChanelG(resultImage), MainFrame.this);
                }
        });
        JMenuItem showB =new JMenuItem("BLUE");
        showB.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) { 
                    new ResultFrame("Extracted BLUEChannel", ImageProcessor.showChanelB(resultImage), MainFrame.this);
                }
        });
        JMenuItem showgray =new JMenuItem("GRAY");
        showgray.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) { 
                    new ResultFrame("Extracted GRAY Channel", ImageProcessor.showGray(resultImage), MainFrame.this);
                }
        });
        JMenuItem showbaw =new JMenuItem("Black and White");
        showbaw.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) { 
                    new ResultFrame("Extracted Channel: Black and White Image", ImageProcessor.showBAW(resultImage), MainFrame.this);
                }
        });

        JMenu ExChMenu = new JMenu("Extract Channel");
        ExChMenu.add(showR);
        ExChMenu.add(showG);
        ExChMenu.add(showB);
        ExChMenu.add(showgray);
        ExChMenu.add(showbaw);
        processMenu.add(ExChMenu);

        JMenu GLTransMenu = new JMenu("Gray Level Transform");
        JMenuItem slicingItem = new JMenuItem("Slicing");
        JMenuItem bSlicingItem = new JMenuItem("Binary Slicing");
        JMenuItem logItem = new JMenuItem("Log");
        JMenuItem powerLawItem = new JMenuItem("Power Law");
        JMenuItem negativeItem = new JMenuItem("Negative");
        GLTransMenu.add(slicingItem);
        GLTransMenu.add(bSlicingItem);
        GLTransMenu.add(logItem);
        GLTransMenu.add(powerLawItem);
        GLTransMenu.add(negativeItem);
        processMenu.add(GLTransMenu);

        slicingItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    String arr[] = {"Lower Bound", "Upper Bound"};
                    GeneralDialog gd = new GeneralDialog(MainFrame.this, arr);
                    int param1 = Integer.parseInt(gd.get(0));
                    int param2 = Integer.parseInt(gd.get(1));
                    new ResultFrame("Gray Level Slicing", GTransProcessor.process(resultImage, GTransProcessor.GRAY_LEVEL_SLICING, param1, param2), MainFrame.this);
                }
        });
        bSlicingItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    String arr[] = {"Lower Bound", "Upper Bound"};
                    GeneralDialog gd = new GeneralDialog(MainFrame.this, arr);
                    int param1 = Integer.parseInt(gd.get(0));
                    int param2 = Integer.parseInt(gd.get(1));
                    new ResultFrame("Gray Level Slicing(Binary)", GTransProcessor.process(resultImage, GTransProcessor.GRAY_LEVEL_SLICING_BINARY, param1, param2), MainFrame.this);
                }
        });
        logItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new ResultFrame("Log Transformation", GTransProcessor.process(resultImage, GTransProcessor.LOG), MainFrame.this);
                }
        });
        powerLawItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    String arr[] = {"Gamma Value"};
                    GeneralDialog gd = new GeneralDialog(MainFrame.this, arr);
                    double param1 = Double.parseDouble(gd.get(0));
                    new ResultFrame("Power Law Transformation: Gamma = " + param1, GTransProcessor.process(resultImage, GTransProcessor.POWER_LAW, param1), MainFrame.this);
                }
        });
        negativeItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new ResultFrame("Negative Transformation", GTransProcessor.process(resultImage, GTransProcessor.NEGATIVE), MainFrame.this);
                }
        });

        JMenuItem qu =new JMenuItem("Quantize");
        processMenu.add(qu);
        qu.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) { 
                    QuantizeDialog qd = new QuantizeDialog(MainFrame.this);
                    new ResultFrame("Quantize", ImageProcessor.quantize(resultImage, qd.getLevel()), MainFrame.this);
                }
        });
        JMenuItem sc =new JMenuItem("Scale");
        processMenu.add(sc);
        sc.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    ScaleDialog sd = new ScaleDialog(MainFrame.this); 
                    new ResultFrame("Scale", ImageProcessor.scale(resultImage, sd.getInputWidth(), sd.getInputHeight()), MainFrame.this);
                }
        });
        JMenuItem fd =new JMenuItem("Fade");
        processMenu.add(fd);
        fd.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    Image newImg;
                    JFileChooser jc = new JFileChooser();
                    int rVal = jc.showOpenDialog(MainFrame.this);  //显示打开文件的对话框
                    if(rVal == JFileChooser.APPROVE_OPTION) {
                        String dir=jc.getCurrentDirectory().toString();
                        String file=jc.getSelectedFile().getName();
                        String prefix=file.substring(file.lastIndexOf(".")+1);
                        try {
                            File input = new File(dir, file);
                            newImg = ImageIO.read(input);
                            new FadeFrame(resultImage, newImg, MainFrame.this);
                        } catch (Exception ex) {
                            System.out.println("Unknown image format:" + prefix);
                        }
                    }
                }
        });
        JMenuItem aes =new JMenuItem("Apply Equalization(Seperate)");
        aes.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new ResultFrame("Apply Equalization", EqualizeHistProcessor.process(resultImage), MainFrame.this);
                }
        });
        JMenuItem aea =new JMenuItem("Apply Equalization(Average)");
        aea.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new ResultFrame("Apply Equalization", EqualizeHistProcessor.process(resultImage,EqualizeHistProcessor.RGB_AVERAGE_MODE), MainFrame.this);
                }
        });
        JMenuItem ph =new JMenuItem("Produce Histogram");
        ph.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new ResultFrame("Image Histogram", EqualizeHistProcessor.getHistogramImage(resultImage, ImageProcessor.RED), MainFrame.this);
                }
        });
        JMenu histoMenu = new JMenu("Histogram Equalization");
        histoMenu.add(aes);
        histoMenu.add(aea);
        histoMenu.add(ph);
        processMenu.add(histoMenu);

        JMenuItem spn =new JMenuItem("Apply SaltAndPepper Noise");
        spn.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new ResultFrame("Apply SaltAndPepper Noise", NoiseGenerator.addSaltAndPepperNoise(resultImage, 0, 0.2), MainFrame.this);
                }
        });
        JMenuItem gn =new JMenuItem("Apply Gaussian Noise");
        gn.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new ResultFrame("Apply Gaussian Noise", NoiseGenerator.addGaussianNoise(resultImage, 0, 40), MainFrame.this);
                }
        });

        JMenu noiseMenu = new JMenu("Noise Genarator");
        noiseMenu.add(spn);
        noiseMenu.add(gn);
        processMenu.add(noiseMenu);


        JMenuItem sf =new JMenuItem("Spatial Filtering ");
        processMenu.add(sf);
        sf.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new Filter2dFrame(resultImage, MainFrame.this);
                }
        });

        JMenuItem ff =new JMenuItem("Frequency Filtering ");
        processMenu.add(ff);
        ff.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new FreqFilter2dFrame(resultImage, MainFrame.this);
                }
        });

        JMenu dehazeMenu = new JMenu("Dehazing");   // Haze removal menu item
        JMenuItem dhazeItem = new JMenuItem("Apply Dehazing");
        JMenuItem darkItem = new JMenuItem("Dark Channel");
        JMenuItem transItem = new JMenuItem("Transmission Map");
        processMenu.add(dehazeMenu);
        dehazeMenu.add(dhazeItem);
        dehazeMenu.add(darkItem);
        dehazeMenu.add(transItem);
        dhazeItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new ResultFrame("Apply Haze Removal", HazeRemovalProcessor.process(resultImage), MainFrame.this);
                }
        });
        darkItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new ResultFrame("Extract Dark Channel", HazeRemovalProcessor.getDarkImage(resultImage), MainFrame.this);
                }
        });
        transItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new ResultFrame("Extract Transmission Map", HazeRemovalProcessor.getTransImage(resultImage), MainFrame.this);
                }
        });

        JMenuItem rotateItem = new JMenuItem("Rotate");
        processMenu.add(rotateItem);
        rotateItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new RotateFrame("Rotate Image", resultImage, MainFrame.this);
                }
        });

        // Attention!!!!! 这里添加Process菜单项

        // Flash player
        JMenu flashMenu = new JMenu("Animation");
        JMenuItem flashItem = new JMenuItem("Play Flash");
        flashItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new FlashFrame(MainFrame.this);
                }
        });
        flashMenu.add(flashItem);

        // Help 菜单项
        JMenu helpMenu = new JMenu("Help");
        JMenuItem demoItem = new JMenuItem("Demo");
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(demoItem);
        helpMenu.add(aboutItem);
        demoItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new DemoFrame(MainFrame.this);
                }
        });
        aboutItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    String info = new String("This is a simple image processing tool\nAuthor: SunDongliang");
                    JOptionPane.showMessageDialog(null,
                        info, "About",
                        JOptionPane.INFORMATION_MESSAGE);
                }
        });

        JMenuBar bar = getJMenuBar();
        bar.add(fileMenu);
        bar.add(processMenu);
        bar.add(flashMenu);
        bar.add(helpMenu);
        JMenuItem exitItem = bar.getMenu(0).getItem(3);
        exitItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    System.exit(0);
                }
        });

        // 显示提示信息
        setLabelText("Please open a new image!");

        // 设置窗口大小，显示，退出操作
        pack();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}