package voronoi.map;

import java.util.List;


public abstract class Node {

	public Node parent = null;
	public int distance;
	
	public abstract int getX();
	public abstract int getY();
	
	public abstract List<Node> getNeighbors();
	
	/**
	 * Gets the distance to goal estimate
	 * 	Useful for the A* algorithm
	 * @return int
	 */
	public abstract int getDistanceToGoalEstimate();
	
	/**
	 * Returns the Manhattan distance between two Nodes
	 */
	public int manhattanDistTo(Node that){
		return Math.abs(this.getX() - that.getX()) 
			 + Math.abs(this.getY() - that.getY());
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Node){
			Node that = (Node) obj;
			return this.getX() == that.getX() && this.getY() == that.getY();
		}
		return false;
	}
	
	public String getKey(){
		return getX() + "," + getY();
	}
	
	@Override
	public String toString(){
		return getKey();
	}
	
}

