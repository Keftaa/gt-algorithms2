package com.pathfinding.ai;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class TouchManager implements GestureListener, InputProcessor {
	public static final int NODE_MODE = 0;
	public static final int VECTOR_MODE = 1;
	public static final int SET_START_MODE = 2;
	public static final int SET_END_MODE = 3;
	public static final int NO_MODE = 4;

	public InputMultiplexer multiplexer;
	public int touchMode;
	private Main main;
	private Map<Integer, TouchInfo> touches;

	public TouchManager(Main main) {
		touchMode = NODE_MODE;
		this.main = main;
		touches = new HashMap<Integer, TouchInfo>();
		touches.put(0, new TouchInfo());
		touches.put(1, new TouchInfo());
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(new GestureDetector(this));
		multiplexer.addProcessor((InputProcessor) this);
		Gdx.input.setInputProcessor(multiplexer);
	}

	class TouchInfo {
		public float touchX = 0;
		public float touchY = 0;
		public boolean touched = false;
	}

	private Vector2 coordinate(float x, float y) {
		Vector2 screenCoords = new Vector2(x, y);
		Vector2 realCoords = main.viewport.unproject(screenCoords);
		return realCoords;
	}

	private Node getTouchedNode(float x, float y) {
		Vector2 realCoords = coordinate(x, y);
		for (Node n : main.graphMan.nodes) {
			if (n.sprite.getBoundingRectangle().contains(realCoords.x,
					realCoords.y)) {
				return n;
			}
		}
		return null;
	}

	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {

		if (touchMode == VECTOR_MODE) {
			if (pointer < 2) {
				touches.get(pointer).touchX = x;
				touches.get(pointer).touchY = y;
				touches.get(pointer).touched = true;
			}
		}
		if (pointer == 1) {
			Node node1 = null, node2 = null;
			int found = 0;
			for (int touch : touches.keySet()) {
				if (touches.get(touch).touched) {
					if (found == 0) {
						node1 = getTouchedNode(touches.get(touch).touchX,
								touches.get(touch).touchY);
					} else if (found == 1) {
						node2 = getTouchedNode(touches.get(touch).touchX,
								touches.get(touch).touchY);
					}
					found++;
				}
			}
			if (node1 != null && node2 != null) {
				main.graphMan.removeVector(node1, node2);
			}
		}

		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		if (touchMode == NODE_MODE) {
			if (main.graphMan.selectedNode != null) {
				main.graphMan.moveSelectedNode(
						coordinate(x, y).x
								- main.graphMan.selectedNode.sprite.getWidth()
								/ 2,
						coordinate(x, y).y
								- main.graphMan.selectedNode.sprite.getHeight()
								/ 2);
				return true;
			}

			Node n = getTouchedNode(x, y);
			if (n != null) {
				main.graphMan.selectedNode = n;
				return true;
			}
			n = new Node();
			n.sprite.setPosition(coordinate(x, y).x - n.sprite.getWidth() / 2,
					coordinate(x, y).y - n.sprite.getHeight() / 2);
			main.graphMan.addNode(n);
		} else if (touchMode == VECTOR_MODE) {
			Node n = getTouchedNode(x, y);
			if (n == null) {
				return true;
			}
			if (main.graphMan.selectedNode == null) {
				main.graphMan.selectedNode = n;
				return true;
			}

			main.graphMan.addVector(main.graphMan.selectedNode, n);

		} else if (touchMode == SET_START_MODE) {
			Node n = getTouchedNode(x, y);
			if (n == null)
				return true;
			main.graphMan.setStartNode(n);
		} else if (touchMode == SET_END_MODE) {
			Node n = getTouchedNode(x, y);
			if (n == null)
				return true;
			main.graphMan.setEndNode(n);
		}
		return true;
	}

	@Override
	public boolean longPress(float x, float y) {
		if (touchMode == NODE_MODE) {
			Node n = getTouchedNode(x, y);
			if (n != null) {
				main.graphMan.removeNode(n);
			}
		}
		return true;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		main.viewport.getCamera().translate(-deltaX, deltaY, 0); // thanks Adam
																	// Brickhill
		return true;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		if (distance < initialDistance) {
			((OrthographicCamera) main.viewport.getCamera()).zoom += (initialDistance / distance) * 0.1;
		} else {
			((OrthographicCamera) main.viewport.getCamera()).zoom -= (initialDistance / distance) * 0.1;
			if (((OrthographicCamera) main.viewport.getCamera()).zoom < 0.5f)
				((OrthographicCamera) main.viewport.getCamera()).zoom = 0.5f;
		}

		return true;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		touches.get(pointer).touched = false;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	private ChangeListener pListener, nListener;

	public ChangeListener previousButtonListener() {
		pListener = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (touchMode == 0)
					return;
				touchMode--;
			}

		};
		return pListener;
	}

	public ChangeListener nextButtonListener() {
		nListener = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (touchMode == 4) {
					try {
						//main.graphMan.dijkstra();
                        //main.algorithms.bellman.calculate();
                        main.algorithms.dijkstra.calculate();
                    } catch (NoPathFoundException e) {
                        e.printStackTrace();
					}
					return;
				} else if (touchMode == SET_START_MODE) {
					if (main.graphMan.startNode != null) {
						touchMode++;
					}
				} else if (touchMode == SET_END_MODE) {
					if (main.graphMan.endNode != null) {
						touchMode++;
					}
				} else {
					touchMode++;
				}
			}

		};
		return nListener;
	}

	private ChangeListener resetListener;

	public ChangeListener resetButtonListener() {
		resetListener = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				main.graphMan.reset();
			}

		};
		return resetListener;

	}
	

}
