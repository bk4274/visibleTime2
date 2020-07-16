package com.example.visibletime.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Data.ReportData;
import com.example.visibletime.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectReportAdatper extends RecyclerView.Adapter<SelectReportAdatper.ReportViewHolder>  {

    private final String tag = "로그";

    /**생성자 변수*/
    private Context context;
    ArrayList<ReportData> mArrayList;

    /**체크된 알람을 확인 하기 위한 변수*/
    private SparseBooleanArray initCheckBoxs = new SparseBooleanArray(0);
    //초기화 하기 위함.
    // ★★★ 문제 해결 방법
    // 처음에 값만 저장하고 그 후로 영향을 받지 않기 위함.
    private SparseBooleanArray mSelectedCheckBoxs = new SparseBooleanArray(0);
    private boolean isChoisedFromUser = false;      // 사용자가 선택했을 때 체크박스가 변함을 주기 위해서
    String type;

    public SelectReportAdatper(Context context, ArrayList<ReportData> mArrayList, String type) {
        this.context = context;
        this.mArrayList = mArrayList;
        this.type = type;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(context);
        View view = inflate.inflate(R.layout.item_record_body_check, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        /**초기 설정된 체크 여부를 확인하여 해당 position을 saveStateOfCheckBoxs 상태에 넣기 */
        if(mArrayList.get(position).getType().equals(type) && initCheckBoxs.get(position) == false) {
            Log.d(tag, "SelectReportAdatper - onBindViewHolder() | position:" + position);
            setStateOfCheckBoxs(position);
        }

        // 데이터 set
        ReportData reportData = mArrayList.get(position);
        // 색 변경
        GradientDrawable bgShape = (GradientDrawable) holder.lineImageView.getBackground();
        bgShape.setColor(reportData.getColor());

        // 시간 데이터 변경
        int time = reportData.getTimeDuration();
        //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
        int min = (time) %3600 / 60;
        int hour = (time) / 3600;
        String timeDuration = String.format("%02d:%02d", hour, min);

        String timeUpto = reportData.getTimeStart() + "-" + reportData.getTimeEnd();


        holder.title.setText(reportData.getCategory());
        holder.context.setText(reportData.getContent());
        holder.timeMeasured.setText(timeDuration);
        holder.timeUpTo.setText(timeUpto);

        holder.checkBox.setChecked(isItemSelected(position));   // 상태값에 따라 booean값 반환

        // 만족도를 1,2,3 이렇게 숫자에서 최하, 하, 중 이렇게 문자로 변환하기 위함.
        switch (reportData.getLevel()){
            case 1:
                holder.satisfaction.setText("(최하)");
                break;
            case 2:
                holder.satisfaction.setText("(하)");
                break;
            case 3:
                holder.satisfaction.setText("(중)");
                break;
            case 4:
                holder.satisfaction.setText("(상)");
                break;
            case 5:
                holder.satisfaction.setText("(최상)");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lineImageView_RecordCheckBox)
        ImageView lineImageView;
        @BindView(R.id.title_RecordCheckBox)
        TextView title;
        @BindView(R.id.satisfaction_RecordCheckBox)
        TextView satisfaction;
        @BindView(R.id.context_RecordCheckBox)
        TextView context;
        @BindView(R.id.timeMeasured_RecordCheckBox)
        TextView timeMeasured;
        @BindView(R.id.timeUpTo_RecordCheckBox)
        TextView timeUpTo;
        @BindView(R.id.checkBox_RecordCheckBox)
        CheckBox checkBox;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveStateOfCheckBoxs(getAdapterPosition());
                }
            });
        }
    }

    public ArrayList<ReportData>  getmArrayList() {
        return mArrayList;
    }

    // 체크 박스 초기화 할때
    private void setStateOfCheckBoxs(int position) {
        if (mSelectedCheckBoxs.get(position, false) == true) {
            mSelectedCheckBoxs.delete(position);    // mSelectedCheckBoxs에 position 삭제한다.
        } else {
            mSelectedCheckBoxs.put(position, true); // mSelectedCheckBoxs에 position을 추가한다.
            initCheckBoxs.put(position, true);      //체크 박스 초기화 하는 용도, ★★★ 문제 해결 방법
        }
    }
    // 사용자가 선택함에 따라 달라지게 하기 위함.
    private void saveStateOfCheckBoxs(int position) {
        if (mSelectedCheckBoxs.get(position, false) == true) {
            mSelectedCheckBoxs.delete(position);    // mSelectedCheckBoxs에 position 삭제한다.
            notifyItemChanged(position);            // 즉각적으로 Ui로 보여주기 위해서 item이 갱신 됨을 알려준다.
        } else {
            mSelectedCheckBoxs.put(position, true); // mSelectedCheckBoxs에 position을 추가한다.
            notifyItemChanged(position);            // 즉각적으로 Ui로 보여주기 위해서 item이 갱신 됨을 알려준다.
        }
    }

    private boolean isItemSelected(int position) {
        return mSelectedCheckBoxs.get(position, false);
    }

    public ArrayList<ReportData> getReportDataArrayList(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i=0; i<mSelectedCheckBoxs.size(); i++){
            arrayList.add(mSelectedCheckBoxs.keyAt(i));     // 해당 position에 있는 값만 저장.
            Log.d(tag,"SelectReportAdatper - getReportDataArrayList() | mSelectedCheckBoxs.keyAt(i): "+mSelectedCheckBoxs.keyAt(i));
        }
        Log.d(tag,"SelectReportAdatper - getReportDataArrayList() | arrayList.size(): "+arrayList.size());
        /**처음에는 다 "so"로 초기화*/
        for(int i = 0; i<mArrayList.size(); i++){
            mArrayList.get(i).setType("so");
        }
        /**체크 되어 있는 값만 type 값으로 저장*/
        for(int i = 0; i<arrayList.size(); i++){
            Log.d(tag,"SelectReportAdatper - editCheckBox() | arrayList.get(i): "+arrayList.get(i));
            mArrayList.get(arrayList.get(i)).setType(type);
        }
        // 잘 바뀌었는지 확인
        for(int i = 0; i<mArrayList.size(); i++){
            Log.d(tag,"SelectReportAdatper - editCheckBox() | mArrayList.get(i).isChecked() "+i + mArrayList.get(i).getType());
        }
        return mArrayList;
    }
}
