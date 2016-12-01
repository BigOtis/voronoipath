package voronoi.driver;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import voronoi.map.PixelNode;

/**
 * SimpleImage for Voronoi Images
 * 
 * Contains a matrix of Pixel's and methods for
 * manipulating them in regards to the Voronoi simulation
 * 
 * @author Phillip Lopez - pgl5711@rit.edu
 *
 */
public class SimpleImage {

	/**
	 * Size of this image
	 */
	private int height, width;

	/**
	 * The Pixel Matrix
	 */
	private Pixel[][] pixels;
	
	/**
	 * A map of colors to be used when
	 * assigning Voronoi regions colors
	 */
	private Map<Integer, Color> colorMap;
	
	/**
	 * The mapper class using this image
	 */
	private VoronoiMapper mapper;
	
	/**
	 * Used to display this image
	 */
	public static JLabel label;

	
	/**
	 * Creates a default simple image with the given parameters
	 * All non-shape pixels are initialized to white
	 * @param height
	 * @param width
	 * @param colorMap
	 * @param mapper
	 */
	public SimpleImage(int height, int width, Map<Integer, Color> colorMap, VoronoiMapper mapper){
		
		this.setHeight(height);
		this.setWidth(width);
		this.colorMap = colorMap;
		this.mapper = mapper;
		
		pixels = new Pixel[height][width];
		
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				pixels[i][j] = new Pixel(i,j, this);
			}
		}
	}
		
	/**
	 * Converts this SimpleImage into a BufferedImage
	 * @return
	 */
	public BufferedImage toImage(){
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				img.setRGB(i, j, getColorRGB(getPixel(i, j)));
			}
		}
		return img;
	}
	
	/**
	 * Gets the RGB value mapped to the given Pixel
	 * @param p
	 * @return int RGB
	 */
	public int getColorRGB(Pixel p){
		int val = p.getVal();
		
		if(val == 0){
			return Color.WHITE.getRGB();
		}
		
		if(p.isEdge()){
			return Color.BLACK.getRGB();
		}
		
		if(p.isShape()){
			return p.getColor().getRGB();
		}
		
		return colorMap.get(val).getRGB();
	}
	
	/**
	 * Gets the RBG value of the Pixel at the given coordinates
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPixelRGB(int x, int y){
		return getColorRGB(getPixel(x, y));
	}
		

	
	/**
	 * Adds the Pixel at the given x,y coordinates to the VoronoiMapper's edgemap
	 * @param x
	 * @param y
	 */
	public void addToEdgeMap(int x, int y){
		PixelNode edge = new PixelNode(x, y, mapper.pathMap);
		mapper.pathMap.put(edge.getKey(), edge);
	}
	
	/**
	 * Shows the current image
	 */
	public void showImg(){
		JFrame frame = new JFrame("Voronoi Image");
		frame.setSize(width+10, height+10);
		label = new JLabel(new ImageIcon(toImage()));	
		frame.add(label);
		frame.setVisible(true);
	}
	
	/**
	 * Updates the displayed image with the current state
	 */
	public void updateImg(){
		label.setIcon(new ImageIcon(toImage()));
	}
	
	/**
	 * Gets the Pixel at the given coordinates
	 * @param x
	 * @param y
	 * @return
	 */
	public Pixel getPixel(int x, int y){
		return pixels[x][y];
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
