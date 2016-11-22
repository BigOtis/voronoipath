package voronoi.map;

import java.util.ArrayList;
import java.util.List;

import voronoi.driver.VoronoiMapper;

public class PixelNode extends Node {
	
	private int x;
	private int y;
	
	public PixelNode(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public List<Node> getNeighbors() {
	
		ArrayList<Node> neighbors = new ArrayList<>();
		for(int i = -1; i <= 1; i++){
			for(int j = -1; j <= 1; j++){
				if(i != 0 && j != 0){
					Node result = VoronoiMapper.pathMap.get(i+","+j);
					if(result != null){
						neighbors.add(result);
					}
				}
			}
		}
	
		return neighbors;
	}

	@Override
	public int getDistanceToGoalEstimate() {
		// TODO Auto-generated method stub
		return 0;
	}

}
