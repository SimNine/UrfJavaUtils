package xyz.urffer.urfutils.pannablepanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PannablePanel extends JPanel {
	protected HashSet<Integer> keys = new HashSet<Integer>();
	
	protected int xScr = 0;
	protected int yScr = 0;	
	
	protected int xMouse = 0;
	protected int yMouse = 0;
	
	protected int xMouseDown = 0;
	protected int yMouseDown = 0;
	protected boolean mousePressed = false;
	
	public PannablePanel(int width, int height, boolean rerenderOnMove) {
		super();
		super.setFocusable(true);
		super.requestFocusInWindow();
		super.setSize(width, height);
		
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				xScr += (xMouseDown - e.getX());
				yScr += (yMouseDown - e.getY());
				
				if (rerenderOnMove)
					repaint();
				
				xMouseDown = e.getX();
				yMouseDown = e.getY();
			}
			public void mouseMoved(MouseEvent e) {
				xMouse = e.getX();
				yMouse = e.getY();
			}
		});
		
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				mousePressed = true;
				xMouseDown = e.getX();
				yMouseDown = e.getY();
			}
			public void mouseReleased(MouseEvent e) {
				mousePressed = false;
			}
		});
		
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				keys.add(e.getKeyCode());
			}
			public void keyReleased(KeyEvent e) {
				keys.remove(e.getKeyCode());
			}
			public void keyTyped(KeyEvent e) {}
		});
	}
	
	public int getXScr() {
		return xScr;
	}
	
	public int getYScr() {
		return yScr;
	}
	
	public int getMouseX() {
		return xMouse;
	}
	
	public int getMouseY() {
		return yMouse;
	}
}
