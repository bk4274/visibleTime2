package com.example.visibletime.Ui.Fragement;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.visibletime.Data.ReportData;
import com.example.visibletime.R;
import com.example.visibletime.Service.MeasureTimeService;
import com.example.visibletime.Ui.MainActivity;
import com.example.visibletime.Ui.MeasureTimeActivity;
import com.example.visibletime.Ui.TutorialActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.security.auth.callback.CallbackHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import me.huteri.analogclock.AnalogClockView;
import me.huteri.analogclock.ArcSlice;

public class HomeFragment extends Fragment {

    private final String tag = "로그";
    private final String TAG = "로그";
    private final String SERVICE_TAG = "서비스";
    View root;
    Context context;
    int totalTime = 0;  // 시간

    /**
     * ButterKnife
     */
    @BindView(R.id.time_Main)
    TextView measuredTime;
    @BindView(R.id.dayMonthTextView_Main)
    TextView dayMonthTextView;
    @BindView(R.id.stopMeasureBtn_Main)
    Button stopMeasureBtn;

    @BindView(R.id.analogClockView_Main)
    AnalogClockView analogClockView;
    @BindView(R.id.categoryName_Home)
    TextView categoryName;
    @BindView(R.id.categoryTime_Home)
    TextView categoryTime;


    private Messenger mServiceCallback = null;      //서비스로부터 전달받는 객체, 서비스 코드에서 바인딩할 때 던져주는 IBinder로 만들어진 Messenger 객체
    private Messenger mClientCallback = new Messenger(new CallbackHandler());       //서비스에서 Activity로 결과를 리턴하기 위해 사용되는 Messenger 객체

    /**날짜*/
    Calendar calendar;
    SimpleDateFormat dateFormat;
    String tmpDateForSearch;       // 기록을 저장한 것을 분류하기 위한 값

    /**Report Data*/
    ArrayList<ReportData> totalArrayList;     // Data를 선별하기 앞서 전체 데이터를 저장함.
    ArrayList<ReportData> arrayListSelected;    // 날짜에 맞춰 선별된 list

    /**ArcSlice Data*/
    ArrayList<ArcSlice> arcSliceList;
    ArcSlice arcSliceMeasured;

    // 핸들러에서 이 불린 변수를 통해, arcSliceMeasured를 초기화 여부를 결정한다.
    boolean isRunningService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(tag,"HomeFragment - onCreateView() |  ");
        root = inflater.inflate(R.layout.fragment_home, container, false);        // Inflate the layout for this fragment
        this.context = container.getContext();
        ButterKnife.bind(this, root);

        // Option Menu 활성화
        setHasOptionsMenu(true);

        // 초기화
        categoryName.setVisibility(View.GONE);
        categoryTime.setVisibility(View.GONE);

        totalArrayList = new ArrayList<>();
        arrayListSelected = new ArrayList<>();
        arcSliceList = new ArrayList<>();

        arcSliceMeasured = new ArcSlice();
        arcSliceMeasured.setColor(Color.parseColor("#71BBFC"));
        arcSliceMeasured.setStartTime(new Date());
        arcSliceMeasured.setEndTime(new Date());
        getTime();


        /** UI를 변경만 해주면 되는 것
         * */
        Boolean isMyServiceRunning = isMyServiceRunning(MeasureTimeService.class);
        if( isMyServiceRunning == true){
            Log.d(tag, "HomeFragment - onCreateView() |  ");
            Intent intent = new Intent(context, MeasureTimeService.class);
            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            stopMeasureBtn.setText("STOP");
        }
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(tag,"HomeFragment - onResume() |  ");

        Log.d(tag,"HomeFragment - onResume() | 측정 시간: "+measuredTime.getText().toString());

        getListOfDataFromSharedPreferences();
        Log.d(tag,"HomeFragment - onResume() | totalArrayList.size() "+totalArrayList.size());
        selectDate();
        Log.d(tag,"HomeFragment - onResume() | arrayListSelected.size() "+arrayListSelected.size());
        setArcSliceList();
        Log.d(tag,"HomeFragment - onResume() | arcSliceList.size(): "+arcSliceList.size());

        analogClockView.setHourColor(Color.parseColor("#00ff0000"));
        analogClockView.setMinuteColor(Color.parseColor("#00ff0000"));
        analogClockView.setSecondColor(Color.parseColor("#00ff0000"));
//        analogClockView.setClockThickness(10);

        analogClockView.setList(arcSliceList);
        Log.d(tag,"HomeFragment - onResume() | analogClockView.setList(arcSliceList) called ");

