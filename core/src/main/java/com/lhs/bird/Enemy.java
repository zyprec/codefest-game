package com.lhs.bird;

import org.mini2Dx.core.graphics.Sprite;

import java.Player;

public class Enemy extends Object {

    private int speedx;
    private int speedy;
    private int damage;

    public Enemy(int _x, int _y, Sprite _sprite) {
        super(_x, _y, _sprite);
        int speedx = 100;
        int speedy = 100;
        int damage = 1;
        
    }

    public void setSpeed(int speedx, int speedy){
        this.speedx = speedx;
        this.speedy = speedy;
    }

    public void doDamage(){
        changeHealthDown();
        
    }

    
    
}
