package com.lhs.bird;

public class Button {
    private float x, y, width, height;
    private int ID;
    private String text;

    public Button (float x, float y, float width, float height, int ID, String text){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.ID = ID;
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getWidth(){
        return width;
    }

    public float getHeight(){
        return height;
    }
    
    public int getID(){
        return ID;
    }

    public float CenterX(){
        return (this.x + this.x + this.width)/2f;
    }

    public float CenterY(){
        return (this.y + this.y + this.height)/2f;
    }
}
