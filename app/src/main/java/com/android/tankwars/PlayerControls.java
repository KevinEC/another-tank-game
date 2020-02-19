package com.android.tankwars;

import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class PlayerControls {

    private PlayerTank mPlayerTank;

    private Button mLeft;
    private Button mRight;
    private Button mUp;
    private Button mDown;
    private Button mFire;
    private JoystickView mJoystick;
    private LinearLayout mControlsView;
    private ArrayMap<String, View> mControls;

    PlayerControls(ArrayMap<String, View> controls, PlayerTank playerTank) {
        mPlayerTank = playerTank;
        mControls = controls;

        mLeft = (Button) controls.get("left");
        mRight = (Button)controls.get("right");
        mUp = (Button) controls.get("up");
        mDown = (Button) controls.get("down");
        mFire = (Button) controls.get("fire");
        mJoystick = (JoystickView) controls.get("joystick");
        mControlsView = (LinearLayout) controls.get("controlArea");

        mLeft.setOnTouchListener(buttonListener);
        mRight.setOnTouchListener(buttonListener);
        mUp.setOnTouchListener(buttonListener);
        mDown.setOnTouchListener(buttonListener);
        mFire.setOnTouchListener(buttonListener);

        mJoystick.setOnMoveListener(joystickOnMoveListener);
        mControlsView.setOnTouchListener(controlAreaTouchListener);

    }



    private LinearLayout.OnTouchListener controlAreaTouchListener = new LinearLayout.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            Log.d("joystick", "touch registered in linear layout");
            if (e.getAction() == MotionEvent.ACTION_DOWN)
                mPlayerTank.setMoving(true);
            else if (e.getAction() == MotionEvent.ACTION_UP)
                mPlayerTank.setMoving(false);
            return true;
        }
    };

    private JoystickView.OnMoveListener joystickOnMoveListener = new JoystickView.OnMoveListener() {
        @Override
        public void onMove(int angle, int strength) {
            if(strength > 0) mPlayerTank.setRotation(-angle);
            mPlayerTank.setSpeedFactor(strength);
        }
    };

    private View.OnTouchListener buttonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent e) {

            // set playerTank move state determined on touch action
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                if (v.getId() == mFire.getId()) {
                    mPlayerTank.setFireInput(true);
                    Log.d("fire", "fireInput set to true");
                }
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                if (v.getId() == mFire.getId()) {
                    mPlayerTank.setFireInput(false);
                }
            }


            return true;
        }
    };


}
