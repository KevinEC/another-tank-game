package com.android.tankwars;

import android.graphics.Color;
import android.graphics.Paint;

public class Bullet extends GameObject {

    private static int ID = 0;
    private int id;


    Bullet(float x, float y, float direction){
        super(x,y, 4.0f,4.0f);
        id = ID; ID++;
        this.rotation = direction;
        speed = 500.0f;
        color = new Paint();
        color.setColor(Color.argb(255, 255, 100, 100));
    }

    @Override
    public void collision(GameObject otherObject) {
        speed = 0;
        if(otherObject.getClass() == Obstacle.class)
        {
            /* TODO
            * maybe concurrent modification links to here?
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
