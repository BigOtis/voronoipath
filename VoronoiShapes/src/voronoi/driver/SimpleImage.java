package voronoi.driver;
import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SimpleImage {

	private int height, width;

	private Pixel[][] pixels;
	private Color[] colors = new Color[]
			{Color.BLUE, Color.RED, new Color(255, 204, 204),
			 Color.GREEN, Color.YELLOW, Color.CYAN, 
			 Color.MAGENTA, Color.ORANGE, Color.PINK, 
			 Color.LIGHT_GRAY, Color.DARK_GRAY, Color.GRAY,
			 new Color(255, 102, 102), new Color(153, 255, 255),
			 new Color(255, 102, 255), new Color(51, 255, 51),
			 new Color(102, 204, 0), new Color(0, 204, 204),
			 new Color(0, 255, 255), new Color(255, 153, 153),
			 new Color(76, 153, 0), new Color(153, 76, 0),
			 new Color(0, 204, 0), new Color(0, 100, 0),
			 new Color(51, 51, 255), new Color(255, 255, 51),};
	
	public SimpleImage(int height, int width){
		
		this.setHeight(height);
		this.setWidth(width);
		pixels = new Pixel[height][width];
		
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				pixels[i][j] = new Pixel(i,j, this);
			}
		}
	}
		
	public BufferedImage toImage(){
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				img.setRGB(i, j, getColorRGB(getPixel(i, j)));
			}
		}
		return img;
	}
	
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
		
		return Driver.colorMap.get(val).getRGB();
//		if(val < colors.length){
//			return colors[val].getRGB();
//		}
//		
//		return colors[val % colors.length].getRGB();
	}
	
	public static JLabel label;
	
	public void showImg(){
		JFrame frame = new JFrame("Voronoi Image");
		frame.setSize(width+10, height+10);
		label = new JLabel(new ImageIcon(toImage()));
		frame.add(label);
		frame.setVisible(true);
	}
	
	public void updateImg(){
		label.setIcon(new ImageIcon(toImage()));
	}
	
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
