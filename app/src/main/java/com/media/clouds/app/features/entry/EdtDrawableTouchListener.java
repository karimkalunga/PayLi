package com.media.clouds.app.features.entry;

import android.annotation.SuppressLint;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.media.clouds.app.R;

public class EdtDrawableTouchListener implements View.OnTouchListener {
    private boolean isShown = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        EditText edt = (EditText) v;
        final int DRAWABLE_RIGHT = 2;

        if(event.getAction() == MotionEvent.ACTION_UP) {
            if(event.getRawX() >= (edt.getRight() - edt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                final int cursor = edt.getSelectionStart();
                if (! isShown) {
                    isShown = true;
                    edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off, 0);

                } else {
                    isShown = false;
                    edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_on, 0);
                }
                edt.setSelection(cursor);
                return true;
            }
        }
        return false;
    }
}
