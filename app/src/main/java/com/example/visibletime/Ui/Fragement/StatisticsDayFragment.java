package com.example.visibletime.Ui.Fragement;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Adapter.ReportAdatper;
import com.example.visibletime.Adapter.StatisticsAdapter;
import com.example.visibletime.Data.PlanData;
import com.example.visibletime.Data.ReportData;
import com.example.visibletime.Data.StatisticsData;
import com.example.visibletime.R;
import com.example.visibletime.Statistics;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class StatisticsDayFragment extends Fragment {

    private final String tag = "로그";

    View root;
    Context context;

    /**날짜*/
    Calendar calendar;
    String today;
    SimpleDateFormat dateFormat;
    SimpleDateFormat titleFormat;
    String tmpDateForSearch;       // 기록을 저장한 것을 분류하기 위한 값

    /**Report Data*/
    ArrayList<ReportData> totalArrayList;     // Data를 선별하기 앞서 전체 데이터를 저장함.
    ArrayList<ReportData> arrayListSelected;    // 날짜에 맞춰 선별된 list

    /**Plan Data*/
    ArrayList<PlanData> totalPlanDataArrayList;
    ArrayList<PlanData> selectedPlanDataArrayList;

    /**StatisticsData Data*/
    ArrayList<StatisticsData> statisticsDataArrayList;    // 통계데이터, 리사이클러뷰 용
    ArrayList<StatisticsData> statisticsChildDataArrayList;    // 통계데이터, 원형 그래프 용



    /**StatisticsData 데이터 모으기, 정렬 역할을 하는 클래스*/
    Statistics statistics;

    /**
     * RecyclerView
     */
    private RecyclerView mRecyclerView;
    private StatisticsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    /**Butter knife*/
    @BindView(R.id.titleBtn_StatisticsDay)
    Button titleBtn;
    @BindView(R.id.alarmTextView_StatisticsDay)
    TextView alarmTextView;
    @BindView(R.id.backButton_StatisticsDay)
    ImageView backButton;
    @BindView(R.id.nextButton_StatisticsDay)
    ImageView nextButton;
    @BindView(R.id.linearLayout_StatisticsDay)
    LinearLayout linearLayout;
    @BindView(R.id.pieChart_StatisticsDay)
    com.github.mikephil.charting.charts.PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(tag,"StatisticsDayFragment - onCreateView() |  ");
        root = inflater.inflate(R.layout.fragment_statistics_day, container, false);        // Inflate the layout for this fragment
        this.context = container.getContext();
        ButterKnife.bind(this, root);

        getCurrentDate();   // 날짜 설정
        setTitle();         // 날짜 Title 및
        // ArrayList 초기화
        totalArrayList = new ArrayList<>();
        arrayListSelected = new ArrayList<>();

        totalPlanDataArrayList = new ArrayList<>();
        selectedPlanDataArrayList = new ArrayList<>();

        statisticsDataArrayList = new ArrayList<>();
        statisticsChildDataArrayList = new ArrayList<>();

        // Statistics 객체 생성
        statistics = new Statistics();

        //
        setRecyclerView();

        getListOfDataFromSharedPreferences();
        Log.d(tag,"StatisticsDayFragment - onCreateView() | totalArrayList.size() "+totalArrayList.size());

        selectDate();
        Log.d(tag,"StatisticsDayFragment - onCreateView() | arrayListSelected.size() "+arrayListSelected.size());
        for(int i = 0; i<arrayListSelected.size(); i++){
            Log.d(tag," ");
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 이름: "+arrayListSelected.get(i).getCategory());
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 부모: "+arrayListSelected.get(i).getCategoryParent());
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 시간: "+arrayListSelected.get(i).getTimeDuration());
        }

        getListOfPlanDataFromSharedPreferences();
        Log.d(tag,"StatisticsDayFragment - onCreateView() | totalPlanDataArrayList.size(): "+totalPlanDataArrayList.size());
        selectPlanDate();
        Log.d(tag,"StatisticsDayFragment - onCreateView() | selectedPlanDataArrayList.size() "+selectedPlanDataArrayList.size());

        setVisibleLinearLayout();

        statistics.setStatisticsArrayList(arrayListSelected, statisticsDataArrayList);
        Log.d(tag,"StatisticsDayFragment - onCreateView() | 실행값 추가 statisticsDataArrayList.size() "+statisticsDataArrayList.size());

        statistics.inputTargetTimeStatisticsArrayList(selectedPlanDataArrayList, statisticsDataArrayList);
        Log.d(tag,"StatisticsDayFragment - onCreateView() | 목표값 추가 statisticsDataArrayList.size() : "+statisticsDataArrayList.size());

        for(int i=0; i<statisticsDataArrayList.size(); i++){
            Log.d(tag," ");
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 타입: "+statisticsDataArrayList.get(i).getType());
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 부모 "+statisticsDataArrayList.get(i).getCategoryParent());
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 이름: "+statisticsDataArrayList.get(i).getCategory());
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 색상: "+statisticsDataArrayList.get(i).getColor());
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 시간: "+statisticsDataArrayList.get(i).getActionTime());
        }
        statistics.sortStatisticsArrayList(statisticsDataArrayList);
        for(int i=0; i<statisticsDataArrayList.size(); i++){
            Log.d(tag," ");
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 타입: "+statisticsDataArrayList.get(i).getType());
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 부모 "+statisticsDataArrayList.get(i).getCategoryParent());
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 이름: "+statisticsDataArrayList.get(i).getCategory());
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 색상: "+statisticsDataArrayList.get(i).getColor());
            Log.d(tag,"StatisticsDayFragment - onCreateView() | 시간: "+statisticsDataArrayList.get(i).getActionTime());
        }
        statistics.setStatisticsChildDataArrayList(statisticsDataArrayList, statisticsChildDataArrayList);
        Log.d(tag,"StatisticsDayFragment - onCreateView() | statisticsChildDataArrayList.size() : "+statisticsChildDataArrayList.size());

        initGraph();
        return root;
    }

    @OnClick(R.id.titleBtn_StatisticsDay)
    public void titleBtnClicked(){
        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                Log.d(tag,"StatisticsDayFragment - onDateSet() |  ");
                // DatePickerDialog로 요일을 구할 수 없기 때문에 Calendar & SimpleDateFormat 를 통해 얻음;
//                Calendar calendarForWeek = Calendar.getInstance();
//                calendarForWeek.set(year, month, date);
//                Date dateForWeek = new Date(calendarForWeek.getTimeInMillis());
                calendar.set(year, month, date);

//                SimpleDateFormat formatWeek = new SimpleDateFormat("yyyy-MM-dd(EE)");

                // 날짜 정보 & RecordData를 선별하기 위한 값 얻음.
                tmpDateForSearch = dateFormat.format(calendar.getTime());             // 선별하기 위한 값
                Log.d(tag,"StatisticsDayFragment - onDateSet() | dateForSearch 날짜 선택 후: "+tmpDateForSearch);
                renewStatistics();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        dialog.getDatePicker().setMaxDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
        dialog.show();
    }

    // 해당 프래그먼트가 처음 시작되었을 때는, 오늘 날짜를 보여주기 위한 초기화 작업.
    private void getCurrentDate(){
        calendar = Calendar.getInstance();
        titleFormat = new SimpleDateFormat("yyyy-MM-dd(EE)");
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        today = dateFormat.format(calendar.getTime());
        tmpDateForSearch = dateFormat.format(calendar.getTime());
        Log.d(tag,"StatisticsDayFragment - getCurrentDate() | dateForSearch 날짜 선택 전: "+tmpDateForSearch);
    }

    // 날짜에 따른 날짜 title & back, next 버튼 표시 변화
    private void setTitle(){
        if(tmpDateForSearch.equals(today)){
            titleBtn.setText("오늘("+titleFormat.format(calendar.getTime())+")");
            nextButton.setVisibility(View.GONE);
        } else {
            titleBtn.setText(titleFormat.format(calendar.getTime()));
            nextButton.setVisibility(View.VISIBLE);
        }

    }

    /**통게 그래프*/
    private void initGraph() {
        // 그래프
        pieChart = (PieChart) root.findViewById(R.id.pieChart_StatisticsDay);

        ArrayList<PieEntry> categorys = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for(int i=0; i<statisticsChildDataArrayList.size(); i++){
            StatisticsData statisticsData = statisticsChildDataArrayList.get(i);
            categorys.add(new PieEntry(statisticsData.getActionTime()));
//            categorys.add(new PieEntry(statisticsData.getActionTime(), statisticsData.getCategory()));
            colors.add(statisticsData.getColor());
        }


        PieDataSet pieDataSet = new PieDataSet(categorys , "실행한 활동의 비율");

        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(10f);


        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);     // 마크 표시
