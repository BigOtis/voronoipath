package voronoi.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PixelNode extends Node {
	
	private int x;
	private int y;
	private Map<String, Node> pathMap;
	
	public PixelNode(int x, int y, Map<String, Node> pathMap){
		this.x = x;
		this.y = y;
		this.pathMap = pathMap;
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
		for(int i = -2; i <= 2; i++){
			for(int j = -2; j <= 2; j++){
				if(i != 0 && j != 0){
					Node result = pathMap.get((x + i) + "," + (y + j));
					if(result != null){
						neighbors.add(result);
					}
				}
			}
		}
		
		if(neighbors.size() < 2){
			System.out.println("less than 2");
		}
	
		return neighbors;
	}

	@Override
	public int getDistanceToGoalEstimate() {
		// TODO Auto-generated method stub
		return 0;
	}

}
