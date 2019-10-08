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
     * ����ͼƬ��ʽ�����з������
     * @param int multiple   ͼƬ�Ŵ���
     * @param int degree     �ַ��ܼ��̶�
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
     * �ж�ͼƬ��ʽ
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
     * ��һ��ͼƬת��ΪAscii�ַ���
     * @param BufferedImage bi  ��Ҫ�����ͼƬ
     * @param boolean isGif     ����gif��Ϊ��������һ�����ش�����ͼƬ��Ϊ�٣����ڱ��ر���ͼƬ���˳�
     * @param int multiple      ͼƬ�Ŵ���
     * @param int degree        �ַ��ܼ��̶�
     */
    private synchronized BufferedImage picToAscii(BufferedImage bi,boolean isGif,int multiple, int degree) {
        final String base = "@#&$%*o!;.";// �ַ����ɸ��ӵ���

        BufferedImage result;
        Graphics2D g2 = null;
        //�ڴ��д����հ׻��壬���Ascii
        result = new BufferedImage(bi.getWidth()*multiple, bi.getHeight()*multiple, BufferedImage.TYPE_INT_RGB);
        g2 = result.createGraphics();
//      ͸������
//		result = g2.getDeviceConfiguration().createCompatibleImage(image.getWidth(), image.getHeight(), Transparency.TRANSLUCENT);
//		g2.dispose();
//		g2 = result.createGraphics();

        //���û��屳��Ϊ��ɫ
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0,bi.getWidth()*multiple, bi.getHeight()*multiple);//���б����У����û�У�����Ϊ��
        //���û���Ϊ��ɫ
        g2.setPaint(Color.BLACK);

        //��ȡͼƬ��������
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
     * ��gifͼƬ�ֽ⣬�ҳ�������֡
     * @param File f         ͼƬԴ
     * @param int multiple   ͼƬ�Ŵ���
     * @param int degree     �ַ��ܼ��̶�
     */
    private  BufferedImage[] gifDecode(File f,int multiple, int degree) {
        //ԴͼƬ
        BufferedImage[] source;
        //AsciiͼƬ
        BufferedImage[] result;

        GifDecoder decoder = new GifDecoder();
        try {
            decoder.read(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // ��ȡ��֡��
        int frameCount = decoder.getFrameCount();
        //�����ʼ��
        source = new BufferedImage[frameCount];
        result = new BufferedImage[frameCount];
        // ��ȡ��ÿһ֡�����ݣ�ͬʱ����ת�����ÿһ֡
        for (int i = 0; i < frameCount; i++) {
            source[i] = decoder.getFrame(i);
            result[i] = picToAscii(source[i],true,multiple,degree);
        }
        return result;
    }

    /*
     * ������ͼƬ�ϳ�gif
     * BufferedImage pic[]  �ڴ���ͼƬ����
     * int playTime         gifһ֡ˢ���ٶ�
     */
    private synchronized  void gifToGif(BufferedImage pic[], int playTime) {
        try {
            //gif������
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.setRepeat(0);
            //���ñ���Ŀ����Ե�ַ
            e.start(getTarget());
            for (int i = 0; i < pic.length; i++) {
                e.setDelay(playTime); //���ò��ŵ��ӳ�ʱ��
                // ������Ҫ���ŵ�jpg�ļ�
                e.addFrame(pic[i]);  //��ӵ�֡��
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