//        pieChart.setCenterText("날짜");
        pieChart.setCenterTextSize(20f);
        pieChart.setUsePercentValues(true);
        pieChart.animate();

//        Date today = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        String todayStr = sdf.format(today);
//
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(today);
//        int weekIf = cal.get(Calendar.WEEK_OF_YEAR);
//
//        Log.d(tag,"MainActivity - onCreate() | 주차 "+weekIf+"주차 입니다.");
    }

    /**
     * ● Shared에 저장된 값을 가져온다.
            ○ 해당 ID를 가지는 Shared를 불러온다.
                 ⇒ getSharedPreferences(파일명)
                 ⇒ getString(key)
            ○ ID에 해당하는 SharedPreference를 불러온다.
                 ⇒ getSharedPreferences(ID)
            ○ 얻은 SharedPreference의 report라는 key에 있는 값을 JSONArray & JSONObject로 만든다.
            ○ For문을 통해 해당 JSONObject의 값을 ArrayList<ReportData> totalArrayList 에 넣는다.
     *
     * */
    private void getListOfDataFromSharedPreferences(){
        SharedPreferences sharedPresesntID = this.getActivity().getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(presentID, Context.MODE_PRIVATE);

        try {
            // ● JSONArray 생성
            JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("report", null) + "]");
            // ● 반복문(JSONArray.length())
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

//                String dateForSearch, String dateForSort, String category, String categoryParent,
//                int level, String content, int color, String timeStart, String timeEnd, int timeDuration)

                // ○ JSON에 String으로 저장된 값을 ReportData 데이터 타입에 맞게 변환을 위한 변수 선언
                int level = 0;
                int color = 0;
                int timeDuration = 0;
                int dateForSort =0;
                // int 형을 얻기 위한 데이터 변환
                dateForSort = Integer.parseInt(jsonObject.getString("dateForSort"));
                level = Integer.parseInt(jsonObject.getString("level"));
                color = Integer.parseInt(jsonObject.getString("color"));
                timeDuration = Integer.parseInt(jsonObject.getString("timeDuration"));

                // ○ ArrayList에 값 넣기
                // Data 객체 생성 & 값 넣어주기
                ReportData reportData = new ReportData();
                reportData.setDateForSearch(jsonObject.getString("dateForSearch"));
                reportData.setDateForSort(dateForSort);
                reportData.setCategory(jsonObject.getString("category"));
                reportData.setCategoryParent(jsonObject.getString("categoryParent"));
                reportData.setLevel(level);
                reportData.setContent(jsonObject.getString("content"));
                reportData.setColor(color);
                reportData.setTimeStart(jsonObject.getString("timeStart"));
                reportData.setTimeEnd(jsonObject.getString("timeEnd"));
                reportData.setType(jsonObject.getString("type"));
                reportData.setTimeDuration(timeDuration);

                // totalArrayList 값 넣어주기
                totalArrayList.add(reportData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag, "StatisticsDayFragment - onLoginBtnClicked() | Exception " + e);
        }
    }

    /**selectDate()
     * ● 값이 일치하는 것을 선별한다. (yyyyMMdd)
     *      ○  For문
     *          - totalArrayList 만큼 반복한다.
     *          -  totalArrayList에서 tmpDateForSearch일치 하는 값을 arrayListSelected에 추가한다.
     * ● 데이터 정렬
     *      ○ Collections.sort();
     * */
    private void selectDate(){
        Log.d(tag,"StatisticsDayFragment - selectDate() |  ");
        for(int i=0; i<totalArrayList.size(); i++){
            if(totalArrayList.get(i).getDateForSearch().equals(tmpDateForSearch)){
                arrayListSelected.add(totalArrayList.get(i));
            }
        }
        if(arrayListSelected.size() == 0){
            alarmTextView.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.INVISIBLE);
        } else {
            alarmTextView.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
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

        SharedPreferences sharedPresesntID = getActivity().getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(presentID, Context.MODE_PRIVATE);

        // 값이 있는지 여부 확인하기
        if(sharedPreferences.getString("plan",null) == null){
//            alarmTextView.setVisibility(View.VISIBLE);
        } else {
//            alarmTextView.setVisibility(View.GONE);
            try {
                // ● JSONArray 생성
                JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("plan", null) + "]");
                // ● 반복문(JSONArray.length())
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // ○ JSON에 String으로 저장된 값을 AlarmData 데이터 타입에 맞게 변환을 위한 변수 선언
                    int color = 0;
                    int type = -1;      // 0은 부모, 1은 자식이므로
                    int targetTime = 0;

                    // boolean isChecked를 얻기 위한 데이터 변환

                    // int hour_24, hour_12, minute 을 얻기 위한 데이 변환
                    color = Integer.parseInt(jsonObject.getString("color"));
                    type = Integer.parseInt(jsonObject.getString("type"));
                    targetTime = Integer.parseInt(jsonObject.getString("targetTime"));

                    // ○ ArrayList에 값 넣기
                    // Data 객체 생성 & 값 넣어주기
                    PlanData planData = new PlanData();
                    planData.setRoutine(jsonObject.getString("routine"));
                    planData.setDateForSearch(jsonObject.getString("dateForSearch"));
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
    /**selectDate()
     * ● 값이 일치하는 것을 선별한다. (yyyyMMdd)
     *      ○  For문
     *          - totalArrayList 만큼 반복한다.
     *          -  totalArrayList에서 tmpDateForSearch일치 하는 값을 arrayListSelected에 추가한다.
     * ● 데이터 정렬
     *      ○ Collections.sort();
     * */
    private void selectPlanDate(){
        Log.d(tag,"StatisticsDayFragment - selectDate() |  ");
        for(int i=0; i<totalPlanDataArrayList.size(); i++){
            if(totalPlanDataArrayList.get(i).getDateForSearch().equals(tmpDateForSearch)){
                selectedPlanDataArrayList.add(totalPlanDataArrayList.get(i));
            }
        }
//        if(selectedPlanDataArrayList.size() == 0){
//            alarmTextView.setVisibility(View.VISIBLE);
//        } else {
//            alarmTextView.setVisibility(View.GONE);
//        }
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


        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView_StatisticsDay);  //RecyclerView 객체 초기화(initiate)
        mAdapter = new StatisticsAdapter(getActivity(), statisticsDataArrayList);    // Custom Adater 객체 생성 & Adapter와 mArrayList(Data List) 연결.
        mRecyclerView.setAdapter(mAdapter);             // RecyclerView와 Adater와 연결
        mLayoutManager = new LinearLayoutManager(getActivity());     // RecyclerView를 어떻게 보여줄지 결정
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.backButton_StatisticsDay)
    public void backButtonClicked(){
        calendar.add(calendar.DATE, -1);
        tmpDateForSearch = dateFormat.format(calendar.getTime());
        Log.d(tag,"StatisticsDayFragment - backButtonClicked() | tmpDateForSearch: "+tmpDateForSearch);
        renewStatistics();
    }

    @OnClick(R.id.nextButton_StatisticsDay)
    public void nextButtonClicked(){
        calendar.add(calendar.DATE, +1);
        tmpDateForSearch = dateFormat.format(calendar.getTime());
        Log.d(tag,"StatisticsDayFragment - nextButtonClicked() | tmpDateForSearch "+tmpDateForSearch);
        renewStatistics();
    }
    /**값 갱신 용도*/
    private void renewStatistics(){
        setTitle();
        arrayListSelected.clear();
        selectDate();
        selectedPlanDataArrayList.clear();
        selectPlanDate();

        setVisibleLinearLayout();

        statisticsDataArrayList.clear();
        statistics.setStatisticsArrayList(arrayListSelected, statisticsDataArrayList);      //실행값 추가
        statistics.inputTargetTimeStatisticsArrayList(selectedPlanDataArrayList, statisticsDataArrayList); // 목표값 추가
        statistics.sortStatisticsArrayList(statisticsDataArrayList);
        statisticsChildDataArrayList.clear();       //해당 날짜에 대한 데이터를 집어 넣기 전 기존 값 삭제
        statistics.setStatisticsChildDataArrayList(statisticsDataArrayList, statisticsChildDataArrayList);
        initGraph();
        mAdapter.notifyDataSetChanged();
    }

    private void setVisibleLinearLayout(){
        if(arrayListSelected.size() == 0 && selectedPlanDataArrayList.size() == 0){
            linearLayout.setVisibility(View.INVISIBLE);
        }else{
            linearLayout.setVisibility(View.VISIBLE);
        }
    }
}