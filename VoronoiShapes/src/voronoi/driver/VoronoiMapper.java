package voronoi.driver;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JLabel;

import voronoi.graphics.GifSequenceWriter;
import voronoi.graphics.VRender;
import voronoi.map.Node;
import voronoi.map.PathFinder;

/**
 * VoronoiMapper - Creates a Voronoi map using the provided
 * shape files in the given directory. The map will be displayed
 * and then the edges on it can be used to perform a shortest path
 * traversal while avoiding objects.
 * 
 * @author Phillip Lopez - pgl5711@rit.edu
 *
 */
public class VoronoiMapper {
	
	/**
	 * The edge map containing the path that 
	 * can be traversed while avoiding objects
	 */
	public Map<String, Node> pathMap;
	
	/**
	 * The color map linking back to shape's area of influence
	 */
	public Map<Integer, Color> colorMap = new HashMap<>();	
	
	/**
	 * A* search implementation for the edges generated
	 */
	public PathFinder pathFinder = new PathFinder();
	
	/**
	 * The simple imaged used
	 */
	public SimpleImage image;
	
	/**
	 * Instead of showing the mobile robot moving
	 * Just show the trail of the path it takes
	 */
	public boolean showTrail = false;

	/**
	 * First node clicked in edge list
	 */
	public Node node1 = null;
	
	/**
	 * Second node clicked in edge list
	 */
	public Node node2 = null;
	
	/**
	 * Creates a Voronoi map of the given shapes
	 * @param directory
	 */
	public void createVoronoiMap(String directory) throws IOException{
		
		// Initialize a new path map
		pathMap = new HashMap<>();
		
		// random number generator for random colors
		Random random = new Random(10);
		
		// Load the input folder and files
		File folder = new File(directory);
		File[] files = folder.listFiles();
		File dimsFile = files[0];
		
		// Read in the image dimensions
		BufferedReader dimsReader = new BufferedReader(new FileReader(dimsFile));
		String[] dimsStr = dimsReader.readLine().split(",");
		int height = Integer.parseInt(dimsStr[0]);
		int width = Integer.parseInt(dimsStr[1]);
		dimsReader.close();
		
		// Initialize a blank image
		image = new SimpleImage(height, width, colorMap, this);
		List<List<Pixel>> shapes = new ArrayList<>();
		
		// Load in all of the shape data from the provided files
		for(int i = 1; i < files.length; i++){
			List<Pixel> shape = new ArrayList<>();
			BufferedReader reader = new BufferedReader(new FileReader(files[i]));
			String line;
			while((line = reader.readLine()) != null){
				String[] pixStr = line.split(",");
				int x = Integer.parseInt(pixStr[0]);
				int y = Integer.parseInt(pixStr[1]);
				int r = Integer.parseInt(pixStr[2]);
				int g = Integer.parseInt(pixStr[3]);
				int b = Integer.parseInt(pixStr[4]);
				Pixel p = image.getPixel(y-1, x-1);
				p.setVal(i);
				p.setShape(true);
				p.setRGB(r, g, b);
				shape.add(p);
			}
			shapes.add(shape);
			// Assign a random color to the shapes tile
			float rv = random.nextFloat();// / 2f + 0.5f;
			float gv = random.nextFloat();// / 2f + 0.5f;
			float bv = random.nextFloat();// / 2f + 0.5f;
			colorMap.put(i, new Color(rv, gv, bv));
			reader.close();
		}
		
		// Count the number of stages and pixels parsed
		int numStages = 0;
		Date tic = new Date();
		int numPix = 0;

		// Create a gif output stream to save an animation of the algorithm
		ImageOutputStream ios = ImageIO.createImageOutputStream(new File("out.gif"));
		GifSequenceWriter writer = new GifSequenceWriter(ios, BufferedImage.TYPE_INT_RGB, 25, true);
		
		// Run the actual flood fill Voronoi algorithm
		// Run until the shapes are fully flooded
		while(!shapesEmpty(shapes)){
			for(int i = 0; i < shapes.size(); i++){
				// Loop through every shape
				List<Pixel> shape = shapes.get(i);
				List<Pixel> newShape = new ArrayList<>();
				for(Pixel p : shape){
					// Go through each pixel belonging to the
					// shape and flood it to it's neighbors 
					numPix ++;
					newShape.addAll(p.grow());
				}
				// Update the new edge of the shape region
				shapes.set(i, newShape);
 			}
			// Write the current stage to the output gif
			writer.writeToSequence(image.toImage());
			numStages++;
		}
		writer.close();
		ios.close();
		
		// Print some run time statistics
		System.out.println("num pix: " + numPix);
		Date toc = new Date();
		System.out.println("It took " + numStages + " steps to create the diagram");
		System.out.println("It took " + (toc.getTime() - tic.getTime()) + "ms generate it");
		
		// Display a version of the Voronoi Image 
		// with interactive path exploration
		VRender render = new VRender(this);
		
		
	}
	
