import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import Util.*;
public  class PicToAscii {

    private String path;
    private String target;
    private String format;

    public PicToAscii(){
        setPath(FileUtil.read());
        setFormat();
        setTarget();
    }
    public String getFormat() {
        return format;
    }

    public void setFormat() {
        this.format = regexFormat(getPath());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget() {
        String name = regexName(getPath());
        this.target = getPath().replace(name,name+"-output");
    }

    public static void main(String[] args) {
        PicToAscii pic = new PicToAscii();
        pic.init(4,1);
    }

    public void init(int multiple, int degree){
        createAsciiPic(multiple, degree);
    }

    /*
     * 根据图片格式，进行分类操作
     * @param int multiple   图片放大倍率
     * @param int degree     字符密集程度
     */
    private  void createAsciiPic(int multiple, int degree){
        switch (getFormat()){
            case "gif":
                gifToGif(gifDecode(new File(getPath()),multiple,degree),100);
                break;
            default:
                try {
                    picToAscii(ImageIO.read(new File(getPath())),false,multiple,degree);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /*
     * 判断图片格式
     */
    private  String regexFormat(String str) {
        String regex = "\\.(jpg|jpeg|png|gif|bmp)";
        Pattern p =Pattern.compile(regex);
        Matcher m = p.matcher(str);
        if(m.find()) {
            System.out.println(m.group(1));
            return m.group(1);
        }else {
            System.out.println("No Match");
            return null;
        }
    }

    private String regexName(String path){
        String regex = "\\\\([^.\\\\]+)";
        Pattern p =Pattern.compile(regex);
        Matcher m = p.matcher(path);
        int start = 0, end = 0;
        while(m.find()) {
            start = m.start();
            end =m.end();
        }
        char[] temp = new char[end-start-1];
        path.getChars(start+1,end,temp,0);
        return new String(temp);
    }
    /*
     * 将一张图片转化为Ascii字符画
     * @param BufferedImage bi  需要处理的图片
     * @param boolean isGif     区分gif，为真则向上一级返回处理后的图片，为假，则于本地保存图片并退出
     * @param int multiple      图片放大倍率
     * @param int degree        字符密集程度
     */
    private synchronized BufferedImage picToAscii(BufferedImage bi,boolean isGif,int multiple, int degree) {
        final String base = "@#&$%*o!;.";// 字符串由复杂到简单

        BufferedImage result;
        Graphics2D g2 = null;
        //内存中创建空白画板，存放Ascii
        result = new BufferedImage(bi.getWidth()*multiple, bi.getHeight()*multiple, BufferedImage.TYPE_INT_RGB);
        g2 = result.createGraphics();
//      透明画板
//		result = g2.getDeviceConfiguration().createCompatibleImage(image.getWidth(), image.getHeight(), Transparency.TRANSLUCENT);
//		g2.dispose();
//		g2 = result.createGraphics();

        //设置画板背景为白色
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0,bi.getWidth()*multiple, bi.getHeight()*multiple);//此行必须有，如果没有，画板为黑
        //设置画笔为黑色
        g2.setPaint(Color.BLACK);

        //读取图片所有像素
        for (int y = 0; y < bi.getHeight() * multiple; y += degree * multiple) {
            for (int x = 0; x < bi.getWidth() * multiple; x += degree * multiple) {
                final int pixel = bi.getRGB(x / multiple, y / multiple);
                final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
                final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                final int index = Math.round(gray * (base.length() + 1) / 255);
                g2.drawString(index >= base.length() ? " " : String.valueOf(base.charAt(index)), x, y);
            }
        }
        if(!isGif){
            File f = new File(getTarget());
            try {
                if(f.exists()){
                    f.delete();
                }else{
                    f.createNewFile();
                }
                ImageIO.write(result, getFormat(), f);
                System.out.println(getTarget()+" is generated !!!");
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                return null;
            }
        }
        g2.dispose();
        return result;
    }

    /*
     * 将gif图片分解，找出其所有帧
     * @param File f         图片源
     * @param int multiple   图片放大倍率
     * @param int degree     字符密集程度
     */
    private  BufferedImage[] gifDecode(File f,int multiple, int degree) {
        //源图片
        BufferedImage[] source;
        //Ascii图片
        BufferedImage[] result;

        GifDecoder decoder = new GifDecoder();
        try {
            decoder.read(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 获取到帧数
        int frameCount = decoder.getFrameCount();
        //数组初始化
        source = new BufferedImage[frameCount];
        result = new BufferedImage[frameCount];
        // 获取到每一帧的数据，同时保存转化后的每一帧
        for (int i = 0; i < frameCount; i++) {
            source[i] = decoder.getFrame(i);
            result[i] = picToAscii(source[i],true,multiple,degree);
        }
        return result;
    }

    /*
     * 将多张图片合成gif
     * BufferedImage pic[]  内存中图片集合
     * int playTime         gif一帧刷新速度
     */
    private synchronized  void gifToGif(BufferedImage pic[], int playTime) {
        try {
            //gif生成类
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.setRepeat(0);
            //设置保存目标绝对地址
            e.start(getTarget());
            for (int i = 0; i < pic.length; i++) {
                e.setDelay(playTime); //设置播放的延迟时间
                // 读入需要播放的jpg文件
                e.addFrame(pic[i]);  //添加到帧中
            }
            e.finish();
        } catch (Exception e) {
            System.out.println( "jpgToGif Failed!");
            e.printStackTrace();
        }finally{
            System.out.println(getTarget()+" is generated !!!");
        }
    }

}
