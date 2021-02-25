package com.lhs.bird;

import java.util.Scanner;

import org.mini2Dx.core.graphics.Sprite;

public class Player extends Object {

    private int speedx;
    private int speedy;
    private int health;

    public Player(int _x, int _y, Sprite _sprite) {
        super(_x, _y, _sprite);
        int speedx = 100;
        int speedy = 100;
        int health = 5;
    }
    
    public void setSpeed(int speedx, int speedy){
        this.speedx = speedx;
        this.speedy = speedy;
    }

    public int changeHealthDown(int health){
        health -= 1;
        return health;
    }

    public int changeHealthUp(int health){
        health += 1;
        return health;
    }

    public static String playerInput(){
        Scanner reader = new Scanner(System.in);
        System.out.print("User Input: ");
        String output = reader.nextLine();
        return output;
    }

}
