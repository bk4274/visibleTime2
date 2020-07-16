package com.example.visibletime.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Data.ControllerPlanData;
import com.example.visibletime.Data.ReportData;
import com.example.visibletime.Data.RoutineData;
import com.example.visibletime.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoutineAdatper extends RecyclerView.Adapter<RoutineAdatper.RoutineViewHolder>  {

    private final String tag = "로그";

    /**생성자 변수*/
    private Context context;
    ArrayList<ControllerPlanData> mArrayList;

    /**커스텀 리스너*/
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    /**item의 Check 상태를 저장하기 위한 변수*/
    private SparseBooleanArray initCheckBoxs = new SparseBooleanArray(0);
    private SparseBooleanArray mSelectedCheckBoxs = new SparseBooleanArray(0);


    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public RoutineAdatper(Context context, ArrayList<ControllerPlanData> mArrayList) {
        this.context = context;
        this.mArrayList = mArrayList;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(context);
        View view = inflate.inflate(R.layout.item_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        ControllerPlanData controllerPlanData = mArrayList.get(position);

        if (mArrayList.get(position).isSelected() && initCheckBoxs.get(position) == false){
            setStateOfCheckBoxs(position);
        }

        // ViewHolder에 데이터 집어 넣기.
        holder.nameTextView.setText(controllerPlanData.getControllerName());
        holder.itemSwitch.setChecked(isItemSelected(position));
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class RoutineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.nameTextView_ItemRoutine)
        TextView nameTextView;
        @BindView(R.id.itemSwitch_ItemRoutine)
        Switch itemSwitch;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemSwitch.setOnClickListener(this);
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


//            itemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    // do something, the isChecked will be
//                    // true if the switch is in the On position
//                    int pos = getAdapterPosition() ;
//                    if (pos != RecyclerView.NO_POSITION) {
//                        // 리스너 객체의 메서드 호출.
//                        if (mListener != null) {
//                            mListener.onCheckedChanged(buttonView, isChecked, pos) ;
//                        }
//                    }
//                }
//            });
        }
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.itemSwitch_ItemRoutine:
                    Log.d(tag,"RoutineViewHolder - onClick() | 클릭 ");
                    saveStateOfCheckBoxs(getAdapterPosition());
                    break;
            }
        }
    }
    // 체크 박스 초기화 할때
    private void setStateOfCheckBoxs(int position) {
        Log.d(tag, "RoutineAdatper - setStateOfCheckBoxs() |  ");
        Log.d(tag,"RoutineAdatper - setStateOfCheckBoxs() | position: "+position);
        if (mSelectedCheckBoxs.get(position) == true) {
            Log.d(tag, "RoutineAdatper - setStateOfCheckBoxs() | 체크가 되어 있다면 Array 값에서 position을 빼라");
            mSelectedCheckBoxs.delete(position);
        } else {
            Log.d(tag, "RoutineAdatper - setStateOfCheckBoxs() | 체크가 안되어 있다면 Array 값에서 position을 추가해라 ");
            mSelectedCheckBoxs.put(position, true);
            initCheckBoxs.put(position, true);      //체크 박스 초기화 하는 용도, ★★★ 문제 해결 방법
        }
    }

    // 사용자가 선택함에 따라 달라지게 하기 위함.
    private void saveStateOfCheckBoxs(int position) {
        Log.d(tag, "RoutineAdatper - saveStateOfCheckBoxs() |  ");
        Log.d(tag,"RoutineAdatper - saveStateOfCheckBoxs() | position: "+position);
        if (mSelectedCheckBoxs.get(position) == true) {
            Log.d(tag, "RoutineAdatper - saveStateOfCheckBoxs() | 체크가 되어 있다면 Array 값에서 position을 빼라");
            mSelectedCheckBoxs.delete(position);
            notifyItemChanged(position);
        } else {
            /** 라디오그룹 버튼 처럼 하나만 저장되어야 하므로, 모든 값을 지우고 해당 값만 true로 바꿔준다.
             * */
            Log.d(tag, "RoutineAdatper - saveStateOfCheckBoxs() | 체크가 안되어 있다면 Array 값에서 position을 추가해라 ");

            mSelectedCheckBoxs.clear();
//            for(int i =0; i<mArrayList.size(); i++){
//                mSelectedCheckBoxs.put(i, false);
//            }
            mSelectedCheckBoxs.put(position, true);
            notifyDataSetChanged();
        }
    }

    private boolean isItemSelected(int position) {
        Log.d(tag, "RoutineAdatper - isItemSelected() |  ");
        return mSelectedCheckBoxs.get(position, false);
    }

    /** changeControllerPlanDataList()
     *  목적: ControllerPlanData List의 데이터를 수정해줌
     *  기술: 이 메서드가 없다면, 스위치를 변경한 값이 저장되지 않는 점.
     * */
    public void changeControllerPlanDataList(){
        Log.d(tag,"RoutineAdatper - changeControllerPlanDataList() | mSelectedCheckBoxs.size(): "+mSelectedCheckBoxs.size());
        // 저장하기 전 false로 초기화
        for(int i=0; i<mArrayList.size(); i++){
            mArrayList.get(i).setSelected(false);
        }
        // mSelectedCheckBoxs가 있다면, mArrayList에서 해당 position 값만 true로 저장
        if(mSelectedCheckBoxs.size() != 0){
            int index = mSelectedCheckBoxs.keyAt(0);
            mArrayList.get(index).setSelected(true);
        }
        // 값 삭제
        // 이유: changeControllerPlanDataList() 메서드는 RoutineActivity에서 onPause에서 호출 된다.
        // 즉 화면에 나갈 때 이게 실시된다.
        // 문제 현상
        //      - 루틴을 수정하고 나오면, switch가 다 false로 해제되어 있는 현상
        // 문제 원인
        //      - SparseBooleanArray 값들이 초기화 되지 않고 그대로 남아 있는 점.
        // 문제 해결
        //      - 해당 메서드가 실행 될때, SparseBooleanArray 들을 초기화 해준다.

        initCheckBoxs.clear();
        mSelectedCheckBoxs.clear();
    }
}
