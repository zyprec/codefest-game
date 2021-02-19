package com.lhs.bird;

import org.mini2Dx.core.graphics.Sprite;

public class Enemy extends Object {

    float maxSpeed = 5f;
    Player player;

    public Enemy(int _x, int _y, Sprite _sprite, float _mSpeed, Player _player) {
        super(_x, _y, _sprite);
        maxSpeed = _mSpeed;
        player = _player;
    }
     
}
