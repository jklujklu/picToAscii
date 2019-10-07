package com.mysea;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
public class Heel {
	public static void main(String[] args) {
		//jpgToGif(decode(new File("G://1.gif")), "G://hdjahjk.gif", 300);
		createAsciiPic("G://1111.jpeg", "G://hahaha/d.jpg");
	}
	
	private static String regex(String str) {
		String regex = "\\.(jpg|jpeg|png|gif)";
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
	private synchronized static void jpgToGif(BufferedImage pic[], String newPic, int playTime) {  
	        try {  
	            AnimatedGifEncoder e = new AnimatedGifEncoder(); 
	            e.setRepeat(0);  
	            e.start(newPic);   
	            for (int i = 0; i < pic.length; i++) {  
	                e.setDelay(playTime); //设置播放的延迟时间  
	                 // 读入需要播放的jpg文件  
	                e.addFrame(pic[i]);  //添加到帧中  
	            }  
	            e.finish();  
	        } catch (Exception e) {  
	            System.out.println( "jpgToGif Failed:");  
	            e.printStackTrace();  
	        }  
	    }  
	public static void createAsciiPic(final String path,String target) {
			final String base = "@#&$%*o!;.";// 字符串由复杂到简单
			File f = new File(target);
			if(!f.exists()){
				try {
					f.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else{
				f.delete();
			}
			BufferedImage result;
			Graphics2D g2 = null;
			try {
				final BufferedImage image = ImageIO.read(new File(path));
				result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
				g2 = result.createGraphics();
//				result = g2.getDeviceConfiguration().createCompatibleImage(image.getWidth(), image.getHeight(), Transparency.TRANSLUCENT);
//				g2.dispose();
//				g2 = result.createGraphics();
			    g2.setBackground(Color.WHITE);     
			    g2.clearRect(0, 0,image.getWidth(), image.getHeight());     
			    g2.setPaint(Color.BLACK);     
				for (int y = 0; y < image.getHeight(); y += 3) {
					for (int x = 0; x < image.getWidth(); x+=3) {
						final int pixel = image.getRGB(x, y);
						final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
						final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
						final int index = Math.round(gray * (base.length() + 1) / 255);
						g2.drawString(index >= base.length() ? " " : String.valueOf(base.charAt(index)), x, y);
					}
					System.out.println();
				}
				ImageIO.write(result, "png", f);  
				g2.dispose();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

	private static BufferedImage createAsciiPic(BufferedImage bi) {
		final String base = "@#&$%*o!;.";// 字符串由复杂到简单
		
		BufferedImage result;
		Graphics2D g2 = null;
		result = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
//		g2 = result.createGraphics();
//		result = g2.getDeviceConfiguration().createCompatibleImage(bi.getWidth(), bi.getHeight(), Transparency.TRANSLUCENT);
//		g2.dispose();
		g2 = result.createGraphics();
		g2.setBackground(Color.WHITE);     
		g2.clearRect(0, 0,bi.getWidth(), bi.getHeight());     
		g2.setPaint(Color.BLACK);     
		for (int y = 0; y < bi.getHeight(); y += 3) {
			for (int x = 0; x < bi.getWidth(); x+=3) {
				final int pixel = bi.getRGB(x, y);
				final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
				final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
				final int index = Math.round(gray * (base.length() + 1) / 255);
				g2.drawString(index >= base.length() ? " " : String.valueOf(base.charAt(index)), x, y);
			}
			System.out.println();
		}
	
		g2.dispose();
		return result;
	}
	private static BufferedImage[] decode(File f) {
		int delay;
		BufferedImage[] bi;
		BufferedImage[] result;
        GifDecoder decoder = new GifDecoder();
        try {
            decoder.read(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 获取到帧数
        int frameCount = decoder.getFrameCount();
        bi = new BufferedImage[frameCount];
        result = new BufferedImage[frameCount];
        // 获取到每一帧的数据保存到bi
        for (int i = 0; i < frameCount; i++) {
            bi[i] = decoder.getFrame(i);
            result[i] = createAsciiPic(bi[i]);
        }
        // 获取到每帧之间的延迟时间，这里只取第一帧的
        delay = decoder.getDelay(0);
        // ImageIO.write(frame, "jpeg", out); 该方法用于输出分解得到的单个图片文件
        return result;
    }
}
