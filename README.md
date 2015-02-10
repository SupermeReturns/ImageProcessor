#更新日日志
##2015年2月10日 星期二
###编译与运行
####1 编译
源代码可以使用ant进行编译(根目录已提供build.xml)，在源代码根目录输入"ant"就可以即可编译。本程序依赖于外部库SWT，所以编译时需要根据不同的操作平台使用不同的swt库。在swt_lib中已经提供了常见系统的swt库。只需要将相应的jar包覆盖掉src/libs/目录下的swt.jar文件即可(同时改名为swt.jar)。

####2 运行
在dist目录中已经有了一个可以运行的程序ImageProcessor.jar(for windows 64bit)，在命令行中“java -jar ImageProcessor.jar”即可运行，对于其他的系统，只需要相应的jar包覆盖掉dist/libs/目录下的swt.jar文件(同时改名为swt.jar)

###继承关系
       BasicFrame<br>
               |<br>
         EditFrame<br>
         |             |<br>
MainFrame ResultFrame<br>
                        |         |<br>
            FadeFrame Filter2dFrame<br>

###目录结构
简要介绍一些程序的实验细节。源代码的目录结构为<br>
src<br>
    Dialogs<br>
        AverageWidthDialog.java<br>
        GeneralDialog.java<br>
        QuantizeDialog.java<br>
        ScaleDialog.java<br>
    Frames<br>
        BasicFrame.java<br>
        DemoFrame.java<br>
        EditFrame.java<br>
        FadeFrame.java<br>
        Filter2dFrame.java<br>
        FlashFrame.java<br>
        FreqFilter2dFrame.java<br>
        MainFrame.java<br>
        MyFileFilter.java<br>
        ResultFrame.java<br>
        RotateFrame.java<br>
    lib<br>
        DJNativeSwing.jar<br>
        DJNativeSwing-SWT.jar<br>
        swt.jar<br>
    Processors<br>
        EqualizeHistProcessor.java<br>
        FadeProcessor.java<br>
        Filter.java<br>
        Filter2dProcessor.java<br>
        FreqFilter2dProcessor.java<br>
        HazeRemovalProcessor.java<br>
        ImageProcessor.java<br>
        NoiseGenerator.java<br>
        RotateProcessor.java<br>
    resources<br>
        flash_player.swf<br>
        image_process.swf<br>
    Main.java<br>

###功能模块
下面简要介绍一下各个组成部分相应的功能<br>

####1Main.java 程序起点
程序运行的起点，含有main函数。

####2 Processors 图片处理工具类
该目录下的源代码主要负责对图片进行处理。相应的代码对应一种图像处理方法，从其他package中可以调用这些工具类，非常方便。比如说EqualizeHistProcessor.java负责实现图像的直方图均匀化和生成直方图统计图;FadeProcessor.java负责实现图像的渐变效果;Filter2dProcessor.java负责实现图像的空间域滤波...其中有一个类叫ImageProcessor比较特殊，它是其他图像处理类的直接或间接父类，它本身有一些常用图片处理的方法比如说加载和保存图片等，所以其他类直接继承它，使得实现更加方便。

####3 lib 外部库
该目录下主要是软件使用的一些外部的库,主要的就是DJNativeSwing库和SWT库(来自eclipse)，如果没有它们，就无法实现flash文件的播放。SWT库是依赖于操作系统的，所以不同的操作系统就需要不同的swt库，windows和linux使用的swt库是不一样的，32位和64位系统使用的swt库也是不一样的，所以我在根目录都提供这些库文件，当更换运行环境的时候，需要使用它们替换该目录下的文件swt.jar。

####4 Frames 界面设计
该目录下存放实现软件图形界面的代码，主要存放窗口（Frame）的界面代码。比如说文件主界面的代码就在MainFrame.java文件中，演示程序的界面代码就在DemoFrame.java中。

####5 Dialogs 对话框
该目录也是存放软件图形界面的代码，主要是存放对话框(Dialog)的界面代码。比如说AverageWidthDialog.java存放的是询问图片宽度对话框的代码;QuantizeDialog.java存放的是询问量化级数对话框的代码;GeneralDialog.java存放的是通用的对话框的代码。

####6 resources 资源文件
该目录存放的程序需要使用的资源文件的代码，主要是程序的演示视频。

###个人心得
    终于到个人心得的部分了，这肯定是这学期最后一次作业报告了：）<br>
    一路走来，看到了这学期做过的所有报告，感慨良多。<br>
进入数媒方向，确实学到了很多东西，特别是从数字媒体基础和数字图像处理这两门课上面，老师的认真讲课，TA的耐心辅导，终于让自己在数字媒体方向有了一个较全面的入门。这学期学到的东西很杂，特别是数字媒体基础，只要是和数媒沾边的，老师的讲课基本都有过涉猎，这也让我对数媒方向有了一个大概的了解。特别是看到这个图像处理和动画播放的软件，虽然写的界面很挫，但是毕竟都是自己一行一行写的，每一个功能的实现都有一段独特的经历,所以看到它运行起来也很有成就感，感谢TA这次作业的机会使得我能够把所有的功能集合起来。我相信自己还会继续更新它的，把自己学到的新的技术添加到上面去，使它更加完善。<br>