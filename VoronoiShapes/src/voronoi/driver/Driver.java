package voronoi.driver;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Driver {
		
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
