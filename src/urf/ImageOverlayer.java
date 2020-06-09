package urf;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImageOverlayer extends JPanel {
	private BufferedImage top, bottom;
	
	public ImageOverlayer(BufferedImage topImg, BufferedImage bottomImg) {
		top = topImg;
		bottom = bottomImg;
	}
	
	public static void overlay(BufferedImage topImg, BufferedImage bottomImg) {
		bottomImg.createGraphics().drawImage(topImg, 0, 0, null);
	}

//	public static BufferedImage overlay(BufferedImage topImg, BufferedImage bottomImg) {
//		ImageOverlayer imgOver = new ImageOverlayer(topImg, bottomImg);
//		return imgOver.oOverlay();
//	}
//	
//	private BufferedImage oOverlay() {
//		Graphics2D gfix = bottom.createGraphics();
//	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(bottom, 0, 0, null);
		g.drawImage(top, 0, 0, null);
	}
}