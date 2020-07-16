package com.example.visibletime.Ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Adapter.AlarmAdapter;
import com.example.visibletime.AlarmNotification;
import com.example.visibletime.Data.AlarmData;
import com.example.visibletime.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingAlarmActivity extends AppCompatActivity {

    private final String tag = "로그";

    /**
     * ButterKnife
     */
    @BindView(R.id.floatingActionButton_Alarm)
    FloatingActionButton floatingActionBtn;

    @BindView(R.id.showLayout_Alarm)
    LinearLayout showLayout;

    /**
     * RecyclerView
     */
    private RecyclerView mRecyclerView;
    private AlarmAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<AlarmData> mArrayList;

    private SparseBooleanArray sparseBooleanArray;

    /**알람*/
    AlarmNotification alarmNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_alarm);
        ButterKnife.bind(this);

        setRecyclerView();
        showLayout.setVisibility(View.GONE);
    }

    /**알람 생성하기*/
    @OnClick(R.id.floatingActionButton_Alarm)
    void createAlarm(){
        Intent intent = new Intent(SettingAlarmActivity.this, CreateAlarmActivity.class);
        startActivity(intent);
//        startActivityForResult(intent, DATA_CHAENGED);
    }

    /**
     * 인증 여부 확인
     * 근본적인 문제 해결이 안되서 주석처리함.
     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
//        super.onActivityResult(requestCode, resultCode, resultIntent);
//        if (requestCode == DATA_CHAENGED) {
//            if (resultCode == Activity.RESULT_OK) {
//                boolean isChanged = resultIntent.getBooleanExtra("isChanged", false);
//                Log.d(tag, "SettingAlarmActivity - onActivityResult() | isCertified: " + isChanged);
//                if (isChanged) {
//                    getListOfDataFromSharedPreferences();
//                } else {
//                }
//            }
//        }
//    }

    /**
     * 《리사이클러뷰 프로세스》
     * ■ setRecyclerView()
     * □ 목적: 리사이클러뷰 생성하여 View단에 보여주기
     * 1. RecyclerView, Adapter,(Linear)LayoutManager 생성
     * 2. RecyclerView 객체와 Adapter, (Linear)LayoutManager 객체 연결 : set()
     */
    private void setRecyclerView() {
        Log.d(tag,"SettingAlarmActivity - setRecyclerView() |  ");


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_Alarm);  //RecyclerView 객체 초기화(initiate)
        mArrayList = new ArrayList<>();         // RecycelerView에 들어갈 Data묶음 List
        mAdapter = new AlarmAdapter(this, mArrayList);    // Custom Adater 객체 생성 & Adapter와 mArrayList(Data List) 연결.
        mRecyclerView.setAdapter(mAdapter);             // RecyclerView와 Adater와 연결
        mLayoutManager = new LinearLayoutManager(this);     // RecyclerView를 어떻게 보여줄지 결정
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);


        getListOfDataFromSharedPreferences();
        // 데이터 갱신
        mAdapter.notifyDataSetChanged();
//        //child View들을 구분선을 만들어 주는 메서드
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    /*값을 갱신하기 위해서 본래는 생명주기 내부에 갱신 메서드를 넣었지만 다음과 같은 문제가 발생함.
    *
    * ● Adapter에서 들어가는 데이터 가운데 CheckBox 상태를 기억하기 위한 boolean isChecked 변수 값이 존재함.
    * ● Adapter에서 사용자가 선택에 따라 체크 박스가 달라지기 위해서는 mArrayList에 있는 boolean isChecked 값을
    * 곧이 곧대로 집어 넣으면 체크 박스가 반응을 안하고 고정됨.
    * ● 이를 해결하기 위해서는 간접적으로 체크 상태를 저장하고, 상태에 따라 체크 박스 모습을 달라지게 해야 함.
    * ★★★ 근데 문제가 발생한게, 화면이 꺼졌다가, 켜지니까 앞선 과정이 통제가 안됨(고정된 mArrayList에 있는 boolean isChecked 값
    * 때문에 상태가 반전 되는 현상)
    * ● 그래서 데이터 갱신을 생명주기로 하는 것이 아니라, 콜백 메서드로 일정 조건에만 일어나도록 해야 한다.
    * ● 일정 조건이란, 새로운 데이터가 생성되었을 때, 갱신 되었을 때이다.
    * ★★★ 근본적인 문제 해결이 안됨.
    * */
    @Override
    protected void onStart() {
        Log.d(tag,"SettingAlarmActivity - onStart() |  ");
        super.onStart();
        getListOfDataFromSharedPreferences();
        // 데이터 갱신
        mAdapter.notifyDataSetChanged();
    }

