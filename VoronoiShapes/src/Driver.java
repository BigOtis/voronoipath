import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Driver {
	
	public static void main(String args[]) throws IOException{
		
		File folder = new File(args[0]);
		File[] files = folder.listFiles();
		
		File dimsFile = files[0];
		BufferedReader dimsReader = new BufferedReader(new FileReader(dimsFile));
		String[] dimsStr = dimsReader.readLine().split(",");
		int height = Integer.parseInt(dimsStr[0]);
		int width = Integer.parseInt(dimsStr[1]);
		dimsReader.close();
		
		SimpleImage image = new SimpleImage(height, width);
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
			reader.close();
		}
		
		while(!shapesEmpty(shapes)){
			for(int i = 0; i < shapes.size(); i++){
				List<Pixel> shape = shapes.get(i);
				List<Pixel> newShape = new ArrayList<>();
				for(Pixel p : shape){
					newShape.addAll(p.grow());
				}
				shapes.set(i, newShape);
 			}
		}
		
		image.showImg();
	}
	
	public static boolean shapesEmpty(List<List<Pixel>> shapes){
		for(List<Pixel> shape : shapes){
			if(shape.size() != 0){
				return false;
			}
		}
		return true;
	}

}
