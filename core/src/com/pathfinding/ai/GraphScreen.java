package com.pathfinding.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;

public class GraphScreen implements Screen {
	private Main main;
	private SpriteBatch batch;
	public GraphScreen(Main main){
		this.main = main;
		batch = new SpriteBatch();
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(main.viewport.getCamera().combined);
		main.viewport.apply(false);
		batch.begin();
		main.graphMan.render(batch);
		batch.end();
		main.hud.render();
	}

	@Override
	public void resize(int width, int height) {
		main.viewport.update(width, height);
		main.hud.stage.getViewport().update(width, height);
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		VisUI.dispose();
		for(int i = 0; i < main.graphMan.nodes.size; i++){
			main.graphMan.nodes.get(i).sprite.getTexture().dispose();
		}
		for(int i = 0; i < main.graphMan.vectors.size; i++){
			main.graphMan.vectors.get(i).sprite.getTexture().dispose();
		}
				
	}

}
