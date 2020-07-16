package com.example.visibletime.Adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Data.AlarmData;
import com.example.visibletime.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**Adapter
 *
 * ■ 생성자
 *
 * ■ Custom ViewHoler extends RecyclerView.ViewHolder
 *      What:
 * ■ onCreateViewHolder()
 *      What: ViewHolder 생성
 *      How:
 *      Note:
 * ■ onBindViewHolder()
 *      What: 데이터 연결
 * ■ getItemCount();
 *      What: 사용자에게 보여줄 Item 개수 반영
 * */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmHolder> {

    private final String tag = "로그";

    /**생성자 변수*/
    private Context context;
    private ArrayList<AlarmData> mArrayList;

    /**체크된 알람을 확인 하기 위한 변수*/
    private int positionClicked;
    private SparseBooleanArray initCheckBoxs = new SparseBooleanArray(0);
    //초기화 하기 위함.
    // ★★★ 문제 해결 방법
    // 처음에 값만 저장하고 그 후로 영향을 받지 않기 위함.
    private SparseBooleanArray mSelectedCheckBoxs = new SparseBooleanArray(0);
    private boolean isChoisedFromUser = false;      // 사용자가 선택했을 때 체크박스가 변함을 주기 위해서

    public AlarmAdapter(Context context, ArrayList<AlarmData> mArrayList) {
        this.context = context;
        this.mArrayList = mArrayList;
    }

    @NonNull
    @Override
    public AlarmAdapter.AlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(context);
        View view = inflate.inflate(R.layout.item_alarm, parent, false);
        return new AlarmHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmAdapter.AlarmHolder holder, int position) {
        /**초기 설정된 체크 여부를 확인하여 해당 position을 saveStateOfCheckBoxs 상태에 넣기 */
        Log.d(tag,"AlarmAdapter - onBindViewHolder() | initCheckBoxs.get(position): "+initCheckBoxs.get(position));
        // 처음 세팅 할때만 해당 값에 따라 저장하기 위함.
        if(mArrayList.get(position).isChecked() && initCheckBoxs.get(position) == false){
            setStateOfCheckBoxs(position);
        }
        /**Data Format*/
        Calendar calendarAlarm = Calendar.getInstance();
        calendarAlarm.set(Calendar.HOUR_OF_DAY, mArrayList.get(position).getHour_24());
        calendarAlarm.set(Calendar.MINUTE, mArrayList.get(position).getMinute());
        calendarAlarm.set(Calendar.SECOND, 0);
        Date currentDateTime = calendarAlarm.getTime();

        SimpleDateFormat formatHour = new SimpleDateFormat("hh");
        SimpleDateFormat formatMinute = new SimpleDateFormat("mm");
        String hour = formatHour.format(currentDateTime);
        String minute = formatMinute.format(currentDateTime);

//        holder.isChecked.setChecked(mArrayList.get(position).isChecked());  상태를 저장하고 상태를 확인하여 체크 상태를 보여주기 위함
        holder.nameTextView.setText(mArrayList.get(position).getName());
        holder.amPmTextView.setText(mArrayList.get(position).getAm_pm());
        holder.weekTextView.setText(mArrayList.get(position).getWeekName());
        holder.timeTextView.setText(hour+":"+minute);

        holder.isChecked.setChecked(isItemSelected(position));

//        if(positionClicked == position){
//            isChoisedFromUser = false;
//        }

        Log.d(tag,"AlarmAdapter - onBindViewHolder() | BindView 종료 ");
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class AlarmHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**View*/
        @BindView(R.id.checkBox_itemAlarm)
        CheckBox isChecked;
        @BindView(R.id.nameTextView_itemAlarm)
        TextView nameTextView;
        @BindView(R.id.timeTextView_itemAlarm)
        TextView timeTextView;
        @BindView(R.id.amPmTextView_itemAlarm)
        TextView amPmTextView;
        @BindView(R.id.weekTextView_itemAlarm)
        TextView weekTextView;
        @BindView(R.id.clockImage_itemAlarm)
        ImageView clockImage;
        @BindView(R.id.nudgeImage_itemAlarm)
        ImageView nudgeImage;

        public AlarmHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            isChecked.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            positionClicked = getAdapterPosition();

            switch (view.getId()) {
                case R.id.checkBox_itemAlarm:
                    Log.d(tag,"AlarmHolder - onClick() | 클릭 ");
                    saveStateOfCheckBoxs(positionClicked);
                    isChoisedFromUser = true;
                    break;
            }
        }
    }

    // 체크 박스 초기화 할때
    private void setStateOfCheckBoxs(int position) {
        Log.d(tag, "AlarmAdapter - setStateOfCheckBoxs() |  ");
        Log.d(tag,"AlarmAdapter - setStateOfCheckBoxs() | position: "+position);
        if (mSelectedCheckBoxs.get(position, false) == true) {
            Log.d(tag, "AlarmAdapter - setStateOfCheckBoxs() | 체크가 되어 있다면 Array 값에서 position을 빼라");
            mSelectedCheckBoxs.delete(position);
        } else {
            Log.d(tag, "AlarmAdapter - setStateOfCheckBoxs() | 체크가 안되어 있다면 Array 값에서 position을 추가해라 ");
            mSelectedCheckBoxs.put(position, true);
            initCheckBoxs.put(position, true);      //체크 박스 초기화 하는 용도, ★★★ 문제 해결 방법
        }
    }
    // 사용자가 선택함에 따라 달라지게 하기 위함.
    private void saveStateOfCheckBoxs(int position) {
        Log.d(tag, "AlarmAdapter - saveStateOfCheckBoxs() |  ");
        Log.d(tag,"AlarmAdapter - saveStateOfCheckBoxs() | position: "+position);
        if (mSelectedCheckBoxs.get(position, false) == true) {
            Log.d(tag, "AlarmAdapter - saveStateOfCheckBoxs() | 체크가 되어 있다면 Array 값에서 position을 빼라");
            mSelectedCheckBoxs.delete(position);
            notifyItemChanged(position);
        } else {
            Log.d(tag, "AlarmAdapter - saveStateOfCheckBoxs() | 체크가 안되어 있다면 Array 값에서 position을 추가해라 ");
            mSelectedCheckBoxs.put(position, true);
            notifyItemChanged(position);
        }
    }

    private boolean isItemSelected(int position) {
        Log.d(tag, "AlarmAdapter - isItemSelected() |  ");
        Log.d(tag, "AlarmAdapter - isItemSelected() | mSelectedCheckBoxs.get(position): " + mSelectedCheckBoxs.get(position));
        return mSelectedCheckBoxs.get(position, false);
    }

    public SparseBooleanArray getCheckBoxSelected(){
       return mSelectedCheckBoxs;
    }
}
