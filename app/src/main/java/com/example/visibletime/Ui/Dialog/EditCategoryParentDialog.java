package com.example.visibletime.Ui.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.visibletime.Adapter.CategoryAdapter;
import com.example.visibletime.Data.CatagoryData;
import com.example.visibletime.R;
import com.example.visibletime.SortCategory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;
import static com.example.visibletime.Data.CatagoryData.PARENT_TYPE;

public class EditCategoryParentDialog extends Dialog {

    private final String tag = "로그";

    private Context context;
    SortCategory sortCategory;          // 데이터를 정렬하는 클래스
    ArrayList<CatagoryData> mArrayList;
    int position;

    /**
     * Dialog View
     */
    // Button
    @BindView(R.id.cancelBtn_CategoryParentEdit)
    Button cancelBtn;
    @BindView(R.id.enterBtn_CategoryParentEdit)
    Button enterBtn;
    @BindView(R.id.nameEditTextView_CategoryParentEdit)
    EditText nameEditTextView;

    String nameStored;      // 변경하기 전 Parent Category 이름을 담는 변수,
                            // 용도는 Parent Category안에 있는 하위 Child Category도 일괄적으로 수정하기 위함.

    public EditCategoryParentDialog(@NonNull Context context, ArrayList<CatagoryData> arrayList, int position)  {
        super(context);
        this.mArrayList =arrayList;
        this.position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag,"CreateCategoryParentDialog - onCreate() |  ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_eidt_category_parent);
        ButterKnife.bind(this);

        nameStored = mArrayList.get(position).getName();        // 기존에 저장된 이름 저장.
        nameEditTextView.setText(nameStored);
    }

    @OnClick(R.id.cancelBtn_CategoryParentEdit)
    void cancleBtnClicked(){
        cancel();
    }
    /**확인 버튼 누르면 EditText에 있는 카테고리 이름으로 CatagoryData 데이터를 만들어 ArrayList에 넣는다.
     *
     * 조건문으로 이름이 없다면, Toast Message로 값을 받을 수 있도록 알려준다.
     * */
    @OnClick(R.id.enterBtn_CategoryParentEdit)
    void enterBtnClicked(){
        String name = nameEditTextView.getText().toString();

        if(name.length() == 0){
            Toast.makeText(getContext(), "카테고리 이름을 적어주세요.", Toast.LENGTH_SHORT).show();
        } else{

            /** ■ 중복방지

               □ mArrayList에 저장되어 있는 값과 EditText에 있는 값이 같은지 검사한다.
                    ● 참: 같다면, EditCategoryChildDialog를 닫는다.
                    ● 거짓: 다르다면, 다른 값과 중복된 값이 있는지 확인후 중복된 값이 있다면 사용자에게 Toast Message로 알려준다.
                        ○ 다른 이름으로 바꿨다면, 하위 카테고리 이름까지 찾아 바꿔준다.
                            1. mArrayList에서 Child 가운데, 카테고리의 속성 중 String parentName이 해당 Parent와 같은 것을 골라 변경해준다.
                            2. 해당 ParentChild에 있는 값중에서 ArrayList<CatagoryData> invisibleChildren가 있는지 확인한다.
                                - 있다면 해당 Child 카테고리의 속성 중 String parentName를 변경해주고 mArrayList에 추가해준다.
            * */
            if(name.equals(nameStored)){
                // 변경되 값이 없으므로 그냥 닫는다.
                dismiss();
            } else {
                boolean isDistinguishable = false;
                for(int i =0; i< mArrayList.size(); i++){
                    if(mArrayList.get(i).getName().equals(name)){
                        isDistinguishable = false;
                        break;
                    } else {
                        isDistinguishable = true;
                    }

                    // 부모 Category 내부에 있는 항목도 검사
                    if(mArrayList.get(i).invisibleChildren != null){
                        for(int j=0; j<mArrayList.get(i).invisibleChildren.size(); j++){
                            if(mArrayList.get(i).invisibleChildren.get(j).getName().equals(name)){
                                isDistinguishable = false;
                                break;
                            }
                        }
                    }
                }
                if(isDistinguishable){
                    // 중복이 없고 고유한 경우

                    // 부모 이름 변경
                    CatagoryData catagoryData = mArrayList.get(position);
                    catagoryData.setName(name);
                    mArrayList.set(position, catagoryData);

                    //○ 다른 이름으로 바꿨다면, 하위 카테고리 이름까지 찾아 바꿔준다.
                    // 1. mArrayList에서 Child 가운데, 카테고리의 속성 중 String parentName이 해당 Parent와 같은 것을 골라 변경해준다.
                    for(int i=0; i<mArrayList.size(); i++){
                        // mArrayList.get(i).getType() == CHILD_TYPE : Child 카테고리 가운데
                        // mArrayList.get(i).getParentName().equals(nameStored) : parentName 속성이 기존에 저장된 값(nameStored)과 같은 경우
                        if(mArrayList.get(i).getType() == CHILD_TYPE && mArrayList.get(i).getParentName().equals(nameStored)){
                            mArrayList.get(i).setParentName(name);      // 새로운 이름으로 변경해준다.
                        }
                    }
                    //  2. 해당 ParentChild에 있는 값중에서 ArrayList<CatagoryData> invisibleChildren가 있는지 확인한다.
                    if(catagoryData.invisibleChildren != null){
                        Log.d(tag,"EditCategoryParentDialog - enterBtnClicked() | 내부 ArrayList<CatagoryData> 존재 ");
                      // 숨기고 있는 값이 있는 경우
                        Log.d(tag,"EditCategoryParentDialog - enterBtnClicked() | catagoryData.invisibleChildren.size(): "+catagoryData.invisibleChildren.size());
                        for(int i =0; i<catagoryData.invisibleChildren.size(); i++){
                            catagoryData.invisibleChildren.get(i).setParentName(name);      //Parent 내부에 있는 CategoryData의 parentName을 수정된 값으로 변경해준다.
//                            mArrayList.add(catagoryData.invisibleChildren.get(i));          //Parent 내분에 있는 CategoryData 객체를 mArrayList에 추가해준다.
                        }
                    }
//                    sortCategory = new SortCategory(mArrayList);
//                    mArrayList = sortCategory.rectifyArrayList();
                    dismiss();
                } else {
                    // 중복값이 존재하는 경우
                    Toast.makeText(getContext(), "중복된 항목이 있습니다. 이름을 다시 설정해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}
