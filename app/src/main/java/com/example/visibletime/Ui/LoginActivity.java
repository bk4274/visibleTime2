package com.example.visibletime.Ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.visibletime.Data.AuthenticationData;
import com.example.visibletime.R;
import com.example.visibletime.Ui.Fragement.SettingFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    private final String tag = "로그";

    /**
     * View
     */
    @BindView(R.id.loginBtn_Login)
    Button loginBtn;
    @BindView(R.id.signUpBtn_Login)
    Button signUpBtn;
    @BindView(R.id.emailEditText_Login)
    EditText emailEditText;
    @BindView(R.id.passwordEditText_Login)
    EditText passwordEditText;
    @BindView(R.id.autoLogin_Login)
    CheckBox autoLogin;

    // 자동로그인
    SharedPreferences sharedAutoLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Log.d(tag, "LoginActivity - initView() |  ");

//        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (hasFocus) {
//                    Pattern pattern = Pattern.compile("^[a-zA-X0-9]@[a-zA-Z0-9].[a-zA-Z0-9]");
//                    Matcher matcher = pattern.matcher((emailEditText).getText().toString());
//
//                    if (!matcher.matches()) {
//                        Toast.makeText(LoginActivity.this, "Email형식으로 입력하세요", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
        /**자동로그인
         * ■ SharedPreferences sharedAutoLogin 에서 기본값이 false로 체크
         *     참 ⇒ 바로 화면전환
         *
         * ■ 이 Shared값은 로그아웃 할때 값을 지우자.
         * */
        sharedAutoLogin = getSharedPreferences("autoLogin", MODE_PRIVATE);
        if(sharedAutoLogin.getBoolean("isStateAutoLogin",false)){
            // 자동 로그인
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 회원정보 확인
     * ■ 프로세스
     * <p>
     * 1. JSONArray를 JSONObject로 변환
     * ● SharedPreference(login)에 key(login) 값에 저장된 String을 JSONArray로 변환
     * 2. 반복문을 JSONObject 객체 생성 & 객체의 값을 통해 ID, Password 값 비교
     * ● 일치하면 MainActivity로 이동
     * ● 불일치하면 TMS 보내기
     */
    @OnClick(R.id.loginBtn_Login)
    void onLoginBtnClicked() {
        SharedPreferences sharedLogin = getSharedPreferences("login", MODE_PRIVATE);
        Log.d(tag,"LoginActivity - onLoginBtnClicked() | sharedLogin: "+sharedLogin.getString("login",""));
//        ArrayList<AuthenticationData> mArrayList = new ArrayList<>();
        String id = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean isMember = false;
        try {
            // 1. JSONArray를 JSONObject로 변환
            JSONArray jsonArray = new JSONArray("[" + sharedLogin.getString("login", "") + "]");
            Log.d(tag,"LoginActivity - onLoginBtnClicked() | jsonArray.length() "+jsonArray.length());
            // 2. 반복문을 JSONObject 객체 생성 & 객체의 값을 통해 ID, Password 값 비교
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                AuthenticationData authenticationData = new AuthenticationData(jsonObject.getString("id"),
//                        jsonObject.getString("password"));
//                mArrayList.add(authenticationData);
                Log.d(tag,"LoginActivity - onLoginBtnClicked() | id: "+jsonObject.getString("id"));
                Log.d(tag,"LoginActivity - onLoginBtnClicked() | password: "+jsonObject.getString("password"));
                if(id.equals(jsonObject.getString("id")) && password.equals(jsonObject.getString("password"))){
                    isMember = true;
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag,"LoginActivity - onLoginBtnClicked() | Exception "+e);
        }

        /**로그인 되는 경우
         * ■ 자동 로그인 여부 확인
         *      ● SharedPreferences sharedAutoLogin 에서 key-isStateAutoLogin를 true로 바꿔준다.
         * ■ 현재 ID SharedPreferences(presentID) 로 key-presentID로 저장
         *      ● SharedPreferences에서 값을 불러와 저장.
         *      Todo: Shared로 각 회원을 구분하여 저장하는게 좀 이상한데, 점검을 받아봐야 할듯. 우선은 한 아이디만 사용할 수 있게 하자.
         *
         * */
        if(isMember){
            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
            Log.d(tag,"LoginActivity - onLoginBtnClicked() | 로그인 성공 ");
            // 자동 로그인 여부 확인
            if(autoLogin.isChecked()){
                // 참인 경우, 해당 key 값에 ture로 저장
                SharedPreferences.Editor editorAutoLogin= sharedAutoLogin.edit();
                editorAutoLogin.putBoolean("isStateAutoLogin",true);
                editorAutoLogin.commit();
            }
            // 현재 로그인 되는 E_Mail Text 저장
            SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
            SharedPreferences.Editor editorPresesntID = sharedPresesntID.edit();
            editorPresesntID.putString("presentID",id);
            editorPresesntID.commit();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else{
            Toast.makeText(LoginActivity.this, "회원정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
            Log.d(tag,"LoginActivity - onLoginBtnClicked() | 회원정보를 확인해주세요. ");
        }
    }

    @OnClick(R.id.signUpBtn_Login)
    void onSignUpBtnClicked() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(tag,"LoginActivity - onStart() |  ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(tag,"LoginActivity - onResume() |  ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(tag,"LoginActivity - onPause() |  ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(tag,"LoginActivity - onStop() |  ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(tag,"LoginActivity - onDestroy() |  ");
    }
}
