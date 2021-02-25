package com.lhs.bird;

import org.mini2Dx.core.engine.geom.CollisionBox;
import org.mini2Dx.core.graphics.Sprite;

public class Object {
    public float x, y;
    public float[] speed;
    public Sprite sprite;
    public CollisionBox hitbox;
    public int ID;

    public Object(float _x, float _y, Sprite _sprite){
        speed = new float[2];
        sprite = _sprite;
        x = _x;
        y = _y;
        hitbox = new CollisionBox(x, y, sprite.getWidth(), sprite.getHeight());
    }
    public Object(float _x, float _y, Sprite _sprite, float width, float height, float speedX){
        speed = new float[2];
        speed[0] = speedX;
        sprite = _sprite;
        this.sprite.setSize(width, height);;
        x = _x;
        y = _y;
        hitbox = new CollisionBox(x, y, width, height);
    }

    public Object(float _x, float _y, Sprite _sprite, int ID){
        speed = new float[2];
        sprite = _sprite;
        x = _x;
        y = _y;
        if(sprite != null){
            hitbox = new CollisionBox(x, y, sprite.getWidth(), sprite.getHeight());
        }
    }

    public float getX(){
        return this.x;
    }

    public float getMaxX(){
        return this.x + this.hitbox.getWidth();
    }

    public void setX(float value){
        this.x = value;
    }

    public float getY(){
        return this.y;
    }

    public float getMaxY(){
        return this.hitbox.getMaxY();
    }

    public void setY(float value){
        this.y = value;
    }

    public Sprite getSprite(){
        return this.sprite;
    }

    public CollisionBox getHitbox(){
        return this.hitbox;
    }

    public void preUpdate(){
        this.hitbox.preUpdate();
    }

    public void updateLocation(){
        this.x += this.speed[0];
        this.y += this.speed[1];
    }
}
