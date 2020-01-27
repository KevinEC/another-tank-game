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

    PlayerControls (ArrayMap<String, Button> controls, PlayerTank playerTank) {
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
            if(e.getAction() == MotionEvent.ACTION_DOWN) {
                mPlayerTank.setMoving(true);
            } else if(e.getAction() == MotionEvent.ACTION_UP) {
                mPlayerTank.setMoving(false);
            }

            // determine which button is pressed

            if(v.getId() == mLeft.getId()) {
                mPlayerTank.setDirectionState(mPlayerTank.LEFT);
            }
            else if(v.getId() == mRight.getId()) {
                mPlayerTank.setDirectionState(mPlayerTank.RIGHT);
            }
            else if(v.getId() == mUp.getId()) {
                mPlayerTank.setDirectionState(mPlayerTank.UP);
            }
            else if(v.getId() == mDown.getId()) {
                mPlayerTank.setDirectionState(mPlayerTank.DOWN);
            }
            else if(v.getId() == mFire.getId()) {
                mPlayerTank.setDirectionState(mPlayerTank.FIRE);
            }
            // none of the buttons were pressed
            else {
                return false;
            }

            return true;
        }
    };


}