	/**
	 * Determines if the shapes list 
	 * has run out of Pixels to parse
	 * @param shapes
	 * @return
	 */
	public boolean shapesEmpty(List<List<Pixel>> shapes){
		for(List<Pixel> shape : shapes){
			if(shape.size() != 0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Handles a Pixel being clicked on the 
	 * current Voronoi diagram
	 * @param x
	 * @param y
	 * @param imgLabel
	 */
	public void handleClick(int x, int y, JLabel imgLabel){
		
		Node node = pathMap.get(x+","+y);
		if(node != null){
			// First edge clicked
			if(node1 == null){
				node1 = node;
				System.out.println("Adding edge: " + node1);
			}
			else{
				// Second edge clicked, compute path
				node2 = node;
				System.out.println("Adding edge: " + node2);
				String fileName = node1.getX() + "" + node1.getY() + "" + node2.getX() + "" + node2.getY();
				ImageOutputStream ios;
				GifSequenceWriter writer;
				try {
					// Setup the gif output stream
					ios = ImageIO.createImageOutputStream(new File(fileName + ".gif"));
					writer = new GifSequenceWriter(ios, BufferedImage.TYPE_INT_RGB, 0, true);
				
					// Follow the best path for the given nodes
					List<Node> path = followPath();
					Collections.reverse(path);
					BufferedImage curr = image.toImage();
					Node last = null;
					
					// These loops just create a red block on the image
					// for every single node on the shortest path
					for(Node step : path){
						if(!showTrail && last != null){
							// Reset the last position visited 
							// if we want to simulate a mobile bot
							for(int i = -6; i <= 6; i++){
								for(int j = -6; j <= 6; j++){
									int xl = last.getX() - i;
									int yl = last.getY() - j;
									if(!(xl < 0 || yl < 0 || xl >= image.getWidth() || yl >= image.getHeight())){
										curr.setRGB(xl, yl, image.getPixelRGB(xl, yl));
									}
								}
							}
						}
						// Create the next animation for the current node
						for(int i = -6; i <= 6; i++){
							for(int j = -6; j <= 6; j++){
								int xs = step.getX() - i;
								int ys = step.getY() - j;
								if(!(xs < 0 || ys < 0 || xs >= image.getWidth() || ys >= image.getHeight())){
									int color = Color.RED.getRGB();
									if(i == -6 || i == 6 || j == -6 || j == 6){
										color = Color.BLACK.getRGB();
									}
									if(i == 0 && j == 0){
										color = Color.GREEN.getRGB();
									}
									curr.setRGB(xs, ys, color);
								}
							}
						}
						// Write the image out to a gif
						writer.writeToSequence(curr);
						last = step;
					}
					System.out.println("Imaged saved to gif: " + fileName);
					writer.close();
					ios.close();
				}
				catch(IOException e){
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	/**
	 * Uses A* to find the best path between two nodes, if it exists
	 * @return
	 */
	public List<Node> followPath(){
		System.out.println("Following path: ");
		Node end = pathFinder.doAST(pathMap, node1.getKey(), node2.getKey());
		List<Node> path = pathFinder.doTrace(end, "Path");
		node1 = null;
		node2 = null;
		return path;
	}
}
