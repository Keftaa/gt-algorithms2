package com.pathfinding.ai;

import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class VectorCostKeyPad extends VisTable {
	private Main main;
	private Vector vector;
	private VisLabel label;
	private VisLabel title;
	private Obs obs;
	public VectorCostKeyPad(Main main, Vector v) {
		this.main = main;
		this.vector = v;
		obs = new Obs();
		int width = 400;
		int height = 300;
		setBounds(main.hud.stage.getWidth() / 2 - width / 2,
				main.hud.stage.getHeight() / 2 - height / 2, width, height);
		title = new VisLabel("Vector: "+vector.startNode.ID+"--->"+vector.endNode.ID);
		title.setColor(Color.BLACK);
		title.setFontScale(.6f);
		add(title).expand().fill().colspan(3);
		row();
		label = new VisLabel();
		add(label).expand().fill().colspan(3);
		row();
		for (int i = 1; i <= 12; i++) {
			VisTextButton button;

			if (i == 12) {
				button = new VisTextButton("OK");
				button.setName("okButton");
				add(button).expand().fill();
				continue;
			}
			if (i == 10) {
				button = new VisTextButton("Del");
				button.setName("deleteButton");
				add(button).expand().fill();
				continue;
			}
			if (i == 11) {
				button = new VisTextButton("0");
				add(button).expand().fill();
				continue;
			}
			button = new VisTextButton(String.valueOf(i));
			add(button).expand().fill();
			if (i % 3 == 0) {
				row();
			}
		}
		main.hud.stage.addActor(this);
		process();
	}
	
	private void validateCost(CharSequence charSequence){
		if(label.getText().length>=9){
			label.setColor(Color.RED);
			return;
		}
		else  {
			label.setColor(Color.BLACK);
			label.setText(label.getText()+""+charSequence);
		}
		
	}
	
	private void process(){
		Array<Actor> buttons = this.getChildren();
		for(final Actor button: buttons){
			button.addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if(button.getName() != null && button.getName().equals("okButton")){
						save();
						unlock();
						destroy();
					}
					if(button.getName() != null && button.getName().equals("deleteButton")){
						if(label.getText().length>0){
							label.setText(label.getText().substring(0, label.getText().length-1));
						}
					} else {
						validateCost(((VisTextButton)button).getText());
					}
					
				}


				
			});
		}
	}

	public void lock() {
		for (Actor a : main.hud.stage.getActors()) {
			if (a != this)
				a.setTouchable(Touchable.disabled);
		}
		Gdx.input.setInputProcessor(main.hud.stage);
	}

	public void unlock() {
		for (Actor a : main.hud.stage.getActors()) {
			if (a != this)
				a.setTouchable(Touchable.enabled);
		}
		Gdx.input.setInputProcessor(main.touchMan.multiplexer);
	}

	public void save() {
		vector.cost = Integer.parseInt(label.getText() + "");
		obs.notifyObservers();
	}
	
	private void destroy() {
		main.hud.stage.getActors().removeValue(this, true);
	}
	

}
