package urf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BufferedImageUtils {
	
	/*
	 *  overlays a top image over a bottom image.
	 *  The bottom image must be bigger than the top image.
	 *  
	 *  The offsets specify the coordinates within the bottom image that
	 *  the upper-left corner of the top image is placed
	 */
	public static BufferedImage loadOverlayedImage(BufferedImage top, BufferedImage bottom, int xOffset, int yOffset) {
		bottom.createGraphics().drawImage(top, 0, 0, null);
		return bottom;
	}
	
	public static BufferedImage drawLine(BufferedImage img, int x1, int y1, int x2, int y2, Color color) {
		Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.drawLine(x1, y1, x2, y2);
        return img;
	}
	
	public static BufferedImage drawRect(BufferedImage img, int x, int y, int width, int height, Color color) {
		Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.drawRect(x, y, width, height);
        return img;
	}
	
	public static BufferedImage fillRect(BufferedImage img, int x, int y, int width, int height, Color color) {
		Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(x, y, width, height);
        return img;
	}
	
	public static BufferedImage drawOval(BufferedImage img, int x, int y, int width, int height, Color color) {
		Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.drawOval(x, y, width, height);
        return img;
	}
	
	public static BufferedImage fillOval(BufferedImage img, int x, int y, int width, int height, Color color) {
		Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillOval(x, y, width, height);
        return img;
	}
}