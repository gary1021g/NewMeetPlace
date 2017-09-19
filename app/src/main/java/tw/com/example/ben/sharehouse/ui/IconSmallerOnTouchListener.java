package tw.com.example.ben.sharehouse.ui;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Feng on 2017/8/12.
 */

public class IconSmallerOnTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view.setScaleX(0.9f);
                view.setScaleY(0.9f);
                break;
            case MotionEvent.ACTION_UP:
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                break;
        }

        return false;
    }
}
