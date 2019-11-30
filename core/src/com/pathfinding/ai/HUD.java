package com.pathfinding.ai;

import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.building.utilities.layouts.TableLayout;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

public class HUD implements Observer {
	public Stage stage;
	private Main main;
	private VisTable rootTable, dataTable;
	private VisScrollPane scrollPane;
	private VisTextButton previousPhase, nextPhase, resetButton;
	private VisLabel currentPhaseLabel;
	private VisTable resultTable, resultContainer;
	private VisTextButton resultDismissButton;

	public final static int OBSERVE_RESULTTABLE = 0;
	public final static int OBSERVE_NEWVECTOR = 1;
	public static final int OBSERVE_COSTREGISTERED = 2;
	public static final int OBSERVE_COSTPOSTPONED = 3;
	public static final int OBSERVE_REMOVEVECTOR = 4;
	public static final int OBSERVE_REMOVEVECTORS = 5;
	public static final int OBSERVE_RESET = 6;
	
		
	public HUD(Main main) {
		this.main = main;
		stage = new Stage(new FitViewport(1280, 720));
		rootTable = new VisTable();
		rootTable.top();
		rootTable.setFillParent(true);
		stage.addActor(rootTable);
		stage.setDebugAll(true);
		currentPhaseLabel = new VisLabel();
		currentPhaseLabel.setColor(Color.BLACK);
		rootTable.add(currentPhaseLabel).expandX().center().top();
		rootTable.row();
		dataTable = new VisTable();
		scrollPane = new VisScrollPane(dataTable);
		rootTable.add(scrollPane).height(stage.getHeight() / 2).left();

		resultContainer = new VisTable();
		rootTable.add(resultContainer).expandY().right().top();
		rootTable.row();
		resetButton = new VisTextButton("RESET");
		resetButton.addListener(main.touchMan.resetButtonListener());
		rootTable.add(resetButton).expand().bottom().left();
		previousPhase = new VisTextButton("Previous");
		nextPhase = new VisTextButton("Next");
		rootTable.add(previousPhase).expandY().right().bottom();
		rootTable.add(nextPhase).right().bottom();
		rootTable.row();
		resultDismissButton = new VisTextButton("Dismiss");
		resultDismissButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				resultContainer.clearChildren();
			}

		});
		previousPhase.addListener(main.touchMan.previousButtonListener());
		nextPhase.addListener(main.touchMan.nextButtonListener());

		main.touchMan.multiplexer.addProcessor(0, stage);
		main.graphMan.addObserver(this);
		main.algorithms.addObserver(this);

	}

	public void render() {
		stage.draw();
		stage.act();
		switch (main.touchMan.touchMode) {
		case TouchManager.NODE_MODE:
			currentPhaseLabel.setText("Node mode");
			break;
		case TouchManager.VECTOR_MODE:
			currentPhaseLabel.setText("Vector mode");
			break;
		case TouchManager.SET_START_MODE:
			currentPhaseLabel.setText("Set Start mode");
			break;
		case TouchManager.SET_END_MODE:
			currentPhaseLabel.setText("Set End mode");
			break;
		case TouchManager.NO_MODE:
			currentPhaseLabel.setText("No mode");
			break;
		}

	}

	private void showKeyPad(Vector v) {
		VectorCostKeyPad vckp = new VectorCostKeyPad(main, v);
		vckp.lock();
	}

	@Override
	public void update(Observable arg0, Object obj) {
		@SuppressWarnings("unchecked")
		Object[] notification = (Object[]) obj;
		if (((Integer) notification[0]) == OBSERVE_NEWVECTOR) {
			Array<Vector> vectors = (Array<Vector>) notification[1];
			showKeyPad(vectors.peek());
		} else if ((Integer) notification[0] == OBSERVE_COSTREGISTERED) {
			Array<Vector> vectors = (Array<Vector>) notification[1];
			final Vector v = vectors.peek();
			VisTextButton button = new VisTextButton(v.startNode + " to " + v.endNode + ": "
					+ v.cost);
			button.addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					
					v.sprite.setColor(Color.RED);
				}
				
			});
			dataTable.add();
			dataTable.row();
		}

		else if ((Integer) notification[0] == OBSERVE_COSTPOSTPONED) {

			Array<Vector> vectors = (Array<Vector>) notification[1];
			final Vector v = vectors.peek();
			dataTable.add(new VisLabel(v.startNode + " to " + v.endNode));
			VisTextButton button = new VisTextButton("Enter");
			button.addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					showKeyPad(v);
				}
				
			});
			dataTable.add(button);
			dataTable.row();
		}

		else if (((Integer) notification[0]) == OBSERVE_RESULTTABLE) {
			resultTable = (VisTable) notification[1];
			resultContainer.clearChildren();
			resultContainer.add(resultTable).expand();
			resultContainer.row();
			resultContainer.add(resultDismissButton);
		}

	}

}
