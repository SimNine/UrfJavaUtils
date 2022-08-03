package xyz.urffer.urfutils.grapher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import xyz.urffer.urfutils.Pair;
import xyz.urffer.urfutils.pannablepanel.PannablePanel;

@SuppressWarnings("serial")
public class GraphPanel extends PannablePanel 
	implements MouseMotionListener, KeyListener {
	HashMap<String, GraphDataset> dataSets = new HashMap<String, GraphDataset>();
	HashSet<Integer> keys = new HashSet<Integer>();

	double xMin = -1;
	double xDim = 5;
	double yMin = -1;
	double yDim = 5;
	
	int xMousePos = 0;
	int yMousePos = 0;

	// modified default constructor
	public GraphPanel() {
		super(0, 0, true);
		
		this.setFocusable(true);
		this.requestFocusInWindow();
		
		this.addKeyListener(this);
		this.addMouseMotionListener(this);
	}
	
	/*
	 * Listeners
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		keys.add(e.getKeyCode());
		
//		double xDist = Math.abs(xMax - xMin)*0.05 + 1;
//		double yDist = Math.abs(yMax - yMin)*0.05 + 1;
//		
//		switch (e.getKeyCode()) {
//		case KeyEvent.VK_R:
//			resetGraph();
//			break;
//		case KeyEvent.VK_W: // increase y-zoom
//			if (yDist*20 > 5) {
//				yMin += yDist;
//				yMax -= yDist;
//			}
//			break;
//		case KeyEvent.VK_S: // decrease y-zoom
//			yMin -= yDist;
//			yMax += yDist;
//			break;
//		case KeyEvent.VK_D: // increase x-zoom
//			if (xDist*20 > 5) {
//				xMin += xDist;
//				xMax -= xDist;
//			}
//			break;
//		case KeyEvent.VK_A: // decrease x-zoom
//			xMin -= xDist;
//			xMax += xDist;
//			break;
//		}
		
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys.remove(e.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		xMousePos = e.getX();
		yMousePos = e.getY();
		repaint();
	}
	
	/*
	 * DRAWING METHODS
	 */

	// repaints everything
	public void paintComponent(Graphics g) {
		// clear the panel
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		drawAxes(g);
		drawPoints(g);
		drawScale(g);
		drawMouse(g);
		drawKey(g);
	}
	
	private void drawAxes(Graphics g) {
		// draw axes
		int[] axisPos = pointToScreen(0, 0);

		if (axisPos[0] < 0) {
			axisPos[0] = 0;
		} else if (axisPos[0] > this.getWidth()) {
			axisPos[0] = this.getWidth() - 1;
		}

		if (axisPos[1] < 0) {
			axisPos[1] = 0;
		} else if (axisPos[1] > this.getHeight()) {
			axisPos[1] = this.getHeight() - 1;
		}

		g.setColor(Color.BLACK);
		g.drawLine(0, axisPos[1] - this.yScr, getWidth(), axisPos[1] - this.yScr); // x-axis
		g.drawLine(axisPos[0] - this.xScr, 0, axisPos[0] - this.xScr, getHeight()); // y-axis
	}
	
	private void drawPoints(Graphics g) {
		// draw points
		for (GraphDataset d : dataSets.values()) {
			g.setColor(d.getColor());

			LinkedList<Pair<Double, Double>> ll = d.getPoints();
			for (int i = 0; i < ll.size() - 1; i++) {
				Pair<Double, Double> curr = ll.get(i);
				Pair<Double, Double> next = ll.get(i + 1);

				int[] currPos = pointToScreen(curr.a, curr.b);
				int[] nextPos = pointToScreen(next.a, next.b);

				g.drawRect(currPos[0] - 1 - xScr, currPos[1] - 1 - yScr, 3, 3);
				g.drawLine(currPos[0] - xScr, currPos[1] - yScr, 
						   nextPos[0] - xScr, nextPos[1] - yScr);
			}
		}
	}
	
	private void drawScale(Graphics g) {
		// draw vertical scale
		int yIntervalOrderOfMagnitude = (int)Math.log10(yDim) - 1;
		if (yIntervalOrderOfMagnitude < 0)
			yIntervalOrderOfMagnitude = 0;
		int yIntervalSize = (int)Math.pow(10, yIntervalOrderOfMagnitude);
		int yNumMarkers = ((int)(yDim / yIntervalSize) + 1) * 3;
		int yLowestScaleMarker = (int) (yMin - (yMin % yIntervalSize)) - 
				((yNumMarkers / 3) * yIntervalSize);

		g.setColor(Color.BLACK);
		for (int i = 0; i < yNumMarkers; i++) {
			int yTickCoord = yLowestScaleMarker + i*yIntervalSize;
			int[] tickpos = pointToScreen(0, yTickCoord);
			g.drawLine(tickpos[0] - 5 - xScr, tickpos[1] - yScr, 
					   tickpos[0] + 5 - xScr, tickpos[1] - yScr);
			g.drawString("" + yTickCoord, 
						 tickpos[0] - 20 - xScr, tickpos[1] + 8 - yScr);
		}
		
		// draw horizontal scale
		int xIntervalOrderOfMagnitude = (int)Math.log10(xDim) - 1;
		if (xIntervalOrderOfMagnitude < 0)
			xIntervalOrderOfMagnitude = 0;
		int xIntervalSize = (int)Math.pow(10, xIntervalOrderOfMagnitude);
		int xNumMarkers = ((int)(xDim / xIntervalSize) + 1) * 3;
		int xLowestScaleMarker = (int) (xMin - (xMin % xIntervalSize)) - 
				((xNumMarkers / 3) * xIntervalSize);

		g.setColor(Color.BLACK);
		for (int i = 0; i < xNumMarkers; i++) {
			int xTickCoord = xLowestScaleMarker + i*xIntervalSize;
			int[] tickpos = pointToScreen(xTickCoord, 0);
			g.drawLine(tickpos[0] - xScr, tickpos[1] - 5 - yScr, 
					   tickpos[0] - xScr, tickpos[1] + 5 - yScr);
			g.drawString("" + xTickCoord, 
						 tickpos[0] - 20 - xScr, tickpos[1] + 8 - yScr);
		}
	}
	
	private void drawKey(Graphics g) {
		// draw graph key
		g.setColor(Color.WHITE);
		g.fillRect(20, 20, 200, dataSets.values().size() * 20 + 10);
		g.setColor(Color.BLACK);
		g.drawRect(20, 20, 200, dataSets.values().size() * 20 + 10);
		int count = 0;
		for (GraphDataset d : dataSets.values()) {
			g.setColor(d.getColor());
			g.fillRect(30, 30 + count * 20, 10, 10);
			g.setColor(Color.BLACK);
			g.drawString(d.getName(), 45, 40 + count * 20);
			count++;
		}
	}
	
	private void drawMouse(Graphics g) {
		double[] mouseGraphPos = screenToPoint(xMousePos, yMousePos);
		String posString = Math.round(mouseGraphPos[0]*100.0)/100.0 + "," + Math.round(mouseGraphPos[1]*100.0)/100.0;
		
		int fontSize = g.getFont().getSize()/2;
		int boxSize = 25;
		
		g.setColor(Color.WHITE);
		g.fillRect(xMousePos, yMousePos - boxSize, posString.length()*fontSize, boxSize);
		
		g.setColor(Color.BLACK);
		g.drawRect(xMousePos, yMousePos - boxSize, posString.length()*fontSize + 5, boxSize);
		g.drawString(posString, xMousePos + 5, yMousePos - 5);
	}

	// used to reset the graph's position and scale
	private void resetGraph() {
		double minX = Integer.MAX_VALUE;
		double minY = Integer.MAX_VALUE;
		double maxX = Integer.MIN_VALUE;
		double maxY = Integer.MIN_VALUE;

		for (GraphDataset s : dataSets.values()) {
			for (Pair<Double, Double> p : s.points) {
				if (p.a > maxX)
					maxX = p.a;
				if (p.a < minX)
					minX = p.a;
				if (p.b > maxY)
					maxY = p.b;
				if (p.b < minY)
					minY = p.b;
			}
		}

		xMin = minX - 1;
		xDim = (maxX - minX) + 2;
		yMin = minY - 1;
		yDim = (maxY - minY) + 2;
	}

	// check to see if this
	@SuppressWarnings("unused")
	private boolean isOnscreen(double x, double y) {
		if (x < xMin || x > (xMin + xDim) || 
			y < yMin || y > (yMin + yDim))
			return false;
		else
			return true;
	}

	// converts a data coord to a graphics coord
	private int[] pointToScreen(double x, double y) {
		double xDisp = x - xMin;
		double yDisp = y - yMin;

		int xPos = (int) ((xDisp / xDim) * (this.getWidth() * xScl));
		int yPos = (int)(this.getHeight() * yScl) - (int)((yDisp / yDim) * (this.getHeight() * yScl));

		int[] ret = new int[2];
		ret[0] = xPos;
		ret[1] = yPos;
		return ret;
	}
	
	// converts a graphics coord to an (approximate) data coord
	private double[] screenToPoint(int x, int y) {
		double xPct = ((double)x)/((double)this.getWidth());
		double yPct = ((double)y)/((double)this.getHeight());
		
		double w = xDim * xScl;
		double h = yDim * yScl;
		
		double[] ret = new double[2];
		ret[0] = xMin + w*xPct + xScr;
		ret[1] = (yMin + yDim) - h*yPct - yScr;
		return ret;
	}

	// adds the given double point to the dataset with the given name
	public void addPoint(String setID, double x, double y) {
		if (dataSets.containsKey(setID)) {
			dataSets.get(setID).addPoint(x, y);
			
			if (x > (xMin + xDim) - 1)
				setXBounds(xMin, x + 1);
			else if (x < xMin + 1)
				setXBounds(x - 1, (xMin + xDim));
			
			if (y > (yMin + yDim) - 1)
				setYBounds(yMin, y + 1);
			else if (y < yMin + 1)
				setYBounds(y - 1, (yMin + yDim));
			
			repaint();
		} else {
			System.out.println("err: tried adding to a nonexistent GraphDataset");
		}
	}

	// adds a dataset
	public void addDataset(String setID, String name, Color col) {
		if (dataSets.containsKey(setID)) {
			System.out.println("err: tried adding a dataset that is already present");
		} else {
			dataSets.put(setID, new GraphDataset(name, col));
		}
	}

	// removes the dataset with the given name
	public void removeSet(String setID) {
		dataSets.remove(setID);
	}

	/*
	 * various methods to deal with window scaling and resizing
	 */
	public void setXBounds(double min, double max) {
		xMin = min;
		xDim = (max - min);
	}

	public void setYBounds(double min, double max) {
		yMin = min;
		yDim = (max - min);
	}
	
	public GraphDataset getDataset(String setID) {
		if (dataSets.containsKey(setID))
			return this.dataSets.get(setID);
		else
			return null;
	}
}