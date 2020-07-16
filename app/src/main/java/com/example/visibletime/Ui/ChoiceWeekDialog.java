package com.example.visibletime.Ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.visibletime.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoiceWeekDialog extends Dialog {

    private final String tag = "로그";

    private Context context;

    /**
     * Dialog View
     */
    // Button
    @BindView(R.id.cancelBtn_AlarmDialog)
    Button cancelBtn;
    @BindView(R.id.enterBtn_AlarmDialog)
    Button enterBtn;

    //CheckBox Controller
    @BindView(R.id.weekdaysBtn_AlarmDialog)
    CheckBox weekdaysBtn;
    @BindView(R.id.weekendBtn_AlarmDialog)
    CheckBox weekendBtn;

    //CheckBox days
    @BindView(R.id.mondayCheckBox_AlarmDialog)
    CheckBox mondayCheckBox;
    @BindView(R.id.tuesdayCheckBox_AlarmDialog)
    CheckBox tuesdayCheckBox;
    @BindView(R.id.wednesdayCheckBox_AlarmDialog)
    CheckBox wednesdayCheckBox;
    @BindView(R.id.thursdayCheckBox_AlarmDialog)
    CheckBox thursdayCheckBox;
    @BindView(R.id.fridayCheckBox_AlarmDialog)
    CheckBox fridayCheckBox;
    @BindView(R.id.saturdayCheckBox_AlarmDialog)
    CheckBox saturdayCheckBox;
    @BindView(R.id.sundayCheckBox_AlarmDialog)
    CheckBox sundayCheckBox;


    // week
    boolean[] week;
    TextView weekRepeated;

    public ChoiceWeekDialog(@NonNull Context context, boolean[] week, TextView weekRepeated) {
        super(context);
        this.week = week;
        this.weekRepeated = weekRepeated;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag,"ChoiceWeekDialog - onCreate() |  ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alarm);
        ButterKnife.bind(this);
        Log.d(tag,"ChoiceWeekDialog - onCreate() | week "+week.length);
        Log.d(tag,"ChoiceWeekDialog - onCreate() | week[0] "+week[0]);

        setCheckBox();
        setControlCheckBox();
    }

    
    /**저장된 배열대로 체크상태 보여주기.*/
    private void setCheckBox() {
        mondayCheckBox.setChecked(week[1]);
        tuesdayCheckBox.setChecked(week[2]);
        wednesdayCheckBox.setChecked(week[3]);
        thursdayCheckBox.setChecked(week[4]);
        fridayCheckBox.setChecked(week[5]);
        saturdayCheckBox.setChecked(week[6]);
        sundayCheckBox.setChecked(week[7]);
        
        
    }
    /**1. 초기값에 따라 컨트롤 체크박스 체크 상태 변경
     * 2. 개별 체크박스 상태에 따라 컨트롤 체크 박스 상태 변경*/
    private void setControlCheckBox() {
        // 주중이 체크 되어 있는 경우
        if(week[1] && week[2] && week[3] && week[4] && week[5]){
            weekdaysBtn.setChecked(true);
        }
        // 주말이 다 체크 되어 있는 경우
        if(week[6] && week[7]){
            weekendBtn.setChecked(true);
        }
        // Todo: 체크박스가 변경 될때마다 이를 확인하여 컨트롤 체크박스 상태를 바꿔주는 건 너무 디테일이라 뺐음.
        // 시간 되면 나중에 추구하자.
//        mondayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    txtResult.setText("체크되었음");
//                }else {
//                    txtResult.setText("체크안되었음");
//                }
//            }
//        });

    }


    @OnClick(R.id.weekdaysBtn_AlarmDialog)
    void controllWeekday(){
        if(weekdaysBtn.isChecked() == true){
            setWeekdayTrue();
        } else {
            setWeekdayFalse();
        }
    }

    @OnClick(R.id.weekendBtn_AlarmDialog)
    void controllWeekend(){
        if(weekendBtn.isChecked() == true){
            setWeekendTrue();
        } else {
            setWeekendFalse();
        }
    }

    /**확인 버튼 누르면 체크박스에 있는 상태를 boolean [] 배열에 넣는다.*/
    @OnClick(R.id.enterBtn_AlarmDialog)
    void enterBtnClicked(){
        week[1] = mondayCheckBox.isChecked();
        week[2] = tuesdayCheckBox.isChecked();
        week[3] = wednesdayCheckBox.isChecked();
        week[4] = thursdayCheckBox.isChecked();
        week[5] = fridayCheckBox.isChecked();
        week[6] = saturdayCheckBox.isChecked();
        week[7] = sundayCheckBox.isChecked();
        setText();
        dismiss();
    }
    /**체크된 상태에 따라 TextView 달라지게 하기 위함*/
    private void setText() {
        if(week[1] && week[2] && week[3] && week[4] && week[5] && week[6] && week[7]){
            // 모두 체크되어 있는 경우
            weekRepeated.setText("매일");
        } else if(week[1] == false && week[2] == false && week[3]== false && week[4]== false && week[5]== false && week[6]== false && week[7]== false){
            // 하나도 체크 안 되어 있는 경우
            weekRepeated.setText("반복 안함");
        } else if(week[1] == false && week[2] == false && week[3]== false && week[4]== false && week[5]== false && week[6]== true && week[7]== true){
            // 주말만 체크 되어 있는 경우
            weekRepeated.setText("주말");
        } else if(week[1] == true && week[2] == true && week[3]== true && week[4]== true && week[5]== true && week[6]== false && week[7]== false){
            // 주중에만 체크 되어 있는 경우
            weekRepeated.setText("주중");
        } else{
            // 그 외 요일이 개별적으로 체크 되어 있는 경우
            String tmpText = " ";
            if(week[1] == true){
                tmpText = "월";
            }
            if(week[2] == true){
                tmpText = tmpText +" 화";
            }
            if(week[3] == true){
                tmpText = tmpText +" 수";
            }
            if(week[4] == true){
                tmpText = tmpText +" 목";
            }
            if(week[5] == true){
                tmpText = tmpText +" 금";
            }
            if(week[6] == true){
                tmpText = tmpText +" 토";
            }
            if(week[7] == true){
                tmpText = tmpText +" 일";
            }
            weekRepeated.setText(tmpText);
        }
    }

    @OnClick(R.id.cancelBtn_AlarmDialog)
    void cancleBtnClicked(){
        cancel();
    }
    private void setWeekdayFalse(){
        mondayCheckBox.setChecked(false);
        tuesdayCheckBox.setChecked(false);
        wednesdayCheckBox.setChecked(false);
        thursdayCheckBox.setChecked(false);
        fridayCheckBox.setChecked(false);
    }
    private void setWeekdayTrue(){
        mondayCheckBox.setChecked(true);
        tuesdayCheckBox.setChecked(true);
        wednesdayCheckBox.setChecked(true);
        thursdayCheckBox.setChecked(true);
        fridayCheckBox.setChecked(true);
    }
    private void setWeekendFalse(){
        saturdayCheckBox.setChecked(false);
        sundayCheckBox.setChecked(false);
    }
    private void setWeekendTrue(){
        saturdayCheckBox.setChecked(true);
        sundayCheckBox.setChecked(true);
    }
}
