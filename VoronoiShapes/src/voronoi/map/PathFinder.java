package voronoi.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class PathFinder {
	
	/**
	 * Performs A* search on a graph from start to end
	 * 
	 * @param graph
	 * @param start
	 * @param end
	 * @return
	 */
	public Node doAST(Map<String, Node> graph, String start, String end){
		
		// Initial setup
		HashSet<String> visited = new HashSet<String>();
		PriorityQueue<Node> active = new PriorityQueue<Node>(new AStarComparator(graph.get(end)));
		graph.get(start).parent = null;
		visited.add(start);
		active.add(graph.get(start));
		
		// A* iteration
		Node curr;
		while(!active.isEmpty()){
			curr = active.poll();
			
			if(curr.getKey().equals(end)){
				// Set the starting nodes parent to null
				// This assumes we have no negative loops 
				// in the data
				graph.get(start).parent = null;
				return curr;
			}
			
			for(Node neighbor : curr.getNeighbors()){
				// The neighbor has not been visited, or the path through curr is shorter
				// than the last path that lead to the neighbor		
				int totalDist = curr.distance + curr.manhattanDistTo(neighbor);
				
				if(visited.contains(neighbor.getKey())){
					if(neighbor.distance > totalDist){
						neighbor.parent = curr;
						neighbor.distance = totalDist;
					}
				}
				else{					
					neighbor.parent = curr;
					neighbor.distance = totalDist;
					visited.add(neighbor.getKey());
					active.add(neighbor);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Traces from a resulting cities parents to find
	 * the path taken to locate them in a BFS/DFS/A* search
	 * @param result
	 * @param message
	 * @return
	 */
	public List<Node> doTrace(Node result, String message){
		
		String cities = "";
		if(result == null){
			System.err.println("Error! No path found");
			return null;
		}
		
		Double dist = 0.0;
		ArrayList<String> pathStrings = new ArrayList<String>();
		ArrayList<Node> nodesOnPath = new ArrayList<Node>();
		// Trace back to the start
		nodesOnPath.add(result);
		pathStrings.add(result.getKey());
		Node curr = result.parent;
		if(curr != null){
			dist += result.manhattanDistTo(curr);
		}
		
		while(curr != null){
			pathStrings.add(curr.getKey());
			nodesOnPath.add(curr);
			if(curr.parent != null){
				dist += curr.manhattanDistTo(curr.parent);
			}
			curr = curr.parent;
		}
		
		Collections.reverse(pathStrings);
		for(String str : pathStrings){
			cities += str + "\n";
		}
		System.out.println(message + "\n" + cities + "That took " + (pathStrings.size()-1) + " hops to find.\n"
				+ "Total Distance = " + (int) Math.rint(dist) + " units.");
		
		return nodesOnPath;
	}
	
	/**
	 * Implements the H(x) + G(x) heuristic 
	 * comparison required for A* search 
	 *
	 */
	public class AStarComparator implements Comparator<Node>{

		private Node goal;
		
		public AStarComparator(Node goal){
			this.goal = goal;
		}
		
		@Override
		public int compare(Node c1, Node c2) {
			Integer hg1 = c1.distance + goal.manhattanDistTo(c1); 
			Integer hg2 = c2.distance + goal.manhattanDistTo(c2);
			return hg1.compareTo(hg2);
		}
	}
	
}
