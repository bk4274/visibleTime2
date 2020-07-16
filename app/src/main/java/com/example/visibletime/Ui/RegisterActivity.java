package com.example.visibletime.Ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visibletime.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    private final String tag = "로그";

    /**
     * View
     */
    //Email
    @BindView(R.id.eMailEditText_Register)
    EditText eMailEditText;
    @BindView(R.id.checkIDBtn_Register)
    Button checkIDBtn;
    //Email에 상태에 따른 알림글
    @BindView(R.id.ableIDTextView_Register)
    TextView ableIDTextView;
    @BindView(R.id.overlapIDTextView_Register)
    TextView overlapIDTextView;
    @BindView(R.id.unAbleIDTextView_Register)
    TextView unAbleIDTextView;
    //비밀번호
    @BindView(R.id.passwordEditText_Register)
    EditText password;
    @BindView(R.id.passwordCheckEditText_Register)
    EditText passwordCheck;
    @BindView(R.id.checkPasswordImgView_Register)
    ImageView checkPasswordImgView;
    //확인버튼
    @BindView(R.id.enterBtn_Register)
    Button enterBtn;

    /**
     * 정규식
     */
    Pattern pattern = Pattern.compile("^[A-z|0-9]([A-z|0-9]*)(@)([A-z]*)(\\.)([A-z]*)$");
    Matcher matcher;

    /**
     * 회원정보 불러오기 위한 변수
     */
    SharedPreferences sharedUserList;
    ArrayList<String> usersArrayList;

    /**
     * 인증 여부 확인
     */
    boolean isCertified = false;
    String eMailCertified = null;

    /**
     * onActivityResult를 위한 변수
     */
    private static final int CERTIFY_EMAIL = 1;

    /**KeyBoard 내리기 위한 변수*/
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "RegisterActivity - onCreate() |  ");

        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Log.d(tag, "RegisterActivity - initView() |  ");

        /**Email에 상태에 따른 알림글 초기화*/
        setTextViewVisibilityGone();

        /**KeyBoard 내리기 위한 객체 생성*/
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        /**이메일 중복검사 준비*/
        // 이메일 중복 검사를 위해 sharedPreferences 객체 불러오기
        // 데이터가 화면 여부에 상관 없이 한번만 불러오면 되므로 onCreate()에서 생성
        sharedUserList = getSharedPreferences("userList", MODE_PRIVATE);     //UserList : 앱의 회원정보를 가지고 있는 SharedPreference
        String users = sharedUserList.getString("users", null);
        usersArrayList = new ArrayList<>();
        if (users != null) {
            Log.d(tag, "RegisterActivity - initView() | sharedPreferences 값O, 회원들 값 List로 넣기 ");
            String[] usersArray = users.split(",");
            for (int i = 0; i < usersArray.length; i++) {
                usersArrayList.add(usersArray[i]);
            }
        } else {
            Log.d(tag, "RegisterActivity - initView() | sharedPreferences 값X, 첫 회원가입 ");
        }

        /**E_Mail 정규식 확인
         *
         * E_Mail EditText에서 변화가 있을 때 이메일 상태를 검사한다.
         * 값이 달라지면,
         * */
        eMailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setTextViewVisibilityGone();
                matcher = pattern.matcher((eMailEditText).getText().toString());
                Log.d(tag, "RegisterActivity - afterTextChanged() | eMailEditText: " + eMailEditText.getText());
                if (eMailCertified != null) {
                    if (eMailCertified.equals(eMailEditText.getText().toString())) {
                        ableIDTextView.setVisibility(View.VISIBLE);
                    } else {
                        setTextViewVisibilityGone();
                    }
                }
            }
        });
//        eMailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                Log.d(tag, "RegisterActivity - onFocusChange() |  ");
//                if (!hasFocus) {
//                    Log.d(tag, "RegisterActivity - onFocusChange() | hasFocus _ False ");
//                    matcher = pattern.matcher((eMailEditText).getText().toString());
//                }
//            }
//        });
        /**비밀번호 체크*/
        setPassword();

        /**기존에 잘못 입력된 Shared 값 지우기*/
