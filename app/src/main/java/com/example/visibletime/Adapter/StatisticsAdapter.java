package com.example.visibletime.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Data.CatagoryData;
import com.example.visibletime.Data.StatisticsData;
import com.example.visibletime.R;
import com.example.visibletime.Ui.Dialog.DeleteCategoryParentDialog;
import com.example.visibletime.Ui.Dialog.EditCategoryChildDialog;
import com.example.visibletime.Ui.Dialog.EditCategoryParentDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;
import static com.example.visibletime.Data.CatagoryData.PARENT_TYPE;


public class StatisticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String tag = "로그";

    Context context;
    ArrayList<StatisticsData> mArrayList;

//    private int positionClicked;

    public StatisticsAdapter(Context context, ArrayList<StatisticsData> mArrayList) {
        this.context = context;
        this.mArrayList = mArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case PARENT_TYPE:
                Log.d(tag,"CategoryAdapter - onCreateViewHolder() | PARENT_TYPE ");
                view = inflater.inflate(R.layout.item_statistics_general, parent, false);
                return new ParentViewHolder(view);
            case CHILD_TYPE:
                Log.d(tag,"CategoryAdapter - onCreateViewHolder() | CHILD_TYPE ");
                view = inflater.inflate(R.layout.item_statistics_specific, parent, false);
                return new ChildViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StatisticsData statisticsData = mArrayList.get(position);


        // 이름
        String name = statisticsData.getCategory();

        // 시간 데이터 변경
        int targetTimeInt = statisticsData.getTargetTime();
        int targetMin = (targetTimeInt) %3600 / 60;
        int targetHour = (targetTimeInt) / 3600;
        String targetTimeStr = String.format("%02d:%02d", targetHour, targetMin);

        int actionTimeInt = statisticsData.getActionTime();
        int actionMin = (actionTimeInt) %3600 / 60;
        int actionHour = (actionTimeInt) / 3600;
        String actionTimeStr = String.format("%02d:%02d", actionHour, actionMin);

        String tempAchievementStr;
        // 달성률,
        if(targetTimeInt != 0){
            Log.d(tag,"StatisticsAdapter - onBindViewHolder() | targetTimeInt: "+targetTimeInt);
            Log.d(tag,"StatisticsAdapter - onBindViewHolder() | actionTimeInt: "+actionTimeInt);
            double rate = (double)((double)actionTimeInt/(double)targetTimeInt)*100;
            Log.d(tag,"StatisticsAdapter - onBindViewHolder() | rate: "+rate);
//            String dispPattern = "(%.2f)";
//            DecimalFormat format = new DecimalFormat(dispPattern);

            String achievementTmep =String.format("%.2f",rate);
            tempAchievementStr = "("+achievementTmep+"%)";

        } else {
            tempAchievementStr = "( 측정X )";
        }

        // StatisticsData 변수
        // (String category, String categoryParent, int color, int type, int targetTime, int actionTime)
        if (statisticsData != null) {
            switch (statisticsData.getType()) {
                case PARENT_TYPE:
                    Log.d(tag,"CategoryAdapter - onBindViewHolder() | PARENT_TYPE ");
                    ((ParentViewHolder) holder).nameTextView.setText(name);
                    ((ParentViewHolder) holder).targetTimeTextView.setText(targetTimeStr);
                    ((ParentViewHolder) holder).actionTimeTextView.setText(actionTimeStr);
                    ((ParentViewHolder) holder).achievementTextView.setText(tempAchievementStr);

                    break;
                case CHILD_TYPE:
                    Log.d(tag,"CategoryAdapter - onBindViewHolder() | CHILD_TYPE ");
                    ((ChildViewHolder) holder).bgShape.setColor(statisticsData.getColor());
                    ((ChildViewHolder) holder).nameTextView.setText(name);
                    ((ChildViewHolder) holder).targetTimeTextView.setText(targetTimeStr);
                    ((ChildViewHolder) holder).actionTimeTextView.setText(actionTimeStr);
                    ((ChildViewHolder) holder).achievementTextView.setText(tempAchievementStr);
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
//        Log.d(tag,"CategoryAdapter - getItemCount() |  ");
        if (mArrayList == null)
            return 0;
        return mArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mArrayList != null) {
            StatisticsData statisticsData = mArrayList.get(position);
            if (statisticsData != null) {
                return statisticsData.getType();
            }
        }
        return 0;
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.nameTextView_ItemStatisticsParent)
        TextView nameTextView;
        @BindView(R.id.targetTimeTextView_ItemStatisticsParent)
        TextView targetTimeTextView;
        @BindView(R.id.actionTimeTextView_ItemStatisticsParent)
        TextView actionTimeTextView;
        @BindView(R.id.achievementTextView_ItemStatisticsParent)
        TextView achievementTextView;

        public ParentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public class ChildViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.circleImageView_ItemStatisticsChild)
        ImageView circleImageView;
        @BindView(R.id.nameTextView_ItemStatisticsChild)
        TextView nameTextView;
        @BindView(R.id.targetTimeTextView_ItemStatisticsChild)
        TextView targetTimeTextView;
        @BindView(R.id.actionTimeTextView_ItemStatisticsChild)
        TextView actionTimeTextView;
        @BindView(R.id.achievementTextView_ItemStatisticsChild)
        TextView achievementTextView;
        GradientDrawable bgShape;       // Shape의 안 색상을 변경하기 위함.

        public ChildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            bgShape = (GradientDrawable) circleImageView.getBackground();
            // OnCreateContextMenuListener를 현재 클래스에서 구현한다고 설정해둔 것.
        }
    }
}
