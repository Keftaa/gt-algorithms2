package com.pathfinding.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class Vector {
	public Node startNode;
	public Node endNode;
	public int cost;
	public Sprite sprite;
	
	public Vector(){
		sprite = new Sprite(new Texture(Gdx.files.internal("node.png")));
		sprite.setOriginCenter();
	}
	
	public void adjustPosition() {
		float deltaY = startNode.sprite.getY() - endNode.sprite.getY();
		float deltaX = startNode.sprite.getX() - endNode.sprite.getX();
		float angleInDegrees = (float) (MathUtils.atan2(deltaY, deltaX) * 180 / Math.PI);
		sprite.setSize(
				(float) Math.abs(Math.sqrt(Math.pow(deltaX, 2)
						+ Math.pow(deltaY, 2))), 10f);
		sprite.setRotation(angleInDegrees);
		sprite.setFlip(true, false);
		sprite.setPosition(endNode.sprite.getX() + endNode.sprite.getWidth() / 2,
				endNode.sprite.getY() + endNode.sprite.getHeight() / 2);

	}
}
