package com.example.visibletime.Ui.Fragement;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Adapter.ReportAdatper;
import com.example.visibletime.Data.DiaryData;
import com.example.visibletime.Data.PlanData;
import com.example.visibletime.Data.ReportData;
import com.example.visibletime.R;
import com.example.visibletime.Ui.WriteDiaryActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiaryFragment extends Fragment {

    private final String tag = "로그";

    View root;
    Context context;


    /**날짜*/
    Calendar calendar;
    SimpleDateFormat formatHeader;
    SimpleDateFormat dateFormat;
    String tmpDateForSearch;       // 기록을 저장한 것을 분류하기 위한 값
    String dayTitle;       // PickReportActivity 에서 Title로 쓰일 값


    /**Report Data*/
    ArrayList<ReportData> totalArrayList;     // Data를 선별하기 앞서 전체 데이터를 저장함.
    ArrayList<ReportData> goodArrayList;    // 해당 날짜에 good type 인 list
    ArrayList<ReportData> badArrayList;    // 해당 날짜에 bad type인 list

    /**Diary Data*/
    ArrayList<DiaryData> totalDiaryArrayList;     // Data를 선별하기 앞서 전체 데이터를 저장함.
    boolean isExisted;
    ReportAdatper goodAdapter;
    ReportAdatper badAdapter;



    /**Text*/
    private Uri imageUri = null;


    /**메뉴*/
    Menu mMenu;
    MenuItem editItemMenu;
    /**
     * ButterKnife
     */
    @BindView(R.id.dateTextView_DiaryFragment)
    TextView dateTextView;
    @BindView(R.id.scrollView_DiaryFragment)
    ScrollView scrollView;
    @BindView(R.id.goodTextView_DiaryFragment)
    TextView goodTextView;
    @BindView(R.id.badTextView_DiaryFragment)
    TextView badTextView;
    @BindView(R.id.willTextView_DiaryFragment)
    TextView willTextView;
    @BindView(R.id.pictureImageView_DiaryFragment)
    ImageView pictureImageView;

    @BindView(R.id.alarmLayout_DiaryFragment)
    LinearLayout alarmLayout;
    @BindView(R.id.goodLinearLayout_DiaryFragment)
    LinearLayout goodLinearLayout;
    @BindView(R.id.badLinearLayout_DiaryFragment)
    LinearLayout badLinearLayout;
    @BindView(R.id.writeDiary_DiaryFragment)
    TextView writeDiary;

    @BindView(R.id.goodConstraintLayout_DiaryFragment)
    androidx.constraintlayout.widget.ConstraintLayout goodConstraintLayout;
    @BindView(R.id.badConstraintLayout_DiaryFragment)
    androidx.constraintlayout.widget.ConstraintLayout badConstraintLayout;
    @BindView(R.id.willConstraintLayout_DiaryFragment)
    androidx.constraintlayout.widget.ConstraintLayout willConstraintLayout;

    /***/
    String good, bad, will;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(tag,"DiaryFragment - onCreateView() |  ");
        root = inflater.inflate(R.layout.fragment_diary, container, false);        // Inflate the layout for this fragment
        this.context = container.getContext();
        ButterKnife.bind(this, root);
        setHasOptionsMenu(true);
        initialization();
        getCurrentDate();
        setRecyclerView();
        return root;
    }

    @Override
    public void onResume() {
        Log.d(tag,"DiaryFragment - onResume() |  ");
        super.onResume();
        // diary data 불러오기
        getListOfDiaryDataFromSharedPreferences();
        try {
            selectDiaryDate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // report data 불러오기
        getListOfDataFromSharedPreferences();
        selectDate();
        // 데이터 있는지 확인하기
        checkNullData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(tag,"DiaryFragment - onCreateOptionsMenu() |  ");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_calendar_edit, menu);
        mMenu = menu;
        Log.d(tag,"DiaryFragment - onCreateOptionsMenu() | mMenu: "+mMenu);
        editItemMenu = menu.findItem(R.id.action_eidt);
        Log.d(tag,"DiaryFragment - onCreateOptionsMenu() | editItemMenu: "+editItemMenu);

        if(goodArrayList.size() == 0 && badArrayList.size() == 0 && isExisted == false){
            editItemMenu.setVisible(false);
        } else{
            editItemMenu.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_calendar :
                Log.d(tag,"DiaryFragment - onOptionsItemSelected() | 캘린더 버튼 클릭 ");

                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        Log.d(tag, "DiaryFragment - onDateSet() |  ");

                        // DatePickerDialog로 요일을 구할 수 없기 때문에 Calendar & SimpleDateFormat 를 통해 얻음;
                        Calendar calendarForWeek = Calendar.getInstance();
                        calendarForWeek.set(year, month, date);
                        Date dateForWeek = new Date(calendarForWeek.getTimeInMillis());
                        SimpleDateFormat formatWeek = new SimpleDateFormat("yyyy년 MM월 dd일(EE)");

                        // 날짜 정보 & RecordData를 선별하기 위한 값 얻음.
                        dateTextView.setText(formatWeek.format(dateForWeek));       //TextView를 위함
                        dayTitle = formatWeek.format(dateForWeek);
                        tmpDateForSearch = dateFormat.format(dateForWeek);             // 선별하기 위한 값
                        Log.d(tag, "DiaryFragment - onDateSet() | dateForSearch 날짜 선택 후: " + tmpDateForSearch);

                        // goodArrayList & badArrayList 사용자가 선택한 날짜에 맞춰기 전 기존 값 지우기
                        goodArrayList.clear();
                        badArrayList.clear();

                        // goodArrayList & badArrayList 다시 날짜에 맞추어 리스트 만들기
                        selectDate();
                        goodAdapter.notifyDataSetChanged();
                        badAdapter.notifyDataSetChanged();

                        // diary data 불러오기
                        isExisted = false;      // false 해주는 이유, selectDiaryDate 과정에서 해당날짜에 데이터가 있으면 true가 나오고
                                                // 그대로 false이면 데이터가 없다는 것을 알리기 위함.
                        try {
                            selectDiaryDate();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        // 데이터 있는지 확인하기
                        checkNullData();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

                dialog.getDatePicker().setMaxDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();
                break;

            case R.id.action_eidt :
                // isExisted 를 false로 바꾸는 이유
                // 문제: WriteDairayActivity에서 데이터 삭제를 하고 나서 다시 본 Fragment를 다시 띄워 졌을 때,
                // 기존 isExisted의 값이 (true)이기 때문에, checkNullData() 값에서 if()문 구분이 잘 안되었기 때문
                // 해결:isExisted가 true로 값이 변경되는 경우는 Shared에 저장 된 값을 불러올 때 실행 되기 때문에, isExisted 값을 false로
                // 초기화 해주자.
                isExisted = false;
                editDiary();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    private void initialization() {
        // 데이터 List 초기화
        totalArrayList = new ArrayList<>();
        goodArrayList = new ArrayList<>();
        badArrayList = new ArrayList<>();
        totalDiaryArrayList = new ArrayList<>();

        scrollView.setVisibility(View.GONE);

        isExisted = false;// false 해주는 이유, selectDiaryDate 과정에서 해당날짜에 데이터가 있으면 true가 나오고
                            // 그대로 false이면 데이터가 없다는 것을 알리기 위함.
    }

    // 해당 프래그먼트가 처음 시작되었을 때는, 오늘 날짜를 보여주기 위한 초기화 작업.
    // 현재시간을 가져와 fromat에 따라 TextView에 보여준다.
    private void getCurrentDate(){

        calendar = Calendar.getInstance();
        formatHeader = new SimpleDateFormat("yyyy년 MM월 dd일(EE)");
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateTextView.setText(formatHeader.format(System.currentTimeMillis()));
        dayTitle = formatHeader.format(System.currentTimeMillis());
        tmpDateForSearch = dateFormat.format(System.currentTimeMillis());
        Log.d(tag,"RecordFragment - getCurrentDate() | dateForSearch 날짜 선택 전: "+tmpDateForSearch);
    }

    /**
     * 《리사이클러뷰 프로세스》
     * ■ setRecyclerView()
     *      □ 목적: 리사이클러뷰 생성하여 View단에 보여주기
     *          1. RecyclerView, Adapter,(Linear)LayoutManager 생성
     *          2. RecyclerView 객체와 Adapter, (Linear)LayoutManager 객체 연결 : set()
     */
    private void setRecyclerView() {
        Log.d(tag,"WriteDiaryActivity - setRecyclerView() |  ");
        RecyclerView goodRecyclerView = (RecyclerView) root.findViewById(R.id.goodRecyclerView_DiaryFragment);
        RecyclerView badRecyclerView = (RecyclerView) root.findViewById(R.id.badRecyclerView_DiaryFragment);
        goodAdapter = new ReportAdatper(getActivity(), goodArrayList);    // Custom Adater 객체 생성 & Adapter와 mArrayList(Data List) 연결.
        badAdapter = new ReportAdatper(getActivity(), badArrayList);    // Custom Adater 객체 생성 & Adapter와 mArrayList(Data List) 연결.

        goodRecyclerView.setAdapter(goodAdapter);             // RecyclerView와 Adater와 연결
        badRecyclerView.setAdapter(badAdapter);             // RecyclerView와 Adater와 연결

        LinearLayoutManager goodLayoutManager = new LinearLayoutManager(getActivity());     // RecyclerView를 어떻게 보여줄지 결정
        goodLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        LinearLayoutManager badLayoutManager = new LinearLayoutManager(getActivity());     // RecyclerView를 어떻게 보여줄지 결정
        badLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        goodRecyclerView.setLayoutManager(goodLayoutManager);
        badRecyclerView.setLayoutManager(badLayoutManager);

    }

    private void getListOfDiaryDataFromSharedPreferences(){
        Log.d(tag,"DiaryFragment - getListOfDiaryDataFromSharedPreferences() |  ");

        // 이미 Shared에서 데이터를 받아온 적이 있어 totalArrayList의 값이 존재한다면
        // 최신 정보를 받아오기 위해 기존 데이터를 삭제한다.
        // ⇒ totalArrayList
        if(totalDiaryArrayList.size()>0){
            totalDiaryArrayList.clear();
        }

        SharedPreferences sharedPresesntID = getActivity().getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(presentID, Context.MODE_PRIVATE);

        try {
            // ● JSONArray 생성
            JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("diary", null) + "]");
            Log.d(tag,"DiaryFragment - getListOfDiaryDataFromSharedPreferences() | jsonArray 생성: "+jsonArray);
            // ● 반복문(JSONArray.length())
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // ○ ArrayList에 값 넣기
                // Data 객체 생성 & 값 넣어주기
                DiaryData diaryData = new DiaryData();
                diaryData.setDateForSearch(jsonObject.getString("dateForSearch"));
                diaryData.setGoodText(jsonObject.getString("goodText"));
                diaryData.setBadText(jsonObject.getString("badText"));
                diaryData.setWillText(jsonObject.getString("willText"));
                diaryData.setPhoto(jsonObject.getString("photo"));
                // totalDiaryArrayList 값 넣어주기
                totalDiaryArrayList.add(diaryData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag,"DiaryFragment - getListOfDiaryDataFromSharedPreferences() | Exception " + e);
        }
        Log.d(tag,"DiaryFragment - getListOfDiaryDataFromSharedPreferences() | totalDiaryArrayList: "+totalDiaryArrayList.size());
    }

    private void selectDiaryDate() throws FileNotFoundException {
        Log.d(tag,"DiaryFragment - selectDiaryDate() |  ");

        pictureImageView.setVisibility(View.GONE);

        for(int i = 0; i < totalDiaryArrayList.size(); i++) {
            if (totalDiaryArrayList.get(i).getDateForSearch().equals(tmpDateForSearch)) {
                isExisted = true;
                goodTextView.setText(totalDiaryArrayList.get(i).getGoodText());
                badTextView.setText(totalDiaryArrayList.get(i).getBadText());
                willTextView.setText(totalDiaryArrayList.get(i).getWillText());
                Log.d(tag,"DiaryFragment - selectDiaryDate() | 사진: "+totalDiaryArrayList.get(i).getPhoto());
                if(totalDiaryArrayList.get(i).getPhoto().equals("null") == false){
                    Log.d(tag,"DiaryFragment - selectDiaryDate() | 사진이 null 이 아닌 경우 ");
                    imageUri = Uri.parse(totalDiaryArrayList.get(i).getPhoto());
                    pictureImageView.setVisibility(View.VISIBLE);
                    Log.d(tag,"DiaryFragment - selectDiaryDate() | 사진 VISIBLE ");
                    InputStream in = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap tempImg = BitmapFactory.decodeStream(in);
                    pictureImageView.setImageBitmap(tempImg);
                }
            }
        }
    }
    /**
     * ● Shared에 저장된 값을 가져온다.
     ○ 해당 ID를 가지는 Shared를 불러온다.
     ⇒ getSharedPreferences(파일명)
     ⇒ getString(key) : 받아온 Value는 ID
     ○ ID에 해당하는 SharedPreference를 불러온다.
     ⇒ getSharedPreferences(ID)
     ○ 얻은 SharedPreference의 report라는 key에 있는 값을 JSONArray & JSONOBject로 만든다.
     ○ For문을 통해 해당 JSONObject의 값을 ArrayList<ReportData> totalArrayList 에 넣는다.
     *
     * */
    private void getListOfDataFromSharedPreferences(){
        Log.d(tag,"WriteDiaryActivity - getListOfDataFromSharedPreferences() |  ");
        SharedPreferences sharedPresesntID = getActivity().getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(presentID, Context.MODE_PRIVATE);

        // 이미 Shared에서 데이터를 받아온 적이 있어 totalArrayList의 값이 존재한다면
        // 최신 정보를 받아오기 위해 기존 데이터를 삭제한다.
        // ⇒ totalArrayList
        if(totalArrayList.size()>0){
            totalArrayList.clear();
        }
        if(goodArrayList.size()>0){
            goodArrayList.clear();
        }if(badArrayList.size()>0){
            badArrayList.clear();
        }
        Log.d(tag,"WriteDiaryActivity - getListOfDataFromSharedPreferences() | sharedPreferences.getString(\"report\", null): "+sharedPreferences.getString("report", null));
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
            Log.d(tag,"WriteDiaryActivity - getListOfDataFromSharedPreferences() | Exception " + e);
        }
    }
    /**selectDate()
     * ● 값이 일치하는 것을 선별한다. (yyyyMMdd)
     *      ○ ArrayList<ReportData> arrayListSelected 생성한다.
     *          - 그 이유는 totalArrayList에 있는 값 중에서 tmpDateForSearch에 일치하는 값만 불러와 따로 저장하기 위함.
     *          - 그리고 지역변수로 선언하는 이유는 날짜를 선택함에 따라 새로 데이터를 집어넣어야 하기 때문.
     *      ○  For문
     *          - totalArrayList 만큼 반복한다.
     *          - totalArrayList에서 tmpDateForSearch일치 하며
     *          - totalArrayList의 타입이 null 이 아니며
     *          - totalArrayList의 type 값이 good, bad에 따라 goodArrayList, badArrayList에 값을 넣는다.
     * ● 데이터 정렬
     *      ○ Collections.sort();
     * */
    private void selectDate(){
        Log.d(tag,"WriteDiaryActivity - selectDate() |  ");
        for(int i = 0; i < totalArrayList.size(); i++) {
            if (totalArrayList.get(i).getDateForSearch().equals(tmpDateForSearch)) {
                if (totalArrayList.get(i).getType().equals("good")) {
                    goodArrayList.add(totalArrayList.get(i));
                } else if (totalArrayList.get(i).getType().equals("bad")) {
                    badArrayList.add(totalArrayList.get(i));
                }
            }
        }
        Log.d(tag,"WriteDiaryActivity - selectDate() | goodArrayList.size() "+goodArrayList.size());
        Log.d(tag,"WriteDiaryActivity - selectDate() | badArrayList.size() "+badArrayList.size());

        Collections.sort(goodArrayList);        // 시간 내림차순으로 정렬
        Collections.sort(badArrayList);

        /*리스트에 따라 안보이게 바꾸기*/
//        if(arrayListSelected.size() == 0){
//            alarmTextView.setVisibility(View.VISIBLE);
//        } else {
//            alarmTextView.setVisibility(View.GONE);
//        }
    }
    private void checkNullData(){
        Log.d(tag,"DiaryFragment - checkNullData() |  ");
        if(goodArrayList.size() == 0 && badArrayList.size() == 0 && isExisted == false){
            scrollView.setVisibility(View.GONE);
            alarmLayout.setVisibility(View.VISIBLE);
            // 수정 item
            if(editItemMenu != null){
                editItemMenu.setVisible(false);
            }
        } else {
            scrollView.setVisibility(View.VISIBLE);
            if(editItemMenu != null){
                editItemMenu.setVisible(true);
            }
            alarmLayout.setVisibility(View.GONE);
            // good 리사이클러뷰
            if(goodArrayList.size() == 0){
                goodLinearLayout.setVisibility(View.GONE);
            } else {
                goodLinearLayout.setVisibility(View.VISIBLE);
            }
            // bad 리사이클러뷰
            if(badArrayList.size() == 0){
                badLinearLayout.setVisibility(View.GONE);
            } else {
                badLinearLayout.setVisibility(View.VISIBLE);
            }
            //
            if(goodTextView.getText().toString().length() == 0){
                goodTextView.setText("잘한 점이 없나요? \n 어제보다 조금이라도 나아진 점이 있지 않을까요?");
                goodTextView.setTextColor(Color.parseColor("#CCC9C9"));
                goodTextView.setTextSize(15);
            } else {
                goodTextView.setTextSize(20);
                goodTextView.setTextColor(Color.parseColor("#3855F3"));
            }
            if(badTextView.getText().toString().length() == 0){
                badTextView.setText("아쉬운 점이 없나요? \n 조금이라도 개선될 점이 있지는 않을까요?");
                badTextView.setTextColor(Color.parseColor("#CCC9C9"));
                badTextView.setTextSize(15);
            } else {
                badTextView.setTextSize(20);
                badTextView.setTextColor(Color.parseColor("#F54279"));
            }
            if(willTextView.getText().toString().length() == 0){
                willTextView.setText("내일은 어떤 모습이 되고 싶으신가요?");
                willTextView.setTextColor(Color.parseColor("#CCC9C9"));
                willTextView.setTextSize(15);
            } else {
                willTextView.setTextSize(20);
                willTextView.setTextColor(Color.parseColor("#7E7D7D"));
            }
        }
    }
    @OnClick(R.id.writeDiary_DiaryFragment)
    public void WriteDiaryClicked(){
        Log.d(tag,"DiaryFragment - WriteDiaryClicked() | 버튼 클릭 ");
        Intent intent = new Intent(getActivity(), WriteDiaryActivity.class);
        intent.putExtra("dayTitle",dayTitle);
        intent.putExtra("tmpDateForSearch",tmpDateForSearch);
        intent.putExtra("isEdit",false);
        startActivity(intent);
    }

    public void editDiary(){
        Intent intent = new Intent(getActivity(), WriteDiaryActivity.class);
        intent.putExtra("dayTitle",dayTitle);
        intent.putExtra("tmpDateForSearch",tmpDateForSearch);
        intent.putExtra("isEdit",true);
        startActivity(intent);
    }
}