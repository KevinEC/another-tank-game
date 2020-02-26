package com.android.tankwars;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class TankWarsView extends SurfaceView implements Runnable {
    Context context;

    // View Thread
    private Thread gameThread = null;

    // Surfaceholder used for locking the frame while drawing
    private SurfaceHolder holder;

    // Game State. Running / Not Running
    private Boolean playing;

    // Game State. Paused
    // Initial Game State
    private Boolean paused = false;

    private Canvas canvas;
    private Paint paint;

    private long fps;
    // fps calculate helper
    private long timeThisFrame;

    // Screen size in pixels
    private int screenX, screenY;

    Camera camera;

    //Player Tank
    private PlayerTank playerTank;

    //Control Buttons
    private ArrayMap<String, View> mControls;

    //PlayerControls
    private PlayerControls playerControls;

    // FX
    private SoundPool soundPool;
    private int shootID = -1;

    // Map Obstacles
    private ArrayList<Obstacle> mapObstacles;


    // Default Constructor
    public TankWarsView(Context context){
        super(context);
    }

    public TankWarsView(Context context, int x, int y, ArrayMap<String, View> controls) {
        super(context);
        this.context = context;

        mControls = controls;
        holder = getHolder();
        paint = new Paint();

        screenX = x;  screenY = y;

        mapObstacles = new ArrayList<>();

        // deprecated but still works
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        try {
            // Load assets
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load fx to memory
            descriptor = assetManager.openFd("kaboom.wav");
            shootID = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            Log.e("error", "failed to load sound files");
        }
        prepareLevel();
    }

    private void prepareLevel() {

        playerTank = new PlayerTank(context, screenX, screenY, 1);
        playerControls = new PlayerControls(mControls, playerTank);
        camera = new Camera(screenX / 2.0f,screenY / 2.0f);


        Obstacle screenBorderTop = new Obstacle(0, 0, screenX-200, 5);

        Obstacle screenBorderBot = new Obstacle(0, screenY, screenX, 5);

        Obstacle screenBorderLeft = new Obstacle(0, 0, 5, screenY-100);

        Obstacle screenBorderRight = new Obstacle(screenX, 0, 5, screenY-500);

        mapObstacles.add(new Obstacle(400, 200, 30, 200));
        mapObstacles.add(new Obstacle(1200, 800, 300, 30));
        mapObstacles.add(new Obstacle(1300, 300, 200, 200));
        mapObstacles.add(screenBorderTop);
        mapObstacles.add(screenBorderBot);
        mapObstacles.add(screenBorderLeft);
        mapObstacles.add(screenBorderRight);

        GameObject.allGameObjects.addAll(mapObstacles);
    }

    @Override
    public void run() {
        while (playing) {

            // Get current time in milliseconds
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if (!paused) {
                update(fps);
            }
            // Draw the frame
            draw();

            // Calculate current frame rate for timing animations
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update(long fps) {
        boolean lost = false;

        updateGameObjects(fps);
        if (lost) {
            prepareLevel();
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            // color background and set brush color
            canvas.drawColor(Color.argb(255, 20, 20, 20));
            paint.setColor(Color.argb(255, 255, 255, 255));

            // draw player
            //canvas.drawBitmap(playerTank.getBitmap(), playerTank.getX(), playerTank.getY(), paint);

            //draw GameObjects
            for(GameObject object: GameObject.allGameObjects) {
                // prepare canvas for transformations
                canvas.save();
                // translate GameObject by camera coordinates
                canvas.translate(-camera.getX(), -camera.getY());
                // rotate GameObject
                canvas.rotate(object.getRotation(), object.getRect().centerX(), object.getRect().centerY());
                // draw GameObject in transformed state
                object.draw(canvas);
                // restore the canvas transformation
                canvas.restore();
            }

            // Draw everything to the screen
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void updateGameObjects(long fps) {
        // add GamObjects here to avoid ConcurrentModificationException.
        GameObject.allGameObjects.addAll(GameObject.toBeAdded);
        GameObject.toBeAdded.clear();
        Collections.sort(GameObject.allGameObjects);
        camera.update(playerTank, screenX, screenY);
        for (GameObject object : GameObject.allGameObjects){
            object.update(fps);
        }
        // remove GameObjects here to avoid ConcurrentModificationException.
        GameObject.allGameObjects.removeAll(GameObject.toBeRemoved);
        GameObject.toBeRemoved.clear();
    }
}
