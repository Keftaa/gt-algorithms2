package com.pathfinding.ai;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;

public class Main extends Game {
	public GraphManager graphMan;
	public TouchManager touchMan;
	public FitViewport viewport;
	public BitmapFont font;
	public HUD hud;
    public Algorithms algorithms;
	private GraphScreen graphScreen;
	
	@Override
	public void create() {
		VisUI.load(SkinScale.X2);
		font = new BitmapFont();
		viewport = new FitViewport(1280, 720);
		graphMan = new GraphManager();
		touchMan = new TouchManager(this);
		graphScreen = new GraphScreen(this);
        algorithms = new Algorithms(this);
		hud = new HUD(this);
		this.setScreen(graphScreen);
		
	}
	
	
}
