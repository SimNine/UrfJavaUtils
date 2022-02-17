package urf.grapher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JPanel;

import urf.Pair;

@SuppressWarnings("serial")
public class GraphPanel extends JPanel implements MouseMotionListener, KeyListener {
	HashMap<String, GraphDataset> dataSets = new HashMap<String, GraphDataset>();
	HashSet<Integer> keys = new HashSet<Integer>();

	double xMin = -1;
	double xMax = 5;
	double yMin = -1;
	double yMax = 5;

	double xStep = 1.0;
	double yStep = 1.0;
	
	int xMousePos = 0;
	int yMousePos = 0;

	// modified default constructor
	public GraphPanel() {
		this.setFocusable(true);
		this.requestFocusInWindow();
	}
	
	/*
	 * Listeners
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		keys.add(e.getKeyCode());

		double xDist = Math.abs(xMax - xMin)*0.05 + 1;
		double yDist = Math.abs(yMax - yMin)*0.05 + 1;
		
		switch (e.getKeyCode()) {
		case KeyEvent.VK_R:
			resetGraph();
			break;
		case KeyEvent.VK_RIGHT:
			xMin += xDist;
			xMax += xDist;
			break;
		case KeyEvent.VK_LEFT:
			xMin -= xDist;
			xMax -= xDist;
			break;
		case KeyEvent.VK_DOWN:
			yMin -= yDist;
			yMax -= yDist;
			break;
		case KeyEvent.VK_UP:
			yMin += yDist;
			yMax += yDist;
			break;
		case KeyEvent.VK_W: // increase y-zoom
			if (yDist*20 > 5) {
				yMin += yDist;
				yMax -= yDist;
			}
			break;
		case KeyEvent.VK_S: // decrease y-zoom
			yMin -= yDist;
			yMax += yDist;
			break;
		case KeyEvent.VK_D: // increase x-zoom
			if (xDist*20 > 5) {
				xMin += xDist;
				xMax -= xDist;
			}
			break;
		case KeyEvent.VK_A: // decrease x-zoom
			xMin -= xDist;
			xMax += xDist;
			break;
		}
		
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
		g.drawLine(0, axisPos[1], getWidth(), axisPos[1]); // x-axis
		g.drawLine(axisPos[0], 0, axisPos[0], getHeight()); // y-axis
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

				g.drawRect(currPos[0] - 1, currPos[1] - 1, 3, 3);
				g.drawLine(currPos[0], currPos[1], nextPos[0], nextPos[1]);
			}
		}
	}
	
	private void drawScale(Graphics g) {
		// draw vertical scale
		double yRange = (int)(yMax - yMin);
		int yIntervalOrderOfMagnitude = (int)Math.log10(yRange) - 1;
		if (yIntervalOrderOfMagnitude < 0)
			yIntervalOrderOfMagnitude = 0;
		int yIntervalSize = (int)Math.pow(10, yIntervalOrderOfMagnitude);
		int yLowestScaleMarker = (int) (yMin - (yMin % yIntervalSize));
		int yNumMarkers = (int)(yRange / yIntervalSize) + 1;

		g.setColor(Color.BLACK);
		for (int i = 0; i < yNumMarkers; i++) {
			int yTickCoord = yLowestScaleMarker + i*yIntervalSize;
			int[] tickpos = pointToScreen(0, yTickCoord);
			g.drawLine(tickpos[0] - 5, tickpos[1], tickpos[0] + 5, tickpos[1]);
			g.drawString("" + yTickCoord, tickpos[0] - 20, tickpos[1] + 8);
		}
		
		// draw horizontal scale
		double xRange = (int)(xMax - xMin);
		int xIntervalOrderOfMagnitude = (int)Math.log10(xRange) - 1;
		if (xIntervalOrderOfMagnitude < 0)
			xIntervalOrderOfMagnitude = 0;
		int xIntervalSize = (int)Math.pow(10, xIntervalOrderOfMagnitude);
		int xLowestScaleMarker = (int) (xMin - (xMin % xIntervalSize));
		int xNumMarkers = (int)(xRange / xIntervalSize) + 1;

		g.setColor(Color.BLACK);
		for (int i = 0; i < xNumMarkers; i++) {
			int xTickCoord = xLowestScaleMarker + i*xIntervalSize;
			int[] tickpos = pointToScreen(xTickCoord, 0);
			g.drawLine(tickpos[0], tickpos[1] - 5, tickpos[0], tickpos[1] + 5);
			g.drawString("" + xTickCoord, tickpos[0] - 20, tickpos[1] + 8);
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
		xMax = maxX + 1;
		yMin = minY - 1;
		yMax = maxY + 1;
	}

	// check to see if this
	@SuppressWarnings("unused")
	private boolean isOnscreen(double x, double y) {
		if (x < xMin || x > xMax || y < yMin || y > yMax)
			return false;
		else
			return true;
	}

	// converts a data coord to a graphics coord
	private int[] pointToScreen(double x, double y) {
		double w = xMax - xMin;
		double h = yMax - yMin;

		double xDisp = x - xMin;
		double yDisp = y - yMin;

		int xPos = (int) ((xDisp / w) * (this.getWidth()));
		int yPos = this.getHeight() - (int) ((yDisp / h) * (this.getHeight()));

		int[] ret = new int[2];
		ret[0] = xPos;
		ret[1] = yPos;
		return ret;
	}
	
	// converts a graphics coord to an (approximate) data coord
	private double[] screenToPoint(int x, int y) {
		double xPct = ((double)x)/((double)this.getWidth());
		double yPct = ((double)y)/((double)this.getHeight());
		
		double w = xMax - xMin;
		double h = yMax - yMin;
		
		double[] ret = new double[2];
		ret[0] = xMin + w*xPct;
		ret[1] = yMax - h*yPct;
		return ret;
	}

	// adds the given double point to the dataset with the given name
	public void addPoint(String setID, double x, double y) {
		if (dataSets.containsKey(setID)) {
			dataSets.get(setID).addPoint(x, y);
			
			if (x > xMax - 1)
				xMax = x + 1;
			else if (x < xMin + 1)
				xMin = x - 1;
			
			if (y > yMax - 1)
				yMax = y + 1;
			else if (y < yMin + 1)
				yMin = y - 1;
			
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
		xMax = max;
	}

	public void setXStep(double step) {
		xStep = step;
	}

	public void setYBounds(double min, double max) {
		yMin = min;
		yMax = max;
	}

	public void setYStep(double step) {
		yStep = step;
	}
	
	public GraphDataset getDataset(String setID) {
		if (dataSets.containsKey(setID))
			return this.dataSets.get(setID);
		else
			return null;
	}
}