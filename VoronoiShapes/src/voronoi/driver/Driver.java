package voronoi.driver;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * The main driver program for the Voronoi Simulator
 * 
 * @author Phillip Lopez - pgl5711@rit.edu
 *
 */
public class Driver {
	
	/**
	 * The folder containing shapes to be used in the Voronoi map
	 */
	public static String dir = "C:\\Voronoi\\";
		
	/**
	 * Main method for the Voronoi Shape Simulation.
	 * 
	 * Runs continiously, allowing the user to specify a folder
	 * with shape data. Runs the VoronoiMapper to create
	 * a Voronoi map of the given shape files.
	 * 
	 */
	public static void main(String args[]) throws IOException{
		
		Scanner scan = new Scanner(System.in);
		Random rand = new Random(1337);
		File dirFile = new File(dir);
		File[] files = dirFile.listFiles();
		while(true){
			try{
				System.out.println("Enter the image dir name: ");
				String fldr = scan.nextLine();
				
				// User didn't specify a file, pick a random one
				if("".equals(fldr)){
					fldr = files[rand.nextInt(files.length)].getName();
					System.out.println("Creating Voronoi for randon file: " + fldr);
				}
				
				if("QUIT".equals(fldr)){
					scan.close();
					return;
				}
				
				// Create a Voronoi map for the given shapes
				new VoronoiMapper().createVoronoiMap(dir + fldr);
			}
			catch(Exception e){
				System.out.println("Invalid directory: try again");
				e.printStackTrace();
			}
		}

	}
	
}
