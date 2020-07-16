package com.example.visibletime.Ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visibletime.AlarmNotification;
import com.example.visibletime.Data.AlarmData;
import com.example.visibletime.Data.AuthenticationData;
import com.example.visibletime.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditPasswordActivity extends AppCompatActivity {

    private final String TAG = "로그";
    private final String tag = "로그";
    /**
     * View
     */
    //Email
    @BindView(R.id.passwordPresentEditText_EditPassword)
    EditText passwordPresentEditText;
    @BindView(R.id.checkPasswordBtn_EditPassword)
    Button checkPasswordBtn;

    //Email에 상태에 따른 알림글
    @BindView(R.id.correctPasswordView_EditPassword)
    TextView correctPasswordView;
    @BindView(R.id.incorrectPasswordTextView_EditPassword)
    TextView incorrectPasswordTextView;

    //비밀번호
    @BindView(R.id.passwordEditText_EditPassword)
    EditText password;
    @BindView(R.id.passwordCheckEditText_EditPassword)
    EditText passwordCheck;
    @BindView(R.id.checkPasswordImgView_EditPassword)
    ImageView checkPasswordImgView;
    //확인버튼
    @BindView(R.id.enterBtn_EditPassword)
    Button enterBtn;

    /**회원 리스트*/
    ArrayList<AuthenticationData> authenticationDataArrayList;

    // 현재 사용자의 ID
    String presentId;
    String presentPassword;
    int index;
    boolean isCertified;

    /**알람 해제를 위한 변수*/
    private ArrayList<AlarmData> mArrayList;
    AlarmNotification alarmNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        ButterKnife.bind(this);

        authenticationDataArrayList = new ArrayList<>();

        correctPasswordView.setVisibility(View.GONE);
        incorrectPasswordTextView.setVisibility(View.GONE);

        isCertified = false;        // 인증 확인 초기화

        getAuthList();
        Log.d(TAG,"EditPasswordActivity - onCreate() | authenticationDataArrayList.size: "+authenticationDataArrayList.size());

        getPresendId();
        Log.d(TAG,"EditPasswordActivity - onCreate() | presentId: "+presentId);
        getPresentPassword();
        Log.d(TAG,"EditPasswordActivity - onCreate() | presentPassword: "+presentPassword);
        Log.d(TAG,"EditPasswordActivity - onCreate() | index: "+index);
        setPassword();

    }

    @OnClick(R.id.checkPasswordBtn_EditPassword)
    public void checkPasswordBtnClicked(){
        if(passwordPresentEditText.getText().toString().equals(presentPassword)){
            correctPasswordView.setVisibility(View.VISIBLE);
            incorrectPasswordTextView.setVisibility(View.GONE);

            isCertified = true;
        } else{
            incorrectPasswordTextView.setVisibility(View.VISIBLE);
            correctPasswordView.setVisibility(View.GONE);
            isCertified = false;
        }
    }

    @OnClick(R.id.enterBtn_EditPassword)
    public void enterBtnClicked(){
        if (isCertified) {
            // □ 비밀 번호 6자리 이상 & 일치 여부 확인
            if (password.getText().toString().length() >= 6 && password.getText().toString().equals(passwordCheck.getText().toString())) {
                // 조건에 충족한 경우
                authenticationDataArrayList.get(index).setPassword(password.getText().toString());
                // 회원정보 수정값 저장
                editAuthenticationData();

                // 자동 로그인 해제
                SharedPreferences sharedAutoLogin = getSharedPreferences("autoLogin", MODE_PRIVATE);
                SharedPreferences.Editor editorAutoLogin= sharedAutoLogin.edit();
                editorAutoLogin.clear();
                editorAutoLogin.commit();

                /**알람 해제*/
                getListOfDataFromSharedPreferences();
                requestAlarm();

                // 로그인 화면으로 이동
                Intent intent = new Intent(this, LoginActivity.class);
                
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    Log.d(TAG,"EditPasswordActivity - enterBtnClicked() | 진저그레이져 이상 ");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); 
                } else { 
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
                }                
                startActivity(intent);
                finish();
                Toast.makeText(EditPasswordActivity.this, "비밀번호 변경 완료. 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 비밀번호 인증 안된경우
                Toast.makeText(EditPasswordActivity.this, "새 비밀번호 양식을 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 현재 비밀번호 인증이 완료가 안된 경우
            Toast.makeText(EditPasswordActivity.this, "기존 비밀번호 확인 인증을 해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
    // 현재 사용자의 ID 가져오기
    private void getPresendId(){
        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        presentId = sharedPresesntID.getString("presentID",null);
    }

    /**회원정보 다 가져오기*/
    private void getAuthList(){
        SharedPreferences sharedLogin = getSharedPreferences("login", MODE_PRIVATE);

        try {
            JSONArray jsonArray = new JSONArray("[" + sharedLogin.getString("login", "") + "]");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                AuthenticationData authenticationData = new AuthenticationData();
                authenticationData.setId(jsonObject.getString("id"));
                authenticationData.setPassword(jsonObject.getString("password"));

                authenticationDataArrayList.add(authenticationData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"EditPasswordActivity - getAuthList() | Exception "+e);
        }
    }

    /**회원정보 수정후 다시 저장하기*/
    private void editAuthenticationData(){
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("login");
        editor.commit();


        // ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
        if(authenticationDataArrayList.size() != 0){
            for(int i=0; i<authenticationDataArrayList.size(); i++){
                /**JSON 형태로 저장*/
                String strDataOfJSON;
                strDataOfJSON = "{\"id\"" + ":" + "\"" + authenticationDataArrayList.get(i).getId() + "\"" + ","
                        + "\"password\"" + ":" + "\"" + authenticationDataArrayList.get(i).getPassword() + "\""
                        + "}";

                // □ 값을 처음으로 저장하는 확인
                String login = sharedPreferences.getString("login", null);

                if (login == null) {
                    // 첫 카테고리 정보를 저장하는 경우
                    editor.putString("login", strDataOfJSON);
                    editor.commit();
                } else {
                    // 카테고리 정보가 처음이 아닌 경우
                    editor.putString("login", sharedPreferences.getString("login", "") + "," + strDataOfJSON);
                    editor.commit();
                }
            }
        }
    }

    /**비밀번호 가져오기*/
    private void getPresentPassword(){
        for(int i=0; i<authenticationDataArrayList.size(); i++){
            if(authenticationDataArrayList.get(i).getId().equals(presentId)){
                presentPassword=authenticationDataArrayList.get(i).getPassword();
                index = i;
            }
        }
    }

    private void setPassword() {
        /**패스워드가 같은 경우 사용자에게 보여주기 위함*/
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 비밀번호 체크 EditText에 값이 있는지 여부 확인
                // ⇒ 값이 있다면 같은지 다른지에 따른 ImageView 보여주기
                // ⇒ 값이 없다면 ImageView 사라지게 하자.
                if (!passwordCheck.getText().toString().isEmpty()) {
                    Log.d(tag, "EditPasswordActivity - onTextChanged() |  ");
                    if (password.getText().toString().equals(passwordCheck.getText().toString())) {
                        checkPasswordImgView.setImageResource(R.drawable.ic_correct);
                    } else {
                        checkPasswordImgView.setImageResource(R.drawable.ic_quit);
                    }
                } else {
                    checkPasswordImgView.setImageResource(R.drawable.ic_trasparent_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (password.getText().toString().length() == 16) {
                    sendToast("비밀번호는 16자리보다 작어야 합니다.");
                }
            }
        });

        passwordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 비밀번호 password EditText 값이 있는지 여부 확인
                // ⇒ 값이 있다면 같은지 다른지에 따른 ImageView 보여주기
                // ⇒ 값이 없다면 ImageView 사라지게 하자.
                Log.d(tag, "EditPasswordActivity - onTextChanged() | password.getText().toString().isEmpty(): " + password.getText().toString().isEmpty());
                if (!password.getText().toString().isEmpty()) {
                    if (password.getText().toString().equals(passwordCheck.getText().toString())) {
                        Log.d(tag, "EditPasswordActivity - onTextChanged() | 값 일치 ");
                        checkPasswordImgView.setImageResource(R.drawable.ic_correct);
                    } else {
                        Log.d(tag, "EditPasswordActivity - onTextChanged() | 값 불일치 ");
                        checkPasswordImgView.setImageResource(R.drawable.ic_quit);
                    }
                } else {
                    Log.d(tag, "EditPasswordActivity - onTextChanged() | password 값 존재 X ");
                    checkPasswordImgView.setImageResource(R.drawable.ic_trasparent_24dp);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (password.getText().toString().length() < 6) {
                    sendToast("비밀번호는 6자리보다 커야 합니다.");
                }
            }
        });
    }

    private void getListOfDataFromSharedPreferences(){
//        mArrayList.clear();
        mArrayList = new ArrayList<>();
        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);

        // 값이 있는지 여부 확인하기
        if(sharedPreferences.getString("alarm",null) == null){
            Log.d(tag,"SettingFragment - getListOfDataFromSharedPreferences() | 값 X ");
        } else {
            Log.d(tag,"SettingFragment - getListOfDataFromSharedPreferences() | 값 존재 ");
            try {
                // ● JSONArray 생성
                JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("alarm", null) + "]");
                // ● 반복문(JSONArray.length())
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // ○ JSON에 String으로 저장된 값을 AlarmData 데이터 타입에 맞게 변환을 위한 변수 선언
                    boolean isChecked = false;
                    boolean[] arrayBooleanWeek = new boolean[8];
                    int hour_24 = 0;
                    int hour_12 = 0;
                    int minute = 0;

                    // boolean isChecked를 얻기 위한 데이터 변환
                    if (jsonObject.getString("isChecked").equals("true")) {
                        isChecked = true;
                    }
                    // boolean [] arrayWeek을 얻기 위한 데이터 변환(String > boolean[])
                    String[] arrayStrWeek = jsonObject.getString("arrayWeek").split(",");
                    for (int j = 0; j < arrayStrWeek.length; j++) {
                        if (arrayStrWeek[j].equals("true")) {
                            arrayBooleanWeek[j] = true;
                        }
                    }
                    // int hour_24, hour_12, minute 을 얻기 위한 데이 변환
                    hour_24 = Integer.parseInt(jsonObject.getString("hour_24"));
                    hour_12 = Integer.parseInt(jsonObject.getString("hour_12"));
                    minute = Integer.parseInt(jsonObject.getString("minute"));

                    // ○ ArrayList에 값 넣기
                    // Data 객체 생성 & 값 넣어주기
                    AlarmData alarmData = new AlarmData();
                    alarmData.setChecked(isChecked);
                    alarmData.setName(jsonObject.getString("name"));
                    alarmData.setAm_pm(jsonObject.getString("am_pm"));
                    alarmData.setWeekName(jsonObject.getString("weekName"));
                    alarmData.setHour_24(hour_24);
                    alarmData.setHour_12(hour_12);
                    alarmData.setMinute(minute);
                    alarmData.setArrayWeek(arrayBooleanWeek);
                    // mArrayList에 값 넣어주기
                    mArrayList.add(alarmData);
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.d(tag, "SettingFragment - onLoginBtnClicked() | Exception " + e);
            }
        }
    }
    /**알람 요청하기*/
    private void requestAlarm() {
        for(int i=0; i<mArrayList.size(); i++){
            if(mArrayList.get(i).isChecked()){
                Log.d(tag,"SettingAlarmActivity - requestAlarm() | i: "+i);
                int hour_24, minute;
                boolean [] arrayWeek;
                hour_24 = mArrayList.get(i).getHour_24();
                minute = mArrayList.get(i).getMinute();
                arrayWeek = mArrayList.get(i).getArrayWeek();
                alarmNotification = new AlarmNotification(this,hour_24,minute,arrayWeek,i);
                alarmNotification.cancelAlarm();
            }
        }
    }

    private void sendToast(String msg) {
        Toast.makeText(EditPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
