package com.lhs.bird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import org.mini2Dx.core.game.BasicGame;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;
import org.mini2Dx.core.graphics.viewport.FitViewport;
import org.mini2Dx.core.engine.geom.*;

public class MainGame extends BasicGame {
  public static final String GAME_IDENTIFIER = "com.lhs.bird";

  FitViewport viewport;

  float gameWidth = 800;
  float gameHeight = 600;

  @Override
  public void initialise() {
    viewport = new FitViewport(gameWidth, gameHeight);
  }
    
  @Override
  public void update(float delta) {
  }
    
  @Override
  public void interpolate(float alpha) {
  }
    
  @Override
  public void render(Graphics g) {
    viewport.apply(g);
  }
}