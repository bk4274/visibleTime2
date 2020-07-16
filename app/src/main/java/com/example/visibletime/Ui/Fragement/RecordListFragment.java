package com.example.visibletime.Ui.Fragement;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Adapter.ReportAdatper;
import com.example.visibletime.Data.ReportData;
import com.example.visibletime.R;
import com.example.visibletime.Ui.EditReportActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordListFragment extends Fragment {

    private final String tag = "로그";
    private final String TAG = "로그";

    View root;
    Context context;

    /**날짜*/
    String tmpDateForSearch;       // 기록을 저장한 것을 분류하기 위한 값
    String title;

    /**Report Data*/
    ArrayList<ReportData> totalArrayList;     // Data를 선별하기 앞서 전체 데이터를 저장함.
    ArrayList<ReportData> arrayListSelected;    // 날짜에 맞춰 선별된 list

    /**
     * RecyclerView
     */
    private RecyclerView mRecyclerView;
    private ReportAdatper mAdapter;
    private LinearLayoutManager mLayoutManager;

    /**
     * onActivityResult를 위한 변수
     */
    private static final int EDIT_REPORT = 544;

    RecordGraphFragment recordGraphFragment;

    /**
     * ButterKnife
     */
    @BindView(R.id.dateTextView_RecordListFragment)
    TextView dateTextView;

    @BindView(R.id.alarmTextView_RecordListFragment)
    TextView alarmTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(tag,"RecordListFragment - onCreateView() |  ");
        root = inflater.inflate(R.layout.fragment_record_list, container, false);        // Inflate the layout for this fragment
        ButterKnife.bind(this, root);

        if(getArguments() != null){
            tmpDateForSearch = getArguments().getString("tmpDateForSearch");
            title = getArguments().getString("title");
            dateTextView.setText(title);
//            recordGraphFragment = (RecordGraphFragment) getArguments().get("recordGraphFragment");
            Log.d(TAG,"RecordListFragment - onCreateView() | 그래프 프래그먼트 객체 전달 확인: "+recordGraphFragment);
        }

        setHasOptionsMenu(true);
        this.context = container.getContext();

        // 초기화
        totalArrayList = new ArrayList<>();
        arrayListSelected = new ArrayList<>();

        getListOfDataFromSharedPreferences();
        Log.d(tag,"RecordListFragment - onCreateView() | totalArrayList.size() "+totalArrayList.size());
        selectDate();
        Log.d(tag,"RecordListFragment - onCreateView() | arrayListSelected.size() "+arrayListSelected.size());
        setRecyclerView();
        editReport();
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(tag,"RecordListFragment - onPause() |  ");
        saveReportDataList();
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
        SharedPreferences sharedPresesntID = this.getActivity().getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(presentID, Context.MODE_PRIVATE);

        if(totalArrayList.size() != 0){
            totalArrayList.clear();
        }

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
            Log.d(tag, "CategoryActivity - onLoginBtnClicked() | Exception " + e);
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
        Log.d(tag,"RecordListFragment - selectDate() |  ");
        for(int i=0; i<totalArrayList.size(); i++){
            if(totalArrayList.get(i).getDateForSearch().equals(tmpDateForSearch)){
                arrayListSelected.add(totalArrayList.get(i));
            }
        }
        Collections.sort(arrayListSelected);        // 시간 내림차순으로 정렬
        if(arrayListSelected.size() == 0){
            alarmTextView.setVisibility(View.VISIBLE);
        } else {
            alarmTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 《리사이클러뷰 프로세스》
     * ■ setRecyclerView()
     * □ 목적: 리사이클러뷰 생성하여 View단에 보여주기
     * 1. RecyclerView, Adapter,(Linear)LayoutManager 생성
     * 2. RecyclerView 객체와 Adapter, (Linear)LayoutManager 객체 연결 : set()
     */
    private void setRecyclerView() {
        Log.d(tag,"RecordListFragment - setRecyclerView() |  ");
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView_RecordListFragment);  //RecyclerView 객체 초기화(initiate)
        mAdapter = new ReportAdatper(getActivity(), arrayListSelected);    // Custom Adater 객체 생성 & Adapter와 mArrayList(Data List) 연결.
        mRecyclerView.setAdapter(mAdapter);             // RecyclerView와 Adater와 연결
        mLayoutManager = new LinearLayoutManager(getActivity());     // RecyclerView를 어떻게 보여줄지 결정
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.notifyDataSetChanged();
//        //child View들을 구분선을 만들어 주는 메서드
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    /** editReport()
     * ReportAdapter 에서 interface를 정의하고, RecordFragment에서 구현함으로써
     * 수정 이벤트를 Activity에서 수행 가능하게 함.
     * 자세한 사항은 https://recipes4dev.tistory.com/168 참조
     * */
    public void editReport(){
        /**Adapter의 ViewHolder에서 클릭시 이벤트 수행*/
        mAdapter.setOnItemClickListener(new ReportAdatper.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(tag,"RecordListFragment - onItemClick() | position: "+position);
                int totalArrayListPosition =0;         // ReportAdapter에서 클릭한 Data가 totalArrayList에서의 위치
                // item에 해당하는 Data가 totalArrayList에서 몇번째에 위치하는지 찾기
                for(int i=0; i<totalArrayList.size(); i++){
                    if(totalArrayList.get(i).getDateForSearch().equals(arrayListSelected.get(position).getDateForSearch())&&
                            totalArrayList.get(i).getDateForSort()==arrayListSelected.get(position).getDateForSort()){
                        totalArrayListPosition = i;
                    }
                }
                Log.d(tag,"RecordListFragment - onItemClick() | totalArrayListPosition: "+totalArrayListPosition);

                // 수정으로 보낼 데이터 정리
                ReportData reportData = totalArrayList.get(totalArrayListPosition);

                /*인텐트 생성 & startActivityForResult로 값 받아오기.
                 * */
                Intent intent = new Intent(getActivity(), EditReportActivity.class);
                intent.putExtra("category", reportData.getCategory());
                intent.putExtra("categoryParent", reportData.getCategoryParent());
                intent.putExtra("timeDuration", reportData.getTimeDuration());
                intent.putExtra("timeStart", reportData.getTimeStart());
                intent.putExtra("timeEnd", reportData.getTimeEnd());
                intent.putExtra("level", reportData.getLevel());
                intent.putExtra("content", reportData.getContent());
                intent.putExtra("color", reportData.getColor());
                intent.putExtra("position", totalArrayListPosition);
                Log.d(tag,"RecordListFragment - onItemClick() | reportData.getTimeDuration(): "+reportData.getTimeDuration());
                startActivityForResult(intent, EDIT_REPORT);
            }
        }) ;
    }
    /**
     * 수정 된 값 받기
     * - onActivityResult에서 받은 포지션으로 totalArrayList에 값 변경하기
     * - selectDate() 호출
     *      - arrayListSelected 내용물 삭제
     * - mAdapter.notifyDataSetChanged(); 데이터 변경 하기
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == EDIT_REPORT) {
            if (resultCode == Activity.RESULT_OK) {
                int resultPosition = resultIntent.getIntExtra("position", -1);
                if(resultPosition != -1){
                    ReportData reportData;

                    // 값을 전달 받은 경우
                    reportData = totalArrayList.get(resultPosition);
                    reportData.setCategory(resultIntent.getStringExtra("category"));
                    reportData.setCategoryParent(resultIntent.getStringExtra("categoryParent"));
                    reportData.setLevel(resultIntent.getIntExtra("level", 0));
                    reportData.setContent(resultIntent.getStringExtra("content"));
                    reportData.setColor(resultIntent.getIntExtra("color", 0));

                    // 데이터가 중복으로 들어가는 문제가 생겨서 arrayListSelected 안에 있는 내용물 삭제
                    arrayListSelected.clear();
                    selectDate();
                    mAdapter.notifyDataSetChanged();
                }
            } else if(resultCode == Activity.RESULT_FIRST_USER){
                int resultPosition = resultIntent.getIntExtra("position", -1);
                if(resultPosition != -1){
                    // 값을 전달 받은 경우
//                    totalArrayList.remove(resultPosition);
//                    saveReportDataList();
                    getListOfDataFromSharedPreferences();
                    Log.d(tag,"RecordFragment - onActivityResult() | totalArrayList.size(): "+totalArrayList.size());
                    // 데이터가 중복으로 들어가는 문제가 생겨서 arrayListSelected 안에 있는 내용물 삭제
                    arrayListSelected.clear();
                    selectDate();
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG,"RecordListFragment - onActivityResult() | 문제 확인 ");
//                    recordGraphFragment.getListOfDataFromSharedPreferences();
                }
            }
        }
    }


    /**saveReportDataList()
     * ■ 메서드 목적:
     *      - mArrayList에 있는 값을 SharedPreference에 저장하기 위함.
     * ■ 프로세스
     *      ● 해당 아이디를 불러온다.
     *      ● 우선 기존에 있는 SharedPreference에 있는 값을 지운다.
     *
     *      ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
     *
     * ■ 호출
     *      - onPause()
     * */
    private void saveReportDataList(){
        Log.d(tag,"RecordListFragment - saveCategoryDataList() |  ");

        SharedPreferences sharedPresesntID = this.getActivity().getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ● ID를 파일명으로 하는 Shared.xml 파일 생성 및 기존 값 지우기
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(presentID, Context.MODE_PRIVATE);
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

    public void changeDate(String tmpDateForSearch, String title){
        this.tmpDateForSearch = tmpDateForSearch;
        this.title = title;
        Log.d(tag,"RecordListFragment - changeDate() | tmpDateForSearch: " + tmpDateForSearch);

        dateTextView.setText(title);
        arrayListSelected.clear();
        selectDate();
        mAdapter.notifyDataSetChanged();
    }
}