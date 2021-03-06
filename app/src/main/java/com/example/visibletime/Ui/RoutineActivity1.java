//package com.example.visibletime.Ui;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.CompoundButton;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.visibletime.Adapter.RoutineAdatper;
//import com.example.visibletime.Data.ControllerPlanData;
//import com.example.visibletime.Data.PlanData;
//import com.example.visibletime.Data.RoutineData;
//import com.example.visibletime.R;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
//
//public class RoutineActivity1 extends AppCompatActivity {
//
//    private final String tag = "로그";
//
//    /**
//     * ButterKnife
//     */
//    @BindView(R.id.addRoutineBtn_Routine)
//    ImageButton addRoutineBtn;
//    @BindView(R.id.alarmTextView_Routine)
//    TextView alarmTextView;
//
//    /**날짜*/
//    Calendar calendar;
//    SimpleDateFormat dateFormat;
//    String tmpDateForSearch;       // 기록을 저장한 것을 분류하기 위한 값
//
//    /**Plan Data*/
//    ArrayList<PlanData> planDataArrayList;
//
//    /**ControllerPlanData Data*/
//    ArrayList<ControllerPlanData> controllerPlanDataArrayList;
//
//    /**RoutineData*/
//    ArrayList<RoutineData> routineDataArrayList;
//    /**
//     * RecyclerView
//     */
//    private RecyclerView mRecyclerView;
//    private RoutineAdatper mAdapter;
//    private LinearLayoutManager mLayoutManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_routin);
//        ButterKnife.bind(this);
//
//        planDataArrayList = new ArrayList<>();
//        routineDataArrayList = new ArrayList<>();
//        controllerPlanDataArrayList = new ArrayList<>();
//
//        setRecyclerView();
//        countrollRoutine();
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        getListOfDataFromSharedPreferences();
//        Log.d(tag,"RoutineActivity - onResume() | planDataArrayList.size(): "+planDataArrayList.size());
//        setRoutineDataArrayList();
//        Log.d(tag,"RoutineActivity - onResume() | routineDataArrayList.size(): "+routineDataArrayList.size());
//        mAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    protected void onPause() {
//        Log.d(tag,"RoutineActivity - onPause() |  ");
//        super.onPause();
//        savePlanDataList();
//    }
//
//    @OnClick(R.id.addRoutineBtn_Routine)
//    public void addRoutineBtnClicked(){
//        Intent intent = new Intent(RoutineActivity1.this, CreateRoutineActivity.class);
//        startActivity(intent);
//    }
//
//
//    /**리사이클러뷰에 보여줄 데이터 리스트 만들기
//     *
//     * ■ 데이터 있는지 여부 확인
//     *      - 없다면, 현재 값이 없다는 내용을 가진 Layout(showLayout) 띄우기
//     *      - 있다면, Layout(showLayout) 사라지게 하기
//     *
//     * ■ Shared
//     *      ● Shared에 저장된 현재 아이디를 불러온다.
//     *      ● 아이디에 해당하는 Shared파일을 불러온다.
//     * ■ JSONArray
//     *      ● JSONArray 생성
//     *      ● 반복문(JSONArray.length())
//     *          ○ JSON 객체 생성
//     *          ○ String으로 저장된 값 변환
//     *                  - String > boolean
//     *                  - String > int
//     *          ○ ArrayList에 값 넣기
//     *
//     * */
//    private void getListOfDataFromSharedPreferences(){
//
//        // planDataArrayList가 있는 경우에 Shared에서 다시 불러올 경우 데이터 중복을 막기 위해 초기화 해준다.
//        if(planDataArrayList.size() != 0){
//            planDataArrayList.clear();
//        }
//
//        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
//        String presentID = sharedPresesntID.getString("presentID",null);
//
//        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
//        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
//
//        // 값이 있는지 여부 확인하기
//        if(sharedPreferences.getString("plan",null) == null){
//            alarmTextView.setVisibility(View.VISIBLE);
//        } else {
//            Log.d(tag,"RoutineActivity - getListOfDataFromSharedPreferences() | Shared에 데이터 존재 ");
//            alarmTextView.setVisibility(View.GONE);
//            try {
//                // ● JSONArray 생성
//                JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("plan", null) + "]");
//                // ● 반복문(JSONArray.length())
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                    // ○ JSON에 String으로 저장된 값을 AlarmData 데이터 타입에 맞게 변환을 위한 변수 선언
//                    boolean isOn = false;
//                    int color = 0;
//                    int type = -1;      // 0은 부모, 1은 자식이므로
//                    int targetTime = 0;
//
//                    // boolean isChecked를 얻기 위한 데이터 변환
//                    if (jsonObject.getString("isOn").equals("true")) {
//                        isOn = true;
//                    } else {
//
//                    }
//                    // int hour_24, hour_12, minute 을 얻기 위한 데이 변환
//                    color = Integer.parseInt(jsonObject.getString("color"));
//                    type = Integer.parseInt(jsonObject.getString("type"));
//                    targetTime = Integer.parseInt(jsonObject.getString("targetTime"));
//
//                    // ○ ArrayList에 값 넣기
//                    // Data 객체 생성 & 값 넣어주기
//                    PlanData planData = new PlanData();
//                    planData.setRoutine(jsonObject.getString("routine"));
//                    planData.setDateForSearch(jsonObject.getString("dateForSearch"));
//                    planData.setCategory(jsonObject.getString("category"));
//                    planData.setCategoryParent(jsonObject.getString("parentName"));
//                    planData.setColor(color);
//                    planData.setType(type);
//                    planData.setTargetTime(targetTime);
//                    planData.setOn(isOn);
//
//                    // planDataArrayList에 값 넣어주기
//                    planDataArrayList.add(planData);
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.d(tag, "CreateAlarmActivity - onLoginBtnClicked() | Exception " + e);
//            }
//        }
//    }
//
//    /**
//     * 《리사이클러뷰 프로세스》
//     * ■ setRecyclerView()
//     * □ 목적: 리사이클러뷰 생성하여 View단에 보여주기
//     * 1. RecyclerView, Adapter,(Linear)LayoutManager 생성
//     * 2. RecyclerView 객체와 Adapter, (Linear)LayoutManager 객체 연결 : set()
//     */
//    private void setRecyclerView() {
//        Log.d(tag,"RecordFragment - setRecyclerView() |  ");
//
//        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_Routine);  //RecyclerView 객체 초기화(initiate)
//        mAdapter = new RoutineAdatper(this, routineDataArrayList);    // Custom Adater 객체 생성 & Adapter와 mArrayList(Data List) 연결.
//        mRecyclerView.setAdapter(mAdapter);             // RecyclerView와 Adater와 연결
//        mLayoutManager = new LinearLayoutManager(this);     // RecyclerView를 어떻게 보여줄지 결정
//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
////        mAdapter.notifyDataSetChanged();
//        //child View들을 구분선을 만들어 주는 메서드
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
//    }
//
//    /**setRoutineDataArrayList()
//     * ■ 목적: routineDataArrayList를 만들기
//     *
//     * */
//    private void setRoutineDataArrayList(){
//
//        // planDataArrayList가 있는 경우에 Shared에서 다시 불러올 경우 데이터 중복을 막기 위해 초기화 해준다.
//        if(routineDataArrayList.size() != 0){
//            routineDataArrayList.clear();
//        }
//        Log.d(tag,"RoutineActivity - setRoutineDataArrayList() | routineDataArrayList.size() "+routineDataArrayList.size());
//        for(int i=0; i<planDataArrayList.size(); i++){
//            if(routineDataArrayList.size() == 0){
//                // routineDataArrayList 데이터 추가
//                RoutineData routineData = new RoutineData(planDataArrayList.get(i).getRoutine(), planDataArrayList.get(i).getDateForSearch(), false);
//                routineDataArrayList.add(routineData);
//            } else {
//                boolean isExist = false;
//                for(int j=0; j<routineDataArrayList.size(); j++){
//                    // 중복되지 않은 경우
//                    if(planDataArrayList.get(i).getRoutine().equals(routineDataArrayList.get(j).getName())){
//                        isExist = true;
//                    }
//                }
//                if(isExist == false){
//                    // routineDataArrayList 데이터 추가
//                    RoutineData routineData = new RoutineData(planDataArrayList.get(i).getRoutine(), planDataArrayList.get(i).getDateForSearch(), false);
//                    routineDataArrayList.add(routineData);
//                }
//            }
//        }
//    }
//
//    /** editRoutine()
//     * RoutineAdatper 에서 interface를 정의하고, RecordFragment에서 구현함으로써
//     * 수정 이벤트를 Activity에서 수행 가능하게 함.
//     * 자세한 사항은 https://recipes4dev.tistory.com/168 참조
//     * */
//    public void countrollRoutine(){
//        /**Adapter의 ViewHolder에서 클릭시 이벤트 수행*/
//        mAdapter.setOnItemClickListener(new RoutineAdatper.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//                Log.d(tag,"RoutineActivity - onItemClick() | position: "+position);
//                /*인텐트 생성 & startActivityForResult로 값 받아오기.
//                 * */
//                Intent intent = new Intent(RoutineActivity1.this, EditRoutineActivity.class);
//                intent.putExtra("routine", routineDataArrayList.get(position).getName());
//                intent.putExtra("dateForSearch", routineDataArrayList.get(position).getDateForSearch());
//                startActivity(intent);
//            }
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
//                Log.d(tag,"RoutineActivity - onCheckedChanged() | position: "+position);
//                RoutineData routineData = routineDataArrayList.get(position);
//                String name = routineData.getName();
//                String dateForSearch = routineData.getDateForSearch();
//                if(isChecked){
//                    Log.d(tag,"RoutineActivity - onCheckedChanged() | true ");
//                    for(int i=0; i<planDataArrayList.size(); i++){
//                        if(planDataArrayList.get(i).getRoutine().equals(name) && planDataArrayList.get(i).getDateForSearch().equals(dateForSearch)){
//                            planDataArrayList.get(i).setOn(true);
//                        }
//                    }
//                }
//                else{
//                    Log.d(tag,"RoutineActivity - onCheckedChanged() | false ");
//                    for(int i=0; i<planDataArrayList.size(); i++){
//                        if(planDataArrayList.get(i).getRoutine().equals(name) && planDataArrayList.get(i).getDateForSearch().equals(dateForSearch)){
//                            planDataArrayList.get(i).setOn(false);
//                        }
//                    }
//                }
//
//                for(int i=0; i<planDataArrayList.size(); i++){
//                    Log.d(tag," ");
//                    Log.d(tag,"RoutineActivity - onCheckedChanged() | 루틴 이름: "+planDataArrayList.get(i).getRoutine());
//                    Log.d(tag,"RoutineActivity - onCheckedChanged() | 활동 명: "+planDataArrayList.get(i).getCategory());
//                    Log.d(tag,"RoutineActivity - onCheckedChanged() | 불린 값: "+planDataArrayList.get(i).isOn());
//                }
//            }
//        }) ;
//    }
//    /**savePlanDataList()
//     * ■ 메서드 목적:
//     *      - planDataArrayList에 있는 값을 SharedPreference에 저장하기 위함.
//     * ■ 프로세스
//     *      ● 해당 아이디를 불러온다.
//     *      ● 우선 기존에 있는 SharedPreference에 있는 값을 지운다.
//     *      ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
//     *
//     * ■ 호출
//     *      - enterBtnClicked()
//     * */
//    private void savePlanDataList(){
//        Log.d(tag,"RoutineActivity - savePlanDataList() |  ");
//
//        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
//        String presentID = sharedPresesntID.getString("presentID",null);
//
//        // ● ID를 파일명으로 하는 Shared.xml 파일 생성 및 기존 값 지우기
//        // 지울 필요가 없는게, 수정하는게 아닌 추가이므로 일단 주석처리 해놓음
//        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
//        SharedPreferences.Editor editorCategory = sharedPreferences.edit();
//        editorCategory.remove("plan");
//        editorCategory.commit();
//
//
//        // ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
//        if(planDataArrayList.size() != 0){
//            for(int i=0; i<planDataArrayList.size(); i++){
//                /**JSON 형태로 저장*/
//                String strDataOfJSON;
//                strDataOfJSON = "{ \"routine\"" + ":" + "\"" + planDataArrayList.get(i).getRoutine() + "\"" + ","
//                        + "\"dateForSearch\"" + ":" + "\"" + planDataArrayList.get(i).getDateForSearch() + "\""+ ","
//                        + "\"category\"" + ":" + "\"" + planDataArrayList.get(i).getCategory() + "\""+ ","
//                        + "\"parentName\"" + ":" + "\"" + planDataArrayList.get(i).getCategoryParent() + "\""+ ","
//                        + "\"color\"" + ":" + "\"" + planDataArrayList.get(i).getColor() + "\""+ ","
//                        + "\"type\"" + ":" + "\"" + planDataArrayList.get(i).getType() + "\""+ ","
//                        + "\"targetTime\"" + ":" + "\"" + planDataArrayList.get(i).getTargetTime() + "\""+ ","
//                        + "\"isOn\"" + ":" + "\"" + planDataArrayList.get(i).isOn() + "\""
//                        + "}";
//
//                // □ 값을 처음으로 저장하는 확인
//                String plan = sharedPreferences.getString("plan", null);
//
//                if (plan == null) {
//                    // 첫 카테고리 정보를 저장하는 경우
//                    editorCategory.putString("plan", strDataOfJSON);
//                    editorCategory.commit();
//                } else {
//                    // 카테고리 정보가 처음이 아닌 경우
//                    editorCategory.putString("plan", sharedPreferences.getString("plan", "") + "," + strDataOfJSON);
//                    editorCategory.commit();
//                }
//            }
//        }
//    }
//
//    private void getListOfControllerPlanDataFromSharedPreferences(){
//        Log.d(tag,"CreateRoutineActivity - getListOfControllerPlanDataFromSharedPreferences() |  ");
//        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
//        String presentID = sharedPresesntID.getString("presentID",null);
//
//        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
//        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
//
//        // 값이 있는지 여부 확인하기
//        if(sharedPreferences.getString("controllerPlan",null) == null){
////            showLayout.setVisibility(View.VISIBLE);
//        } else {
////            showLayout.setVisibility(View.GONE);
//            try {
//                // ● JSONArray 생성
//                JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("controllerPlan", null) + "]");
//                // ● 반복문(JSONArray.length())
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                    // ○ JSON에 String으로 저장된 값을 ControllerPlanData 데이터 타입에 맞게 변환을 위한 변수 선언
//                    boolean isSelected = false;
//                    if(jsonObject.getString("isSelected").equals("true")){
//                        isSelected = true;
//                    }
//
//                    // ○ ArrayList에 값 넣기
//                    ControllerPlanData controllerPlanData = new ControllerPlanData(jsonObject.getString("controllerName"),isSelected);
//                    controllerPlanDataArrayList.add(controllerPlanData);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.d(tag,"CreateRoutineActivity - getListOfControllerPlanDataFromSharedPreferences() | Exception " + e);
//            }
//        }
//    }
//
////    private void getCurrentDate(){
////        calendar = Calendar.getInstance();
////        dateFormat = new SimpleDateFormat("yyyyMMdd");
////        tmpDateForSearch = dateFormat.format(calendar.getTime());
////        Log.d(tag,"StatisticsDayFragment - getCurrentDate() | dateForSearch 날짜 선택 전: "+tmpDateForSearch);
////    }
//
//}
