package com.pathfinding.ai;
import com.badlogic.gdx.utils.Array;
public class BellmanComponent implements Component {
    public int potential;
    public Array<Node> arborescences;
    public boolean isTraversed;

    public BellmanComponent(){
        arborescences = new Array<Node>();
    }

    public BellmanComponent potential(int potential){
        this.potential = potential;
        return this;
    }
}