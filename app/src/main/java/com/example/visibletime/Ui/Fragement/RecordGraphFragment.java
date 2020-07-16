package com.example.visibletime.Ui.Fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Adapter.ReportAdatper;
import com.example.visibletime.Data.ReportData;
import com.example.visibletime.R;
import com.example.visibletime.Ui.EditReportActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.huteri.analogclock.AnalogClockView;
import me.huteri.analogclock.ArcSlice;

public class RecordGraphFragment extends Fragment  {

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

    /**ArcSlice Data*/
    ArrayList<ArcSlice> arcSliceList;

    /**
     * ButterKnife
     */
    @BindView(R.id.dateTextView_RecordGraphFragment)
    TextView dateTextView;

    @BindView(R.id.alarmTextView_RecordGraphFragment)
    TextView alarmTextView;

    @BindView(R.id.analogClockView_RecordGraphFragment)
    AnalogClockView analogClockView;
    @BindView(R.id.categoryName_RecordGraphFragment)
    TextView categoryName;
    @BindView(R.id.categoryTime_RecordGraphFragment)
    TextView categoryTime;


    @BindView(R.id.constraintLayout_RecordGraphFragment)
    androidx.constraintlayout.widget.ConstraintLayout constraintLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(tag,"RecordGraphFragment - onCreateView() |  ");
        root = inflater.inflate(R.layout.fragment_record_graph, container, false);        // Inflate the layout for this fragment
        ButterKnife.bind(this, root);
        setHasOptionsMenu(true);
        this.context = container.getContext();

        if(getArguments() != null){
            tmpDateForSearch = getArguments().getString("tmpDateForSearch");
            title = getArguments().getString("title");
            dateTextView.setText(title);
        }

