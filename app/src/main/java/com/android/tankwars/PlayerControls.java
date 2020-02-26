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

    private Button mFire;
    private JoystickView mJoystick;
    private ArrayMap<String, View> mControls;

    PlayerControls(ArrayMap<String, View> controls, PlayerTank playerTank) {
        mPlayerTank = playerTank;
        mControls = controls;

        mFire = (Button) controls.get("fire");
        mJoystick = (JoystickView) controls.get("joystick");

        mFire.setOnTouchListener(buttonListener);
        mJoystick.setOnMoveListener(joystickOnMoveListener);

    }

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
