package edu.nyu.cs.recursion_exercise;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Represents an instance of the Mandlebrot set, a fractal invented by Benoit B. Mandlebrot.
 * @author Foo Barstein, with comments by Ambrose Lo (hl4777).
 */
public final class Mandelbrot extends PApplet {
	/**
	 * Maximum value of
	 */
	private int max = 64;

	/** 
	 * An array of float arrays that stores the various shades of gray used in the program.
	 */
	private float[][] colors = new float[48][3];

	/**
	 * Stores the X and Y values of 
	 */
	private double viewX = 0.0;
	private double viewY = 0.0;


	/**
	 * Stores the current zoom scale.
	 */
	private double zoom = 1.0;

	/**
	 * Stores the X and Y values of where the mouse was last clicked.
	 */
	private int mousePressedX;
	private int mousePressedY;

	/**
	 * Flag that tells the draw() method to redraw the window display.
	 * Set true by mouseReleased() when the box is drawn.
	 * Set false at the end of every call of draw().
	 */
	private boolean renderNew = true;

	/**
	 * Flag that tells the program that a box is being drawn for zooming in.
	 * Set true by mousePressed() when the left mouse button is pressed.
	 * Set false by mouseReleased() when the right mouse button is pressed.
	 */
	private boolean drawBox = false;

	/**
	 * Initialises the size of the Processing applet window.
	 * @Override settings() method from PApplet.
	 */
	public void settings() {
		this.size(600,400);
	}

	/**
	 * Initialises the colour array.
	 * @Override setup() method from PApplet.
	 */
	public void setup() {
		for (int i = 0; i < colors.length; i++) {
			int c = 2 * i * 256 / colors.length;
			if (c > 255)
				c = 511 - c;
			float[] color = {c, c, c};
			this.colors[i] = color;
		}
	}

	/**
	 * Draws the window whenever it needs to be updated.
	 * @Override draw() method from PApplet.
	 */
	public void draw() {

		if (!renderNew && !this.drawBox) return;
		this.background(0, 0, 0);
		if (this.drawBox) {
			this.noFill();
			this.stroke(255, 0, 0);
			rect(this.mousePressedX, this.mousePressedY, this.mouseX - this.mousePressedX, this.mouseY - this.mousePressedY);
		}
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				double r = zoom / Math.min(this.width, this.height);
				double dx = 2.5 * (x * r + this.viewX) - 2.0;
				double dy = 1.25 - 2.5 * (y * r + this.viewY);
				int value = this.mandel(dx, dy);
				float[] color = this.colors[value % this.colors.length];
				this.stroke(color[0], color[1], color[2]);
				this.line(x, y, x, y);
			}
		}
		
		this.textAlign(PConstants.CENTER);
		this.text("Click and drag to draw an area to zoom into.", this.width / 2, this.height-20);

		this.renderNew = false;
	}

	private int mandel(double px, double py) {
		double zx = 0.0, zy = 0.0;
		double zx2 = 0.0, zy2 = 0.0;
		int value = 0;
		while (value < this.max && zx2 + zy2 < 4.0) {
			zy = 2.0 * zx * zy + py;
			zx = zx2 - zy2 + px;
			zx2 = zx * zx;
			zy2 = zy * zy;
			value++;
		}
		return value == this.max ? 0 : value;
	}

	/**
	 * Stores the location of the first mouse-press.
	 * Initialises the drawBox flag for zooming in.
	 * @Override mousePressed() method from PApplet.
	 */
	public void mousePressed() {
		this.mousePressedX = this.mouseX;
		this.mousePressedY = this.mouseY;
		this.drawBox = true;
	}

	/**
	 * 
	 * @Override mouseReleased() method from PApplet.
	 */
	public void mouseReleased() {
		int mouseReleasedX = this.mouseX;
		int mouseReleasedY = this.mouseY;
		if (this.mouseButton == PConstants.LEFT) {
			if (mouseReleasedX != mousePressedX && mouseReleasedY != mousePressedY) {
				int w = this.width;
				int h = this.height;
				this.viewX += this.zoom * Math.min(mouseReleasedX, mousePressedX) / Math.min(w, h);
				this.viewY += this.zoom * Math.min(mouseReleasedY, mousePressedY) / Math.min(w, h);
				this.zoom *= Math.max((double)Math.abs(mouseReleasedX - mousePressedX) / w, (double)Math.abs(mouseReleasedY - mousePressedY) / h);
			}
		}
		else if (this.mouseButton == PConstants.RIGHT) {
			this.max += max / 4;
		}
		else {
			this.max = 64;
			this.viewX = this.viewY = 0.0;
			this.zoom = 1.0;
		}

		this.drawBox = false;
		this.renderNew = true;
	}

	/**
	 * Runs the program drawing a Mandelbrot set using the Processing library.
	 * @param args 
	 */
	public static void main(String[] args) {
		PApplet.main("edu.nyu.cs.recursion_exercise.Mandelbrot");
	}


}