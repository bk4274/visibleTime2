package com.example.visibletime.Ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.visibletime.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateAlarmActivity extends AppCompatActivity {

    private final String tag = "로그";
    
    @BindView(R.id.linearLayoutWeek_Alarm)
    LinearLayout linearLayoutWeek;
    @BindView(R.id.weekRepeated_Alarm)
    TextView weekRepeated;
    @BindView(R.id.enterBtn_Alarm)
    Button enterBtn;
    @BindView(R.id.timePicker_Alarm)
    TimePicker timePicker;
    @BindView(R.id.nameTextView_CreateAlarm)
    TextView nameTextView;
    /**요일*/
    boolean[] week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag,"CreateAlarmActivity - onCreate() |  ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);
        ButterKnife.bind(this);

        // 0 = false로 넣음. 그 이유는, 알람매니저가 월요일을 1로 세기 때문.
        week = new boolean[8];
    }

    /**요일 선택 다이얼로그 띄우기
     *
     * 인자로, 배열과, TextView로 넣어줌.
     * 배열은 요일별 반복정도를 알기 위함.
     * Text는 사용자에게 알려주기 위함.
     * */
    @OnClick(R.id.linearLayoutWeek_Alarm)
    void choiceWeek(){
        ChoiceWeekDialog choiceWeekDialog = new ChoiceWeekDialog(this, week, weekRepeated);
        choiceWeekDialog.show();
    }

    /**알람 생성
     * ■ JSON에 들어갈 값 구하기
     *      ● boolean 체크 여부
     *      ● String, 알람 이름
     *      ● int 시,분
     *      ● boolean [] 반복할 요일
     *      ● 오전, 오후
     *      ● 요일
     *
     * ■ Shared .xml 파일 만들기
     * SharedPreference(presentID)를 통해 현재 아이디를 불러와 아이디를 파일명으로 하는 Shared 파일 만들기
     *
     * ■ JSON 객체 만들기
     * 처음으로 저장하는지 구분하여 저장
     * */
    @OnClick(R.id.enterBtn_Alarm)
    void createAlarm(){

        // ■ JSON에 들어갈 값 구하기
        // 알람 이름
        String isChecked = "true";
        String name = nameTextView.getText().toString();
        if(name.length() == 0){
            name = "알람";
        }
        // 반복 요일
        String weekName = weekRepeated.getText().toString();
        // 시, 분 변수 선언
        int hour_12,  hour_24, minute;
        String am_pm;
        // 사용자가 TimePicker에 선택한 숫자를 시_24,분 변수에 집어 넣음
        if (Build.VERSION.SDK_INT >= 23) {
            hour_24 = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour_24 = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }
        // 선택받은 '시_24'로 오전 오후 구분, '시_12'로 값 저장
        if (hour_24 > 12) {
            am_pm = "오후";
            hour_12 = hour_24 - 12;
        } else {
            hour_12 = hour_24;
            am_pm = "오전";
        }


        // ■ Shared .xml 파일 만들기
        // 해당 ID 불러오기
        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);
        if(presentID != null){
            // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
            SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
            SharedPreferences.Editor editorAlarm = sharedPreferences.edit();

            /**기존에 잘못 입력된 Shared 값 지우기*/
//            editorAlarm.remove("alarm");
//            editorAlarm.commit();

            /**JSON 형태로 저장*/
            String strDataOfJSON;
            strDataOfJSON = "{ \"isChecked\"" + ":" + "\"" + isChecked + "\"" + ","
                    + "\"name\"" + ":" + "\"" + name + "\""+ ","
                    + "\"am_pm\"" + ":" + "\"" + am_pm + "\""+ ","
                    + "\"weekName\"" + ":" + "\"" + weekName + "\""+ ","
                    + "\"hour_24\"" + ":" + "\"" + hour_24 + "\""+ ","
                    + "\"hour_12\"" + ":" + "\"" + hour_12 + "\""+ ","
                    + "\"minute\"" + ":" + "\"" + minute + "\""+ ","
                    + "\"arrayWeek\"" + ":" + " \"" + week[0] + "," + week[1] + ","+ week[2] + ","+ week[3] + ","+ week[4] + ","+ week[5] + ","+ week[6] + ","+ week[7] + "\""
                    + "}";

            // □ 값을 처음으로 저장하는 확인
            String alarm = sharedPreferences.getString("alarm", null);

            if (alarm == null) {
                // 첫 회원정보를 저장하는 경우
                editorAlarm.putString("alarm", strDataOfJSON);
                editorAlarm.commit();
                Toast.makeText(CreateAlarmActivity.this, "알람 생성", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//                intent.putExtra("isChanged", true);
//                setResult(RESULT_OK, intent);
                finish();
            } else {
                // 회원정보 저장이 처음이 아닌 경우
                editorAlarm.putString("alarm", sharedPreferences.getString("alarm", "") + "," + strDataOfJSON);
                editorAlarm.commit();
                Toast.makeText(CreateAlarmActivity.this, "알람 생성", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//                intent.putExtra("isChanged", true);
//                setResult(RESULT_OK, intent);
                finish();
            }

        } else{
            Toast.makeText(CreateAlarmActivity.this, "알람 설정을 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
