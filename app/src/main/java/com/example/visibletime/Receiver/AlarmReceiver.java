package com.example.visibletime.Receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.visibletime.R;
import com.example.visibletime.Ui.MainActivity;
import com.example.visibletime.Ui.WriteDiaryActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {
    private final String tag = "로그";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(tag,"AlarmReceiver - onReceive() |  ");
        boolean[] week = intent.getBooleanArrayExtra("weekday");

        Calendar calendar = Calendar.getInstance();
        Log.d(tag,"AlarmReceiver - onReceive() | Calendar.DAY_OF_WEEK: "+Calendar.DAY_OF_WEEK);
        Log.d(tag,"AlarmReceiver - onReceive() | week[1] "+week[1]);
        Log.d(tag,"AlarmReceiver - onReceive() | week[2] "+week[2]);
        Log.d(tag,"AlarmReceiver - onReceive() | week[3] "+week[3]);
        Log.d(tag,"AlarmReceiver - onReceive() | week[4] "+week[4]);
        Log.d(tag,"AlarmReceiver - onReceive() | week[5] "+week[5]);
        Log.d(tag,"AlarmReceiver - onReceive() | week[6] "+week[6]);
        Log.d(tag,"AlarmReceiver - onReceive() | week[7] "+week[7]);

        // 추가 확인 필요
//        if (!week[calendar.get(Calendar.DAY_OF_WEEK)]) return; // 체크한 요일이 아니면


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        /**Intent에 setFlags를 해주는 이유
         * ■ Task란?
         *       - Stack 구조
         *       - 어플리케이션에서 실행되는 Activity를 보관, 관리
         * □ 사용법
         *       -  AndroidManifest, 소스코드 두 군데서 제어 가능
         *       ● 소스코드 제어
         *       ○ addFlags vs setFlags
         *           - addFlags() : 새로운 flag를 기존 flag에 붙임
         *           - setFlags() : 오래된 flag 전체를 대체
         *      ○ 값
         *          - FLAG_ACTIVITY_CLEAR_TOP :
         *              호출하는 Activity가 스택에 있을 경우
         *              해당 Activity를 최상위로 올리고, 그 위에 있던 Activity들을 모두 삭제
         *              Ex) ABCDE → C call → ABC
         *          - FLAG_ACTIVITY_SINGLE_TOP:
         *              호출되는 Activity가 최상위에 있을 경우 해당 Activity를 다시 생성하지 않고, 있던 Activity를 다시 사용
         * */

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0,notificationIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");


        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d(tag,"AlarmReceiver - onReceive() | 오레오 이상인 경우 ");

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남


            String channelName ="매일 알람 채널";
            String description = "매일 정해진 시간에 알람합니다.";

            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }else {
            Log.d(tag,"AlarmReceiver - onReceive() | 오레오 미만인 경우 ");
            builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
        }


        builder.setAutoCancel(true)     // 알람 터치시 자동으로 삭제
                .setDefaults(NotificationCompat.DEFAULT_ALL)    // 알람발생시 진동, 사운드 설정
                .setWhen(System.currentTimeMillis())            // 알람 시간
                .setTicker("{Time to watch some cool stuff!}")  // 알람 발생시 잠깐 나오는 텍스트
                .setContentTitle("하루 일기를 작성하세요.")
                .setContentText("내용")
                .setContentInfo("INFO")
                .setContentIntent(pendingI);        // 알람 눌렀을 때 실행할 작업 인텐트


        if (notificationManager != null) {

            // 노티피케이션 동작시킴
            notificationManager.notify(1234, builder.build());
            Log.d(tag,"AlarmReceiver - onReceive() | notification 동작 ");

            Calendar nextNotifyTime = Calendar.getInstance();

//            // 내일 같은 시간으로 알람시간 결정
//            nextNotifyTime.add(Calendar.DATE, 1);
//
//            //  Preference에 설정한 값 저장
//            SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
//            editor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
//            editor.apply();

            Date currentDateTime = nextNotifyTime.getTime();
            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context.getApplicationContext(),"다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();
        }
    }
}