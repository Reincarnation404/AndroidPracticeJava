package com.example.testjava;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.testjava.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {

    ActivityMain2Binding binding;
    final String TAG = "ActivityWithFragment";

    private BackgroundThread bgThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 接收 MainActivity 傳過來的 string
//        String str = getIntent().getStringExtra("login");
//        Log.d(TAG, str);
//
//
//        // 將結果 string 回傳給 MainActivity
//        Intent intent = new Intent();
//        intent.putExtra("login_result", "world");
//        setResult(RESULT_OK, intent);
//        finish();


        CustomObject mCustomObject = null;
        mCustomObject = (CustomObject) getIntent().getSerializableExtra("CustomObject");
        System.out.println("putSerializable= "+mCustomObject.toString());


        binding.switchColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSwitchChanged(isChecked);
            }
        });



    }


    //忘記寫所出現的錯誤: Attempt to read from field 'android.os.Handler com.example.testjava.MainActivity2$BackgroundThread.bgHandler' on a null object reference
    @Override
    protected void onResume() {
        super.onResume();
        bgThread = new BackgroundThread();
        bgThread.start();
        System.out.println("bgThread start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.switchColor.setChecked(false);
        bgThread.interrupt();
        System.out.println("bgThread end");
    }

    private void onSwitchChanged(boolean isChecked) {
        // When the checked state of the switch changes we need to send a message to the background thread, either
        // because it needs to start "doing work" ot it needs to stop "doing work".

        // Ask the background thread's handler for a message instance.
        Message messageToBG = this.bgThread.bgHandler.obtainMessage();
        // Set the correct value for the what field so that it knows what this message is about.
        messageToBG.what = isChecked ? BackgroundThread.Work : BackgroundThread.Stop;

        // Deliver the message to the background thread. This isn't handled immediately, the Handler actually puts
        // it in that thread's message queue.
        this.bgThread.bgHandler.sendMessage(messageToBG);
    }

    //把message傳到main thread  //負責UI更新
    @SuppressLint("HandlerLeak")
    private Handler uiThreadHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            // Get the color that was sent to us.
            int color = msg.arg1;
            // Set it. this can only be done on the Main Thread.
            binding.colorBG.setBackgroundColor(color);
        }
    };

    public class BackgroundThread extends Thread{
        //設定顏色播放的秒數
        private static final int sec = 1000;

        private static final int Work = 1;
        private static final int Stop = 0;

        private Handler bgHandler;


        @Override
        public void run() {
            Looper.prepare();

            this.bgHandler = new Handler(Looper.getMainLooper()){

                @Override
                public void handleMessage(@NonNull Message msg) {
                    switch (msg.what){
                        case Stop:
                            //如果是stop，取消work的message
                            bgHandler.removeMessages(Work);
                            break;
                        case Work:
                            //得到要傳送到main thread的message
                            Message colorUI = MainActivity2.this.uiThreadHandler.obtainMessage();
                            //把顏色給message
                            colorUI.arg1 = getRandomColor();
                            //message傳到main thread
                            MainActivity2.this.uiThreadHandler.sendMessage(colorUI);

                            System.out.println(colorUI);


                            this.sendEmptyMessageDelayed(Work, sec);
                            break;

                    }

                }
            };
        }
    }

    private int getRandom() {
        return (int) (Math.random() * 256);
    }

    private int getRandomColor() {
        // Create a random fully opaque color packed in an int.
        return 0xFF000000 | (getRandom() << 16) | (getRandom() << 8) | getRandom();
    }

}