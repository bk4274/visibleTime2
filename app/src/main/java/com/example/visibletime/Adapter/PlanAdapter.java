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

import com.example.visibletime.Data.PlanData;
import com.example.visibletime.Data.StatisticsData;
import com.example.visibletime.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;
import static com.example.visibletime.Data.CatagoryData.PARENT_TYPE;


public class PlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String tag = "로그";

    Context context;
    ArrayList<PlanData> mArrayList;

//    private int positionClicked;

    /** 클릭이벤트를 Activity에서 받기 위한 로직
     * 1. 인터페이스 정의
     * */
    // 커스텀 리스너
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
        void onItemLongClick(View v, int position) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public PlanAdapter(Context context, ArrayList<PlanData> mArrayList) {
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
                view = inflater.inflate(R.layout.item_plan_general, parent, false);
                return new ParentViewHolder(view);
            case CHILD_TYPE:
                Log.d(tag,"CategoryAdapter - onCreateViewHolder() | CHILD_TYPE ");
                view = inflater.inflate(R.layout.item_plan_specific, parent, false);
                return new ChildViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlanData planData = mArrayList.get(position);
        String name = planData.getCategory();

        // 시간 데이터 변경
        int time = planData.getTargetTime();
        int min = (time) %3600 / 60;
        int hour = (time) / 3600;
        String targetTime = String.format("%02d시간:%02d분", hour, min);

        switch (planData.getType()) {
            case PARENT_TYPE:
                Log.d(tag, "CategoryAdapter - onBindViewHolder() | PARENT_TYPE ");
                ((ParentViewHolder) holder).nameTextView.setText(name);
                ((ParentViewHolder) holder).targetTimeTextView.setText(targetTime);
                break;
            case CHILD_TYPE:
                Log.d(tag, "CategoryAdapter - onBindViewHolder() | CHILD_TYPE ");
                ((ChildViewHolder) holder).bgShape.setColor(planData.getColor());
                ((ChildViewHolder) holder).nameTextView.setText(name);
                ((ChildViewHolder) holder).targetTimeTextView.setText(targetTime);
                break;
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
            PlanData planData = mArrayList.get(position);
            if (planData != null) {
                return planData.getType();
            }
        }
        return 0;
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.nameTextView_ItemPlanParent)
        TextView nameTextView;
        @BindView(R.id.targetTimeTextView_ItemPlanParent)
        TextView targetTimeTextView;

        public ParentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public class ChildViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.circleImageView_ItemPlanChild)
        ImageView circleImageView;
        @BindView(R.id.nameTextView_ItemPlanChild)
        TextView nameTextView;
        @BindView(R.id.targetTimeTextView_ItemPlanChild)
        TextView targetTimeTextView;
        GradientDrawable bgShape;       // Shape의 안 색상을 변경하기 위함.

        public ChildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            bgShape = (GradientDrawable) circleImageView.getBackground();
            // OnCreateContextMenuListener를 현재 클래스에서 구현한다고 설정해둔 것.

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(view, pos) ;
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemLongClick(view, pos) ;
                        }
                    }
                    return false;
                }
            });
        }
    }
}
