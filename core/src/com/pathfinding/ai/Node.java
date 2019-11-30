package com.pathfinding.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

public class Node {
	public int ID;
	public Array<Vector> startVectors;
	public Array<Vector> endVectors;
	public Sprite sprite;
    public Component component;
    
	public Node(){
		startVectors = new Array<Vector>();
		endVectors = new Array<Vector>();
		sprite = new Sprite(new Texture(Gdx.files.internal("point.png")));
	}

}
