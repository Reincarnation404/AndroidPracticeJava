package com.example.testjava;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

//組件要去AndroidManifest.xml註冊
public class MyService extends Service {


    @Nullable
    @Override  //必須實作
    //返回的IBinder对象相当于Service组件的代理对象，Service允许其他程序组件通过IBinder对象来访问Service内部数据
    public IBinder onBind(Intent intent) {
        System.out.println("MyService.onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("service啟動成功");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("service停用成功");
    }



    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("MyService.onUnbind");
        return super.onUnbind(intent);
    }
}
