package com.pathfinding.ai;
import com.badlogic.gdx.utils.Array;

public class DijkstraComponent implements Component {
    
    	int distanceFromSource = Integer.MAX_VALUE;
		Node previousNode; 
		Array<Node> nodesHistory;
        Array<Integer> distancesHistory;
        boolean isTraversed;
        
        DijkstraComponent(){
            nodesHistory = new Array<Node>();
            distancesHistory = new Array<Integer>();
        }
        
		DijkstraComponent distance(int distanceFromSource) {
			this.distanceFromSource = distanceFromSource;
			return this;
		}
}