package com.example.visibletime.Service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.visibletime.R;
import com.example.visibletime.Ui.MainActivity;

import javax.security.auth.callback.CallbackHandler;

public class MeasureTimeService extends Service {

    private final String tag = "로그";

    public static final int MSG_CLIENT_CONNECT = 1;
    public static final int MSG_CLIENT_DISCONNECT = 2;
    public static final int MSG_TIME_VALUE = 3;
    public static final int MSG_PAUSE_VALUE = 5;
    public static final int MSG_RESTART_VALUE = 6;

    /**
     * Activity 통신을 위한 메신져 선언
     */
    private Messenger mClientCallback = null;//    private ArrayList<Messenger> mClientCallbacks = new ArrayList<Messenger>();     // Activity에 값을 전달
    final Messenger mMessenger = new Messenger(new CallbackHandler());     // 액티비티가 서비스에 이벤트를 전달하면 그때 반응을 하는 메신져 객체

    /**
     * 시간 쓰레드
     */
    private Thread timeThread = null;
    private Boolean isRunning = true;     // TimeThread 통제용(일시정지 기능)

    int startTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(tag, "MeasureTimeService - onBind() |  ");
        Log.d(tag, "MeasureTimeService - onBind() | intent: " + intent);
        Log.d(tag, "MeasureTimeService - onBind() | mMessenger.getBinder() : " + mMessenger.getBinder());
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(tag, "MeasureTimeService - onStartCommand() |  ");
        super.onStartCommand(intent, flags, startId);

        startForegroundService();
        Log.d(tag, "MeasureTimeService - onStartCommand() | startForegroundService() called ");

        timeThread = new Thread(new TimeThread());
        Log.d(tag, "MeasureTimeService - onCreate() | 시간 쓰레드 설정 ");
        timeThread.start();
        Log.d(tag, "MeasureTimeService - onCreate() | Thread 시작 ");
        return START_NOT_STICKY;
    }

    /**
     * 포어그라운드 실행
     */

    private void startForegroundService() {
        Log.d(tag, "MeasureTimeService - startForegroundService() |  ");

        // Notification 눌렀을 때 실행할 작업 인텐트 생성
        // notification 중복 문제는 flag로 해결이 안되서, manifest singleTask로 구현함
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // default 채널 ID로 알림 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Visible Time");
        builder.setContentText("시간 측정중");
        builder.setContentIntent(pendingIntent);        // Notification 눌렀을 때 실행할 작업 인텐트 연결

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(tag, "MeasureTimeService - startForegroundService() | 오레오 이상 ");
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Log.d(tag, "MeasureTimeService - startForegroundService() | a NotificationManager 생성 | manager: " + manager);
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
            Log.d(tag, "MeasureTimeService - startForegroundService() | void manager.createNotificationChannel called ");
        }
        // 포그라운드로 시작
        startForeground(1, builder.build());
        Log.d(tag, "MeasureTimeService - startForegroundService() | startForeground(1, builder.build()) called ");
    }

    private class CallbackHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CLIENT_CONNECT:
                    Log.d(tag, "MeasureTimeService - CallbackHandler - handleMessage() | handleMessage( Message msg ) == MSG_CLIENT_CONNECT ");
                    mClientCallback = msg.replyTo;
                    startTime = msg.arg1;
                    Log.d(tag,"CallbackHandler - handleMessage() | startTime ");
                    break;
                case MSG_CLIENT_DISCONNECT:
                    Log.d(tag, "MeasureTimeService - CallbackHandler - handleMessage() | handleMessage( Message msg ) == MSG_CLIENT_DISCONNECT ");
                    mClientCallback = null;
                    break;
                case MSG_PAUSE_VALUE:
                    Log.d(tag, "MeasureTimeService - CallbackHandler - handleMessage() | handleMessage( Message msg ) == MSG_PAUSE_VALUE ");
                    isRunning = false;      //시간 쓰레드 일시중지
                    break;
                case MSG_RESTART_VALUE:
                    Log.d(tag, "MeasureTimeService - CallbackHandler - handleMessage() | handleMessage( Message msg ) == MSG_RESTART_VALUE ");
                    isRunning = true;       //시간 쓰레드 시작
                    break;

            }
        }
    }


    /**
     * ■ Handler 객체
     * handleMessage(Message)
     *      - Looper에게 받은 Message를 처리하는 메서드
     */
    @SuppressLint("HandlerLeak")
    Handler handlerTime = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int time = msg.arg1;
            Log.d(tag, "MeasureTimeService - handleMessage() | 핸들러 측정 시간: "+time);
            /*
            작업스레드가 메인스레드와 완전히 분리되어 있어서 메인스레드에서 생성한 핸들러를 작업스레드에서
            직접 참조 할수 없을때, Message 생성자 대신 obtain() 메소드 메세지를 생성하여 보내줄수도 있습니다.
            */
            // Message.obtain 메소드로 메세지 생성
            Message time_msg = Message.obtain(null, MeasureTimeService.MSG_TIME_VALUE);
            time_msg.arg1 = time;
            time_msg.arg2 = startTime;
            try {
                if (mClientCallback != null) {
                    mClientCallback.send(time_msg);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * TimeThread
     * 메세지 송신 스레드.
     * <p>
     * ■ Message 객체
     * What: 핸들러를 사용하여 데이터를 보내기 위해서, 데이터 내용을 가지고 있는 식별자
     * 메세지 객체를 핸들러가 sendMessage(message 인스턴스)메서드로 자신의 속해 있는 Thread의
     * MessageQueue로 보내고 순차적으로
     * 루퍼가 Message의 담당 Handler에게 Message를 전달하게 된다
     */
    public class TimeThread implements Runnable {
        @Override
        public void run() {
            Log.d(tag, "TimeThread - run() |  ");
            int i = 0;
            while (true) {
                while (isRunning) { //btnPause 누르면 멈춤

                    Message msg = new Message();
                    msg.arg1 = i++;
                    handlerTime.sendMessage(msg);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return; // 인터럽트 받을 경우 return
                    }
                }
            }
        }
    }
}
