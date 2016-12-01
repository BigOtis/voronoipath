package voronoi.driver;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple class to represent a single pixel 
 * in our Voronoi Map
 * @author Phillip Lopez - pgl5711
 */
public class Pixel {

	private int x, y;
	private int r,g,b; // shapes only
	private int val = 0;
	private boolean isShape = false;
	private boolean isEdge = false;
	private SimpleImage image;
	
	/**
	 * Create a Pixel associated with the given image
	 * at the given x,y coordinates
	 * @param x
	 * @param y
	 * @param image
	 */
	public Pixel(int x, int y, SimpleImage image){
		this.x = x; this.y = y;
		this.image = image;
	}
	
	/**
	 * Return the shape # value assigned to this pixel
	 * @return
	 */
	public int getVal() {
		return val;
	}

	/**
	 * Set the shape # value assigned to this pixel
	 * @param val
	 */
	public void setVal(int val) {
		this.val = val;
	}

	/**
	 * Is this pixel part of a shape?
	 * @return
	 */
	public boolean isShape() {
		return isShape;
	}

	/**
	 * Set this pixel to be part of a shape or not
	 * @param isShape
	 */
	public void setShape(boolean isShape) {
		this.isShape = isShape;
	}

	public boolean isEdge() {
		return isEdge;
	}

	public void setEdge(boolean isEdge) {
		this.isEdge = isEdge;
	}
	
	/**
	 * Expands the current pixel if possible. 
	 * Observers the left, right, top and bottom neighbors
	 * 	- If they are blank and not part of a shape, their value
	 * 	  is set to the same value as this pixel.
	 *  - If the pixel already has a value, it is changed into an edge
	 * @return List of pixels that were expanded to
	 */
	public List<Pixel> grow(){
		
		List<Pixel> results = new ArrayList<>();
		
		// Examine neighbors of this pixel	
		Pixel result = expandTo(x + 1, y); // Right
		if(result != null){
			results.add(result);
		}
		
		result = expandTo(x - 1, y); // Left
		if(result != null){
			results.add(result);
		}
		
		result = expandTo(x, y + 1); // Top
		if(result != null){
			results.add(result);
		}
		
		result = expandTo(x, y - 1); // Bot
		if(result != null){
			results.add(result);
		}
		
		// Edge of shape, set to black
		if(isShape && results.size() > 0){
			this.setRGB(0, 0, 0);
		}
		
		return results;
	}
	
	/**
	 * Simulates this pixel flooding to the given pixel neighbor
	 * 
	 * @param x
	 * @param y
	 * @return Neighbor Pixel or null if no flooding took place
	 */
	public Pixel expandTo(int x, int y){
		
		// Neighbor out of bounds
		if(x >= image.getWidth() || x < 0 || y >= image.getHeight() || y < 0){
			return null;
		}
		
		// Check if neighbor hasn't been visited
		Pixel neighbor = image.getPixel(x, y);
		if(neighbor.val == 0 && !neighbor.isEdge){
			neighbor.val = this.val;
			return neighbor;
		}
		
		// Neighbor visited by another shape already: Make it an edge
		if(!neighbor.isShape && neighbor.val != this.val){
			neighbor.setEdge(true);
			image.addToEdgeMap(x, y);
		}
		
		// Neighbor is either edge point or shape
		// Do nothing
		return null;
	}
	
	/**
	 * Returns the color assigned to this Pixel
	 * - Used for shapes only
	 * @return
	 */
	public Color getColor(){
		return new Color(r,g,b);
	}
	
	/**
	 * Assigns a color for this pixel
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setRGB(int r, int g, int b){
		this.r = r;
		this.g = g;
		this.b = b;
	}
}
