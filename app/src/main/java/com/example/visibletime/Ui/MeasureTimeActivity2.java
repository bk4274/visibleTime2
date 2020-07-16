//package com.example.visibletime.Ui;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.visibletime.Data.CatagoryData;
//import com.example.visibletime.Data.ReportData;
//import com.example.visibletime.R;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
//import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;
//
//
//public class MeasureTimeActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
//    private final String tag = "로그";
//
//    /**
//     * View
//     */
//    @BindView(R.id.category_MeasureTime)
//    TextView categoryTextView;
//    @BindView(R.id.measuredTime_MeasureTime)
//    TextView measuredTime;
//    @BindView(R.id.timeUpTo_MeasureTime)
//    TextView timeUpTo;
//    @BindView(R.id.valueSpinner_MeasureTime)
//    Spinner spinner;
//    @BindView(R.id.feedbackEditText_MeasureTime)
//    EditText feedbackEditText;
//    @BindView(R.id.enterBtn_MeasureTime)
//    Button enterBtn;
//
//    ArrayList<CatagoryData> mArrayList;     // 카테고리 이름, 컬러를 받아오게 하기 위함.
//
//    // Report에 들어가야 하는 Data
//
//    // 날짜
//    String dateForSearch;
//    int dateForSort;
//
//    String categoryParent;
//    String category;        // 카테고리 ex. 공부, 휴식, 하부르타
//    int level;              // 만족도
//    String content;         // 피드백 내용
//
//    int color;      // 카테고리 선택시 값 받아오기.
//    int indexNumber;     // color를 얻기 위한 변수
//
//    String timeStart, timeEnd;      // 시작 시간, 종료시간
//    long currentTime;        // 현재 시간을 밀리 세컨드로 알기 위함
//    int time;       // 00:00:00 을 나타내는 시간
//
//    //
//    ArrayList<String> arrayListSpinner;         // Spinner에 보여줄 ArrayList.
//    ArrayList<ReportData> arrayListForReport;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_measure_time);
//        ButterKnife.bind(this);
//
//        // 카테고리 선택시 내가 저장한 값을 보여주기 위한 arrayList;
//        mArrayList = new ArrayList<>();
//        getListOfDataFromSharedPreferences();       // 버튼 클릭시 계속 호출하니까 mArrayList에 계속 저장되는 문제 발생
//                                                    // onCreate에서 한번만 발생 하도록 함.
//
//        // 데이터 수신 : 시간
//        Intent intent = getIntent(); /*데이터 수신*/
//        time = intent.getExtras().getInt("time");
//
//        int sec = (time) % 60;
//        int min = (time) % 3600 / 60;
//        int hour = (time) / 3600;
//        //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
//
//        String result = String.format("%02d:%02d:%02d", hour, min, sec);
//        measuredTime.setText(result);
//
//        getDate();
//        setSpinner();
//    }
//
//    /**■ 카테고리 항목 선택하기
//     *      ● Shared에 저장된 값 불러오기.
//     * ■
//     * */
//    @OnClick(R.id.category_MeasureTime)
//    void onCategoryClicked() {
//        Log.d(tag,"MeasureTimeActivity - onCategoryClicked() |  ");
//
//        // ● 자식 Category만 따로 ArrayList<CatagoryData>에 담는다.
//        ArrayList<String> arrayListChildName = new ArrayList<>();
//        ArrayList<Integer> arrayListChildColor = new ArrayList<>();
//        ArrayList<String> arrayListParentName = new ArrayList<>();
//
//        for(int i = 0; i<mArrayList.size(); i++){
//            CatagoryData catagoryData = mArrayList.get(i);
//            if(catagoryData.getType() == CHILD_TYPE){
//                arrayListChildName.add(catagoryData.getName());
//                arrayListChildColor.add(catagoryData.getColor());
//                arrayListParentName.add(catagoryData.getParentName());
//            }
//        }
//        final String[] items =  arrayListChildName.toArray(new String[ arrayListChildName.size()]);
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeasureTimeActivity2.this);
//        alertDialogBuilder.setTitle("활동 목록");
//        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                for(int i=0; i<items.length; i++){
//                    if(id == i){
//                        category = items[i];
//                        categoryTextView.setText(items[i]);
//                        indexNumber = id;
//                    }
//                }
//                color = arrayListChildColor.get(indexNumber);
//                categoryParent =arrayListParentName.get(indexNumber);
//                Log.d(tag,"MeasureTimeActivity - onCategoryClicked() | indexNumber: "+indexNumber);
//                Log.d(tag,"MeasureTimeActivity - onCategoryClicked() | color: "+color);
//                Log.d(tag,"MeasureTimeActivity - onCategoryClicked() | categoryParent: "+categoryParent);
//            }
//        });
//
//
//        // 다이얼로그 생성
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        // 다이얼로그 보여주기
//        alertDialog.show();
//    }
//
//    @OnClick(R.id.enterBtn_MeasureTime)
//    void enterBtnClicked() {
//        content = feedbackEditText.getText().toString();
//        // 생성자
//        // String dateForSearch, String dateForSort, String category, String categoryParent,
//        // int level, String content, int color, String timeStart, String timeEnd, int timeDuration
//
//        if(category!= null){
//            // 카테고리 선택한 경우
//            ReportData reportData = new ReportData();
//            reportData.setDateForSearch(dateForSearch);
//            reportData.setDateForSort(dateForSort);
//            reportData.setCategory(category);
//            reportData.setCategoryParent(categoryParent);
//            reportData.setLevel(level);
//            reportData.setContent(content);
//            reportData.setColor(color);
//            reportData.setTimeStart(timeStart);
//            reportData.setTimeEnd(timeEnd);
//            reportData.setType("so");               // 새로 추가된 Repord 변수로, type에 따라 일기장에 보여주는 것을 다르게 하기 위함.
//            reportData.setTimeDuration(time);
//
//            arrayListForReport = new ArrayList<>();
//            arrayListForReport.add(reportData);
//
//            saveReportDataList();
//            finish();
//
//        } else{
//            Toast.makeText(MeasureTimeActivity2.this, "카테고리를 선택해 주세요.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**saveReportDataList()
//     * ■ 메서드 목적:
//     *      - mArrayList에 있는 값을 SharedPreference에 저장하기 위함.
//     * ■ 프로세스
//     *      ● 해당 아이디를 불러온다.
//     *      ● 우선 기존에 있는 SharedPreference에 있는 값을 지운다.
//     *      ● Parent Item가 닫힌 상태로, 내부 가지고 있는 CategoryData를 mArrayList에 저장한다. (정렬을 해야 하나?)
//     *      ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
//     *
//     * ■ 호출
//     *      - onActivityResult()
//     *      - 사용자가 item View를 클릭하고 나서 나왔을 때 데이터가 수정 될 수 있기 때문에 이때 호출한다.
//     * */
//    private void saveReportDataList(){
//        Log.d(tag,"CategoryActivity - saveCategoryDataList() |  ");
//
//        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
//        String presentID = sharedPresesntID.getString("presentID",null);
//
//        // ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
//        if(mArrayList.size() != 0){
//
//            /**JSON 형태로 저장*/
//            String strDataOfJSON;
//            strDataOfJSON = "{ \"dateForSearch\"" + ":" + "\"" + arrayListForReport.get(0).getDateForSearch() + "\"" + ","
//                    + "\"dateForSort\"" + ":" + "\"" + arrayListForReport.get(0).getDateForSort() + "\"" + ","
//                    + "\"category\"" + ":" + "\"" + arrayListForReport.get(0).getCategory() + "\"" + ","
//                    + "\"categoryParent\"" + ":" + "\"" + arrayListForReport.get(0).getCategoryParent() + "\"" + ","
//                    + "\"level\"" + ":" + "\"" + arrayListForReport.get(0).getLevel() + "\"" + ","
//                    + "\"content\"" + ":" + "\"" + arrayListForReport.get(0).getContent() + "\"" + ","
//                    + "\"color\"" + ":" + "\"" + arrayListForReport.get(0).getColor() + "\"" + ","
//                    + "\"timeStart\"" + ":" + "\"" + arrayListForReport.get(0).getTimeStart() + "\"" + ","
//                    + "\"timeEnd\"" + ":" + "\"" + arrayListForReport.get(0).getTimeEnd() + "\"" + ","
//                    + "\"type\"" + ":" + "\"" + arrayListForReport.get(0).getType() + "\"" + ","
//                    + "\"timeDuration\"" + ":" + "\"" + arrayListForReport.get(0).getTimeDuration() + "\""
//                    + "}";
//
//            // □ 값을 처음으로 저장하는 확인
//            SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
//            String report = sharedPreferences.getString("report", null);
//            SharedPreferences.Editor editorReport = sharedPreferences.edit();
//
//            if (report == null) {
//                // 첫 카테고리 정보를 저장하는 경우
//                editorReport.putString("report", strDataOfJSON);
//                editorReport.commit();
//            } else {
//                // 카테고리 정보가 처음이 아닌 경우
//                editorReport.putString("report", sharedPreferences.getString("report", "") + "," + strDataOfJSON);
//                editorReport.commit();
//            }
//
//        }
//    }
//
//    /**getListOfDataFromSharedPreferences
//     *
//     * ■ 메서드 목적
//     *      - Shared에 저장된 값을 리사이클러뷰에 보이기.
//     *
//     * ■ 데이터 있는지 여부 확인
//     *      - 없다면, 현재 값이 없다는 내용을 가진 Layout(showLayout) 띄우기
//     *      - 있다면, Layout(showLayout) 사라지게 하기
//     *
//     * ■ Shared
//     *      ● Shared에 저장된 현재 아이디를 불러온다.
//     *      ● 아이디에 해당하는 Shared파일을 불러온다.
//     *
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
//        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
//        String presentID = sharedPresesntID.getString("presentID",null);
//
//        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
//        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
//
//        // 값이 있는지 여부 확인하기
//        if(sharedPreferences.getString("category",null) == null){
////            showLayout.setVisibility(View.VISIBLE);
//        } else {
////            showLayout.setVisibility(View.GONE);
//            try {
//                // ● JSONArray 생성
//                JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("category", null) + "]");
//                // ● 반복문(JSONArray.length())
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                    // ○ JSON에 String으로 저장된 값을 AlarmData 데이터 타입에 맞게 변환을 위한 변수 선언
//                    int type = 0;
//                    int color = 0;
//                    int count = 0;
//
//                    // int type, color, count 을 얻기 위한 데이 변환
//                    type = Integer.parseInt(jsonObject.getString("type"));
//                    color = Integer.parseInt(jsonObject.getString("color"));
//                    count = Integer.parseInt(jsonObject.getString("count"));
//
//                    // ○ ArrayList에 값 넣기
//                    // Data 객체 생성 & 값 넣어주기
//                    CatagoryData catagoryData = new CatagoryData();
//                    catagoryData.setType(type);
//                    catagoryData.setColor(color);
//                    catagoryData.setCount(count);
//                    catagoryData.setName(jsonObject.getString("name"));
//                    catagoryData.setParentName(jsonObject.getString("parentName"));
//                    // mArrayList에 값 넣어주기
//                    mArrayList.add(catagoryData);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.d(tag, "CategoryActivity - onLoginBtnClicked() | Exception " + e);
//            }
//        }
//    }
//
//    private void getDate(){
//        currentTime = System.currentTimeMillis();
//
//        SimpleDateFormat formatForSearch = new SimpleDateFormat("yyyyMMdd");
//        SimpleDateFormat formatForSort = new SimpleDateFormat("HHmmss");
//        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
//
//
//        dateForSearch = formatForSearch.format(currentTime);
//        dateForSort = Integer.parseInt(formatForSort.format(currentTime));
//        timeEnd = formatTime.format(currentTime);
//        timeStart = formatTime.format(currentTime-time*1000);
//
//        String timeStartEnd = timeStart + " - " + timeEnd;
//        timeUpTo.setText(timeStartEnd);
//
//        Log.d(tag,"MeasureTimeActivity - getDate() | time: "+time);
//        Log.d(tag,"MeasureTimeActivity - getDate() | currentTime: "+currentTime);
//        Log.d(tag,"MeasureTimeActivity - getDate() | dateForSearch: "+dateForSearch);
//        Log.d(tag,"MeasureTimeActivity - getDate() | dateForSort: "+dateForSort);
//        Log.d(tag,"MeasureTimeActivity - getDate() | timeStart: "+timeStart);
//        Log.d(tag,"MeasureTimeActivity - getDate() | timeEnd: "+timeEnd);
//        Log.d(tag,"MeasureTimeActivity - getDate() | time: "+time);
//    }
//
//    private void setSpinner() {
//        /**Spinner 등록
//         *
//         * 참조: https://kangchobo.tistory.com/45
//         *
//         * 프로세스
//         *      - Spinner와 Adapter과 연결
//         *      - Adapter에는 Spinner를 눌렀을때 사용자에게 1-5로 최하부터 최상까지 보여준다.
//         *      - 기본값을 3 _ 중으로 둔다.
//         * */
//
//        spinner.setOnItemSelectedListener(this);
//        arrayListSpinner = new ArrayList<>();
//        arrayListSpinner.add("1 (최하)");
//        arrayListSpinner.add("2 (하)");
//        arrayListSpinner.add("3 (중)");
//        arrayListSpinner.add("4 (상)");
//        arrayListSpinner.add("5 (최상)");
//
//
//        // Activity 에서 받아온 ArrayList<CatagoryData> mArrayList 에서 부모 타입인 것만 arrayListSpinner에 집어 넣는다.
////        for(int i =0; i<mArrayList.size(); i++){
////            CatagoryData catagoryData = mArrayList.get(i);
////            if(catagoryData.getType() == PARENT_TYPE){
////                arrayListSpinner.add(catagoryData.getName());
////            }
////        }
//
//        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arrayListSpinner);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setSelection(2);        // 2: 중 을 기본값으로 한다. (arrayList는 0부터 시작하므로)
//        level = 3;                      // 3: 중
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//     level = i+1;   // 0+1 : 최하, ~ 4+1 : 최상
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//        level = 3; //(기본값)
//    }
//}
