package Util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileUtil {
    public static String read(){
        String path = "";
        FileNameExtensionFilter ff = new FileNameExtensionFilter("图片(jpg/gif/png)", "bmp","jpg","png", "jpeg", "gif");
        JFileChooser fc = new JFileChooser("d:/");
        fc.setMultiSelectionEnabled(false);
        fc.setFileFilter(ff);
        int result = fc.showOpenDialog(null);
        //JFileChooser.APPROVE_OPTION是个整型常量，代表0。就是说当返回0的值我们才执行相关操作，否则什么也不做。
        if(result == JFileChooser.APPROVE_OPTION){
            //获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
            path = fc.getSelectedFile().getAbsolutePath();
            System.out.println(path);
        }
        return path;
    }
}
