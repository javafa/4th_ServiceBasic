package com.veryworks.android.servicebasic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class MyService extends Service {
    public MyService() {
    }

    // 컴포넌트는 바인더를 통해 서비스에 접근할 수 있다
    class CustomBinder extends Binder {
        public CustomBinder(){

        }
        public MyService getService(){
            return MyService.this;
        }
    }

    IBinder binder = new CustomBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MyService","========onBind()");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("MyService","========onUnbind()");
        return super.onUnbind(intent);
    }

    public int getTotal(){
        return total;
    }

    private int total = 0;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            switch(action){
                case "START":
                    setNotification("PAUSE");
                    break;
                case "PAUSE":
                    setNotification("START");
                    break;
                case "DELETE":
                    stopForeground(true);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    // 포어그라운드 서비스하기
    // 포어그라운드 서비스 번호
    public static final int FLAG = 17465;

    private void setNotification(String cmd){
        // 포어그라운드 서비스에서 보여질 노티바 만들기
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher) //최상단 스테이터스 바에 나타나는 아이콘
                .setContentTitle("음악제목")
                .setContentText("가수명");
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(icon); // 노티바에 나타나는 큰 아이콘
        // icon.release 필요

        // 노티바 전체를 클릭했을 때 발생하는 액션처리
        Intent deleteIntent = new Intent(getBaseContext(), MyService.class);
        deleteIntent.setAction("DELETE"); // <- intent.getAction에서 취하는 명령어
        PendingIntent mainIntent = PendingIntent.getService(getBaseContext(), 1, deleteIntent, 0);
        builder.setContentIntent(mainIntent);

        /*
           노티에 나타나는 버튼 처리
         */
        // 클릭을 했을때 noti를 멈추는 명령어를 서비스에서 다시 받아서 처리
        Intent pauseIntent = new Intent(getBaseContext(), MyService.class);
        pauseIntent.setAction(cmd); // <- intent.getAction에서 취하는 명령어
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 1, pauseIntent, 0);

        // 노티피케이션에 들어가는 버튼을 만드는 명령
        int iconId = android.R.drawable.ic_media_pause;
        if(cmd.equals("START"))
            iconId = android.R.drawable.ic_media_play;
        String btnTitle = cmd;

        NotificationCompat.Action pauseAction
                = new NotificationCompat.Action.Builder(iconId, btnTitle, pendingIntent).build();
        builder.addAction(pauseAction);
        
        Notification notification = builder.build();
        startForeground(FLAG, notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService","========onCreate()");
    }

    @Override
    public void onDestroy() {

        stopForeground(true); // 포그라운드 상태에서 해제된다. 서비스는 유지

        super.onDestroy();
        Log.d("MyService","========onDestroy()");
    }
}
