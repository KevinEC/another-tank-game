package com.android.tankwars;

import android.graphics.Color;
import android.graphics.Paint;

public class Bullet extends GameObject {

    private static int ID = 0;
    private int id;


    Bullet(float x, float y, float rotation){
        super(x,y, 20.0f,20.0f);
        id = ID; ID++;
        this.rotation = rotation;
        setDirectionVector();
        speed = 500.0f;
        color = new Paint();
        color.setColor(Color.argb(255, 255, 100, 100));
    }

    @Override
    public void collision(GameObject otherObject) {
        speed = 0;
        if(otherObject.getClass() == Obstacle.class) {
            /* TODO
            * Implement logic here for what to happen if a player is hit by a bullet
            * */
        }
        toBeRemoved.add(this);
    }

    public void update(long fps){
        translate(fps);
    }

    public int getId() {
        return id;
    }


}
