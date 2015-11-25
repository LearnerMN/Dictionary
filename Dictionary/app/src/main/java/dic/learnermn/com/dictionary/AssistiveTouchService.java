package dic.learnermn.com.dictionary;

/**
 * Created by LearnerMN on 11/19/15.
 */

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class AssistiveTouchService extends Service {

    private static final long DELAY_MILLIS = 1000;

    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams mParams;

    View mainView;
    WindowManager.LayoutParams aParams;
    private Button mMouseView;

    private boolean isMoving;

    private Handler mHandler;

    private static float diffParams = 0;
    InputMethodManager _imm;


    AlertDialog alertDialog;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * 创建悬浮窗
     */

    private void createFloatView() {
        mMouseView = new Button(getApplicationContext());

        mHandler = new Handler();

        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();

        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.width = 200;
        mParams.height = 200;
        mParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        mainView = inflater.inflate(R.layout.main, null);

        _imm = (InputMethodManager) getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);

        final EditText editText = (EditText) mainView.findViewById(R.id.input);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.requestFocus()) {
                    Log.e(">>>>>>>>>>: ", "click editText");
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        aParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        aParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

        mMouseView.setOnTouchListener(new OnTouchListener() {
            int lastX, lastY;
            int paramX, paramY;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = mParams.x;
                        paramY = mParams.y;
                        isMoving = false;
                        mMouseView.setBackgroundResource(R.drawable.mouse_button_darker_gray_holo_light);
                        diffParams = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        mParams.x = paramX + dx;
                        mParams.y = paramY + dy;
                        diffParams += Math.abs(dx) + Math.abs(dy);
                        // 更新悬浮窗位置
                        mWindowManager.updateViewLayout(mMouseView, mParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (diffParams > 10) isMoving = true;
                        setMouseColor(true);
                        break;
                }
                return isMoving;
            }
        });



        mMouseView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isMoving) {
                    Log.e(">>>>>>>>>>>: ", "doesn't moved");
                    buttonClick();
                    setMouseColor(true);
                } else {
                    Log.e(">>>>>>>>>>>: ", "moved");
                }
            }
        });

        Button hide_btn = (Button) mainView.findViewById(R.id.hide_btn);
        hide_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mWindowManager.removeView(mainView);
                alertDialog.hide();
                setMouseColor(true);
                mMouseView.setVisibility(View.VISIBLE);
            }
        });

        mWindowManager.addView(mMouseView, mParams);
        setMouseColor(true);

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        alertDialog.setView(mainView);

        final Window dialogWindow = alertDialog.getWindow();
        final WindowManager.LayoutParams dialogWindowAttributes = dialogWindow.getAttributes();

        // Set fixed width (280dp) and WRAP_CONTENT height
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogWindowAttributes);
        lp.width = 500;
        lp.height = 500;
        dialogWindow.setAttributes(lp);

    }

    void buttonClick(){
        alertDialog.show();
//        mWindowManager.addView(mainView, aParams);
        mMouseView.setVisibility(View.GONE);
    }

    private void setMouseColor(boolean touch) {
        if (touch) {
            mMouseView.setBackgroundResource(R.drawable.mouse_button_darker_gray_holo_light);
            mHandler.postDelayed(mRunnable, DELAY_MILLIS);
        } else {
            mMouseView.setBackgroundResource(R.drawable.mouse_button_lighter_gray_holo_light);
        }
    }

    private Runnable mRunnable = new Runnable() {

        public void run() {
            setMouseColor(false);
        }
    };

}