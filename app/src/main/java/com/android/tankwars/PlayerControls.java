package com.android.tankwars;

import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class PlayerControls {

    private PlayerTank mPlayerTank;

    private Button mLeft;
    private Button mRight;
    private Button mUp;
    private Button mDown;
    private Button mFire;
    private ArrayMap<String, Button> mControls;

    PlayerControls(ArrayMap<String, Button> controls, PlayerTank playerTank) {
        mPlayerTank = playerTank;
        mControls = controls;
        mLeft = controls.get("left");
        mRight = controls.get("right");
        mUp = controls.get("up");
        mDown = controls.get("down");
        mFire = controls.get("fire");

        mLeft.setOnTouchListener(buttonListener);
        mRight.setOnTouchListener(buttonListener);
        mUp.setOnTouchListener(buttonListener);
        mDown.setOnTouchListener(buttonListener);
        mFire.setOnTouchListener(buttonListener);
    }

    private View.OnTouchListener buttonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent e) {

            Log.d("PlayerControls", "touch registered");

            // set playerTank move state determined on touch action
            if (e.getAction() == MotionEvent.ACTION_DOWN) {

                // determine which button is pressed
                if (v.getId() == mLeft.getId()) {
                    mPlayerTank.setMoving(true);
                    mPlayerTank.setPlayerInput(mPlayerTank.LEFT);
                } else if (v.getId() == mRight.getId()) {
                    mPlayerTank.setMoving(true);
                    mPlayerTank.setPlayerInput(mPlayerTank.RIGHT);
                } else if (v.getId() == mUp.getId()) {
                    mPlayerTank.setMoving(true);
                    mPlayerTank.setPlayerInput(mPlayerTank.UP);
                } else if (v.getId() == mDown.getId()) {
                    mPlayerTank.setMoving(true);
                    mPlayerTank.setPlayerInput(mPlayerTank.DOWN);
                }

                // should be able to be pressed simultaneously
                if (v.getId() == mFire.getId()) {
                    mPlayerTank.setFireInput(true);
                }
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                if (v.getId() == mLeft.getId() || v.getId() == mRight.getId() ||
                        v.getId() == mUp.getId() || v.getId() == mDown.getId()) {
                    mPlayerTank.setMoving(false);
                }


                if (v.getId() == mFire.getId()) {
                    mPlayerTank.setFireInput(false);
                }
            }


            return true;
        }
    };


}
