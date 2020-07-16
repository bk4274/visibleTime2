package com.example.visibletime.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Data.ReportData;
import com.example.visibletime.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportAdatper  extends RecyclerView.Adapter<ReportAdatper.ReportViewHolder>  {

    private final String tag = "로그";

    /**생성자 변수*/
    private Context context;
    ArrayList<ReportData> mArrayList;

    /**커스텀 리스터*/
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public ReportAdatper(Context context, ArrayList<ReportData> mArrayList) {
        this.context = context;
        this.mArrayList = mArrayList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(context);
        View view = inflate.inflate(R.layout.item_record_body, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportData reportData = mArrayList.get(position);
        // 색 변경
        GradientDrawable bgShape = (GradientDrawable) holder.lineImageView.getBackground();
        bgShape.setColor(reportData.getColor());

        // 시간 데이터 변경
        int time = reportData.getTimeDuration();
        int min = (time) %3600 / 60;
        int hour = (time) / 3600;
        String timeDuration = String.format("%02d:%02d", hour, min);

        String timeUpto = reportData.getTimeStart() + "-" + reportData.getTimeEnd();
        holder.title.setText(reportData.getCategory());
        holder.context.setText(reportData.getContent());
        holder.timeMeasured.setText(timeDuration);
        holder.timeUpTo.setText(timeUpto);

//        holder.satisfaction.setText("("+reportData.getLevel()+")");
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

        @BindView(R.id.lineImageView_Record)
        ImageView lineImageView;
        @BindView(R.id.title_Record)
        TextView title;
        @BindView(R.id.satisfaction_Record)
        TextView satisfaction;
        @BindView(R.id.context_Record)
        TextView context;
        @BindView(R.id.timeMeasured_Record)
        TextView timeMeasured;
        @BindView(R.id.timeUpTo_Record)
        TextView timeUpTo;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }
    }
}
