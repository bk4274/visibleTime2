package com.example.visibletime.Ui.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.visibletime.Data.CatagoryData;
import com.example.visibletime.R;
import com.example.visibletime.SortCategory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;
import static com.example.visibletime.Data.CatagoryData.PARENT_TYPE;

public class DeleteCategoryParentDialog extends Dialog {

    private final String tag = "로그";

    private Context context;
    SortCategory sortCategory;          // 데이터를 정렬하는 클래스
    ArrayList<CatagoryData> mArrayList;
    int position;

    /**
     * Dialog View
     */
    // Button
    @BindView(R.id.cancelBtn_CategoryParentDelete)
    Button cancelBtn;
    @BindView(R.id.enterBtn_CategoryParentDelete)
    Button enterBtn;
    @BindView(R.id.infoTextView_CategoryParentDelete)
    TextView infoTextView;
    @BindView(R.id.checkbox_CategoryParentDelete)
    CheckBox checkbox;
    
    public DeleteCategoryParentDialog(@NonNull Context context, ArrayList<CatagoryData> arrayList, int position)  {
        super(context);
        this.mArrayList =arrayList;
        this.position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag,"DeleteCategoryParentDialog - onCreate() |  ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_category_parent);
        ButterKnife.bind(this);

        String nameToDelete = mArrayList.get(position).getName();
        infoTextView.setText("'"+nameToDelete +"'"+" 상위 카테고리를 정말 삭제하시겠습니까?");
   
    }

    @OnClick(R.id.cancelBtn_CategoryParentDelete)
    void cancleBtnClicked(){
        cancel();
    }

    /**체크 박스 상태에 따라 다르게 한다.
     *
     * 1. 부모의 하위 목록까지 싹 삭제. (체크박스 체크 됨)
     *
     * 2. 부모만 삭제
     * */

    @OnClick(R.id.enterBtn_CategoryParentDelete)
    void enterBtnClicked(){
        CatagoryData catagoryData = mArrayList.get(position);

        // 사용자가 체크박스 체크 여부에 따라 다르게 한다.
        //
        if(checkbox.isChecked()){
            // 1. 부모의 하위 목록까지 싹 삭제. (체크박스 체크 됨)
            // - mArrayList에서 Child 가운데, 카테고리의 속성 중 String parentName이 해당 Parent와 같은 것을 골라 "기타" 으로 변경해준다.
            // 문제 발생
            // 제거시, mArrayList.size가 변경되는데, 이를 반영하지 않는 점.
            // 내림차순으로 검사해야 함을 알게 되었다.
            for(int i=mArrayList.size()-1; i>=0; i--){
                Log.d(tag,"DeleteCategoryParentDialog - enterBtnClicked() | mArrayList.size(): "+mArrayList.size());
                // mArrayList.get(i).getType() == CHILD_TYPE : Child 카테고리 가운데
                // mArrayList.get(i).getParentName().equals(mArrayList.get(position).getName()) : 현 ParentCategory 이름과 ChildCategory의 nameParent 속성이 일치하는 경우
                if(mArrayList.get(i).getType() == CHILD_TYPE && mArrayList.get(i).getParentName().equals(mArrayList.get(position).getName())){
                    Log.d(tag,"DeleteCategoryParentDialog - enterBtnClicked() | 제거 대상: "+mArrayList.get(i).getParentName());
                    mArrayList.remove(i);        // 해당 내용도 삭제해준다. 변경해준다.
                }
            }
            mArrayList.remove(position);        //해당 부모 삭제
            Toast.makeText(getContext(), "상위 및 하위 카테고리 삭제 완료", Toast.LENGTH_LONG).show();
            dismiss();
        }else{
            // 2. 부모만 삭제
            // 부모 관할에 있는 Child Category의 parentName 속성을 기존에서 "기타"으로 바꿔준다.

            // 1. mArrayList에서 Child 가운데, 카테고리의 속성 중 String parentName이 해당 Parent와 같은 것을 골라 "기타" 으로 변경해준다.
            for(int i=0; i<mArrayList.size(); i++){
                // mArrayList.get(i).getType() == CHILD_TYPE : Child 카테고리 가운데
                // mArrayList.get(i).getParentName().equals(mArrayList.get(position).getName()) : 현 ParentCategory 이름과 ChildCategory의 nameParent 속성이 일치하는 경우
                if(mArrayList.get(i).getType() == CHILD_TYPE && mArrayList.get(i).getParentName().equals(mArrayList.get(position).getName())){
                    mArrayList.get(i).setParentName("기타");      // 새로운 이름으로 변경해준다.
                }
            }
            //  2. 해당 ParentChild에 있는 값중에서 ArrayList<CatagoryData> invisibleChildren가 있는지 확인한다.
            if(mArrayList.get(position).invisibleChildren != null){
                // 숨기고 있는 값이 있는 경우
                for(int i =0; i<mArrayList.get(position).invisibleChildren.size(); i++){
                    catagoryData.invisibleChildren.get(i).setParentName("기타");      //Parent 내부에 있는 CategoryData의 parentName을 "기타" 값으로 변경해준다.
                    mArrayList.add(catagoryData.invisibleChildren.get(i));          //Parent 내분에 있는 CategoryData 객체를 mArrayList에 추가해준다.
                }
            }
            // 데이터 정렬
            SortCategory sortCategory = new SortCategory(mArrayList);
            mArrayList = sortCategory.rectifyArrayList();
            mArrayList.remove(position);        //해당 부모 삭제

            Toast.makeText(getContext(), "상위 카테고리 삭제 완료", Toast.LENGTH_LONG).show();
            dismiss();
        }
    }
}
