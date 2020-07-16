package com.example.visibletime.Ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.visibletime.Adapter.CategoryAdapter;
import com.example.visibletime.Adapter.SelectReportAdatper;
import com.example.visibletime.Data.CatagoryData;
import com.example.visibletime.Data.ReportData;
import com.example.visibletime.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PickReportActivity extends AppCompatActivity {

    private final String tag = "로그";
    /**
     * View
     */
    @BindView(R.id.dayTextView_PickReport)
    TextView dayTextView;
    @BindView(R.id.enterBtn_PickReport)
    ImageButton enterBtn;
    @BindView(R.id.alarmTextView_PickReport)
    TextView alarmTextView;
    @BindView(R.id.linearLayout_PickReport)
    LinearLayout linearLayout;

    /**intent로 받은 데이터*/
    String type;
    String tmpDateForSearch;       // 기록을 저장한 것을 분류하기 위한 값


    /**리사이클러뷰*/
    ArrayList<ReportData> totalArrayList;     // Data를 선별하기 앞서 전체 데이터를 저장함.
    ArrayList<ReportData> arrayListSelected;    // 날짜에 맞춰 선별된 list
    SelectReportAdatper mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_report);
        ButterKnife.bind(this);
        initView();
        getListOfDataFromSharedPreferences();
        selectDate();
        setRecyclerView();
    }

    /**intent에서 받은 type에 따라 Report데이터를 선별하는 조건이 달라야 함.
     * */
    private void initView() {
        Intent intent = getIntent(); /*데이터 수신*/
        dayTextView.setText(intent.getExtras().getString("dayTitle"));
        tmpDateForSearch= intent.getExtras().getString("tmpDateForSearch");
        type = intent.getExtras().getString("type");

        // 색 바꿈으로써 자신이 good을 선택하는지, bad을 선택하는지 알 수 있다.
        if(type.equals("good")){
            linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
        } else{
            linearLayout.setBackgroundColor(Color.parseColor("#F54279"));
        }

        // arrayList 생성
        totalArrayList = new ArrayList<>();
        arrayListSelected = new ArrayList<>();
    }

    /**
     * ● Shared에 저장된 값을 가져온다.
     ○ 해당 ID를 가지는 Shared를 불러온다.
     ⇒ getSharedPreferences(파일명)
     ⇒ getString(key)
     ○ ID에 해당하는 SharedPreference를 불러온다.
     ⇒ getSharedPreferences(ID)
     ○ 얻은 SharedPreference의 report라는 key에 있는 값을 JSONArray & JSONOBject로 만든다.
     ○ For문을 통해 해당 JSONObject의 값을 ArrayList<ReportData> totalArrayList 에 넣는다.
     *
     * */
    private void getListOfDataFromSharedPreferences(){
        Log.d(tag,"PickReportActivity - getListOfDataFromSharedPreferences() |  ");
        SharedPreferences sharedPresesntID = this.getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = this.getSharedPreferences(presentID, Context.MODE_PRIVATE);

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
            Log.d(tag,"PickReportActivity - getListOfDataFromSharedPreferences() | totalArrayList.size() "+totalArrayList.size());
            for(int i = 0; i<totalArrayList.size(); i++){
                Log.d(tag,"PickReportActivity - getListOfDataFromSharedPreferences() | totalArrayList.get(i).getType() "+totalArrayList.get(i).getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag,"PickReportActivity - getListOfDataFromSharedPreferences() | Exception "+ e);
        }
    }

    /**selectDate()
     * ● 값이 일치하는 것을 선별한다. (yyyyMMdd)
     *      ○ ArrayList<ReportData> arrayListSelected 생성한다.
     *          - 그 이유는 totalArrayList에 있는 값 중에서 tmpDateForSearch에 일치하는 값만 불러와 따로 저장하기 위함.
     *          - 그리고 지역변수로 선언하는 이유는 날짜를 선택함에 따라 새로 데이터를 집어넣어야 하기 때문.
     *      ○  For문
     *          - totalArrayList 만큼 반복한다.
     *          -  totalArrayList에서 tmpDateForSearch일치 하는 값을 arrayListSelected에 추가한다.
     * ● 데이터 정렬
     *      ○ Collections.sort();
     * */
    private void selectDate(){
        Log.d(tag,"PickReportActivity - selectDate() |  ");
        for(int i=0; i<totalArrayList.size(); i++){
            if(totalArrayList.get(i).getDateForSearch().equals(tmpDateForSearch) &&
                    (totalArrayList.get(i).getType().equals("so") || totalArrayList.get(i).getType().equals(type))){
                arrayListSelected.add(totalArrayList.get(i));
            }
        }
        Log.d(tag,"PickReportActivity - selectDate() | arrayListSelected.size() "+arrayListSelected.size());
        
//        Collections.sort(arrayListSelected);        // 시간 내림차순으로 정렬
        
        if(arrayListSelected.size() == 0){
            alarmTextView.setVisibility(View.VISIBLE);
        } else {
            alarmTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 《리사이클러뷰 프로세스》
     * ■ setRecyclerView()
     *      □ 목적: 리사이클러뷰 생성하여 View단에 보여주기
     *          1. RecyclerView, Adapter,(Linear)LayoutManager 생성
     *          2. RecyclerView 객체와 Adapter, (Linear)LayoutManager 객체 연결 : set()
     */
    private void setRecyclerView() {
        Log.d(tag,"CategoryActivity - setRecyclerView() |  ");

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_PickReport);  //RecyclerView 객체 초기화(initiate)
        mAdapter = new SelectReportAdatper(this, arrayListSelected,type);    // Custom Adater 객체 생성 & Adapter와 mArrayList(Data List) 연결.
        mRecyclerView.setAdapter(mAdapter);             // RecyclerView와 Adater와 연결
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);     // RecyclerView를 어떻게 보여줄지 결정
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // 데이터 갱신
//        mAdapter.notifyDataSetChanged();


//        //child View들을 구분선을 만들어 주는 메서드
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @OnClick(R.id.enterBtn_PickReport)
    public void enterBtnClicked(){
        arrayListSelected = mAdapter.getReportDataArrayList();
        /**특정 날짜의 ArrayList의 변경된 값을 전체 값 ArrayList에 저장하는 방법
         * ■ arrayListSelected 사이즈 동안 반복하여
         *      - type 속성이 null이 아닐 때 즉 ,good 과 bad가 있는 경우
         *      - totalArrayList 를 반복하여
         *      - 해당 totalArrayList와 arrayListSelected의 식별자가 같은 경우
         *      - 해당 totalArrayList의 type을 변경한다.
         * */
        for (int i = 0; i < arrayListSelected.size(); i++) {
            for (int j = 0; j < totalArrayList.size(); j++) {
                if (totalArrayList.get(j).getDateForSearch().equals(arrayListSelected.get(i).getDateForSearch())
                        && totalArrayList.get(j).getDateForSort() == arrayListSelected.get(i).getDateForSort()) {
                    totalArrayList.get(j).setType(arrayListSelected.get(i).getType());
                    Log.d(tag, "PickReportActivity - enterBtnClicked() | i: " + i);
                    Log.d(tag, "PickReportActivity - enterBtnClicked() | totalArrayList.get(j).getType() " + j + totalArrayList.get(j).getType());
                }
            }
        }
        saveReportDataList();
        Log.d(tag,"PickReportActivity - enterBtnClicked() | 데이터 저장후 종료 ");
        finish();
    }

    /**saveReportDataList()
     * ■ 메서드 목적:
     *      - totalArrayList에 있는 값을 SharedPreference에 저장하기 위함.
     * ■ 프로세스
     *      ● 해당 아이디를 불러온다.
     *      ● 우선 기존에 있는 SharedPreference에 있는 값을 지운다.
     *
     *      ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
     *
     * ■ 호출
     *      - enterBtnClicked()
     * */
    private void saveReportDataList(){
        Log.d(tag,"PickReportActivity - saveReportDataList() |  ");

        SharedPreferences sharedPresesntID = getSharedPreferences("presentID",MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ● ID를 파일명으로 하는 Shared.xml 파일 생성 및 기존 값 지우기
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
        SharedPreferences.Editor editorCategory = sharedPreferences.edit();
        editorCategory.remove("report");
        editorCategory.commit();

        // ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
        if(totalArrayList.size() != 0){
            for(int i=0; i<totalArrayList.size(); i++){
                /**JSON 형태로 저장*/
                String strDataOfJSON;
                strDataOfJSON = "{ \"dateForSearch\"" + ":" + "\"" + totalArrayList.get(i).getDateForSearch() + "\"" + ","
                        + "\"dateForSort\"" + ":" + "\"" + totalArrayList.get(i).getDateForSort() + "\"" + ","
                        + "\"category\"" + ":" + "\"" + totalArrayList.get(i).getCategory() + "\"" + ","
                        + "\"categoryParent\"" + ":" + "\"" + totalArrayList.get(i).getCategoryParent() + "\"" + ","
                        + "\"level\"" + ":" + "\"" + totalArrayList.get(i).getLevel() + "\"" + ","
                        + "\"content\"" + ":" + "\"" + totalArrayList.get(i).getContent() + "\"" + ","
                        + "\"color\"" + ":" + "\"" + totalArrayList.get(i).getColor() + "\"" + ","
                        + "\"timeStart\"" + ":" + "\"" + totalArrayList.get(i).getTimeStart() + "\"" + ","
                        + "\"timeEnd\"" + ":" + "\"" + totalArrayList.get(i).getTimeEnd() + "\"" + ","
                        + "\"type\"" + ":" + "\"" + totalArrayList.get(i).getType() + "\"" + ","
                        + "\"timeDuration\"" + ":" + "\"" + totalArrayList.get(i).getTimeDuration() + "\""
                        + "}";

                // □ 값을 처음으로 저장하는 확인
                String report = sharedPreferences.getString("report", null);
                SharedPreferences.Editor editorReport = sharedPreferences.edit();

                if (report == null) {
                    // 첫 카테고리 정보를 저장하는 경우
                    editorReport.putString("report", strDataOfJSON);
                    editorReport.commit();
                } else {
                    // 카테고리 정보가 처음이 아닌 경우
                    editorReport.putString("report", sharedPreferences.getString("report", "") + "," + strDataOfJSON);
                    editorReport.commit();
                }
            }
        }
    }
}
