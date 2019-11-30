package com.pathfinding.ai;

import java.util.Observable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;

public class GraphManager extends Observable {
	public Node startNode;
	public Node endNode;
	public Node selectedNode;
	public Array<Node> nodes;
	public Array<Vector> vectors;
    private BitmapFont font;
	private GlyphLayout fontLayout;
	private int lastID;
	public GraphManager() {
		nodes = new Array<Node>();
		vectors = new Array<Vector>();
		font = new BitmapFont(Gdx.files.internal("font.fnt"));
		fontLayout = new GlyphLayout(font, "9");
		font.setColor(Color.LIGHT_GRAY);
	}

	public void setStartNode(Node n) {
		startNode = n;
		Gdx.input.vibrate(80);
	}

	public void setEndNode(Node n) {
		endNode = n;
		Gdx.input.vibrate(80);
	}

	public void addVector(Node start, Node end) {
		for (Vector v : vectors) {
			if (v.startNode == start && v.endNode == end) {
				selectedNode = null;
				return;
			}
		}
		Vector vector = new Vector();
		vector.startNode = start;
		vector.endNode = end;
		vectors.add(vector);
		start.startVectors.add(vector);
		end.endVectors.add(vector);
		selectedNode = null;
		Gdx.input.vibrate(100);
		setChanged();
		Object[] notification = {HUD.OBSERVE_NEWVECTOR, vectors};
		notifyObservers(notification);

	}

	public void removeVector(Node start, Node end) {
		for (Vector v : vectors) {
			if (v.startNode == start && v.endNode == end) {
				start.startVectors.removeValue(v, true);
				end.endVectors.removeValue(v, true);
				setChanged();
				Object[] notification = {HUD.OBSERVE_REMOVEVECTOR, v};
				notifyObservers(notification);
				vectors.removeValue(v, true);
				
				break;
			}
		}
		Gdx.input.vibrate(new long[]{0, 50, 50, 50}, -1);

	}

	public void addNode(Node n) {
		n.ID = lastID++;
		nodes.add(n);
		Gdx.input.vibrate(100);
	}

	public void removeNode(Node n) {
		Array<Vector> vectors = new Array<Vector>();
		vectors.addAll(n.startVectors);
		vectors.addAll(n.endVectors);
		setChanged();
		Object[] notification = {HUD.OBSERVE_REMOVEVECTORS, vectors};
		notifyObservers(notification);
		vectors.removeAll(n.startVectors, true);
		vectors.removeAll(n.endVectors, true);
		nodes.removeValue(n, true);
		Gdx.input.vibrate(new long[]{0, 50, 50, 50}, -1);

	}

	public void setVectorCost(Vector v, int cost) {
		v.cost = cost;
	}

	public void moveSelectedNode(float newX, float newY) {
		selectedNode.sprite.setPosition(newX, newY);
		selectedNode = null;
		Gdx.input.vibrate(20);
	}

	public void render(SpriteBatch batch) {
		for (Vector v : vectors) {
			v.adjustPosition();
			v.sprite.draw(batch);
		}

		for (Node n : nodes) {
			if (startNode == n) {
                n.sprite.setColor(Color.GREEN);
			} else if (endNode == n) {
                n.sprite.setColor(Color.RED);
			} else if (selectedNode == n) {
                n.sprite.setColor(Color.GRAY);
			} else {
	//			if (!dijkstraResults.contains(n, true))
					n.sprite.setColor(Color.BLACK);
			}
			n.sprite.draw(batch);
	//		if (dijkstraResults.contains(n, true)) {
	//			font.draw(batch, n.ID + " (" + ((DijkstraComponent)n.component).distanceFromSource + ")",
	//					n.sprite.getX() +( n.sprite.getWidth() - fontLayout.width )/ 2,
	//					n.sprite.getY() + ( n.sprite.getHeight() + fontLayout.height )/ 2);
    //
	//		} else {
				font.draw(batch, n.ID + "",
						n.sprite.getX() +( n.sprite.getWidth() - fontLayout.width ) / 2,
						n.sprite.getY() + ( n.sprite.getHeight() + fontLayout.height ) / 2);
		//	}

		}
	//	if (dijkstraResults.size>0) {
	//		for (Node dNode : dijkstraResults) {
		//		if (dNode != endNode && dNode != startNode) {
		//			dNode.sprite.setColor(Color.GOLD);
		//		}
		//	}
		//}

        

	}

	public Vector getVector(Node a, Node b) {
		for (Vector v : vectors) {
			if (v.startNode == a && v.endNode == b)
				return v;
		}
		return null;
	}


	public void reset() {
		nodes.clear();
		vectors.clear();
		startNode = null;
		endNode = null;

		setChanged();
		Object[] notification = {HUD.OBSERVE_RESET};
		notifyObservers(notification);
		lastID = 0;

	}
    
    public void setChanged(){
        super.setChanged();
    }

}
