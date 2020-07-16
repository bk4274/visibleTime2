package com.example.visibletime.Ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.visibletime.GMailSender;
import com.example.visibletime.R;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CertifyEmailActivity extends AppCompatActivity {
    private final String tag = "로그";

    String eMail;
    GMailSender gMailSender;

    /**
     * View
     */
    @BindView(R.id.enterBtn_CertifyEmail)
    Button enterBtn;
    @BindView(R.id.certifyTextEditText_CertifyEmail)
    EditText certifyTextEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certify_email);


        ButterKnife.bind(this);

        // 데이터 수신 : 이메일
        Intent intent = getIntent(); /*데이터 수신*/
        eMail = intent.getExtras().getString("eMail");
        Log.d(tag, "CertifyEmailActivity - initView() | eMail: " + eMail);

        // 인증 Email을 보내기 위한 인터넷 권한
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        // 쓰레드 실행
        // ■ 문제 발생
        // 쓰레드를 통해서 작동을 시키니까, Can't toast on a thread that has not called Looper.prepare()로 토스트 메세지가 보내주지 않은 문제가 발생함.
        // ■ 해결 방법
        // 그래서 구글링 통해 핸들러 안에 동작 코드를 집어 넣으니까 해결.

//        ExampleThread thread = new ExampleThread();
//        thread.start();

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 사용하고자 하는 코드
                sendEmail();
            }
        }, 0);

    }

//    private class ExampleThread extends Thread {
//        private static final String TAG = "ExampleThread";
//
//        public ExampleThread() { // 초기화 작업
//        }
//
//        public void run() {
//            // 스레드에게 수행시킬 동작들 구현
//            sendEmail();
//        }
//    }


    private void sendEmail() {
        try {
            gMailSender = new GMailSender("sthe4274@gmail.com", "passnova0903!!");
            //GMailSender.sendMail(제목, 본문내용, 받는사람);
            gMailSender.sendMail("Visible Time 이메일 인증", "인증 번호는:『" + gMailSender.getEmailCode() + "』입니다.", eMail);
            Log.d(tag, "CertifyEmailActivity - onClick() | 메일 보냄 ");
            Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
        } catch (SendFailedException e) {
            Log.d(tag, "CertifyEmailActivity - onClick() | SendFailedException: " + e);
            Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
            Log.d(tag, "CertifyEmailActivity - onClick() | MessagingException: " + e);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag, "CertifyEmailActivity - onClick() | Exception: " + e);
        }
    }
    /**Click*/
    /**
     * 이메일 인증
     * 성공
     * ⇒
     * 실패
     * ⇒ 토스트 메세지로 알려주기
     **/

    @OnClick(R.id.enterBtn_CertifyEmail)
    void onEnterBtnClicked() {
        Log.d(tag, "CertifyEmailActivity - onEnterBtnClicked() |  ");
        String userAnswer = certifyTextEditText.getText().toString();
        if (userAnswer.equals(gMailSender.getEmailCode())) {
            Log.d(tag, "CertifyEmailActivity - onEnterBtnClicked() | 성공 ");
            Toast.makeText(this, "이메일 인증 성공", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("isCertifyed", true);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Log.d(tag, "CertifyEmailActivity - onEnterBtnClicked() | 실패 ");
            Toast.makeText(this, "이메일 인증 실패", Toast.LENGTH_SHORT).show();
        }
    }
}
