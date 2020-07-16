package com.example.visibletime.Ui.Fragement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.visibletime.AlarmNotification;
import com.example.visibletime.Data.AlarmData;
import com.example.visibletime.R;
import com.example.visibletime.Ui.EditPasswordActivity;
import com.example.visibletime.Ui.LoginActivity;
import com.example.visibletime.Ui.SettingAlarmActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

public class SettingFragment extends Fragment {

    private final String tag = "로그";

    View root;
    Context context;

    /**알람 해제를 위한 변수*/
    private ArrayList<AlarmData> mArrayList;
    AlarmNotification alarmNotification;
    /**
     * ButterKnife
     */
    @BindView(R.id.logoutBtn_Setting)
    Button logoutBtn;

    @BindView(R.id.alarmBtn_Setting)
    Button alarmBtn;

    @BindView(R.id.changePasswordBtn_Setting)
    Button changePasswordBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(tag,"SettingFragment - onCreateView() |  ");
        root = inflater.inflate(R.layout.fragment_setting, container, false);        // Inflate the layout for this fragment
        ButterKnife.bind(this, root);
        this.context = container.getContext();
        return root;
    }



    /**
     * ButterKnife
     */
    @OnClick(R.id.logoutBtn_Setting)
    public void logOutClicked() {
        // 자동로그인 Shared 값 삭제
        SharedPreferences sharedAutoLogin = context.getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor editorAutoLogin= sharedAutoLogin.edit();
        editorAutoLogin.clear();
        editorAutoLogin.commit();

        Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

        /**알람 해제*/
        getListOfDataFromSharedPreferences();
        requestAlarm();

        // 로그인 화면으로 이동
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @OnClick(R.id.alarmBtn_Setting)
    public void callAlarmActivity() {
     Intent intent = new Intent(getActivity(), SettingAlarmActivity.class);
     startActivity(intent);
    }
    @OnClick(R.id.changePasswordBtn_Setting)
    public void changePasswordBtnClicekd() {
        Intent intent = new Intent(getActivity(), EditPasswordActivity.class);
        startActivity(intent);
    }


    private void getListOfDataFromSharedPreferences(){
//        mArrayList.clear();
        mArrayList = new ArrayList<>();
        SharedPreferences sharedPresesntID = context.getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = context.getSharedPreferences(presentID, MODE_PRIVATE);

        // 값이 있는지 여부 확인하기
        if(sharedPreferences.getString("alarm",null) == null){
            Log.d(tag,"SettingFragment - getListOfDataFromSharedPreferences() | 값 X ");
        } else {
            Log.d(tag,"SettingFragment - getListOfDataFromSharedPreferences() | 값 존재 ");
            try {
                // ● JSONArray 생성
                JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("alarm", null) + "]");
                // ● 반복문(JSONArray.length())
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // ○ JSON에 String으로 저장된 값을 AlarmData 데이터 타입에 맞게 변환을 위한 변수 선언
                    boolean isChecked = false;
                    boolean[] arrayBooleanWeek = new boolean[8];
                    int hour_24 = 0;
                    int hour_12 = 0;
                    int minute = 0;

                    // boolean isChecked를 얻기 위한 데이터 변환
                    if (jsonObject.getString("isChecked").equals("true")) {
                        isChecked = true;
                    }
                    // boolean [] arrayWeek을 얻기 위한 데이터 변환(String > boolean[])
                    String[] arrayStrWeek = jsonObject.getString("arrayWeek").split(",");
                    for (int j = 0; j < arrayStrWeek.length; j++) {
                        if (arrayStrWeek[j].equals("true")) {
                            arrayBooleanWeek[j] = true;
                        }
                    }
                    // int hour_24, hour_12, minute 을 얻기 위한 데이 변환
                    hour_24 = Integer.parseInt(jsonObject.getString("hour_24"));
                    hour_12 = Integer.parseInt(jsonObject.getString("hour_12"));
                    minute = Integer.parseInt(jsonObject.getString("minute"));

                    // ○ ArrayList에 값 넣기
                    // Data 객체 생성 & 값 넣어주기
                    AlarmData alarmData = new AlarmData();
                    alarmData.setChecked(isChecked);
                    alarmData.setName(jsonObject.getString("name"));
                    alarmData.setAm_pm(jsonObject.getString("am_pm"));
                    alarmData.setWeekName(jsonObject.getString("weekName"));
                    alarmData.setHour_24(hour_24);
                    alarmData.setHour_12(hour_12);
                    alarmData.setMinute(minute);
                    alarmData.setArrayWeek(arrayBooleanWeek);
                    // mArrayList에 값 넣어주기
                    mArrayList.add(alarmData);
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.d(tag, "SettingFragment - onLoginBtnClicked() | Exception " + e);
            }
        }
    }
    /**알람 요청하기*/
    private void requestAlarm() {
        for(int i=0; i<mArrayList.size(); i++){
            if(mArrayList.get(i).isChecked()){
                Log.d(tag,"SettingAlarmActivity - requestAlarm() | i: "+i);
                int hour_24, minute;
                boolean [] arrayWeek;
                hour_24 = mArrayList.get(i).getHour_24();
                minute = mArrayList.get(i).getMinute();
                arrayWeek = mArrayList.get(i).getArrayWeek();
                alarmNotification = new AlarmNotification(context,hour_24,minute,arrayWeek,i);
                alarmNotification.cancelAlarm();
            }
        }
    }
}