package com.lhs.bird;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import org.mini2Dx.core.collisions.RegionQuadTree;
import org.mini2Dx.core.engine.geom.CollisionBox;
import org.mini2Dx.core.font.BitmapFont;
import org.mini2Dx.core.font.FontGlyphLayout;
import org.mini2Dx.core.game.BasicGame;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;
import org.mini2Dx.core.graphics.viewport.FitViewport;

public class MainGame extends BasicGame {
  public static final String GAME_IDENTIFIER = "com.lhs.bird";

  FitViewport viewport;
  InputProcessor input;
  BitmapFont titleFont, regularFont;

  private FontGlyphLayout glyphLayout;
  private AssetManager manager = new AssetManager();

  final float speed = 6f;
  int gameWidth = 1280;
  int gameHeight = 720;
  float[] mouse = new float[2];

  private RegionQuadTree<CollisionBox> collisions; 
  
  private ArrayList<Button> mainMenuButtons;
  private ArrayList<Button> settingsButtons;
  private ArrayList<Object> mainGameUI;
  private ArrayList<Object> background;
  private ArrayList<Button> pauseButtons;
  private GameState state = GameState.MAIN_MENU;

  private ArrayList<Obstacle> obstacles;
  private boolean gameInit;
  private Player player;
  private long startTime;
  private long pauseTime;
  private long hitTime;
  private int score;
  int lastSpawned = 0;

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
        }
        break;
      case PAUSED:
        if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
          startTime += TimeUtils.millis() - pauseTime;
          state = GameState.MAIN_GAME;
        }
        break;
      case MAIN_GAME:
        score = (int)(Math.pow(1.245, TimeUtils.timeSinceMillis(startTime)/1000f) + 9.5f * TimeUtils.timeSinceMillis(startTime)/1000f);
        //System.out.println("Time Elapsed: " + TimeUtils.timeSinceMillis(startTime)/1000 + "/nScore: " + score);
        updateBackground();
        updatePlayer();
        updateObstacles();
        int secondsPassed = (int)(TimeUtils.timeSinceMillis(startTime)/1000f);
        if(secondsPassed % 2 == 1 && lastSpawned < secondsPassed){
          lastSpawned = secondsPassed;
          spawnObstacles();
        }
        break;
      case GAME_LOST:
        if(!Gdx.input.isKeyJustPressed(Keys.SPACE)){
          break;
        }
        ResetGame();
        state = GameState.MAIN_GAME;
    }
  }

  @Override
  public void interpolate(float alpha) {
    if (state == GameState.MAIN_GAME){
      player.getHitbox().interpolate(null, alpha);
      for (int i = 0; i < obstacles.size(); i++){
        obstacles.get(i).getHitbox().interpolate(null, alpha);
      }
    }
  }

  @Override
  public void render(Graphics g) {
    viewport.apply(g);
    g.setFont(regularFont);
    g.setBackgroundColor(Color.WHITE);
    switch(state){
      case MAIN_MENU:
        renderButtons(g, mainMenuButtons);
        g.setFont(titleFont);
        renderText(g, "Bird Dash", 200);
        g.setFont(regularFont);
        break;
      case SETTINGS:
        renderButtons(g, settingsButtons);
        g.setFont(titleFont);
        g.drawString("Settings", 25, 25);
        g.setFont(regularFont);
        break;
      case LOADING_SCREEN:
        if(manager.update() && gameInit == true && TimeUtils.timeSinceMillis(startTime) > 1000){
          Sprite heart = new Sprite(manager.get("Heart.png", Texture.class));
          Sprite back1 = new Sprite(manager.get("Back_0.png", Texture.class));
          Sprite back2 = new Sprite(manager.get("Back_1.png", Texture.class));
          Sprite back3 = new Sprite(manager.get("Back_1_2.png", Texture.class));
          Sprite back4 = new Sprite(manager.get("Background_3.png", Texture.class));
          Sprite back5 = new Sprite(manager.get("Background_0.png", Texture.class));
          

          background.add(new Object(0, 0, back1, 587, 720, -1f)); // 0
          background.add(new Object(0, 0, back2, 587, 720, -1f));
          background.add(new Object(0, 0, back3, 587, 720, -1f));
          background.add(new Object(0, 0, back1, 587, 720, -1f));
          background.add(new Object(0, 0, back2, 587, 720, -1f));
          background.add(new Object(0, 0, back3, 587, 720, -1f)); // 5
          background.add(new Object(0, 0, back4, 1175, 720, -0.5f)); // 6
          background.add(new Object(0, 0, back4, 1175, 720, -0.5f));
          background.add(new Object(0, 0, back4, 1175, 720, -0.5f)); // 8
          background.add(new Object(0, 0, back5, 606, 720, -0.2f)); // 9 
          background.add(new Object(0, 0, back5, 606, 720, -0.2f));
          background.add(new Object(0, 0, back5, 606, 720, -0.2f));
          background.add(new Object(0, 0, back5, 606, 720, -0.2f)); // 12

          for(int i = 1; i <= 5; i++){
            background.get(i).setX(background.get(i-1).getX()+587);
          }
          for(int i = 7; i <= 8; i++){
            background.get(i).setX(background.get(i-1).getX()+1175);
          }
          for(int i = 10; i <= 12; i++){
            background.get(i).setX(background.get(i-1).getX()+606);
          }
          heart.setSize(30, 30);
          for(int i = 0; i < mainGameUI.size(); i++){
            if(mainGameUI.get(i).ID == 0){
              mainGameUI.get(i).sprite = heart;
            }
          }
          startTime = TimeUtils.millis();
          state = GameState.MAIN_GAME;
        }
        float progress = 0f;
        if(TimeUtils.timeSinceMillis(startTime) < 600){
          progress = manager.getProgress();
        } else {
          progress = manager.getProgress();
        }
        g.drawRect(160, 285, 960, 150);
        g.fillRect(160, 285, 960 * progress, 150);
        break;
      case PAUSED:
      case GAME_LOST:
      case MAIN_GAME:
        renderBackground(g);
        if(TimeUtils.timeSinceMillis(hitTime) < 3000){
          Gdx.gl.glClear(GL20.GL_ALPHA_BITS);
          player.getSprite().setColor(1, 0, 0, 0.2f);
          System.out.println(player.getSprite().getAlpha());
          //player.getSprite().setAlpha((float)(0.3 * Math.cos(TimeUtils.timeSinceMillis(hitTime)/1000f * 7.85) + 0.7));
        } else {
          //player.getSprite().setAlpha(0f);
        }
        g.drawSprite(player.getSprite(), player.getHitbox().getRenderX(), player.getHitbox().getRenderY());
        for (int i = 0; i < obstacles.size(); i++){
          g.drawSprite(obstacles.get(i).getSprite(), obstacles.get(i).getHitbox().getRenderX(), obstacles.get(i).getHitbox().getRenderY());
        }
        renderMainGame(g);
        if (state == GameState.PAUSED){
          renderButtons(g, pauseButtons);
        } else if (state == GameState.GAME_LOST){
          g.drawRect(430, 270, 420, 100);
          g.setColor(Color.WHITE);
          g.fillRect(430+1, 270+1, 420-2, 100-2);
          g.setColor(Color.BLACK);
          renderText(g, "You  Lost", 290);
          renderText(g, "Click Space to Retry", 330);
        }
        break;
    }
  }



  private void updatePlayer(){
    player.setSpeedX(0);
    player.setSpeedY(0);
    if(player.controllable) {
      if(Gdx.input.isKeyPressed(Keys.W)){
        player.setSpeedY(-speed);
      }
      if(Gdx.input.isKeyPressed(Keys.A)){
        player.setSpeedX(-speed);
      }
      if(Gdx.input.isKeyPressed(Keys.S)){
        player.setSpeedY(speed);
      }
      if(Gdx.input.isKeyPressed(Keys.D)){
        player.setSpeedX(speed);
      }
      if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
        pauseTime = TimeUtils.millis();
        state = GameState.PAUSED;
      }
      if(Gdx.input.isKeyJustPressed(Keys.G)){
        player.setLives(0);
      }
    }
    if(player.getLives() == 0){
      state = GameState.GAME_LOST;
    }
    Array<CollisionBox> playerCollisions = new Array<CollisionBox>();
    collisions.getElementsWithinArea(playerCollisions, player.getHitbox());
    for(int i = 0; i < playerCollisions.size; i++){
      if(playerCollisions.get(i) != null && playerCollisions.get(i) != player.getHitbox() && TimeUtils.timeSinceMillis(hitTime) > 3000){
        player.setLives(player.getLives()-1);
        hitTime = TimeUtils.millis();
        break;
      }
    }
    player.updateLocation();
    if (player.getX() < 0){
      player.setX(0);
    } else if (player.getX() + player.getHitbox().getWidth() > gameWidth){
      player.setX(gameWidth - player.getHitbox().getWidth());
    }
    if (player.getY() < 0){
      player.setY(0);
    } else if (player.getY() + player.getHitbox().getHeight() > gameHeight){
      player.setY(gameHeight - player.getHitbox().getHeight());
    }
    Sprite emptyHeart = new Sprite(manager.get("EmptyHeart.png", Texture.class));
    Sprite heart = new Sprite(manager.get("Heart.png", Texture.class));
    emptyHeart.setSize(30, 30);
    heart.setSize(30, 30);
    for(int i = 0; i < 3; i++){
      if(player.getLives() <= i){
        mainGameUI.get(i).sprite = emptyHeart;
      } else {
        mainGameUI.get(i).sprite = heart;
      }
    }
    player.preUpdate();
    player.getHitbox().set(player.getX(), player.getY());
  }

  private void spawnObstacles(){
    int obstacleNumber = generateRandomNumber(4, 10);
    Sprite obstacleSprite = new Sprite(manager.get("obstacle/Crate.png", Texture.class));
    System.out.println(collisions.getTotalElements());
    for (int i = 0; i < obstacleNumber; i++){
      Obstacle temp = new Obstacle(gameWidth + generateRandomNumber(0, gameWidth), generateRandomNumber(0, (int)(gameHeight - obstacleSprite.getHeight())), obstacleSprite, -generateRandomNumber(3, 7));
      obstacles.add(temp);
      collisions.add(temp.getHitbox());
    }
  } 

  private void updateObstacles(){
    for(int i = 0; i < obstacles.size(); i++){
      obstacles.get(i).updateLocation();
      obstacles.get(i).getHitbox().preUpdate();
      obstacles.get(i).getHitbox().set(obstacles.get(i).getX(), obstacles.get(i).getY());
      if(obstacles.get(i).getX() + obstacles.get(i).getHitbox().getWidth() < 0){
        obstacles.remove(i);
        i--;
        //System.out.println(obstacles.size());
      }
    }
  }

  private int generateRandomNumber(int min, int max){
    return (int)(Math.random() * (max - min + 1) + min);
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

    mainGameUI = new ArrayList<Object>();
    mainGameUI.add(new Object(1110, 25, null, 0));
    mainGameUI.add(new Object(1150, 25, null, 0));
    mainGameUI.add(new Object(1190, 25, null, 0));

    pauseButtons = new ArrayList<Button>();
    pauseButtons.add(new Button(440f, 310f, 400f, 50f, 4, "Paused"));
  }

  private void InitializeGame(){
    obstacles = new ArrayList<Obstacle>();
    background = new ArrayList<Object>();
    player = new Player(0f, 0f, new Sprite(new Texture(Gdx.files.internal("mini2Dx.png"))));
    manager.load("obstacle/Crate.png", Texture.class);
    manager.load("Heart.png", Texture.class);
    manager.load("EmptyHeart.png", Texture.class);
    manager.load("Back_0.png", Texture.class);
    manager.load("Back_1.png", Texture.class);
    manager.load("Back_1_2.png", Texture.class);
    manager.load("Background_0.png", Texture.class);
    manager.load("Background_3.png", Texture.class);
    manager.load("background_2.png", Texture.class);
    manager.load("tree.png", Texture.class);
    startTime = TimeUtils.millis();
    hitTime = 0;
    collisions = new RegionQuadTree<CollisionBox>(100, 0, 0, gameWidth * 2, gameHeight);
    collisions.add(player.getHitbox());
    gameInit = true;
  }

  private void ResetGame(){
    lastSpawned = 0;
    collisions.clear();
    obstacles.clear();
    player.setX(0);
    player.setY(0);
    player.setLives(3);
    startTime = TimeUtils.millis();
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

  private void updateBackground(){
    for(int i = 0; i < background.size(); i++){
      background.get(i).updateLocation();
    }
    for(int i = 0; i <= 5; i++){
      if(background.get(i).getX() + 587 < 0){
        if(i == 0) {
          background.get(i).setX(background.get(5).getMaxX()-1);
        } else {
          background.get(i).setX(background.get(i-1).getMaxX());
        }
      }
    }
    for(int i = 6; i <= 8; i++){
      if(background.get(i).getX() + 1175 < 0){
        //System.out.println(background.get(i).getX() + 1175);
        System.out.println("Index: " + i);
        if(i == 6) {
          background.get(i).setX(background.get(8).getMaxX()-1);
        } else {
          background.get(i).setX(background.get(i-1).getMaxX());
        }
      }
    }
    for(int i = 9; i <= 12; i++){
      if(background.get(i).getX() + 606 < 0){
        if(i == 9) {
          background.get(i).setX(background.get(12).getMaxX()-1);
        } else {
          background.get(i).setX(background.get(i-1).getMaxX());
        }
      }
    }
  }

  private void renderBackground(Graphics g){
    for(int i = background.size() - 1; i >=0; i--){
      g.drawSprite(background.get(i).getSprite(), background.get(i).getX(), 0);
    }
  }

  private void renderText(Graphics g, String message, int height){
    glyphLayout = g.getFont().newGlyphLayout();
    glyphLayout.setText(message);
    int renderX = MathUtils.round((getWidth()/2) - (glyphLayout.getWidth() / 2f));
    int renderY = MathUtils.round(height);
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
      if (temp.getID() == 4){
        g.drawRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
        g.setColor(Color.WHITE);
        g.fillRect(temp.getX() + 1, temp.getY() + 1, temp.getWidth() - 2, temp.getHeight() - 2);
        g.setColor(Color.BLACK);
      }
      else if (!coll.contains(mouse[0], mouse[1])){
        g.drawRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
      } else {
        g.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
        g.setColor(Color.WHITE);
      }
      renderText(g, temp.getText(), temp);
      g.setColor(Color.BLACK);
    }
  }

  private void renderMainGame(Graphics g){
    g.setLineHeight(3);
    g.drawRect(mainGameUI.get(1).getX() - 60 + 15, mainGameUI.get(1).getY() - 20 + 15, 120, 40);
    g.setColor(Color.WHITE);
    g.fillRect(mainGameUI.get(1).getX() - 59 + 15, mainGameUI.get(1).getY() - 19 + 15, 118, 38);
    g.setColor(Color.BLACK);
    glyphLayout = g.getFont().newGlyphLayout();
    glyphLayout.setText("Life");
    int renderX = MathUtils.round((mainGameUI.get(1).getX() + 15) - (glyphLayout.getWidth() / 2f));
    int renderY = MathUtils.round((mainGameUI.get(1).getY() + 30) - (glyphLayout.getHeight() / 2f) + 25);
    g.drawString("Life", renderX, renderY);
    renderText(g, "Score: " + String.valueOf(score), 25);
    for(Object uiElement : mainGameUI){
      g.drawSprite(uiElement.getSprite(), uiElement.getX(), uiElement.getY());
    }
  }
}