//        // ID 중복용 방지 검사 Shared
//        SharedPreferences.Editor editorUserList = sharedUserList.edit();
//        editorUserList.clear();
//        editorUserList.commit();
//        // 로그인시 ID, Password 확인 Shared
//        SharedPreferences sharedLogin = getSharedPreferences("login", MODE_PRIVATE);
//        SharedPreferences.Editor editorLogin = sharedLogin.edit();
//        editorLogin.clear();
//        editorLogin.commit();
//        Log.d(tag,"RegisterActivity - initView() | SharedPreferences 값 삭제 ");
    }

    /**
     * E_Mail 상태 알림글 초기화
     */
    // E_Mail 중복, 이메일형식, 인증 여부를 확인에 따른 View를 다르게 보여주기 위해,
    // 초기값을 다 안보이게 설정
    private void setTextViewVisibilityGone() {
        ableIDTextView.setVisibility(View.GONE);
        overlapIDTextView.setVisibility(View.GONE);
        unAbleIDTextView.setVisibility(View.GONE);
    }


    /**Click*/
    /**
     * E_Mail 검사
     * <p>
     * 1. 정규식 검사
     * matcher 값이 null 이면 안된다.
     * matcher.matches에서 여부를 확인한다.
     * ture ⇒ 이메일 형식이 맞으므로 이메일 중복 검사를 실시
     * false ⇒  이메일 형식이 아니므로 이메일 형식이 아님을 보여주자.
     * 2. 이메일 중복 검사
     * 유저 회원 정보들 가운데, 일치하는 ID가 있는지 확인
     * <p>
     * 3. 해당 ID 재인증 여부 검사
     * emailCertified.equal(eMailEditText) = true 인 경우 이미 인증을 받은 이메일이므로 중복 인증 방지 ToastMsg를 보낸다.
     */
    @OnClick(R.id.checkIDBtn_Register)
    void onCheckIDBtnClicked() {
        setTextViewVisibilityGone();
        if (matcher != null) {
            //1. 정규식 검사
            if (matcher.matches()) {
                // 2. 이메일 중복 검사
                if (isExistID()) {
                    // 2-1.중복된 이메일이 있는경우
                    Log.d(tag, "RegisterActivity - onCheckIDBtnClicked() | ID 중복값 존재 O");
                    overlapIDTextView.setVisibility(View.VISIBLE);
                } else {
                    // 2-2. 중복된 이메일이 아닌 경우
                    Log.d(tag, "RegisterActivity - onCheckIDBtnClicked() | ID 중복값 존재 X ");
                    // 3. 해당 ID 재인증 여부 검사
                    if (eMailCertified == null) {
                        // 처음 E_Mail 인증하는 경우
                        hideKeyboard();
                        Intent intent = new Intent(RegisterActivity.this, CertifyEmailActivity.class);
                        intent.putExtra("eMail", eMailEditText.getText().toString());
                        startActivityForResult(intent, CERTIFY_EMAIL);
                    } else {
                        // 이메일 인증한 상태라면 중복 인증을 방지한다.
                        if (eMailCertified.equals(eMailEditText.getText().toString())) {
                            Toast.makeText(RegisterActivity.this, "이미 인증완료 하였습니다.", Toast.LENGTH_SHORT).show();
                            ableIDTextView.setVisibility(View.VISIBLE);
                        } else {
                            Intent intent = new Intent(RegisterActivity.this, CertifyEmailActivity.class);
                            intent.putExtra("eMail", eMailEditText.getText().toString());
                            startActivityForResult(intent, CERTIFY_EMAIL);
                        }
                    }
                }
            } else {
                // 정규식에 반한 경우
                unAbleIDTextView.setVisibility(View.VISIBLE);
            }
        } else {
            unAbleIDTextView.setVisibility(View.VISIBLE);
            eMailEditText.requestFocus();
        }
    }

    /**
     * 회원정보 등록
     * <p>
     * ■ 검사 항목
     * □ 인증한 ID와 eMailEditText와 일치 여부 확인
     * ● eMailCertified.equal(eMailEditText) && isCertified = true
     * □ 비밀 번호 6자리 이상 & 일치 여부 확인
     * ● password.getText().toString().length() > 6 && password.equals(passwordCheck)
     * <p>
     * ■ Shared 저장
     * □ 값을 처음으로 저장하는 확인
     * □ SharedPreference : userList 에 해당 ID저장 (ID 중복 검사용)
     * ● users: 유저 ID만 저장
     * □ SharedPreference : sharedLogin 에 해당 ID, 비밀번호 저장 (로그인 용)
     */
    @OnClick(R.id.enterBtn_Register)
    void onEnterBtnClicked() {
        // □ 인증한 ID와 eMailEditText와 일치 여부 확인
        if (eMailCertified != null) {
            if (eMailCertified.equals(eMailEditText.getText().toString()) && isCertified) {
                // □ 비밀 번호 6자리 이상 & 일치 여부 확인
                if (password.getText().toString().length() >= 6 && password.getText().toString().equals(passwordCheck.getText().toString())) {
                    // ■ Shared 저장
                    // 중복 검사용 Shared Editor 생성
                    SharedPreferences.Editor editorUserList = sharedUserList.edit();
                    // 로그인 검사용 Shared Editor 생성
                    SharedPreferences sharedLogin = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editorLogin = sharedLogin.edit();

                    /**JSON 형태로 저장*/
                    String strDataOfJSON;
                    strDataOfJSON = "{\"id\"" + ":" + "\"" + eMailCertified + "\"" + ","
                            + "\"password\"" + ":" + "\"" + password.getText().toString() + "\""
                            + "}";

                    // □ 값을 처음으로 저장하는 확인
                    String users = sharedUserList.getString("users", null);

                    if (users == null) {
                        // 첫 회원정보를 저장하는 경우
                        editorUserList.putString("users", eMailCertified);
                        editorUserList.commit();
                        editorLogin.putString("login", strDataOfJSON);
                        editorLogin.commit();
                        Toast.makeText(RegisterActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // 회원정보 저장이 처음이 아닌 경우
                        editorUserList.putString("users", sharedUserList.getString("users", "") + "," + eMailCertified);
                        editorUserList.commit();
                        editorLogin.putString("login", sharedLogin.getString("login", "") + "," + strDataOfJSON);
                        editorLogin.commit();
                        Toast.makeText(RegisterActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    // 비밀번호 인증 안된경우
                    Toast.makeText(RegisterActivity.this, "비밀번호 양식을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 이메일 인증 안된 경우
                Toast.makeText(RegisterActivity.this, "이메일 인증 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 이메일 인증 안된 경우
            Toast.makeText(RegisterActivity.this, "이메일 인증 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 중복값 여부 반환 메서드
     * 불린형 변수 returnResult:
     *      - true: 중복값 존재할 때 반환
     *      - false: 중복값 없으면 반환
     * 처음에는 반복문에서 일치한 ID가 있으면 return으로 하게 했으나, 생각과 다르게 계속 false 값이 나와서 수정함.
     */
    private Boolean isExistID() {
//        boolean returnResult =false;
        if (usersArrayList.size() == 0) {
            Log.d(tag,"RegisterActivity - isExistID() | 회원정보가 없는 경우 ");
            // 저장된 회원이 아예 없는 경우
            return false;
        } else {
            Log.d(tag,"RegisterActivity - isExistID() | 회원정보가 있는 경우 ");
            // 저장된 회원이 존재하며 중복 확인
            for (int i = 0; i < usersArrayList.size(); i++) {
                Log.d(tag,"RegisterActivity - isExistID() | usersArrayList.get(i): "+usersArrayList.get(i));
                if (eMailEditText.getText().toString().equals(usersArrayList.get(i))) {
                    Log.d(tag,"RegisterActivity - isExistID() | true 반환 ");
                    return true;
                }
//                else {
//                    Log.d(tag,"RegisterActivity - isExistID() | false 반환 ");
//                    returnResult = false;
//                }
            }
//            Log.d(tag,"RegisterActivity - isExistID() | returnResult 결과값: "+returnResult);
            return false;
        }
    }

    /**
     * 인증 여부 확인
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == CERTIFY_EMAIL) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(tag, "RegisterActivity - onActivityResult() | 값 돌려 받기 확인 ");
                isCertified = resultIntent.getBooleanExtra("isCertifyed", false);
                Log.d(tag, "RegisterActivity - onActivityResult() | isCertified: " + isCertified);
                if (isCertified) {
                    ableIDTextView.setVisibility(View.VISIBLE);
                    eMailCertified = eMailEditText.getText().toString();
                    Log.d(tag, "RegisterActivity - onActivityResult() | eMailCertified: " + eMailCertified);
                } else {
                    Toast.makeText(RegisterActivity.this, "인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
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
                    Log.d(tag, "RegisterActivity - onTextChanged() |  ");
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
                Log.d(tag, "RegisterActivity - onTextChanged() | password.getText().toString().isEmpty(): " + password.getText().toString().isEmpty());
                if (!password.getText().toString().isEmpty()) {
                    if (password.getText().toString().equals(passwordCheck.getText().toString())) {
                        Log.d(tag, "RegisterActivity - onTextChanged() | 값 일치 ");
                        checkPasswordImgView.setImageResource(R.drawable.ic_correct);
                    } else {
                        Log.d(tag, "RegisterActivity - onTextChanged() | 값 불일치 ");
                        checkPasswordImgView.setImageResource(R.drawable.ic_quit);
                    }
                } else {
                    Log.d(tag, "RegisterActivity - onTextChanged() | password 값 존재 X ");
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

    private void sendToast(String msg) {
        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard()
    {
        inputMethodManager.hideSoftInputFromWindow(eMailEditText.getWindowToken(), 0);
    }

}
