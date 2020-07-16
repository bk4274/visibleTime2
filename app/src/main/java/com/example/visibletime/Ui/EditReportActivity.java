package com.example.visibletime.Ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visibletime.Data.CatagoryData;
import com.example.visibletime.Data.ReportData;
import com.example.visibletime.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;


public class EditReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final String tag = "로그";
    private final String TAG = "로그";

    /**
     * View
     */
    @BindView(R.id.category_EditReport)
    TextView categoryTextView;
    @BindView(R.id.measuredTime_EditReport)
    TextView measuredTime;
    @BindView(R.id.timeUpTo_EditReport)
    TextView timeUpTo;
    @BindView(R.id.valueSpinner_EditReport)
    Spinner spinner;
    @BindView(R.id.feedbackEditText_EditReport)
    EditText feedbackEditText;
    @BindView(R.id.enterBtn_EditReport)
    Button enterBtn;
    @BindView(R.id.deleteImageView_Report)
    ImageView deleteImageView;

    ArrayList<CatagoryData> mArrayList;     // 카테고리 이름, 컬러를 받아오게 하기 위함.

    ArrayList<ReportData> totalArrayList;     // Data를 선별하기 앞서 전체 데이터를 저장함.


    // Report에 들어가야 하는 Data

    // 날짜
    String dateForSearch;
    int dateForSort;
    int position;           // totalArrayList에서의 Data 위치
    String categoryParent;
    String category, categoryInitial;        // 카테고리 ex. 공부, 휴식, 하부르타
    int level, levelInitial;              // 만족도
    String content, contentInitial;         // 피드백 내용

    int color;      // 카테고리 선택시 값 받아오기.
    int indexNumber;     // color를 얻기 위한 변수

    String timeStart, timeEnd;      // 시작 시간, 종료시간
    long currentTime;        // 현재 시간을 밀리 세컨드로 알기 위함
    int timeDuration;       // 00:00:00 을 나타내는 시간

    //
    ArrayList<String> arrayListSpinner;         // Spinner에 보여줄 ArrayList.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_report);
        ButterKnife.bind(this);

        // 카테고리 선택시 내가 저장한 값을 보여주기 위한 arrayList;
        mArrayList = new ArrayList<>();
        totalArrayList = new ArrayList<>();
        getListOfDataFromSharedPreferences();       // 버튼 클릭시 계속 호출하니까 mArrayList에 계속 저장되는 문제 발생
                                                    // onCreate에서 한번만 발생 하도록 함.

        getListOfRecordDataFromSharedPreferences();
        Log.d(TAG,"EditReportActivity - onCreate() | totalArrayList.size(): "+totalArrayList.size());
        initView();
        setSpinner();
    }

    /**intent로 받은 값 초기화 작업*/
    private void initView() {
        // 데이터 수신 : 시간
        Intent intent = getIntent(); /*데이터 수신*/
        category = intent.getExtras().getString("category");        // 카테고리
        categoryInitial = category;
        categoryParent = intent.getExtras().getString("categoryParent");
        timeDuration = intent.getExtras().getInt("timeDuration");
        timeStart = intent.getExtras().getString("timeStart");
        timeEnd = intent.getExtras().getString("timeEnd");
        level = intent.getExtras().getInt("level");
        levelInitial = level;
        content = intent.getExtras().getString("content");
        contentInitial = content;
        color = intent.getExtras().getInt("color");
        position = intent.getExtras().getInt("position");
        Log.d(tag,"EditReportActivity - initView() | position: "+position);

        // 받은 데이터로 View 초기화해주기
        categoryTextView.setText(category);

        int sec = (timeDuration) % 60;
        int min = (timeDuration) %3600 / 60;
        int hour = (timeDuration) / 3600;
        //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

        String result = String.format("%02d:%02d:%02d", hour, min, sec);
        measuredTime.setText(result);

        timeUpTo.setText(timeStart+" - "+timeEnd);

        feedbackEditText.setText(content);

    }

    /**■ 카테고리 항목 선택하기
     *      ● Shared에 저장된 값 불러오기.
     * ■
     * */
    @OnClick(R.id.category_EditReport)
    void onCategoryClicked() {
        Log.d(tag,"EditReportActivity - onCategoryClicked() |  ");

        // ● 자식 Category만 따로 ArrayList<CatagoryData>에 담는다.
        ArrayList<String> arrayListChildName = new ArrayList<>();
        ArrayList<Integer> arrayListChildColor = new ArrayList<>();
        ArrayList<String> arrayListParentName = new ArrayList<>();

        for(int i = 0; i<mArrayList.size(); i++){
            CatagoryData catagoryData = mArrayList.get(i);
            if(catagoryData.getType() == CHILD_TYPE){
                arrayListChildName.add(catagoryData.getName());
                arrayListChildColor.add(catagoryData.getColor());
                arrayListParentName.add(catagoryData.getParentName());
            }
        }
        final String[] items =  arrayListChildName.toArray(new String[ arrayListChildName.size()]);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditReportActivity.this);
        alertDialogBuilder.setTitle("활동 목록");
        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                for(int i=0; i<items.length; i++){
                    if(id == i){
                        category = items[i];
                        categoryTextView.setText(items[i]);
                        indexNumber = id;
                    }
                }
                color = arrayListChildColor.get(indexNumber);
                categoryParent =arrayListParentName.get(indexNumber);
                Log.d(tag,"EditReportActivity - onCategoryClicked() | indexNumber: "+indexNumber);
                Log.d(tag,"EditReportActivity - onCategoryClicked() | color: "+color);
                Log.d(tag,"EditReportActivity - onCategoryClicked() | categoryParent: "+categoryParent);
            }
        });


        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();
    }

    @OnClick(R.id.enterBtn_EditReport)
    void enterBtnClicked() {
        content = feedbackEditText.getText().toString();

        if(category.equals(categoryInitial) && level == levelInitial && content.equals(contentInitial)) {
            // 변경된 값이 없는 경우
            finish();
        } else {
            // 변경된 경우가 있는 경우 다이얼 로그 띄워주기.
            showDialog();
        }
    }
    @OnClick(R.id.deleteImageView_Report)
    void deleteImageViewClicked() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditReportActivity.this);
        mBuilder.setTitle("알림");
        mBuilder.setMessage("해당 '기록'을 삭제하시겠습니까?");
        // 버튼 클릭 이벤트 달기
        mBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.putExtra("position", position);
                Log.d(TAG,"EditReportActivity - onClick() | 삭제 전 사이즈: "+totalArrayList.size());
                totalArrayList.remove(position);
                Log.d(TAG,"EditReportActivity - onClick() | 삭제 후 사이즈:  "+totalArrayList.size());
                saveReportDataList();
                setResult(RESULT_FIRST_USER, intent);
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
     *      - Shared에 저장된 값을 리사이클러뷰에 보이기.
     *
     * ■ 데이터 있는지 여부 확인
     *      - 없다면, 현재 값이 없다는 내용을 가진 Layout(showLayout) 띄우기
     *      - 있다면, Layout(showLayout) 사라지게 하기
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
    private void getListOfDataFromSharedPreferences(){

        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);

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
                    // mArrayList에 값 넣어주기
                    mArrayList.add(catagoryData);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(tag, "EditReportActivity - onLoginBtnClicked() | Exception " + e);
            }
        }
    }

    private void setSpinner() {
        /**Spinner 등록
         *
         * 참조: https://kangchobo.tistory.com/45
         *
         * 프로세스
         *      - Spinner와 Adapter과 연결
         *      - Adapter에는 Spinner를 눌렀을때 사용자에게 1-5로 최하부터 최상까지 보여준다.
         *      - 기본값을 3 _ 중으로 둔다.
         * */

        spinner.setOnItemSelectedListener(this);
        arrayListSpinner = new ArrayList<>();
        arrayListSpinner.add("1 (최하)");
        arrayListSpinner.add("2 (하)");
        arrayListSpinner.add("3 (중)");
        arrayListSpinner.add("4 (상)");
        arrayListSpinner.add("5 (최상)");


        // Activity 에서 받아온 ArrayList<CatagoryData> mArrayList 에서 부모 타입인 것만 arrayListSpinner에 집어 넣는다.
//        for(int i =0; i<mArrayList.size(); i++){
//            CatagoryData catagoryData = mArrayList.get(i);
//            if(catagoryData.getType() == PARENT_TYPE){
//                arrayListSpinner.add(catagoryData.getName());
//            }
//        }

        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arrayListSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(level-1);        // intent로 받은 데이터로 기본값을 한다.
                                        // level은 3이 중간이라면 spinner에서는 2에 해당하므로 -1로 보정해준다.
    }
    /**Spinner 메서드*/

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
     level = i+1;   // 0+1 : 최하, ~ 4+1 : 최상
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        level = 3; //(기본값)
    }

    /**수정 될 것임을 사용자에게 알림*/
    private void showDialog() {
        SharedPreferences sharedPresesntID = this.getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID", null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = this.getSharedPreferences(presentID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isConfirmed = sharedPreferences.getBoolean("isConfirmed", false);

        if (isConfirmed == true) {
            // 데이터 전달
            returnData();
        } else {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditReportActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_confrim_edition, null);        // 다이얼로그에 보여주기 위해 초기화 작업
            CheckBox mCheckBox = mView.findViewById(R.id.checkBox);
            mBuilder.setTitle("수정 확인");
            mBuilder.setMessage("기존에 데이터 대신 수정된 값을 \n덮어 씌우시겠습니까?");
            mBuilder.setView(mView);        // 내가 만든 xml파일 붙이기
            // 버튼 클릭 이벤트 달기
            mBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //체크박스 상태 저장
                    if (mCheckBox.isChecked()) {
                        editor.putBoolean("isConfirmed", true);
                        editor.apply();
                    } else {
                        editor.putBoolean("isConfirmed", false);
                        editor.apply();
                    }
                    // 다이얼로그 닫기
                    dialogInterface.dismiss();
                    // 데이터 전달
                    returnData();
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
    }
    /**수정된 데이터 전달*/
    private void returnData(){
        Intent intent = new Intent();
        intent.putExtra("category", category);
        intent.putExtra("categoryParent", categoryParent);
        intent.putExtra("level", level);
        intent.putExtra("content", content);
        intent.putExtra("color", color);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
    }
    /**BackButton을 눌렀을 때, 데이터 유실 알리기*/
    @Override
    public void onBackPressed() {
        content = feedbackEditText.getText().toString();
        if(category.equals(categoryInitial) && level == levelInitial && content.equals(contentInitial)){
            // 변경된 값이 없는 경우
            super.onBackPressed();
        } else{
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditReportActivity.this);
            mBuilder.setTitle("알림");
            mBuilder.setMessage("변경 내용이 저장되지 않았습니다. 변경 내용을 삭제하시겠습니까?");
            // 버튼 클릭 이벤트 달기
            mBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
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
    private void getListOfRecordDataFromSharedPreferences(){
        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, Context.MODE_PRIVATE);

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
        Log.d(TAG,"EditReportActivity - saveReportDataList() |  ");

        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ● ID를 파일명으로 하는 Shared.xml 파일 생성 및 기존 값 지우기
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, Context.MODE_PRIVATE);
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
