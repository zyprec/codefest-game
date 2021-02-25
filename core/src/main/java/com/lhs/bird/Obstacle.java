package com.lhs.bird;

import org.mini2Dx.core.graphics.Sprite;

public class Obstacle extends Object {

    public Obstacle(int _x, int _y, Sprite _sprite, float speedX) {
        super(_x, _y, _sprite);
        speed[0] = speedX;
    }
}
