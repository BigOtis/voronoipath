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
		for(int i = -1; i <= 1; i++){
			for(int j = -1; j <= 1; j++){
				if(i != 0 && j != 0){
					Node result = pathMap.get(i+","+j);
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
