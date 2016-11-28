package voronoi.driver;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import voronoi.map.PixelNode;

public class Pixel {

	private int x, y;
	private int r,g,b; // shapes only
	private int val = 0;
	private boolean isShape = false;
	private boolean isEdge = false;
	private SimpleImage image;
	
	
	public Pixel(int x, int y, SimpleImage image){
		this.x = x; this.y = y;
		this.image = image;
	}
	
	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}

	public boolean isShape() {
		return isShape;
	}

	public void setShape(boolean isShape) {
		this.isShape = isShape;
	}

	public boolean isEdge() {
		return isEdge;
	}

	public void setEdge(boolean isEdge) {
		this.isEdge = isEdge;
	}
	
	public List<Pixel> grow(){
		
		List<Pixel> results = new ArrayList<>();
		
		// Examine neighbors of this pixel		
		Pixel result = expandTo(x + 1, y);
		if(result != null){
			results.add(result);
		}
		
		result = expandTo(x - 1, y);
		if(result != null){
			results.add(result);
		}
		
		result = expandTo(x, y + 1);
		if(result != null){
			results.add(result);
		}
		
		result = expandTo(x, y - 1);
		if(result != null){
			results.add(result);
		}
		
		// Edge of shape, set to black
		if(isShape && results.size() > 0){
			this.setRGB(0, 0, 0);
		}
		
		return results;
	}
	
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
	
	public Color getColor(){
		return new Color(r,g,b);
	}
	
	public void setRGB(int r, int g, int b){
		this.r = r;
		this.g = g;
		this.b = b;
	}
}
