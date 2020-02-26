package com.android.tankwars;

public class Camera {

    private float x,y;

    Camera(float x, float y){
        this.x = x; this.y = y;
    }

    public void update(GameObject playerTank, float screenX, float screenY) {
        x = playerTank.getX() - screenX / 2.0f + playerTank.getWidth() / 2.0f;
        y = playerTank.getY() - screenY / 2.0f + playerTank.getHeight() / 2.0f;
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }
}
