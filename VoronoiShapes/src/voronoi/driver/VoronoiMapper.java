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

public class VoronoiMapper {
	
	public Map<String, Node> pathMap;
	
	public Map<Integer, Color> colorMap = new HashMap<>();	
	public PathFinder pathFinder = new PathFinder();
	public SimpleImage image;
	
	public boolean showTrail = false;

	public Node node1 = null;
	public Node node2 = null;
	
	public void createVoronoiMap(String directory) throws IOException{
		
		pathMap = new HashMap<>();
				
		Random random = new Random(10);
		File folder = new File(directory);
		File[] files = folder.listFiles();
		
		File dimsFile = files[0];
		BufferedReader dimsReader = new BufferedReader(new FileReader(dimsFile));
		String[] dimsStr = dimsReader.readLine().split(",");
		int height = Integer.parseInt(dimsStr[0]);
		int width = Integer.parseInt(dimsStr[1]);
		dimsReader.close();
		
		image = new SimpleImage(height, width, colorMap, this);
		List<List<Pixel>> shapes = new ArrayList<>();
		
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
		
		int[] indexes = new int[shapes.size()];
		for(int i = 0; i < shapes.size(); i++){
			indexes[i] = i;
		}
			
		int numStages = 0;
		Date tic = new Date();
		int numPix = 0;

		ImageOutputStream ios = ImageIO.createImageOutputStream(new File("out.gif"));
		GifSequenceWriter writer = new GifSequenceWriter(ios, BufferedImage.TYPE_INT_RGB, 25, true);
		
		while(!shapesEmpty(shapes)){

			for(int i : indexes){
				List<Pixel> shape = shapes.get(i);
				List<Pixel> newShape = new ArrayList<>();
				for(Pixel p : shape){
					numPix ++;
					newShape.addAll(p.grow());
				}
				shapes.set(i, newShape);
 			}
			writer.writeToSequence(image.toImage());
			numStages++;
		}
		writer.close();
		ios.close();
		
		System.out.println("num pix: " + numPix);
		Date toc = new Date();
		System.out.println("It took " + numStages + " steps to create the diagram");
		System.out.println("It took " + (toc.getTime() - tic.getTime()) + "ms generate it");		
		VRender render = new VRender(this);
		
		
	}
	
	public boolean shapesEmpty(List<List<Pixel>> shapes){
		for(List<Pixel> shape : shapes){
			if(shape.size() != 0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Implementing Fisher–Yates shuffle
	 */
	public void shuffleArray(int[] ar){
		// If running on Java 6 or older, use `new Random()` on RHS here
	    Random rnd = new Random(1337);
	    for (int i = ar.length - 1; i > 0; i--){
	    	int index = rnd.nextInt(i + 1);
	    	// Simple swap
	    	int a = ar[index];
	    	ar[index] = ar[i];
	    	ar[i] = a;
	    }
	}
	
	
	public void handleClick(int x, int y, JLabel imgLabel){
		
		Node node = pathMap.get(x+","+y);
		if(node != null){
			if(node1 == null){
				node1 = node;
				System.out.println("Adding edge: " + node1);
			}
			else{
				node2 = node;
				System.out.println("Adding edge: " + node2);
				String fileName = node1.getX() + "" + node1.getY() + "" + node2.getX() + "" + node2.getY();
				ImageOutputStream ios;
				GifSequenceWriter writer;
				try {
					ios = ImageIO.createImageOutputStream(new File(fileName + ".gif"));
					writer = new GifSequenceWriter(ios, BufferedImage.TYPE_INT_RGB, 0, true);
				
					List<Node> path = followPath();
					Collections.reverse(path);
					BufferedImage curr = image.toImage();
					Node last = null;
					
					for(Node step : path){
						if(!showTrail && last != null){
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
	
	public List<Node> followPath(){
		System.out.println("Following path: ");
		Node end = pathFinder.doAST(pathMap, node1.getKey(), node2.getKey());
		List<Node> path = pathFinder.doTrace(end, "Path");
		node1 = null;
		node2 = null;
		return path;
	}
}
