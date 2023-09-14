package com.example.testjava;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.testjava.databinding.ActivityMainBinding;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity{
    ActivityMainBinding binding;
    private MyBroadcastReceiver mMyReceiver;   //自訂一個繼承 BroadcastReceiver 的類別

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, MyService.class));
            }
        });

        binding.btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, MyService.class));
            }
        });

        binding.btnStartBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindService(new Intent(MainActivity.this, MyService.class),connection, Context.BIND_AUTO_CREATE);
                //service:该参数通过Intent指定需要启动的service。
                //connection:该参数是ServiceConnnection对象，当绑定成功后，系统将调用serviceConnnection的onServiceConnected ()方法，当绑定意外断开后，系统将调用ServiceConnnection中的onServiceDisconnected方法。
                //flags:该参数指定绑定时是否自动创建Service。如果指定为BIND_AUTO_CREATE，则自动创建；指定为0，则不自动创建。
            }
        });

        binding.btnStopBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindService(connection);
            }
        });


        binding.cbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences=getSharedPreferences("Check",MODE_PRIVATE);
                SharedPreferences.Editor edit=sharedPreferences.edit();
                if (binding.cbRemember.isChecked()){
                    edit.putString("account",binding.txtAccount.getText().toString());

                    edit.putBoolean("Check",true);
                }else{
                    edit.putString("account",null);
                    edit.putBoolean("Check",false);
                }
                edit.apply();
                System.out.println(sharedPreferences.getAll());
            }
        });  //未做完

        SharedPreferences s = getSharedPreferences("Check",MODE_PRIVATE);
        binding.txtAccount.setText(s.getString("account",""));


        binding.btnRegReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentFilter itFilter = new IntentFilter("tw.android.ACTION_01");
                mMyReceiver = new MyBroadcastReceiver();
                registerReceiver(mMyReceiver, itFilter); //註冊廣播接收器
            }
        });

        binding.btnUnregReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unregisterReceiver(mMyReceiver);  //撤銷廣播接收器
            }
        });

        binding.btnSendBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent("tw.android.ACTION_01"); //設定廣播識別碼
                it.putExtra("sender_name", binding.txtAccount.getText().toString()); //設定廣播夾帶參數
                sendBroadcast(it); //發送廣播訊息

            }
        });

        binding.btnLocalBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentFilter itFilter = new IntentFilter("tw.android.ACTION_01");
                mMyReceiver = new MyBroadcastReceiver();
                LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMyReceiver,itFilter);

                Intent it = new Intent("tw.android.ACTION_01"); //設定廣播識別碼
                it.putExtra("sender_name", binding.txtAccount.getText().toString()); //設定廣播夾帶參數
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(it); //發送廣播訊息
            }
        });

        binding.btnUnregLocalBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mMyReceiver);
            }
        });

        //失敗  //LifecycleOwner com.example.testjava.MainActivity@eb00efa is attempting to register while current state is RESUMED. LifecycleOwners must call register before they are STARTED.
//        binding.btnToActivityB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityResultLauncher<String> launcher = registerForActivityResult(
//                        new MainActivityResultContract(),
//                        new ActivityResultCallback<String>() {
//                            @Override
//                            public void onActivityResult(String result) {
//                                Log.d(TAG, result);
//                            }
//                        }
//                );
//
//
//                launcher.launch("hello");
//            }
//        });

        //傳送Serializable物件
        binding.btnToActivityB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomObject co = new CustomObject();
                Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("CustomObject", co);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //unbindService()方法成功后，系统并不会调用onServiceConnected()，因为onServiceConnected()只会在意外断开绑定时才被调用。
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

//    public class MainActivityResultContract extends ActivityResultContract<String, String> {
//
//        /** 傳送 */
//        @NonNull
//        @Override
//        public Intent createIntent(@NonNull Context context, String input) {
//            Intent intent = new Intent(MainActivity.this,  MainActivity2.class);
//            intent.putExtra("login", input);
//            return intent;
//        }
//
//
//        /** 接收回傳 */
//        @Override
//        public String parseResult(int resultCode, @Nullable Intent intent) {
//            if (resultCode != RESULT_OK) {
//                return "error";
//            }
//
//            assert intent != null;
//            return intent.getStringExtra("login_result");
//        }
//    }


}


