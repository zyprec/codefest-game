package com.lhs.bird;

import java.util.ArrayList;

import org.mini2Dx.core.engine.geom.CollisionBox;
import org.mini2Dx.core.graphics.Sprite;

public class Object {
    private int x, y;
    private Sprite sprite;
    private CollisionBox hitbox;
    private ArrayList<Object> collided;

    public Object(int _x, int _y, Sprite _sprite){
        sprite = _sprite;
        x = _x;
        y = _y;
        hitbox = new CollisionBox(x, y, sprite.getWidth(), sprite.getHeight());
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public Sprite getSprite(){
        return this.sprite;
    }

    public CollisionBox getHitbox(){
        return this.hitbox;
    }

    public void addCollided(Object object){
        collided.add(object);
    }

    public void removeCollided(Object object){
        if (collided.remove(object)){
            System.out.print("removed");
        } else {
            System.out.print("error");
        }
    }

    public boolean isCollided(Object object){
        return collided.contains(object);
    }
}
