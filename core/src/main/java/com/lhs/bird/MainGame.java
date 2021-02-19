package com.lhs.bird;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;

import org.mini2Dx.core.font.BitmapFont;
import org.mini2Dx.core.font.FontGlyphLayout;
import org.mini2Dx.core.font.GameFont;
import org.mini2Dx.core.game.BasicGame;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.viewport.FitViewport;

public class MainGame extends BasicGame {
  public static final String GAME_IDENTIFIER = "com.lhs.bird";

  FitViewport viewport;
  InputProcessor input;
  BitmapFont titleFont, regularFont;

  private FontGlyphLayout glyphLayout;

  int gameWidth = 1280;
  int gameHeight = 720;
  float[] mouse = new float[2];

  private ArrayList<Button> mainMenuButtons;
  private ArrayList<Button> settingsButtons;
  private ArrayList<Button> mainGameButtons;
  private ArrayList<Button> pauseButtons;
  private GameState state = GameState.MAIN_MENU;

  private boolean gameInit;

  @Override
  public void initialise() {
    InitializeUI();
    gameInit = false;
    viewport = new FitViewport(gameWidth, gameHeight);
    viewport.onResize(gameWidth, gameHeight);
  }

  @Override
  public void update(float delta) {
    switch (state) {
      case MAIN_MENU:
        mouse[0] = Gdx.input.getX();
        mouse[1] = Gdx.input.getY();
        if (!Gdx.input.isTouched()) {
          return;
        }
        if (handleInput(Gdx.input.getX(), Gdx.input.getY(), mainMenuButtons) == -1) {
          return;
        }

        if (handleInput(Gdx.input.getX(), Gdx.input.getY(), mainMenuButtons) == 0) { // detected that you clicked the play button
          state = GameState.LOADING_SCREEN;

        } else if (handleInput(Gdx.input.getX(), Gdx.input.getY(), mainMenuButtons) == 1) { // settingsButtons button
          state = GameState.SETTINGS;

        } else if (handleInput(Gdx.input.getX(), Gdx.input.getY(), mainMenuButtons) == 2) { // quit button?
          Gdx.app.exit();
        }

        break;
      case SETTINGS:
        mouse[0] = Gdx.input.getX();
        mouse[1] = Gdx.input.getY();
        if (!Gdx.input.isTouched()) {
          return;
        }
        if (handleInput(Gdx.input.getX(), Gdx.input.getY(), settingsButtons) == -1) {
          return;
        }
        if (handleInput(Gdx.input.getX(), Gdx.input.getY(), settingsButtons) == 0) { // detected that you clicked the play button
          state = GameState.MAIN_MENU;
        }
        break;
      case LOADING_SCREEN:
        if (!gameInit){
          InitializeGame();
          state = GameState.MAIN_GAME;
        }
        break;
      case MAIN_GAME:
      case PAUSED:
    }
  }

  @Override
  public void interpolate(float alpha) {
  }

  @Override
  public void render(Graphics g) {
    viewport.apply(g);
    g.setFont(regularFont);
    g.setBackgroundColor(Color.WHITE);
    switch(state){
      case MAIN_MENU:
        renderButtons(g, mainMenuButtons);
        break;
      case SETTINGS:
        renderButtons(g, settingsButtons);
        break;
      case LOADING_SCREEN:
        break;
      case MAIN_GAME:
        renderButtons(g, mainGameButtons);
        break;
      case PAUSED:
        renderButtons(g, pauseButtons);
    }

  }




  private void InitializeUI(){
    
    FreeTypeFontParameter normal = new FreeTypeFontParameter();
    normal.size = 40;
    normal.flip = true;
    normal.magFilter = TextureFilter.Linear;
    normal.minFilter = TextureFilter.Linear;

    FreeTypeFontParameter title = new FreeTypeFontParameter();
    title.size = 100;
    title.flip = true;
    title.magFilter = TextureFilter.Linear;
    title.minFilter = TextureFilter.Linear;

    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ARCADECLASSIC.TTF"));

    titleFont = new BitmapFont(generator.generateFont(title).getData(), generator.generateFont(title).getRegions(), true);
    titleFont.setUseIntegerPositions(false);

    regularFont = new BitmapFont(generator.generateFont(normal).getData(), generator.generateFont(normal).getRegions(), true);
    regularFont.setUseIntegerPositions(false);

    mainMenuButtons = new ArrayList<Button>();
    mainMenuButtons.add(new Button(540f, 360f, 200f, 75f, 0, "Play"));
    mainMenuButtons.add(new Button(540f, 460f, 200f, 75f, 1, "Settings"));
    mainMenuButtons.add(new Button(540f, 560f, 200f, 75f, 2, "Quit"));

    settingsButtons = new ArrayList<Button>();
    settingsButtons.add(new Button(25f, 620f, 200f, 75f, 0, "Back"));

    mainGameButtons = new ArrayList<Button>();

    pauseButtons = new ArrayList<Button>();
  }

  private void InitializeGame(){
    gameInit = true;
    
  }

  private int handleInput(int mouseX, int mouseY, ArrayList<Button> buttons){
    Rectangle rect = new Rectangle();
    Button button;
    for (int i = 0; i < buttons.size(); i++){
      button = buttons.get(i);
      rect.set(button.getX(), button.getY(), button.getWidth(), button.getHeight());

      if (rect.contains(mouseX, mouseY)){
        return button.getID();
      }
    }    
    return -1;
  }

  private void renderText(Graphics g, String message){
    glyphLayout = g.getFont().newGlyphLayout();
    glyphLayout.setText(message);
    int renderX = MathUtils.round((getWidth()/2) - (glyphLayout.getWidth() / 2f));
    int renderY = MathUtils.round(glyphLayout.getHeight());
    g.drawString(message, renderX, renderY);
  }

  private void renderText(Graphics g, String message, Button button){
    glyphLayout = g.getFont().newGlyphLayout();
    glyphLayout.setText(message);
    int renderX = MathUtils.round((button.CenterX()) - (glyphLayout.getWidth() / 2f));
    int renderY = MathUtils.round(button.CenterY() - (glyphLayout.getHeight() / 2f));
    g.drawString(message, renderX, renderY, button.getWidth());
  }

  private void renderButtons(Graphics g, ArrayList<Button> buttons){
    Button temp;
    Rectangle coll;
    g.setColor(Color.BLACK);
    for (int i = 0; i < buttons.size(); i++){
      temp = buttons.get(i);
      coll = new Rectangle(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
      if (!coll.contains(mouse[0], mouse[1])){
        g.drawRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
      } else {
        g.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
        g.setColor(Color.WHITE);
      }
      renderText(g, temp.getText(), temp);
      g.setColor(Color.BLACK);
    }
  }

}