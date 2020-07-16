package com.example.visibletime.Ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Adapter.PlanAdapter;
import com.example.visibletime.Data.CatagoryData;
import com.example.visibletime.Data.ControllerPlanData;
import com.example.visibletime.Data.PlanData;
import com.example.visibletime.Plan;
import com.example.visibletime.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;
import static com.example.visibletime.Data.CatagoryData.PARENT_TYPE;

public class EditRoutineActivity extends AppCompatActivity {

    private final String tag = "로그";

    /**Butter knife*/
    @BindView(R.id.floatingActionButton_EditRoutine)
    com.google.android.material.floatingactionbutton.FloatingActionButton floatingActionButton;
    @BindView(R.id.routineNameTextView_EditRoutine)
    EditText routineNameTextView;
    @BindView(R.id.alarmTextView_EditRoutine)
    TextView alarmTextView;
    @BindView(R.id.ableUseTimeTextView_EditRoutine)
    TextView ableUseTimeTextView;
    @BindView(R.id.enterBtn_EditRoutine)
    ImageButton enterBtn;
    @BindView(R.id.deleteBtn_EditRoutine)
    ImageButton deleteBtn;

    // 인텐트로 받은 이름과 인데스
    String routineNameFromIntent;
    int index;

    /**가용시간*/
    int useableTime;

    /**Category Data*/
    ArrayList<CatagoryData> totalArrayList;     // 카테고리 이름, 컬러를 받아오게 하기 위함.

    /**ControllerPlanData Data*/
    ArrayList<ControllerPlanData> controllerPlanDataArrayList;

    /**Plan Data*/
    ArrayList<PlanData> totalPlanDataArrayList;
    ArrayList<PlanData> planDataArrayList;
    ArrayList<PlanData> planChildDataArrayList;    // 가용시간에 값을 빼기 위한 용도


    Plan plan;      // planDataArrayList 추가 & 정렬을 담당하는 클래스

    /**
     * RecyclerView
     */
    private RecyclerView mRecyclerView;
    private PlanAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    /**저장해야 할 데이터*/
    String category, categoryParent;
    int color;
    int indexNumber;     // color를 얻기 위한 변수
    // 여기서 얻어야 하는 데이터
    // PlanData(String routine, String dateForSearch, String category, String categoryParent, int color, int type, int targetTime)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag,"EditRoutineActivity - onCreate() |  ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_routine);
        ButterKnife.bind(this);

        /*데이터 수신*/
        Intent intent = getIntent();
        routineNameFromIntent = intent.getExtras().getString("routine");
        index = intent.getExtras().getInt("index");
        Log.d(tag,"EditRoutineActivity - onCreate() | index: "+index);
        routineNameTextView.setText(routineNameFromIntent);

        // 초기화
        totalArrayList = new ArrayList<>();
        controllerPlanDataArrayList = new ArrayList<>();
        totalPlanDataArrayList = new ArrayList<>();
        planDataArrayList = new ArrayList<>();
        planChildDataArrayList = new ArrayList<>();

        Log.d(tag,"EditRoutineActivity - onCreate() | useableTime: "+useableTime);

        plan = new Plan();


        // 컨트롤러
        getListOfControllerPlanDataFromSharedPreferences();
        Log.d(tag,"EditRoutineActivity - onCreate() | controllerPlanDataArrayList.size() "+controllerPlanDataArrayList.size());
        for(int i=0; i<controllerPlanDataArrayList.size(); i++){
            Log.d(tag,"EditRoutineActivity - onCreate() | 이름: "+controllerPlanDataArrayList.get(i).getControllerName());
            Log.d(tag,"EditRoutineActivity - onCreate() | 불린 값: "+controllerPlanDataArrayList.get(i).isSelected());
        }

        // intent로 받아온 값에서 분류
        getListOfPlanDataFromSharedPreferences();       // Plan Data 전체 데이터 받아옴
        selectPlanData();                               // 선택된 것만 분류하여 받아옴.