//    @Override
//    protected void onResume() {
//        Log.d(tag,"SettingAlarmActivity - onResume() |  ");
//        super.onResume();
//        getListOfDataFromSharedPreferences();
//    }


    @Override
    protected void onPause() {
        super.onPause();
        editCheckBox();     // 체크 박스 수정
        setDataAfterEditing();  // 데이터 수정
        getListOfDataFromSharedPreferences();   // 데이터 갱신
        requestAlarm();         // 알람 요청하기
    }

    private void editCheckBox() {
        /**■ Adpate에서 생성한 sparseBooleanArray 값 불러오기
         *
         * ■ sparseBooleanArray에 저장된 position 값 리스트에 저장
         *
         * ■ 리스트에 저장된 position에 맞게 checkBox 변경
         * */
        sparseBooleanArray = mAdapter.getCheckBoxSelected();
        Log.d(tag,"SettingAlarmActivity - editCheckBox() | sparseBooleanArray.size(): "+sparseBooleanArray.size());
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i=0; i<sparseBooleanArray.size(); i++){
            arrayList.add(sparseBooleanArray.keyAt(i));     // 해당 position에 있는 값만 저장.
            Log.d(tag, "SettingAlarmActivity - editCheckBox() | sparseBooleanArray.keyAt() " + sparseBooleanArray.keyAt(i));
//            Log.d(tag, "SettingAlarmActivity - onPause() | sparseBooleanArray.indexOfValue(i): " + sparseBooleanArray.indexOfValue(true));
//            Log.d(tag, "SettingAlarmActivity - onPause() | sparseBooleanArray.indexOfKey(i) "+sparseBooleanArray.indexOfKey(i));
//            Log.d(tag,"SettingAlarmActivity - onPause() | sparseBooleanArray.get(i) "+sparseBooleanArray.get(i));
        }
        Log.d(tag,"SettingAlarmActivity - editCheckBox() | arrayList.size(): "+arrayList.size());
        /**처음에는 다 false로 초기화*/
        for(int i = 0; i<mArrayList.size(); i++){
            mArrayList.get(i).setChecked(false);
        }
        /**체크 되어 있는 값만 true값으로 저장*/
        for(int i = 0; i<arrayList.size(); i++){
            Log.d(tag,"SettingAlarmActivity - editCheckBox() | arrayList.get(i): "+arrayList.get(i));
            mArrayList.get(arrayList.get(i)).setChecked(true);
        }
        // 잘 바뀌었는지 확인
        for(int i = 0; i<mArrayList.size(); i++){
            Log.d(tag,"SettingAlarmActivity - editCheckBox() | mArrayList.get(i).isChecked() "+i + mArrayList.get(i).isChecked());
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
                alarmNotification = new AlarmNotification(this,hour_24,minute,arrayWeek,i);
                alarmNotification.setAlarm();
            }
        }
    }

    /**리사이클러뷰에 보여줄 데이터 리스트 만들기
     *
     * ■ 데이터 있는지 여부 확인
     *      - 없다면, 현재 값이 없다는 내용을 가진 Layout(showLayout) 띄우기
     *      - 있다면, Layout(showLayout) 사라지게 하기
     *
     * ■ Shared
     *      ● Shared에 저장된 현재 아이디를 불러온다.
     *      ● 아이디에 해당하는 Shared파일을 불러온다.
     * ■ JSONArray
     *      ● JSONArray 생성
     *      ● 반복문(JSONArray.length())
     *          ○ JSON 객체 생성
     *          ○ String으로 저장된 값 변환
     *                  - String > boolean
     *                  - String > int
     *          ○ ArrayList에 값 넣기
     *
     * */
    private void getListOfDataFromSharedPreferences(){
        mArrayList.clear();

        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
        
        // 값이 있는지 여부 확인하기
        if(sharedPreferences.getString("alarm",null) == null){
            Log.d(tag,"SettingAlarmActivity - getListOfDataFromSharedPreferences() | 값 X ");
            showLayout.setVisibility(View.VISIBLE);
        } else {
            Log.d(tag,"SettingAlarmActivity - getListOfDataFromSharedPreferences() | 값 존재 ");
            showLayout.setVisibility(View.GONE);
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
                Log.d(tag, "CreateAlarmActivity - onLoginBtnClicked() | Exception " + e);
            }
        }
    }

    /**setDataAfterEditing()
     * 메서드 목적:
     *      - 아이템이 수정 되었을때 mArrayList에 잇는 값을 SharedPreference에 저장하기 위함.
     * 해결 수단
     *      - 우선 기존에 있는 SharedPreference에 있는 값을 지운다.
     *      - 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
     * 호출
     *      - onActivityResult()
     *      - 사용자가 item View를 클릭하고 나서 나왔을 때 데이터가 수정 될 수 있기 때문에 이때 호출한다.
     * */
    private void setDataAfterEditing(){
        Log.d(tag,"CreateAlarmActivity - setData() |  ");

        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성 및 기존 값 지우기
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
        SharedPreferences.Editor editorAlarm = sharedPreferences.edit();
        editorAlarm.remove("alarm");
        editorAlarm.commit();

        if(mArrayList.size() != 0){
            for(int i=0; i<mArrayList.size(); i++){
                boolean [] week = mArrayList.get(i).getArrayWeek();
                /**JSON 형태로 저장*/
                String strDataOfJSON;
                strDataOfJSON = "{ \"isChecked\"" + ":" + "\"" + mArrayList.get(i).isChecked() + "\"" + ","
                        + "\"name\"" + ":" + "\"" + mArrayList.get(i).getName() + "\""+ ","
                        + "\"am_pm\"" + ":" + "\"" + mArrayList.get(i).getAm_pm() + "\""+ ","
                        + "\"weekName\"" + ":" + "\"" + mArrayList.get(i).getWeekName() + "\""+ ","
                        + "\"hour_24\"" + ":" + "\"" + mArrayList.get(i).getHour_24() + "\""+ ","
                        + "\"hour_12\"" + ":" + "\"" + mArrayList.get(i).getHour_12() + "\""+ ","
                        + "\"minute\"" + ":" + "\"" + mArrayList.get(i).getMinute() + "\""+ ","
                        + "\"arrayWeek\"" + ":" + " \"" + week[0] + "," + week[1] + ","+ week[2] + ","+ week[3] + ","+ week[4] + ","+ week[5] + ","+ week[6] + ","+ week[7] + "\""
                        + "}";

                // □ 값을 처음으로 저장하는 확인
                String alarm = sharedPreferences.getString("alarm", null);

                if (alarm == null) {
                    // 첫 회원정보를 저장하는 경우
                    editorAlarm.putString("alarm", strDataOfJSON);
                    editorAlarm.commit();
//                    Toast.makeText(this, "데이터 변경", Toast.LENGTH_SHORT).show();
//                    finish();
                } else {
                    // 회원정보 저장이 처음이 아닌 경우
                    editorAlarm.putString("alarm", sharedPreferences.getString("alarm", "") + "," + strDataOfJSON);
                    editorAlarm.commit();
//                    Toast.makeText(this, "데이터 변경", Toast.LENGTH_SHORT).show();
//                    finish();
                }
            }
        }
    }



}
