package com.example.visibletime.Ui;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Adapter.ReportAdatper;
import com.example.visibletime.Adapter.SelectReportAdatper;
import com.example.visibletime.Data.DiaryData;
import com.example.visibletime.Data.ReportData;
import com.example.visibletime.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WriteDiaryActivity extends AppCompatActivity {

    private final String tag = "로그";

    /**
     * View
     */
    @BindView(R.id.deleteBtn_Write)
    ImageView deleteBtn;
    @BindView(R.id.cameraImageView_Write)
    ImageView cameraImageView;
    @BindView(R.id.enterBtn_Write)
    ImageButton enterBtn;
    @BindView(R.id.addGoodBtn_Write)
    Button addGoodBtn;
    @BindView(R.id.addBadBtn_Write)
    Button addBadBtn;
    @BindView(R.id.goodTextView_Write)
    TextView goodTextView;
    @BindView(R.id.badTextView_Write)
    TextView badTextView;
    @BindView(R.id.willTextView_Write)
    TextView willTextView;
    @BindView(R.id.pictureImageView_Write)
    ImageView pictureImageView;

    /**시간*/
    String tmpDateForSearch;       // 기록을 저장한 것을 분류하기 위한 값
    String dayTitle;       // PickReportActivity 에서 Title로 쓰일 값

    /**리사이클러뷰*/
    // ArrayList의 생성은 onCreate에서 해준다.

    ArrayList<ReportData> totalArrayList;     // Data를 선별하기 앞서 전체 데이터를 저장함.
    ArrayList<ReportData> goodArrayList;    // 해당 날짜에 good type 인 list
    ArrayList<ReportData> badArrayList;    // 해당 날짜에 bad type인 list

    ArrayList<DiaryData> totalDiaryArrayList;     // Data를 선별하기 앞서 전체 데이터를 저장함.
//    ArrayList<DiaryData> todayDiaryArrayList;     // Data를 선별하기 앞서 전체 데이터를 저장함.

    ReportAdatper goodAdapter;
    ReportAdatper badAdapter;

    /**Text*/
    private Uri imageUri = null;

    String goodText, badText, willText ,photo ;

    /**
     * 권한 설정 변수
     */
    private String[] permissions =
            {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;    //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

    /**
     * onActivityResult를 위한 변수
     */
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_diary);
        ButterKnife.bind(this);

        getCurrentDate();

        // 데이터 List 초기화
        totalArrayList = new ArrayList<>();
        goodArrayList = new ArrayList<>();
        badArrayList = new ArrayList<>();
        totalDiaryArrayList = new ArrayList<>();

        // 사진 안보이게 하기
        pictureImageView.setVisibility(View.GONE);

        setRecyclerView();

        // diary data 불러오기
        getListOfDiaryDataFromSharedPreferences();
        try {
            selectDiaryDate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(tag,"WriteDiaryActivity - onResume() |  ");
        getListOfDataFromSharedPreferences();
        selectDate();
        goodAdapter.notifyDataSetChanged();
        badAdapter.notifyDataSetChanged();
    }

    // 해당 액티비티가 처음 시작되었을 때는, 오늘 날짜를 보여주기 위한 초기화 작업.
    // 현재시간을 가져와 fromat에 따라 TextView에 보여준다.
    // Todo
    //  다이어리에서 일기 작성을 하게 된다면,
    //  날짜 값을 현재 액티비티에서 구하는 게 아니라,다이어리 액티비티에서 구해 intent로 전달 받은 값을 사용할 것이다.
    private void getCurrentDate(){

        Intent intent = getIntent(); /*데이터 수신*/
        Log.d(tag,"WriteDiaryActivity - getCurrentDate() | intent: "+intent);
        dayTitle = intent.getExtras().getString("dayTitle");
        tmpDateForSearch = intent.getExtras().getString("tmpDateForSearch");

        boolean isEdit = intent.getExtras().getBoolean("isEdit");
        if(isEdit){
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            deleteBtn.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.addGoodBtn_Write)
    public void addGoodBtnClicked(){
        Intent intent = new Intent(WriteDiaryActivity.this, PickReportActivity.class);
        intent.putExtra("dayTitle", dayTitle);
        intent.putExtra("tmpDateForSearch", tmpDateForSearch);
        intent.putExtra("type", "good");
        startActivity(intent);
    }
    @OnClick(R.id.addBadBtn_Write)
    public void addBadBtnClicked(){
        Intent intent = new Intent(WriteDiaryActivity.this, PickReportActivity.class);
        intent.putExtra("dayTitle", dayTitle);
        intent.putExtra("tmpDateForSearch", tmpDateForSearch);
        intent.putExtra("type", "bad");
        startActivity(intent);
    }
    /*주석 처리한 이유
    *
    * 1. 값을 받아오는게 아니라 PickReportActivity 종료시 Shared로 바뀐 값을 바로 저장하게 함.
   * */
//    /**
//     * PickReportActivity에서 확인 버튼을 클릭했을 경우
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
//        super.onActivityResult(requestCode, resultCode, resultIntent);
//        if (requestCode == ADD_GOOD) {
//            if (resultCode == Activity.RESULT_OK) {
//
//            }
//        } else if (requestCode == ADD_BAD) {
//            if (resultCode == Activity.RESULT_OK) {
//
//
//            }
//        }
//    }

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
        SharedPreferences sharedPresesntID = this.getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = this.getSharedPreferences(presentID, Context.MODE_PRIVATE);

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

    private void getListOfDiaryDataFromSharedPreferences(){
        Log.d(tag,"WriteDiaryActivity - getListOfDiaryDataFromSharedPreferences() |  ");
        SharedPreferences sharedPresesntID = this.getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = this.getSharedPreferences(presentID, Context.MODE_PRIVATE);

        try {
            // ● JSONArray 생성
            JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("diary", null) + "]");
            Log.d(tag,"WriteDiaryActivity - getListOfDiaryDataFromSharedPreferences() | jsonArray 생성: "+jsonArray);
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
                Log.d(tag,"WriteDiaryActivity - getListOfDiaryDataFromSharedPreferences() | 동작확인1 ");
                diaryData.setPhoto(jsonObject.getString("photo"));
                Log.d(tag,"WriteDiaryActivity - getListOfDiaryDataFromSharedPreferences() | 동작확인2 ");
                // totalDiaryArrayList 값 넣어주기
                totalDiaryArrayList.add(diaryData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag,"WriteDiaryActivity - getListOfDiaryDataFromSharedPreferences() | Exception " + e);
        }
        Log.d(tag,"WriteDiaryActivity - getListOfDiaryDataFromSharedPreferences() | totalDiaryArrayList: "+totalDiaryArrayList.size());
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
        RecyclerView goodRecyclerView = (RecyclerView) findViewById(R.id.goodRecyclerView_WritingDiary);
        RecyclerView badRecyclerView = (RecyclerView) findViewById(R.id.badRecyclerView_WritingDiary);
        goodAdapter = new ReportAdatper(this, goodArrayList);    // Custom Adater 객체 생성 & Adapter와 mArrayList(Data List) 연결.
        badAdapter = new ReportAdatper(this, badArrayList);    // Custom Adater 객체 생성 & Adapter와 mArrayList(Data List) 연결.

        goodRecyclerView.setAdapter(goodAdapter);             // RecyclerView와 Adater와 연결
        badRecyclerView.setAdapter(badAdapter);             // RecyclerView와 Adater와 연결

        LinearLayoutManager goodLayoutManager = new LinearLayoutManager(this);     // RecyclerView를 어떻게 보여줄지 결정
        goodLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        LinearLayoutManager badLayoutManager = new LinearLayoutManager(this);     // RecyclerView를 어떻게 보여줄지 결정
        badLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        goodRecyclerView.setLayoutManager(goodLayoutManager);
        badRecyclerView.setLayoutManager(badLayoutManager);

        // 데이터 갱신
//        mAdapter.notifyDataSetChanged();


//        //child View들을 구분선을 만들어 주는 메서드
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
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

    private void selectDiaryDate() throws FileNotFoundException {
        Log.d(tag,"WriteDiaryActivity - selectDiaryDate() |  ");

        for(int i = 0; i < totalDiaryArrayList.size(); i++) {
            if (totalDiaryArrayList.get(i).getDateForSearch().equals(tmpDateForSearch)) {
                goodTextView.setText(totalDiaryArrayList.get(i).getGoodText());
                badTextView.setText(totalDiaryArrayList.get(i).getBadText());
                willTextView.setText(totalDiaryArrayList.get(i).getWillText());
                if(totalDiaryArrayList.get(i).getPhoto().equals("null") == false){
                    imageUri = Uri.parse(totalDiaryArrayList.get(i).getPhoto());
                    pictureImageView.setVisibility(View.VISIBLE);
                    InputStream in = getContentResolver().openInputStream(imageUri);
                    Bitmap tempImg = BitmapFactory.decodeStream(in);
                    pictureImageView.setImageBitmap(tempImg);
                }
            }
        }
    }

    /**EditText 3개와, 사진을 저장해야 함.*/
    @OnClick(R.id.enterBtn_Write)
    public void enterBtnClicked(){
        Log.d(tag,"WriteDiaryActivity - enterBtnClicked() |  ");
        /**다이어리값 삭제*/
//        SharedPreferences sharedPresesntID = this.getSharedPreferences("presentID", Context.MODE_PRIVATE);
//        String presentID = sharedPresesntID.getString("presentID",null);
//
//        // ● ID를 파일명으로 하는 Shared.xml 파일 생성 및 기존 값 지우기
//        SharedPreferences sharedPreferences = this.getSharedPreferences(presentID, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editorCategory = sharedPreferences.edit();
//        editorCategory.remove("diary");
//        editorCategory.commit();
//        Log.d(tag,"WriteDiaryActivity - enterBtnClicked() | Shared diary 값 삭제 ");

        goodText = goodTextView.getText().toString();
        badText = badTextView.getText().toString();
        willText = willTextView.getText().toString();
        photo = null;
        if(imageUri != null){
            photo = imageUri.toString();
        }

        /** totalDiaryArrayList 에 데이터 추가 & 수정 하는 방법
         *  totalDiaryArrayList 전체를 확인 해, 해당 날짜에 해당하는 diary객체가 있다면 수정, 없다면 추가
         *      ⇒ 존재O; 수정 &  isExisted = true, 반복문 종료
         *      ⇒ 존재X; isExisted = false
         *      ⇒ isExisted = false 인 경우 데이터 추가
         * */
        boolean isExisted = false;
        for(int i = 0; i < totalDiaryArrayList.size(); i++) {
            if (totalDiaryArrayList.get(i).getDateForSearch().equals(tmpDateForSearch)) {
                totalDiaryArrayList.get(i).setGoodText(goodText);
                totalDiaryArrayList.get(i).setBadText(badText);
                totalDiaryArrayList.get(i).setWillText(willText);
                totalDiaryArrayList.get(i).setPhoto(photo);
                isExisted = true;
                break;
            } else {
                isExisted = false;
            }
        }
        if(isExisted == false){
            DiaryData diaryData = new DiaryData(tmpDateForSearch,goodText,badText,willText,photo);
            totalDiaryArrayList.add(diaryData);
            Log.d(tag,"WriteDiaryActivity - enterBtnClicked() | diary가 해당 날짜에 처음인 경우 ");
        }
        saveDairyData();
        finish();
    }

    /**saveDairyData()
     * ■ 메서드 목적:
     *      - Text와 사진의 값을 SharedPreference에 저장하기 위함.
     * ■ 프로세스
     *      ● 해당 아이디를 불러온다.
     *      ● 우선 기존에 있는 SharedPreference에 있는 값을 지운다.
     *
     *      ● 현재 데이터를 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
     *
     * ■ 호출
     *      - enterBtnClicked()
     * */
    private void saveDairyData() {
        Log.d(tag,"WriteDiaryActivity - saveDairyData() |  ");
        
        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID", null);

        // ● ID를 파일명으로 하는 Shared.xml 파일 생성 및 기존 값 지우기
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
        SharedPreferences.Editor editorCategory = sharedPreferences.edit();
        editorCategory.remove("diary");
        editorCategory.commit();

        // ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
        if (totalDiaryArrayList.size() != 0) {
            for (int i = 0; i < totalDiaryArrayList.size(); i++) {
                /**JSON 형태로 저장*/
                String strDataOfJSON;
                strDataOfJSON = "{ \"dateForSearch\"" + ":" + "\"" + totalDiaryArrayList.get(i).getDateForSearch() + "\"" + ","
                        + "\"goodText\"" + ":" + "\"" + totalDiaryArrayList.get(i).getGoodText() + "\"" + ","
                        + "\"badText\"" + ":" + "\"" + totalDiaryArrayList.get(i).getBadText() + "\"" + ","
                        + "\"willText\"" + ":" + "\"" + totalDiaryArrayList.get(i).getWillText() + "\"" + ","
                        + "\"photo\"" + ":" + "\"" + totalDiaryArrayList.get(i).getPhoto() + "\""
                        + "}";

                // □ 값을 처음으로 저장하는 확인
                String report = sharedPreferences.getString("diary", null);
                SharedPreferences.Editor editorReport = sharedPreferences.edit();

                if (report == null) {
                    // 첫 카테고리 정보를 저장하는 경우
                    editorReport.putString("diary", strDataOfJSON);
                    editorReport.commit();
                } else {
                    // 카테고리 정보가 처음이 아닌 경우
                    editorReport.putString("diary", sharedPreferences.getString("diary", "") + "," + strDataOfJSON);
                    editorReport.commit();
                }
            }
        }
    }

    @OnClick(R.id.cameraImageView_Write)
    public void cameraImageViewClicked(){
        checkPermissions();
        createAlertDialog();
    }

    /**
     * <권환 프로세스
     * ● checkPermissions() 권환체크
     * ● onRequestPermissionsResult
     */
    private boolean checkPermissions() {
        Log.d(tag, "WriteDiaryActivity - checkPermissions() |  ");

        List<String> listOfPermissionDenied = new ArrayList<>();

        /** for each 문의 형식
         * for(변수타입 변수이름: 배열이름){
         *  실행부분: 권한이 있는지 반복해서 검사하기 위함.
         *  }
         */
        for (String valuePermission : permissions) {
            if (ContextCompat.checkSelfPermission(this, valuePermission) != PackageManager.PERMISSION_GRANTED) {
                Log.d(tag, "WriteDiaryActivity - checkPermissions() | if(권한 여부 체크) __ true :  " + valuePermission + " 권한 없음");
                listOfPermissionDenied.add(valuePermission);
            }
        }
        if (!listOfPermissionDenied.isEmpty()) {
            Log.d(tag, "WriteDiaryActivity - checkPermissions() | if(권한이 없는게 있는지 체크) __ true: 권한 요청 ");
            /**Permission request method*/
            ActivityCompat.requestPermissions(this, listOfPermissionDenied.toArray(new String[listOfPermissionDenied.size()]), MULTIPLE_PERMISSIONS);
            Log.d(tag, "WriteDiaryActivity - checkPermissions() | called ");
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(tag, " WriteDiaryActivity- onRequestPermissionsResult() |  ");
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Log.d(tag, "WriteDiaryActivity - onRequestPermissionsResult() | if(grantResults.length > 0) __ false :  ");
                }
                break;
        }
    }

    /**
     * <사진 프로세스>
     * ● createAlertDialog()
     * 호출: 사진 버튼이 클릭 되었을 시
     * 프로세스
     * - 사진 선택 목록 보여주기
     * -사진 촬영, 사진 선택, 기본 사진
     * - AlertDialog.Builder 생성
     * - 다이얼로그 title, item 설정
     * - 다이얼로그 생성
     * - build를 통해 생성, show()를 통해 보여주기
     * ● takePhoto()
     * 호출: 다이얼로그에서 사진찍기를 선택했을 시
     * 프로세스
     * - 인텐트 생성 (카메라로 들어가는)
     * - 사진을 찍을 수 있는 액티비티가 존재하는지 여부를 확인
     * - File을 생성해 이미지를 담을 file을 만듬
     * - 사진을 공유하기 위해 uri로 담는다.
     * <p>
     * ● createImageFile()
     * 호출: 사진을 찍을때 이미지를 담을 공간이 필요하기 카메라를 실행하기 전에 만듬
     * 프로세스
     * - 파일 이름을 고유 식별자로 만들기 위해 시간을 집어넣음.
     * <p>
     * ● goToAlbum()
     * 호출 : 일반 이미지에서 값을 가져오기 위함.
     * ● cropImage()
     * 호출: 사진을 찍거나 골랐을 시, 사진을 자르기 위함.
     */
    private void createAlertDialog() {
        Log.d(tag, "WriteDiaryActivity - createAlertDialog() |  ");

        final CharSequence[] items = {"사진 촬영", "사진 선택", "사진 제거"};    //CharSequence 대신 String을 써도 작동된다.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WriteDiaryActivity.this);
        alertDialogBuilder.setTitle("사진 선택 목록");
        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                switch (id) {
                    case 0:
                        Toast.makeText(WriteDiaryActivity.this, "사진 촬영", Toast.LENGTH_SHORT).show();
                        takePhoto();
                        Log.d(tag, "WriteDiaryActivity - onClick() | sendTakePhotoIntent() 완료 ");
                        break;
                    case 1:
                        Toast.makeText(WriteDiaryActivity.this, "앨범 선택", Toast.LENGTH_SHORT).show();
                        goToAlbum();
                        Log.d(tag, "WriteDiaryActivity - onClick() |  ");
                        break;
                    case 2:
                        Toast.makeText(WriteDiaryActivity.this, "사진 제거", Toast.LENGTH_SHORT).show();
                        imageUri = null;
                        pictureImageView.setVisibility(View.GONE);
//                        // drawable 리소스 객체 가져오기
//                        Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher_foreground);
//                        pictureImageView.setImageDrawable(drawable);
                        break;
                }
            }
        });
        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();
    }

    private void takePhoto() {
        Log.d(tag, "WriteDiaryActivity - takePhoto() |  ");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.d(tag, "WriteDiaryActivity - takePhoto() | if(intent.resolveActivity(getPackageManager()) != null) __ true : 처리해줄 App 존재 O");

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(WriteDiaryActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }

            if (photoFile != null) {
                // 카메라 실행
                // 특이한 점. 저장할 공간을 카메라에게 미리 전달해준다는 점.
                // Android7.0 이전 에서는 Uri.fromFile 함수로 파일의 URI 정보를 얻을 수 있지만,
                // Android7.0 이후 부터는 보안상 이슈로 아래와 같이 설정하고, FileProvider.getUriForFile 함수를 이용해서 URI 정보를 얻어야 합니다.
                imageUri = FileProvider.getUriForFile(WriteDiaryActivity.this, getPackageName(), photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        }
    }

    /**카메라에서 찍은 사진을 저장할 파일 만들기(즉 저장소 만들기)*/
    private File createImageFile() throws IOException {
        Log.d(tag, "WriteDiaryActivity - createImageFile() |  ");

        // 이미지 파일 이름 ex. (Visible_Time_yyyyMMdd_HHmmss)
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Visible_Time_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 (Pictures)
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // 빈 파일 생성
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
//        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void goToAlbum() {
        Log.d(tag, "WriteDiaryActivity - goToAlbum() |  ");
        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public void cropImage() {
        Log.d(tag, "WriteDiaryActivity - cropImage() |  ");

        this.grantUriPermission("com.android.camera", imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.d(tag, "WriteDiaryActivity - cropImage() | this.grantUriPermission() ");

        Intent intent = new Intent("com.android.camera.action.CROP");
        Log.d(tag, "WriteDiaryActivity - cropImage() | Intent intent = new Intent(\"com.android.camera.action.CROP\") ");

        intent.setDataAndType(imageUri, "image/*");
        Log.d(tag, "WriteDiaryActivity - cropImage() | intent.setDataAndType() ");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        Log.d(tag, "WriteDiaryActivity - cropImage() | List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0); ");

        grantUriPermission(list.get(0).activityInfo.packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.d(tag, "WriteDiaryActivity - cropImage() | grantUriPermission() ");

        int size = list.size();
        Log.d(tag, "WriteDiaryActivity - cropImage() | int size = list.size(); ");

        if (size == 0) {
            Log.d(tag, "WriteDiaryActivity - cropImage() | if (size == 0)  __ true ");
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.d(tag, "WriteDiaryActivity - cropImage() | if (size == 0)  __ false ");

            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            File croppedFileName = null;
            Log.d(tag, "WriteDiaryActivity - cropImage() | File croppedFileName = null; ");

            try {
                croppedFileName = createImageFile();
                Log.d(tag, "WriteDiaryActivity - cropImage() | createImageFile() called ");
                Log.d(tag, "WriteDiaryActivity - cropImage() | croppedFileName: " + croppedFileName);
            } catch (IOException e) {
                Log.d(tag, "WriteDiaryActivity - cropImage() | catch ");
                e.printStackTrace();
            }

            if (croppedFileName != null) {
                Log.d(tag, "WriteDiaryActivity - cropImage() | if(croppedFileName != null) __ true ");

                imageUri = FileProvider.getUriForFile(WriteDiaryActivity.this, getPackageName(), croppedFileName);
                Log.d(tag, "WriteDiaryActivity - cropImage() | photoUri: " + imageUri);

                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                Log.d(tag, "WriteDiaryActivity - cropImage() | intent.putExtra() ");

                Intent i = new Intent(intent);
                Log.d(tag, "WriteDiaryActivity - cropImage() | Intent i: " + i);
                ResolveInfo res = list.get(0);
                Log.d(tag, "WriteDiaryActivity - cropImage() | ResolveInfo res " + res);

                grantUriPermission(res.activityInfo.packageName, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Log.d(tag, "WriteDiaryActivity - cropImage() | grantUriPermission() called ");

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                Log.d(tag, "WriteDiaryActivity - cropImage() | i.setComponent ");

                startActivityForResult(i, CROP_FROM_CAMERA);
                Log.d(tag, "WriteDiaryActivity - cropImage() | startActivityForResult ");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(tag, "WriteDiaryActivity - onActivityResult() |  ");
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            Log.d(tag, "WriteDiaryActivity - onActivityResult() | if (requestCode == PICK_FROM_ALBUM) ");
            if (data == null) {
                Log.d(tag,"WriteDiaryActivity - onActivityResult() | 데이터 없음 ");
                return;
            }
            pictureImageView.setImageURI(data.getData()); // 가운데 뷰를 바꿔주는 역할
            imageUri = data.getData(); // 이미지 경로 원본
            Log.d(tag,"WriteDiaryActivity - onActivityResult() | imageUri: "+imageUri);
            cropImage();

        } else if (requestCode == PICK_FROM_CAMERA) {
            cropImage();

            // 갤러리에 나타나게
            MediaScannerConnection.scanFile(WriteDiaryActivity.this,
                    new String[]{imageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });

        } else if (requestCode == CROP_FROM_CAMERA) {
            try {
                Log.d(tag,"WriteDiaryActivity - onActivityResult() | requestCode == CROP_FROM_CAMERA ");
                pictureImageView.setVisibility(View.VISIBLE);
                InputStream in = getContentResolver().openInputStream(imageUri);
                Bitmap tempImg = BitmapFactory.decodeStream(in);
                pictureImageView.setImageBitmap(tempImg);
            } catch (Exception e) {
            }
        }
    }
    @OnClick(R.id.deleteBtn_Write)
    public void deleteBtnClicked(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WriteDiaryActivity.this);
        mBuilder.setTitle("알림");
        mBuilder.setMessage("해당 '일기'을 삭제하시겠습니까?");
        // 버튼 클릭 이벤트 달기
        mBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteDiary();
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
    /** Report Data 변경 & DiaryData 데이터 삭제
     * ■ Report Data 변경
     *      - 해당 날짜에 대한 ReportData 객체를 찾는다.
     *          ⇒ if(reportData.getDateForSearch().equals(tmpDateForSearch))
     *      - 해당 객체의 속성 'type'이 "so" 가 아닌 것을 찾는다.
     *          ⇒ if(reportData.getType().equals("so") == false)
     *      - 해당 객체의 String type 속성을 "so"로 바꾼다.
     *          ⇒ reportData.setType("so")
     *      - totalArrayList를 Shared에 저장한다
     *          ⇒ PickReportActivity.saveReportDataList
     * ■ Diary Data 변경
     *      - 해당 날짜에 대한 DiaryData 객체를 찾는다.
     *          ⇒ if(diaryData.getDateForSearch().equals(tmpDateForSearch))
     *      - 해당 index에 대한 데이터를 remove로 지운다.
     *          ⇒ totalDiaryArrayList.remove(i);
     *      - 데이터 저장하는 saveDairyData()를 호출한다.
     * */
    private void deleteDiary(){
        Log.d(tag,"WriteDiaryActivity - deleteDiary() |  ");

        Log.d(tag,"WriteDiaryActivity - deleteDiary() | 날짜: "+tmpDateForSearch);

        for(int i=0; i<totalArrayList.size(); i++){
            ReportData reportData = totalArrayList.get(i);
            if(reportData.getDateForSearch().equals(tmpDateForSearch)){
                if(reportData.getType().equals("so") == false){
                    reportData.setType("so");
                }
            }
        }
        saveReportDataList();

        for(int i=0; i<totalDiaryArrayList.size(); i++){
            DiaryData diaryData = totalDiaryArrayList.get(i);
            if(diaryData.getDateForSearch().equals(tmpDateForSearch)){
                totalDiaryArrayList.remove(i);      //해당 데이터 지우기
            }
        }
        saveDairyData();
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
        Log.d(tag,"WriteDiaryActivity - saveReportDataList() |  ");
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
