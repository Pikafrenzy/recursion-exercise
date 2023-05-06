package edu.nyu.cs.recursion_exercise;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Represents an instance of the Mandlebrot set, a fractal invented by Benoit B. Mandlebrot.
 * @author Foo Barstein, with comments by Ambrose Lo (hl4777).
 */
public final class Mandelbrot extends PApplet {
	/**
	 * Maximum detail level
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
		/*
		 * Sets up the window size
		 * Parameters: int Width, int Height
		 * Inherited from PApplet
		 */
		this.size(600,400);
	}

	/**
	 * Initialises the colour array.
	 * @Override setup() method from PApplet.
	 */
	public void setup() {
		//runs repeatedly to fill the array
		for (int i = 0; i < colors.length; i++) {
			//evenly distributes the shades of grey
			int c = 2 * i * 256 / colors.length;
			//Reverses the direction of colour gradient if c is greater than 255
			if (c > 255)
				c = 511 - c;
			//stores an array of floats which represents a certain shade of grey
			float[] color = {c, c, c};

			//stores said array in colors
			this.colors[i] = color;
		}
	}

	/**
	 * Draws the window whenever it needs to be updated.
	 * @Override draw() method from PApplet.
	 */
	public void draw() {

		//escapes this call of the draw() method if the flags renderNew and drawBox are both false, i.e. if there is nothing new to draw
		if (!renderNew && !this.drawBox) return;

		/*
		 * resets the background into a solid colour
		 * Parameters: float Red, float Green, float Blue
		 * Inherited from PApplet
		 */
		this.background(0, 0, 0);

		//checks whether a box is being drawn
		if (this.drawBox) {
			/* 
			* Sets the rectangle to be drawn to not be filled
			* Inherited from PApplet
			*/ 
			this.noFill();
			/* 
			* Sets the colour of the borders of the rectangle
			* Parameters: float Red, float Green, float Blue
			* Inherited from PApplet
			*/
			this.stroke(255, 0, 0);
			/*
			 * Draws a rectangle with the top left corner from where the mouse was first pressed and the bottom right corner where the mouse currently is
			 * Parameters: int firstCornerX, int firstCornerY, int width, int height
			 * Inherited from PApplet
			 * Variables mouseX and mouseY inherited from PApplet
			 */
			rect(this.mousePressedX, this.mousePressedY, this.mouseX - this.mousePressedX, this.mouseY - this.mousePressedY);
		}
		/*
		 * Runs through every height in the window
		 */
		for (int y = 0; y < this.height; y++) {
			/*
			 * Runs through every width per height
			 * Together, these two loops run through every point in the window
			 */
			for (int x = 0; x < this.width; x++) {
				//Calculates the rate of change per pixel
				double r = zoom / Math.min(this.width, this.height);
				//Calculates the x-coordinate location relative to (0,0) on the graph
				double dx = 2.5 * (x * r + this.viewX) - 2.0;
				//Calculates the y-coordinate location relative to (0,0) on the graph
				double dy = 1.25 - 2.5 * (y * r + this.viewY);
				//Saves the result of the mandel() method into value.
				int value = this.mandel(dx, dy);
				/*
				 * Modulus operator called to choose the grey used for this point from the colors float array declared in setup()
				 * Stores it in a new array color to be called in the next method.
				 */
				float[] color = this.colors[value % this.colors.length];
				/* Sets the colour of the point to the colour decided above
				 * Parameters: int Red, int Green, int Blue
				 * Inherited from PApplet
				 */
				this.stroke(color[0], color[1], color[2]);
				/*
				 * Draws a dot using the line() method by starting and ending the line at the same point
				 * Parameters: int firstPointX, int firstPointY, int secondPointX, int secondPointY
				 * Inherited from PApplet
				 */
				this.line(x, y, x, y);
			}
		}
		/*
		 * Sets text to align the centre to the XY coordinates given in future methods
		 * Parameters: int alignment
		 * Inherited from PApplet
		 */
		this.textAlign(PConstants.CENTER);

		/*
		 * Creates text at the specified location to instruct the app user
		 * Parameters: String text, int x, int y
		 * Inherited from PApplet
		 */
		this.text("Click and drag to draw an area to zoom into.", this.width / 2, this.height-20);

		//Resets flag, telling the program there is nothing new to draw.
		this.renderNew = false;
	}

	/** Checks how many iterations this point undergoes
	 * @param px The X coordinate of the current point
	 * @param py The Y coordinate of the current point
	 * @return An integer representing the number of times it took to 
	 */
	private int mandel(double px, double py) {
		//Stores the current working point
		double zx = 0.0, zy = 0.0;
		//Stores zx squared and zy squared respectively
		double zx2 = 0.0, zy2 = 0.0;
		//Stores the number of iterations
		int value = 0;
		/*
		 * Checks two conditions and runs math:
		 * First condition value < this.max ensures that the code runs in a timely manner by setting a limit on how many times the while loop can run per point
		 * Second condition zx2 + zy2 < 4.0 checks whether the sum of their squares exceeds 4.0, meaning that it will diverge infinitely.
		 * If these conditions are met, recursively runs this code until one is no longer met
		 */
		while (value < this.max && zx2 + zy2 < 4.0) {
			// Calculates the imaginary part of the iterated function
			zy = 2.0 * zx * zy + py;
			// Calculates the real part of the iterated function
			zx = zx2 - zy2 + px;
			//Squares zx and stores it in zx2
			zx2 = zx * zx;
			//Squares zy and stores it in zy2
			zy2 = zy * zy;
			//Increments the number of iterations
			value++;
		}
		/*
		 * Uses a ternary operator to return a value based on whether the number is too large or not
		 * If the value is equal to the maximum detail level, returns 0 to indicate that this pixel should be coloured black
		 * If not, returns the value calculated (number of times the while loop ran)
		 */
		return value == this.max ? 0 : value;
	}

	/**
	 * Stores the location where the mouse is pressed.
	 * Sets the drawBox flag for zooming in to true.
	 * @Override mousePressed() method from PApplet.
	 */
	public void mousePressed() {
		//Stores the X-coordinate of the mouse when it is pressed.
		this.mousePressedX = this.mouseX;
		//Stores the Y-coordinate of the mouse when it is pressed.
		this.mousePressedY = this.mouseY;
		//Sets a flag indicating the box is being drawn.
		this.drawBox = true;
	}

	/**
	 * 
	 * @Override mouseReleased() method from PApplet.
	 */
	public void mouseReleased() {
		//Initialises and stores the X-coordinate of where the mouse is when it is released
		int mouseReleasedX = this.mouseX;
		//Initialises and stores the Y-coordinate of where the mouse is when it is released
		int mouseReleasedY = this.mouseY;
		//checks if this was a left click
		if (this.mouseButton == PConstants.LEFT) {
			//checks whether the release location is the same as the press location
			if (mouseReleasedX != mousePressedX && mouseReleasedY != mousePressedY) {
				//Initialises and stores the width of the window
				int w = this.width;
				//Initialises and stores the height of the window
				int h = this.height;
				//Stores the x-coordinate of the zoom window relative to the initial window
				this.viewX += this.zoom * Math.min(mouseReleasedX, mousePressedX) / Math.min(w, h);
				//Stores the y-coordinate of the zoom window relative to the initial window
				this.viewY += this.zoom * Math.min(mouseReleasedY, mousePressedY) / Math.min(w, h);
				//Stores the increased zoom scale
				this.zoom *= Math.max((double)Math.abs(mouseReleasedX - mousePressedX) / w, (double)Math.abs(mouseReleasedY - mousePressedY) / h);
			}
		}
		//checks if this was a right click
		else if (this.mouseButton == PConstants.RIGHT) {
			//increases the detail level by 25%
			this.max += max / 4;
		}
		//resets all variables if the mouse button clicked is neither left or right
		else {
			this.max = 64;
			this.viewX = this.viewY = 0.0;
			this.zoom = 1.0;
		}
		//sets flag indicating that the box is no longer being drawn
		this.drawBox = false;
		//sets flag to re-render the window
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