        // 슬리이스 클릭 이벤트
        // OnResume에다가 생성한 이유는, 아직 테스트는 안 해봤지만, setArcSliceList() 후에 와야 할 거 같기 때문.
        analogClockView.setOnSliceClickListener(new AnalogClockView.SliceClickListener() {
            @Override
            public void onSliceClick(int pos) {
                if(pos == 0){
                    categoryName.setVisibility(View.VISIBLE);
                    categoryName.setText("측정 중");
                    categoryName.setTextColor(Color.parseColor("#71BBFC"));
                    categoryTime.setVisibility(View.GONE);
                } else{
                    // 현재 analogClockView에 들어간 arcSliceList의 index 0에 값을 추가로 넣어줬기 때문에, arrayListSelected 보다
                    // 1일 더 많기 때문에 빼준다.
                    ReportData reportData = arrayListSelected.get(pos-1);

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
                }
                Thread timeChangeThread = new Thread(new TextChangeThread());
                timeChangeThread.start();
            }
        });
        Log.d(TAG,"HomeFragment - onResume() | onResume end ");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(tag,"HomeFragment - onCreateOptionsMenu() |  ");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_question :
                Intent intent = new Intent(context, TutorialActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTime() {
        Log.d(tag, "HomeFragment - getTime() |  ");

        /**시간 설정*/
        // 월,일 구하기
        calendar = Calendar.getInstance();
        SimpleDateFormat formatDayMonth = new SimpleDateFormat("MM월 dd일");
        String dayMonth = formatDayMonth.format(calendar.getTime());

        dateFormat = new SimpleDateFormat("yyyyMMdd");
        tmpDateForSearch = dateFormat.format(calendar.getTime());

        // 요일 구하기
        String week = "week";
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                week = "일";
                break;
            case 2:
                week = "월";
                break;
            case 3:
                week = "화";
                break;
            case 4:
                week = "수";
                break;
            case 5:
                week = "목";
                break;
            case 6:
                week = "금";
                break;
            case 7:
                week = "토";
                break;
        }
        dayMonthTextView.setText(dayMonth+" ("+week+")");
    }

    /**
     * 서비스 연결
     */
    private void getService() {
        Log.d(tag, "HomeFragment - getService() |  ");
        Intent intent = new Intent(context, MeasureTimeService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        context.startService(intent);
    }

    /**서비스 연결중인지 확인*/
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mServiceCallback = new Messenger(service); //Service에서 생성한 IBinder를 통해서 Messenger가 생성됨

            // 시작 시간을 메세지로 보내기
            SimpleDateFormat formatForSort = new SimpleDateFormat("HHmmss");
            String startTimeStr = formatForSort.format(new Date());
            int startTimeInt = Integer.parseInt(startTimeStr);

            // connect to service
            Message connect_msg = Message.obtain(null, MeasureTimeService.MSG_CLIENT_CONNECT);
            connect_msg.arg1=startTimeInt;
            connect_msg.replyTo = mClientCallback;
            try {
                mServiceCallback.send(connect_msg);     //Send MSG_CLIENT_CONNECT message to Service
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceCallback = null;
        }
    };

    /**
     * 서비스에서 보낸 메세지를 보내면 callback하는 메서드
     */
    private class CallbackHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MeasureTimeService.MSG_TIME_VALUE:
                    /**arcSliceMeasured 데이터 변경*/
                    totalTime = msg.arg1;
                    /**시간계산*/
                    int sec = (msg.arg1) % 60;
                    int min = (msg.arg1) % 3600/ 60;
                    int hour = (msg.arg1) / 3600;
                    //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
                    @SuppressLint("DefaultLocale")
                    String result = String.format("%02d:%02d:%02d", hour, min, sec);
                    measuredTime.setText(result);

                    // 시작 시간 계산
                    Log.d(tag,"CallbackHandler - handleMessage() | startTimeInt 전: "+msg.arg2 );
                    int startTimeInt = msg.arg2;
                    String startTimeStr = String.format("%06d", startTimeInt);
                    Log.d(TAG,"CallbackHandler - handleMessage() | startTimeStr: "+startTimeStr);

                    String hourStr = startTimeStr.substring(0,2);
                    String minutesStr = startTimeStr.substring(2,4);
                    String secondStr = startTimeStr.substring(4,6);

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
                    Log.d(tag,"CallbackHandler - handleMessage() | 시: "+hourInt/2);
                    Log.d(tag,"CallbackHandler - handleMessage() | 분: "+sumMinute);
                    Log.d(tag,"CallbackHandler - handleMessage() | 초: "+sumSecond);

                    calendar.set(Calendar.HOUR_OF_DAY, hourInt/2);
                    calendar.set(Calendar.MINUTE, sumMinute);
                    calendar.set(Calendar.SECOND, sumSecond);
                    // 시작 시간 값 넣기
                    arcSliceList.get(0).setStartTime(calendar.getTime());

                    calendar.set(Calendar.HOUR_OF_DAY, hourInt/2);
                    calendar.set(Calendar.MINUTE, sumMinute);
                    // 2를 나눠주는 이유는, 현재 라이브러리는 12시간제인데 나는 24시간제로 보여줘야 하기 때문이다.
                    calendar.set(Calendar.SECOND, sumSecond+totalTime/2);