        // 가용시간
        setUseableTime();

        setRecyclerView();
        controllRecyclerViewItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListOfCategoryDataFromSharedPreferences();   // 카테고리 전체 데이터를 받아옴.
    }

    @OnClick(R.id.floatingActionButton_EditRoutine)
    public void floatingActionButtonClicked(){

        // 상위 활동의 위치를 저장하기 위한 리스트
        ArrayList<Integer> arrayListIndexOfParent = new ArrayList<>();

        // ● 자식 Category만 따로 ArrayList<CatagoryData>에 담는다.
        ArrayList<String> arrayListName = new ArrayList<>();
//        ArrayList<Integer> arrayListChildColor = new ArrayList<>();
//        ArrayList<String> arrayListParentName = new ArrayList<>();

        for(int i = 0; i<totalArrayList.size(); i++){
            CatagoryData catagoryData = totalArrayList.get(i);
//                arrayListChildColor.add(catagoryData.getColor());
//                arrayListParentName.add(catagoryData.getParentName());
            if(catagoryData.getType() == PARENT_TYPE){
                arrayListIndexOfParent.add(i);
                arrayListName.add(catagoryData.getName());
            } else {
                arrayListName.add("       - "+catagoryData.getName());
            }
        }
        // 새 항목 추가하기 Text 추가
        arrayListName.add("                      새 항목 추가하기");

        //다이얼로그에 보여주기 위해서는 ArrayList가 아니라 배열로 변환해야 함.
        final String[] items =  arrayListName.toArray(new String[ arrayListName.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(EditRoutineActivity.this);
        builder.setTitle("활동 목록");

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position, convertView, parent);
                for(int i=0; i<arrayListIndexOfParent.size(); i++){
                    if(position == arrayListIndexOfParent.get(i)){
                        // 상위 활동인 경우
                        view.setBackgroundColor(Color.parseColor("#CCC9C9"));
                        break;
                    } else {
                        // 하위 활동인 경우
                        view.setBackgroundColor(Color.parseColor("#ffffff"));
                    }
                }
                if(position == arrayListName.size()-1){
                    view.setBackground(getDrawable(R.drawable.dialog_gray));
                }
                return view;
            }
        };
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                if(position == arrayListName.size()-1){
                    Toast.makeText(EditRoutineActivity.this, "추가", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditRoutineActivity.this, CategoryActivity.class);
                    startActivity(intent);
                } else {
                    CatagoryData catagoryData   = null;
                    boolean isParentType = false;

                    for(int i=0; i<arrayListIndexOfParent.size(); i++){
                        if(position == arrayListIndexOfParent.get(i)){
                            // 상위 활동인 경우
                            Toast.makeText(EditRoutineActivity.this, "상위 활동은 선택하 실 수 없습니다.", Toast.LENGTH_SHORT).show();
                            isParentType = true;
                            break;
                        } else {
                            // 하위 활동인 경우
                        }
                    }

                    if(isParentType == false){
                        catagoryData = totalArrayList.get(position);
                        plan.setPlanDataArrayList(catagoryData, planDataArrayList);     // planDataArrayList 리스트 데이터 추가
                    }
                    plan.sortPlanDataArrayList(planDataArrayList);                  // 부모에 맞춰서 자식 카테고리 정렬

                    setInfoText();      // 알람테스트 보일지 여부를 정하는 메서드
                    mAdapter.notifyDataSetChanged();                                // 데이터 갱신
                }
            }
        }).show();
    }

    @OnClick(R.id.enterBtn_EditRoutine)
    public void enterBtnClicked() {
        Log.d(tag, "EditRoutineActivity - enterBtnClicked() | 저장 버튼 클릭 ");
        if (routineNameTextView.getText().toString().length() == 0) {
            Toast.makeText(EditRoutineActivity.this, "'활동 목표'의 이름을 정해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            deletePlanData();       //기존 받은 데이터 삭제

            boolean isOverlap = false;

            // 저장전에 식별자 역할인 이름이 중복된 값이 없었는지 확인해야 한다.
            Log.d(tag,"EditRoutineActivity - enterBtnClicked() | 이름 "+controllerPlanDataArrayList.get(index).getControllerName());
            Log.d(tag,"EditRoutineActivity - enterBtnClicked() | 비교 이름 "+routineNameTextView.getText().toString());
            if(controllerPlanDataArrayList.get(index).getControllerName().equals(routineNameTextView.getText().toString()) == false){
                Log.d(tag,"EditRoutineActivity - enterBtnClicked() | 이름을 수정하는 경우 중복 검사를 확인해야 한다. ");
                for(int i=0; i<controllerPlanDataArrayList.size(); i++){
                    if(controllerPlanDataArrayList.get(i).getControllerName().equals(routineNameTextView.getText().toString())){
                        isOverlap = true;
                    }
                }
            }
//            for (int i = 0; i < controllerPlanDataArrayList.size(); i++) {
//                Log.d(tag,"EditRoutineActivity - enterBtnClicked() | 이름: "+controllerPlanDataArrayList.get(i).getControllerName());
//                if (controllerPlanDataArrayList.get(i).getControllerName().equals(routineNameFromIntent)) {
//                    Log.d(tag, "EditRoutineActivity - enterBtnClicked() | 위치 ");
//                    if (controllerPlanDataArrayList.get(i).getControllerName().equals(routineNameTextView.getText().toString()) == false) {
//                        index = i;
//                    }
//                    isOverlap = false;
//                    break;
//                } else if (controllerPlanDataArrayList.get(i).getControllerName().equals(routineNameTextView.getText().toString())) {
//                    Log.d(tag, "EditRoutineActivity - enterBtnClicked() | 이름이 중복된 경우 ");
//                    isOverlap = true;
//                    index = -1;
//                }
//            }

            if (isOverlap == true) {
                Toast.makeText(EditRoutineActivity.this, "현재 활동목표 이름은 기존에 존재하는 값이므로 사용하실 수 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                // 데이터 수정
                controllerPlanDataArrayList.get(index).setControllerName(routineNameTextView.getText().toString());

                Log.d(tag, "EditRoutineActivity - enterBtnClicked() | 저장 ");
                // 데이터 이름 저장.
                plan.setExtraData(routineNameTextView.getText().toString(), planDataArrayList);
                addPlanData();          //수정한 값 데이터 추가
                savePlanFrameDataList();
                saveControllerPlanData();
                finish();
            }
        }
    }

    @OnClick(R.id.deleteBtn_EditRoutine)
    public void deleteBtn(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditRoutineActivity.this);
        mBuilder.setTitle("알림");
        mBuilder.setMessage("해당 '활동 목록'을 삭제하시겠습니까?");
        // 버튼 클릭 이벤트 달기
        mBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteRoutine();
                saveControllerPlanData();
                finish();
            }
        });
        mBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        // 다이얼로그 보여주기
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
    /**getListOfDataFromSharedPreferences
     *
     * ■ 메서드 목적
     *      - Shared에 저장된 값을 다이얼로그에 보여주기 위함
     *
     * ■ Shared
     *      ● Shared에 저장된 현재 아이디를 불러온다.
     *      ● 아이디에 해당하는 Shared파일을 불러온다.
     *
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
    private void getListOfCategoryDataFromSharedPreferences(){
        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);

        if(totalArrayList.size() !=0){
            totalArrayList.clear();
        }

        // 값이 있는지 여부 확인하기
        if(sharedPreferences.getString("category",null) == null){
//            showLayout.setVisibility(View.VISIBLE);
        } else {
//            showLayout.setVisibility(View.GONE);
            try {
                // ● JSONArray 생성
                JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("category", null) + "]");
                // ● 반복문(JSONArray.length())
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // ○ JSON에 String으로 저장된 값을 AlarmData 데이터 타입에 맞게 변환을 위한 변수 선언
                    int type = 0;
                    int color = 0;
                    int count = 0;

                    // int type, color, count 을 얻기 위한 데이 변환
                    type = Integer.parseInt(jsonObject.getString("type"));
                    color = Integer.parseInt(jsonObject.getString("color"));
                    count = Integer.parseInt(jsonObject.getString("count"));

                    // ○ ArrayList에 값 넣기
                    // Data 객체 생성 & 값 넣어주기
                    CatagoryData catagoryData = new CatagoryData();
                    catagoryData.setType(type);
                    catagoryData.setColor(color);
                    catagoryData.setCount(count);
                    catagoryData.setName(jsonObject.getString("name"));
                    catagoryData.setParentName(jsonObject.getString("parentName"));
                    // totalArrayList에 값 넣어주기
                    totalArrayList.add(catagoryData);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(tag, "CategoryActivity - onLoginBtnClicked() | Exception " + e);
            }
        }
    }

    private void getListOfControllerPlanDataFromSharedPreferences(){
        Log.d(tag,"EditRoutineActivity - getListOfControllerPlanDataFromSharedPreferences() |  ");
        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);

        // 값이 있는지 여부 확인하기
        if(sharedPreferences.getString("controllerPlan",null) == null){
//            showLayout.setVisibility(View.VISIBLE);
        } else {
//            showLayout.setVisibility(View.GONE);
            try {
                // ● JSONArray 생성
                JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("controllerPlan", null) + "]");
                // ● 반복문(JSONArray.length())
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // ○ JSON에 String으로 저장된 값을 ControllerPlanData 데이터 타입에 맞게 변환을 위한 변수 선언
                    boolean isSelected = false;
                    if(jsonObject.getString("isSelected").equals("true")){
                        isSelected = true;
                    }

                    // ○ ArrayList에 값 넣기
                    ControllerPlanData controllerPlanData = new ControllerPlanData(jsonObject.getString("controllerName"),isSelected);
                    controllerPlanDataArrayList.add(controllerPlanData);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(tag,"CreateRoutineActivity - getListOfControllerPlanDataFromSharedPreferences() | Exception " + e);
            }
        }
    }

    /**
     * 《리사이클러뷰 프로세스》
     * ■ setRecyclerView()
     * □ 목적: 리사이클러뷰 생성하여 View단에 보여주기
     *      1. RecyclerView, Adapter,(Linear)LayoutManager 생성
     *      2. RecyclerView 객체와 Adapter, (Linear)LayoutManager 객체 연결 : set()
     */
    private void setRecyclerView() {
        Log.d(tag,"RecordFragment - setRecyclerView() |  ");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_EditRoutine);  //RecyclerView 객체 초기화(initiate)
        mAdapter = new PlanAdapter(this, planDataArrayList);    // Custom Adater 객체 생성 & Adapter와 mArrayList(Data List) 연결.
        mRecyclerView.setAdapter(mAdapter);             // RecyclerView와 Adater와 연결
        mLayoutManager = new LinearLayoutManager(this);     // RecyclerView를 어떻게 보여줄지 결정
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter.notifyDataSetChanged();
    }
    /**setInfoText()
     * □ 목적: alarmTextView를 planDataArrayList의 데이터 여부에 따라 보여주고 안 보여주기 위함
     * */

    private void setInfoText(){
        if(planDataArrayList.size()==0){
            alarmTextView.setVisibility(View.VISIBLE);
        } else {
            alarmTextView.setVisibility(View.GONE);
        }
    }

    /** getTatgetTimeFromTimePicker()
     * ReportAdapter 에서 interface를 정의하고, RecordFragment에서 구현함으로써
     * 수정 이벤트를 Activity에서 수행 가능하게 함.
     * 자세한 사항은 https://recipes4dev.tistory.com/168 참조
     * */
    public void controllRecyclerViewItem(){
        /**Adapter의 ViewHolder에서 클릭시 이벤트 수행*/
        mAdapter.setOnItemClickListener(new PlanAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(tag, "EditRoutineActivity - onItemClick() | position " + position);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditRoutineActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_number_picker, null);        // 다이얼로그에 TimePicker을 보여주기 위해 inflate 작업
                TimePicker timePicker = mView.findViewById(R.id.timePicker);
                timePicker.setIs24HourView(true);

                PlanData planData = planDataArrayList.get(position);

                // 시간 데이터 초기값
                int time = planData.getTargetTime();
                int hour = (time) / 3600;
                int min = (time) % 3600 / 60;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    timePicker.setHour(hour);
                    timePicker.setMinute(min);
                }

                mBuilder.setTitle("목표 시간 설정");
                mBuilder.setView(mView);        // 내가 만든 xml파일 붙이기
                // 버튼 클릭 이벤트 달기

                mBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int targetHour = timePicker.getCurrentHour() * 3600;       // 1시간 = 3600 초
                        int targetMinutes = timePicker.getCurrentMinute() * 60;        // 1분 = 60초
                        int targetTime = targetHour + targetMinutes;
                        Log.d(tag,"EditRoutineActivity - onClick() | hour: "+targetHour);
                        Log.d(tag,"EditRoutineActivity - onClick() | minutes: "+targetMinutes);

                        // 가용시간이 측정시간보다 많거나 같은경우 경우
                        // 기존 시간보다 낮추는 경우
                        if(useableTime>=targetTime || time > targetTime || useableTime+time >=targetTime){
                            Log.d(tag,"EditRoutineActivity - onClick() | 가용시간이 목표시간보다 많은 경우 ");
                            plan.setChildTargetTime(targetTime, position, planDataArrayList);       // 자식 TargetTime 얻기
                            plan.setParentTargetTime(planDataArrayList);            // 부모 TargetTime 얻기
                            setUseableTime();
                            mAdapter.notifyDataSetChanged();
                            // 다이얼로그 닫기
                            dialogInterface.dismiss();
                        } else {
                            Log.d(tag,"EditRoutineActivity - onClick() | 가용시간이 목표시간보다 적은 경우 ");
                            int useableHour = (useableTime) / 3600;
                            int useableMinutes = (useableTime) % 3600  / 60;
                            String useabletimeDuration = String.format("%02d시간:%02d분", useableHour, useableMinutes);
                            Toast.makeText(EditRoutineActivity.this, "사용 가능한 시간은 "+useabletimeDuration+"입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                // 다이얼로그 보여주기
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }

            @Override
            public void onItemLongClick(View v, int position) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditRoutineActivity.this);
                mBuilder.setTitle("알림");
                mBuilder.setMessage("해당 '활동'을 삭제하시겠습니까?");
                // 버튼 클릭 이벤트 달기
                mBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        planDataArrayList.remove(position);
                        plan.setParentTargetTime(planDataArrayList);            // 부모 TargetTime 얻기
                        plan.deleteParentCategory(planDataArrayList);
                        mAdapter.notifyDataSetChanged();
                        setInfoText();
                        setUseableTime();
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                // 다이얼로그 보여주기
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        }) ;
    }

    /**목표
     * 활동에 목표시간을 부여한 만큼 가용시간에 값을 빼주기 위함*/
    private void setUseableTime(){
        planChildDataArrayList.clear();         // planChildDataArrayList을 새로 구하기 전에 기존 값 비우기.
        plan.setPlanChildDataArrayList(planDataArrayList, planChildDataArrayList);
        useableTime = 60*60*24;        // 1초 , 60초, 60분, 24시간: 사용자가 활동에 시간을 쓰면 가용시간이 줄어드는 것을 보여주게 하기 위함.
        int totalTargetTime = 0;

        for(int i = 0; i<planChildDataArrayList.size(); i++){
            totalTargetTime += planChildDataArrayList.get(i).getTargetTime();
        }
        useableTime -= totalTargetTime;
        Log.d(tag,"EditRoutineActivity - setUseableTime() | useableTime: "+useableTime);
        int min = (useableTime) %3600 / 60;
        int hour = (useableTime) / 3600;
        String useableTime = String.format("%02d시간:%02d분", hour, min);

        ableUseTimeTextView.setText(useableTime);
    }


    /**savePlanDataList()
     * ■ 메서드 목적:
     *      - planDataArrayList에 있는 값을 SharedPreference에 저장하기 위함.
     * ■ 프로세스
     *      ● 해당 아이디를 불러온다.
     *      ● 우선 기존에 있는 SharedPreference에 있는 값을 지운다.
     *      ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
     *
     * ■ 호출
     *      - enterBtnClicked()
     * */
    private void savePlanFrameDataList(){
        Log.d(tag,"CategoryActivity - saveCategoryDataList() |  ");


        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ● ID를 파일명으로 하는 Shared.xml 파일 생성 및 기존 값 지우기
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
        SharedPreferences.Editor editorCategory = sharedPreferences.edit();
        editorCategory.remove("planFrame");
        editorCategory.commit();


        // ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
        if(totalPlanDataArrayList.size() != 0){
            for(int i=0; i<totalPlanDataArrayList.size(); i++){
                /**JSON 형태로 저장*/
                String strDataOfJSON;
                strDataOfJSON = "{ \"routine\"" + ":" + "\"" + totalPlanDataArrayList.get(i).getRoutine() + "\"" + ","
                        + "\"category\"" + ":" + "\"" + totalPlanDataArrayList.get(i).getCategory() + "\""+ ","
                        + "\"parentName\"" + ":" + "\"" + totalPlanDataArrayList.get(i).getCategoryParent() + "\""+ ","
                        + "\"color\"" + ":" + "\"" + totalPlanDataArrayList.get(i).getColor() + "\""+ ","
                        + "\"type\"" + ":" + "\"" + totalPlanDataArrayList.get(i).getType() + "\""+ ","
                        + "\"targetTime\"" + ":" + "\"" + totalPlanDataArrayList.get(i).getTargetTime() + "\""
                        + "}";

                // □ 값을 처음으로 저장하는 확인
                String plan = sharedPreferences.getString("planFrame", null);

                if (plan == null) {
                    // 첫 카테고리 정보를 저장하는 경우
                    editorCategory.putString("planFrame", strDataOfJSON);
                    editorCategory.commit();
                } else {
                    // 카테고리 정보가 처음이 아닌 경우
                    editorCategory.putString("planFrame", sharedPreferences.getString("planFrame", "") + "," + strDataOfJSON);
                    editorCategory.commit();
                }
            }
        }
    }

    private void saveControllerPlanData(){
        Log.d(tag,"EditRoutineActivity - saveControllerPlanData() |  ");

        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ● ID를 파일명으로 하는 Shared.xml 파일 생성 및 기존 값 지우기
        // 수정해야 하므로 기존에 Shared를 지워야 함.
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
        SharedPreferences.Editor editorControllerPlan = sharedPreferences.edit();
        editorControllerPlan.remove("controllerPlan");
        editorControllerPlan.commit();

        // ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
        if(controllerPlanDataArrayList.size() != 0){
            for(int i=0; i<controllerPlanDataArrayList.size(); i++){
                /**JSON 형태로 저장*/
                String strDataOfJSON;
                strDataOfJSON = "{ \"controllerName\"" + ":" + "\"" + controllerPlanDataArrayList.get(i).getControllerName() + "\"" + ","
                        + "\"isSelected\"" + ":" + "\"" + controllerPlanDataArrayList.get(i).isSelected() + "\""
                        + "}";

                // □ 값을 처음으로 저장하는 확인
                String plan = sharedPreferences.getString("controllerPlan", null);

                if (plan == null) {
                    // 첫 카테고리 정보를 저장하는 경우
                    editorControllerPlan.putString("controllerPlan", strDataOfJSON);
                    editorControllerPlan.commit();
                } else {
                    // 카테고리 정보가 처음이 아닌 경우
                    editorControllerPlan.putString("controllerPlan", sharedPreferences.getString("controllerPlan", "") + "," + strDataOfJSON);
                    editorControllerPlan.commit();
                }
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
    private void getListOfPlanDataFromSharedPreferences(){

        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);

        // 값이 있는지 여부 확인하기
        if(sharedPreferences.getString("planFrame",null) == null){
        } else {
            try {
                // ● JSONArray 생성
                JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("planFrame", null) + "]");
                // ● 반복문(JSONArray.length())
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // ○ JSON에 String으로 저장된 값을 AlarmData 데이터 타입에 맞게 변환을 위한 변수 선언
                    int color = 0;
                    int type = -1;      // 0은 부모, 1은 자식이므로
                    int targetTime = 0;

                    // int hour_24, hour_12, minute 을 얻기 위한 데이 변환
                    color = Integer.parseInt(jsonObject.getString("color"));
                    type = Integer.parseInt(jsonObject.getString("type"));
                    targetTime = Integer.parseInt(jsonObject.getString("targetTime"));

                    // ○ ArrayList에 값 넣기
                    // Data 객체 생성 & 값 넣어주기
                    PlanData planData = new PlanData();
                    planData.setRoutine(jsonObject.getString("routine"));
                    planData.setCategory(jsonObject.getString("category"));
                    planData.setCategoryParent(jsonObject.getString("parentName"));
                    planData.setColor(color);
                    planData.setType(type);
                    planData.setTargetTime(targetTime);

                    // planDataArrayList에 값 넣어주기
                    totalPlanDataArrayList.add(planData);
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.d(tag, "CreateAlarmActivity - onLoginBtnClicked() | Exception " + e);
            }
        }
    }

    /** 데이터 선별
     * */
    private void selectPlanData(){
        Log.d(tag,"EditRoutineActivity - selectPlanData() |  ");
        for(int i=0; i<totalPlanDataArrayList.size(); i++){
            if(totalPlanDataArrayList.get(i).getRoutine().equals(routineNameFromIntent)){
                planDataArrayList.add(totalPlanDataArrayList.get(i));
            }
        }
        if(planDataArrayList.size() == 0){
            alarmTextView.setVisibility(View.VISIBLE);
        } else {
            alarmTextView.setVisibility(View.GONE);
        }
    }

    /**기존 값 삭제*/
    private void deletePlanData(){
        Log.d(tag,"EditRoutineActivity - deletePlanData() |  ");
        for(int i=totalPlanDataArrayList.size()-1; i>=0; i--){
            if(totalPlanDataArrayList.get(i).getRoutine().equals(routineNameFromIntent)){
                Log.d(tag,"EditRoutineActivity - deletePlanData() | 삭제 position: "+i);
                Log.d(tag,"EditRoutineActivity - deletePlanData() | 삭제 이름: "+totalPlanDataArrayList.get(i).getCategory());
                totalPlanDataArrayList.remove(i);
            }
        }
    }

    private void addPlanData(){
        Log.d(tag,"EditRoutineActivity - addPlanData() |  ");
        for(int i= 0; i<planDataArrayList.size(); i++){
            Log.d(tag,"EditRoutineActivity - addPlanData() | 추가 position: "+i);
            Log.d(tag,"EditRoutineActivity - addPlanData() | 추가 이름: "+planDataArrayList.get(i).getCategory());
            totalPlanDataArrayList.add(planDataArrayList.get(i));
        }
    }

    /**루틴 삭제*/
    private void deleteRoutine(){
        Log.d(tag,"EditRoutineActivity - deleteRoutine() |  ");
        controllerPlanDataArrayList.remove(index);
    }
}
