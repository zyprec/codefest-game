package com.lhs.bird;

import org.mini2Dx.core.graphics.Sprite;

public class Player extends Object {
    
    public boolean controllable = true;
    private int lives;

    public Player(float _x, float _y, Sprite _sprite) {
        super(_x, _y, _sprite);
        lives = 3;
    }

    public float[] getSpeed(){
        return this.speed;
    }

    public void setSpeedX(float x){
        this.speed[0] = x;
    }
    
    public void setSpeedY(float y){
        this.speed[1] = y;
    }      

    public int getLives(){
        return this.lives;
    }

    public void setLives(int num){
        this.lives = num;
    }

    public int changeHealthDown(int health){
        health -= 1;
        return health;
    }

    public int changeHealthUp(int health){
        health += 1;
        return health;
    }
}