        initView();



        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"RecordGraphFragment - onResume() |  ");
        getListOfDataFromSharedPreferences();
        Log.d(tag,"RecordGraphFragment - onCreateView() | totalArrayList.size() "+totalArrayList.size());
        selectDate();
        Log.d(tag,"RecordGraphFragment - onCreateView() | arrayListSelected.size() "+arrayListSelected.size());
        setArcSliceList();
        Log.d(tag,"RecordGraphFragment - onCreateView() | arcSliceList.size(): "+arcSliceList.size());
        analogClockView.setList(arcSliceList);

        // 슬리이스 클릭 이벤트
        // OnResume에다가 생성한 이유는, 아직 테스트는 안 해봤지만, setArcSliceList() 후에 와야 할 거 같기 때문.
        analogClockView.setOnSliceClickListener(new AnalogClockView.SliceClickListener() {
            @Override
            public void onSliceClick(int pos) {
                // 현재 analogClockView에 들어간 arcSliceList의 index 0에 값을 추가로 넣어줬기 때문에, arrayListSelected 보다
                // 1일 더 많기 때문에 빼준다.
                ReportData reportData = arrayListSelected.get(pos);

                int time = reportData.getTimeDuration();
                int min = (time) % 3600 / 60;
                int hour = (time) / 3600;
                //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

                String result = String.format("%02d시간 %02d분", hour, min);

                categoryName.setText(reportData.getCategory());
                categoryName.setTextColor(reportData.getColor());
                categoryTime.setText(result);
                categoryName.setVisibility(View.VISIBLE);
                categoryTime.setVisibility(View.VISIBLE);
                Thread timeChangeThread = new Thread(new TextChangeThread());
                timeChangeThread.start();
            }
        });
    }

    private void initView() {
        // 초기화
        categoryName.setVisibility(View.GONE);
        categoryTime.setVisibility(View.GONE);

        totalArrayList = new ArrayList<>();
        arrayListSelected = new ArrayList<>();
        arcSliceList = new ArrayList<>();

        analogClockView.setHourColor(Color.parseColor("#00ff0000"));
        analogClockView.setMinuteColor(Color.parseColor("#00ff0000"));
        analogClockView.setSecondColor(Color.parseColor("#00ff0000"));

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
    public void getListOfDataFromSharedPreferences(){
        Log.d(TAG,"RecordGraphFragment - getListOfDataFromSharedPreferences() |  ");
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
        Log.d(tag,"RecordGraphFragment - selectDate() |  ");

        if(arrayListSelected.size() != 0){
            arrayListSelected.clear();
        }

        for(int i=0; i<totalArrayList.size(); i++){
            if(totalArrayList.get(i).getDateForSearch().equals(tmpDateForSearch)){
                arrayListSelected.add(totalArrayList.get(i));
            }
        }
        Collections.sort(arrayListSelected);        // 시간 내림차순으로 정렬
        if(arrayListSelected.size() == 0){
            alarmTextView.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.GONE);
        } else {
            alarmTextView.setVisibility(View.GONE);
            constraintLayout.setVisibility(View.VISIBLE);
        }
    }

    /** setArcSliceList()
     *  AnalogClockView는 도넛형 이미지를 가지고 있다.
     *  AnalogClockView.setList() 로 ArcSlice의 데이터 리스트를 넣어주게 되면
     *  ArcSlice 객체 가지고 있는 시작시간과 끝시간에 맞춰서 색상을 표현해 주는 기능을 가지고 있다.
     *
     *  그러므로 우리는 ArcSlice의 데이터 리스트를 만들어야 한다.
     *  ArcSlice는 Date startTime, Date endTime; int color; 가 들어가야 한다.
     *  arrayListSelected의 Report Data에서 dateForSort, timeDuration, color를 가공해 얻을 수 있다.
     * */

    private void setArcSliceList(){
        Log.d(tag,"RecordGraphFragment - setArcSliceList() |  ");

        // 시간 측정할 때 보여줄 ArcSlice 인스턴스 추가
        if(arcSliceList.size() != 0){
            Log.d(tag,"RecordGraphFragment - setArcSliceList() | arcSliceList 값 지우기 ");
            arcSliceList.clear();
        }

        Calendar calendar = Calendar.getInstance();

        for(int i=0; i<arrayListSelected.size(); i++){
            ReportData reportData = arrayListSelected.get(i);
            ArcSlice arcSlice = new ArcSlice();

            // 기록 종료 시점인, HHmmss 의 int형 데이터가 들어가게 됨
            int endTimeInt = reportData.getDateForSort();
            String endTimeStr = String.format("%06d", endTimeInt);

            String hourStr = endTimeStr.substring(0,2);
            String minutesStr = endTimeStr.substring(2,4);
            String secondStr = endTimeStr.substring(4,6);

            int hourInt = Integer.parseInt(hourStr);
            int minutesInt = Integer.parseInt(minutesStr);
            int secondInt = Integer.parseInt(secondStr);

            /**
             * 현재 시 분 초의 값을 알고 있는데 이를 절반으로 나눠야 한다.
             * 그 이유는 12시간에서 24시간제로 바꾸고 싶기 때문이다.
             * 처음에는 단순히 시작시간 값을 2로 나누면 되겠다고 생각했는데, 그러면 안되는게 시간은 10진수가 아니라 60진수이기
             * 때문이다.
             * 그러므로 각 시, 분 , 초를 2로 나누었을 때 나머지 없이 딱 떨어지지 않는 경우 밑 자리수에 30을 더해줘야 한다.
             * 로직은 다음과 같다.
             *      1. 시, 분, 초를 %2를 했을 때 값이 1인지 확인한다.
             *          ⇒ if(harfHour % 2 == 1)
             *      2. 1인 경우에는 harfHour, harfMinutes 를 30, 아닌 경우에는 0으로 하자
             *      3. Calendar에 값을 넣을 때는 /2를 한 상태에서 + harf 변수 값을 넣어주자.
             * */
            int harfHour = 0;
            int harfMinutes = 0;

            if(hourInt %2 == 1){
                harfHour = 30;
            }
            if(minutesInt %2 == 1){
                harfMinutes = 30;
            }

            int sumMinute = minutesInt/2 + harfHour;
            int sumSecond = secondInt/2+harfMinutes;

            calendar.set(Calendar.HOUR_OF_DAY, hourInt/2);
            calendar.set(Calendar.MINUTE, sumMinute);
            calendar.set(Calendar.SECOND, sumSecond);

            arcSlice.setEndTime(calendar.getTime());

            calendar.set(Calendar.HOUR_OF_DAY, hourInt/2);
            calendar.set(Calendar.MINUTE, sumMinute);
            // 2를 나눠주는 이유는, 현재 라이브러리는 12시간제인데 나는 24시간제로 보여줘야 하기 때문이다.
            calendar.set(Calendar.SECOND, sumSecond-reportData.getTimeDuration()/2);  // 측정된 초만큼 빼주면, 시작시간을 알 수 있다.
            arcSlice.setStartTime(calendar.getTime());

            arcSlice.setColor(reportData.getColor());

            arcSliceList.add(arcSlice);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int result = msg.arg1;
            if(result == 1){
                categoryName.setVisibility(View.GONE);
                categoryTime.setVisibility(View.GONE);
            }
        }
    };

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//
//    }

    /**
     * TextChangeThread
     * 역할
     */
    public class TextChangeThread implements Runnable {
        @Override
        public void run() {
            Log.d(tag, "TextChangeThread - run() |  ");
            int i = 1;
            Message msg = new Message();
            msg.arg1 = i;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return; // 인터럽트 받을 경우 return
            }
            handler.sendMessage(msg);
        }
    }

    public void changeDate(String tmpDateForSearch, String title){
        this.tmpDateForSearch = tmpDateForSearch;
        this.title = title;
        Log.d(tag,"RecordGraphFragment - changeDate() | tmpDateForSearch: " + tmpDateForSearch);

        dateTextView.setText(title);
        arrayListSelected.clear();
        selectDate();
        Log.d(tag,"RecordGraphFragment - changeDate() | arrayListSelected.size() "+arrayListSelected.size());
        setArcSliceList();
        Log.d(tag,"RecordGraphFragment - changeDate() | arcSliceList.size(): "+arcSliceList.size());
        analogClockView.setList(arcSliceList);
    }
}