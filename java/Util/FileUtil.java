package Util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileUtil {
    public static String read(){
        String path = "";
        FileNameExtensionFilter ff = new FileNameExtensionFilter("ͼƬ(jpg/gif/png)", "bmp","jpg","png", "jpeg", "gif");
        JFileChooser fc = new JFileChooser("d:/");
        fc.setMultiSelectionEnabled(false);
        fc.setFileFilter(ff);
        int result = fc.showOpenDialog(null);
        //JFileChooser.APPROVE_OPTION�Ǹ����ͳ���������0������˵������0��ֵ���ǲ�ִ����ز���������ʲôҲ������
        if(result == JFileChooser.APPROVE_OPTION){
            //�����ѡ����ļ�����·�������������Ȼ�����ǻ�����·���󻹿������ܶ���¡�
            path = fc.getSelectedFile().getAbsolutePath();
            System.out.println(path);
        }
        return path;
    }
}
