package voronoi.driver;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Driver {
	
	public static Map<Integer, Color> colorMap = new HashMap<>();
	
	public static void main(String args[]) throws IOException{
		
		Scanner scan = new Scanner(System.in);
		Random rand = new Random(1337);
		String dir = "C:\\Voronoi\\";
		File dirFile = new File(dir);
		File[] files = dirFile.listFiles();
		while(true){
			try{
				System.out.println("Enter the image dir name: ");
				String fldr = scan.nextLine();
				
				if("".equals(fldr)){
					fldr = files[rand.nextInt(files.length)].getName();
					System.out.println("Creating Voronoi for randon file: " + fldr);
				}
				
				if("QUIT".equals(fldr)){
					scan.close();
					return;
				}
				
				new VoronoiMapper().createVoronoiMap(dir + fldr);
			}
			catch(Exception e){
				System.out.println("Invalid directory: try again");
			}
		}

	}
	
}
