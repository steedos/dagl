package cn.proem.stamp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import cn.proem.stamp.TIFFReader;
import cn.proem.stamp.TIFFWriter;


public class PicTifWriter {

	public static void main(String[] args) throws IOException {
		String filePath = "C:\\Users\\Administrator\\Desktop\\01300000098168132125204980537.tif";
		String outPath = "C:\\Users\\Administrator\\Desktop\\图片112.tif";
		// drawTextInImg(filePath, outPath, new FontText("中国", 1, "#CC2BAC", 40, "黑体"));
		String[][] fonts = { { "测试01", "测试02", "测试03" }, { "测试11", "测试12", "测试13" }, { "测试21", "测试22", "aaa测试23" } };
		drawTable(filePath, outPath, 10, 20, 100, 20, 2, 2, fonts, 1, 12,Color.BLACK, Color.RED);
	}
	
	public static BufferedImage convertRenderedImage(RenderedImage img) {
	    if (img instanceof BufferedImage) {
	        return (BufferedImage)img;  
	    }   
	    ColorModel cm = img.getColorModel();
	    int width = img.getWidth();
	    int height = img.getHeight();
	    WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
	    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	    Hashtable properties = new Hashtable();
	    String[] keys = img.getPropertyNames();
	    if (keys!=null) {
	        for (int i = 0; i < keys.length; i++) {
	            properties.put(keys[i], img.getProperty(keys[i]));
	        }
	    }
	    BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
	    img.copyData(raster);
	    return result;
	}
	
	public static void drawTable(String path,String outpath, 
			int x, int y, // 输出位置 
			int width, int height, // 列宽列高 
			int rownums, int colnums, // 列数行数
			String[][] fonts,  // 输出内容
			int weight, int size, Color linecolor, Color fontcolor // 输出样式
			) throws IOException{
		File _file = new File(path);
		TIFFReader reader = new TIFFReader(_file);
		RenderedImage image = reader.getPage(0);
		BufferedImage bimage =  convertRenderedImage(image);
		Graphics2D g = bimage.createGraphics();
		g.setColor(linecolor);
		g.setBackground(Color.white);
		g.setStroke(new BasicStroke(Float.valueOf(String.valueOf(weight))));
		// 绘制横线
		for (int line = 0; line <= rownums; line++) {
			g.drawLine(x, y + line * height, x + width * colnums, y + line * height);
		}

		for (int row = 0; row <= colnums; row++) {
			g.drawLine(x + row * width, y, x + row * width, y + height * rownums);
		}
		
		
		g.setColor(fontcolor);
		Font font = new Font("黑体", Font.BOLD, size);
		g.setFont(font);
		FontMetrics metrics = new FontMetrics(font) {
		};
		for(int ldx = 0 ; ldx < rownums; ldx ++){
        	for(int rdx = 0; rdx < colnums; rdx ++)
        	{
        		Rectangle2D bounds = metrics.getStringBounds(fonts[ldx][rdx], null);
        		int po_x = (width/2 - (int) (bounds.getWidth() / 2)) > 0 ? x + (width/2 - (int) (bounds.getWidth() / 2)) : x;
        		int po_y = (height/2 - (int) (bounds.getHeight() / 2)) > 0 ? y - (height/2 - (int) (bounds.getHeight() / 2)) : y;
        		g.drawString(fonts[ldx][rdx], po_x + rdx * width, po_y + (ldx + 1) * height);
        	}
		}
		g.dispose();
		
		BufferedImage[] images = new BufferedImage[reader.countPages()];
		images[0] = bimage;
		for(int i=1; i < reader.countPages(); i++){
			images[i] = convertRenderedImage(reader.getPage(i));
		}
		TIFFWriter.createTIFFFromImages(images, new File(outpath));
	}

	public static void drawTextInImg(String filePath, String outPath, FontText text) {
		ImageIcon imgIcon = new ImageIcon(filePath);
		Image img = imgIcon.getImage();
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = bimage.createGraphics();
		g.setColor(getColor(text.getWm_text_color()));
		g.setBackground(Color.white);
		g.drawImage(img, 0, 0, null);
		Font font = null;
		if (text.getWm_text_font() != null && text.getWm_text_size() != null) {
			font = new Font(text.getWm_text_font(), Font.BOLD, text.getWm_text_size());
		} else {
			font = new Font(null, Font.BOLD, 15);
		}

		g.setFont(font);
		FontMetrics metrics = new FontMetrics(font) {
		};
		Rectangle2D bounds = metrics.getStringBounds(text.getText(), null);
		int textWidth = (int) bounds.getWidth();
		int textHeight = (int) bounds.getHeight();
		int left = 0;
		int top = textHeight;

		// 九宫格控制位置
		if (text.getWm_text_pos() == 2) {
			left = width / 2;
		}
		if (text.getWm_text_pos() == 3) {
			left = width - textWidth;
		}
		if (text.getWm_text_pos() == 4) {
			top = height / 2;
		}
		if (text.getWm_text_pos() == 5) {
			left = width / 2;
			top = height / 2;
		}
		if (text.getWm_text_pos() == 6) {
			left = width - textWidth;
			top = height / 2;
		}
		if (text.getWm_text_pos() == 7) {
			top = height - textHeight;
		}
		if (text.getWm_text_pos() == 8) {
			left = width / 2;
			top = height - textHeight;
		}
		if (text.getWm_text_pos() == 9) {
			left = width - textWidth;
			top = height - textHeight;
		}
		g.drawString(text.getText(), left, top);
		g.dispose();

		try {
			FileOutputStream out = new FileOutputStream(outPath);
			ImageIO.write(bimage, "JPEG", out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// color #2395439
	public static Color getColor(String color) {
		if (color.charAt(0) == '#') {
			color = color.substring(1);
		}
		if (color.length() != 6) {
			return null;
		}
		try {
			int r = Integer.parseInt(color.substring(0, 2), 16);
			int g = Integer.parseInt(color.substring(2, 4), 16);
			int b = Integer.parseInt(color.substring(4), 16);
			return new Color(r, g, b);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

}