                    arcSliceList.get(0).setEndTime(calendar.getTime());

                    // 화면 갱신
                    analogClockView.setList(arcSliceList);

                    Log.d(tag,"CallbackHandler - handleMessage() | isRunningService: "+isRunningService);
                    break;
            }
        }
    }

    /** stopMeasureBtnClicked
     *      - 서비스가 실행 중인지 아닌지 판단한다.
     *      - 서비스가 실행 중이라면 중지 Activity를 보낸다.
     *      - 서비스가 실행 중이 아니라면 서비스를 시작한다.
     * */
    @OnClick(R.id.stopMeasureBtn_Main)
    public void stopMeasureBtnClicked() {
//        /**Report값 삭제*/
//        SharedPreferences sharedPresesntID = this.getActivity().getSharedPreferences("presentID", Context.MODE_PRIVATE);
//        String presentID = sharedPresesntID.getString("presentID",null);
//
//        // ● ID를 파일명으로 하는 Shared.xml 파일 생성 및 기존 값 지우기
//        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(presentID, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove("alarm");
//        editor.remove("controllerPlan");
//        editor.remove("planFrame");
//        editor.remove("category");
//        editor.remove("report");
//        editor.remove("diary");
//        editor.commit();
//        Log.d(tag,"HomeFragment - stopMeasureBtnClicked() | Shared Report 값 삭제 ");

        if(mServiceCallback == null){
            // 서비스 실행 중이 아닌 경우
            stopMeasureBtn.setText("STOP");
            getService();
            isRunningService = true;
        } else{
            isRunningService = false;
            /**서비스 제어*/
            Message pause_msg = Message.obtain(null, MeasureTimeService.MSG_PAUSE_VALUE);
            try {
                mServiceCallback.send(pause_msg);     //Send MSG_PAUSE_VALUE message to Service
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(tag,"HomeFragment - onClick() | 서비스에게 중지하라고 신호 ");

            /**서비스 종료*/
            //bind 해제 하기
            context.unbindService(mConnection);
            Intent intentStopService = new Intent(context, MeasureTimeService.class);
            context.stopService(intentStopService);
            mServiceCallback = null;

            //View 변경
            stopMeasureBtn.setText("START");
//            measuredTime.setText("00:00:00");
            Log.d(tag,"HomeFragment - stopMeasureBtnClicked() | 측정 시간: "+measuredTime.getText().toString());

            // 기록 측정하는 액티비티로 이동
            Intent intent = new Intent(getActivity(), MeasureTimeActivity.class);
            intent.putExtra("time", totalTime);
            startActivity(intent);
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
    private void getListOfDataFromSharedPreferences(){
        Log.d(tag,"HomeFragment - getListOfDataFromSharedPreferences() |  ");
        SharedPreferences sharedPresesntID = this.getActivity().getSharedPreferences("presentID", Context.MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // 기존 값 지우기
        if(totalArrayList.size() != 0){
            Log.d(tag,"HomeFragment - getListOfDataFromSharedPreferences() | 값 지우기 ");
            totalArrayList.clear();
        }

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(presentID, Context.MODE_PRIVATE);
        try {
            // ● JSONArray 생성
            JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("report", null) + "]");
            // ● 반복문(JSONArray.length())
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

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
            Log.d(tag, "HomeFragment - onLoginBtnClicked() | Exception " + e);
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
        Log.d(tag,"HomeFragment - selectDate() |  ");
        if(arrayListSelected.size() != 0){
            arrayListSelected.clear();
        }
        for(int i=0; i<totalArrayList.size(); i++){
            if(totalArrayList.get(i).getDateForSearch().equals(tmpDateForSearch)){
                arrayListSelected.add(totalArrayList.get(i));
            }
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
        Log.d(tag,"HomeFragment - setArcSliceList() |  ");
        Log.d(tag,"HomeFragment - setArcSliceList() | isRunningService: "+isRunningService);

        // 시간 측정할 때 보여줄 ArcSlice 인스턴스 추가
        if(arcSliceList.size() != 0){
            Log.d(tag,"HomeFragment - setArcSliceList() | arcSliceList 값 지우기 ");
            arcSliceList.clear();
        }
        if (isRunningService == false){
            arcSliceMeasured.setColor(Color.parseColor("#71BBFC"));
            arcSliceMeasured.setStartTime(new Date());
            arcSliceMeasured.setEndTime(new Date());

            // 기존에 stop 버튼을 누르고 나서 text를 변경했으나, 핸들러가 다시 값을 집어 넣어 변경되지 않은 점을 보안
            measuredTime.setText("00:00:00");
        }
//        Log.d(tag,"HomeFragment - setArcSliceList() | 세팅 시간 "+arcSliceMeasured.getStartTime());
//        Log.d(tag,"HomeFragment - setArcSliceList() | 세팅 시간"+arcSliceMeasured.getEndTime());

        arcSliceList.add(arcSliceMeasured);

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
}