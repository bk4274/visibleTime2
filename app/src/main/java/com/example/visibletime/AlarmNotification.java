package com.example.visibletime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.example.visibletime.Receiver.AlarmReceiver;
import com.example.visibletime.Receiver.DeviceBootReceiver;

import java.util.Calendar;
import java.util.Random;

public class AlarmNotification {
    private final String tag = "로그";

    Context context;
    int hour_24, minute;
    boolean [] arrayWeek;
    int REQUEST_CODE;

    public AlarmNotification(Context context, int hour_24, int minute, boolean[] arrayWeek, int requestCode) {
        this.context = context;
        this.hour_24 = hour_24;
        this.minute = minute;
        this.arrayWeek = arrayWeek;
        REQUEST_CODE = requestCode;
    }

    /** 기본 로직
     *
     * ■ 휴대폰 껐다 켜져도 울리게 하는 코드
     *
     * ■ 알림 설정
     *      - intent 생성 (수신자: BroadCastReceiver를 상속받는 Component)
     *      - pendingIntent 생성
     * */
    public void setAlarm(){
        Log.d(tag,"CreateAlarmActivity - diaryNotification() |  ");


        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour_24);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
        if (calendar.before(Calendar.getInstance())) {
            Log.d(tag,"CreateAlarmActivity - onClick() | 다음날 알람 ");
            calendar.add(Calendar.DATE, 1);
        }

        Boolean dailyNotify = true; // 무조건 알람을 사용

        /**휴대폰을 껐다 켜도 울리게 하기 위한 코드*/
        /*
        PackageManager 객체 생성
        ComponentName receiver 객체생성
        */

        PackageManager pm = context.getPackageManager();

        // DeviceBootReceiver 실행
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);

        /**알람 설정*/
        /*
        ■ intent 생성
            -  알람을 구현한 Class를 요청하는 intent
        ■ PendingIntent 생성
            □ 개념
                -  PendingIntent 는 인텐트를 전송하고자 하는 '송신자'가 인텐트를 하나 생성한 후, 별도의 컴포넌트에게
                '이 인텐트를 나중에 나 대신 보내 주렴.' 하고 전달하고자 할 때 사용되는 클래스
                - 즉, 내가 친구에게 은행 통장에서 돈을 대신 뽑아달라고 부탁하며, 뽑을 돈의 액수를 알려주고 (인텐트),
                내 카드를 빌려주는 것과 비슷한 개념
                - 출처: https://emong.tistory.com/208 [에몽이]

            - Notification은 안드로이드 시스템의 NotificationManager가 intent를 실행
            - 다른 프로세스에서 수행하기 때문에 PendingIntent가 필요하다.
            */

        // AlarmReceiver Intent 생성
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("weekday", arrayWeek);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
                 /*
                 ■ AlarmManager.RTC_WAKEUP
                       : 인자로 넘겨진 시간을 기준으로 알람이 동작하여 pendingIntent를 전달
                 ■ AlarmManager.INTERVAL_DAY : 하루 간격으로 실행.
                         AlarmManager.INTERVAL_FIFTEEN_MINUTES : 15분마다 실행
                         AlarmManager.INTERVAL_HALF_HOUR : 30분 마다 실행
                         AlarmManager.INTERVAL_HOUR
​                         AlarmManager.INTERVAL_DAY
                 */
                Log.d(tag,"CreateAlarmActivity - diaryNotification() | alarmManager.setRepeating called ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 현재 장치가 마시멜로보다 높은지 여부를 물음.
                    // 마시멜로 이상부터는 Doze모드가 추가되어 setExact()로 정확한 시간을 보장할 수 없어, 새로 setExactAndAllowWhileIdle가 추가됨
                    // Doze 모드란, 시스템은 배터리를 절약하기 위해 절약모드로 실행되는 것
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Log.d(tag,"CreateAlarmActivity - diaryNotification() | alarmManager.setExactAndAllowWhileIdle called ");
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }
    }

    public void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);
        alarmManager.cancel(pIntent);
    }
}
