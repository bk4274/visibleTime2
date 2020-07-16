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
import static com.example.visibletime.Data.CatagoryData.PARENT_TYPE;

public class CreateCategoryParentDialog extends Dialog {

    private final String tag = "로그";

    private Context context;
    SortCategory sortCategory;          // 데이터를 정렬하는 클래스
    ArrayList<CatagoryData> mArrayList;
    CategoryAdapter mAdapter;

    /**
     * Dialog View
     */
    // Button
    @BindView(R.id.cancelBtn_CategoryParentDialog)
    Button cancelBtn;
    @BindView(R.id.enterBtn_CategoryParentDialog)
    Button enterBtn;
    @BindView(R.id.nameEditTextView_CreateCategory)
    EditText nameEditTextView;

    public CreateCategoryParentDialog(@NonNull Context context,  ArrayList<CatagoryData> arrayList, CategoryAdapter mAdapter) {
        super(context);
        this.mArrayList =arrayList;
        this.mAdapter = mAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag,"CreateCategoryParentDialog - onCreate() |  ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_category_parent);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.cancelBtn_CategoryParentDialog)
    void cancleBtnClicked(){
        cancel();
    }
    /**확인 버튼 누르면 EditText에 있는 카테고리 이름으로 CatagoryData 데이터를 만들어 ArrayList에 넣는다.
     *
     * 조건문으로 이름이 없다면, Toast Message로 값을 받을 수 있도록 알려준다.
     * */
    @OnClick(R.id.enterBtn_CategoryParentDialog)
    void enterBtnClicked(){
        String name = nameEditTextView.getText().toString();

        if(name.length() == 0){
            Toast.makeText(getContext(), "카테고리 이름을 적어주세요.", Toast.LENGTH_SHORT).show();
        } else{
            // 중복 방지
            boolean isDistinguishable = false;
            for(int i =0; i< mArrayList.size(); i++){
                if(mArrayList.get(i).getName().equals(name)){
                    isDistinguishable = false;
                    break;
                } else {
                    isDistinguishable = true;
                }
            }
            //  mArrayList.size() == 0인 경우 처음 입력하는 것이니 값을 넣어준다.
            if(isDistinguishable || mArrayList.size() == 0){
                // 중복이 없고 고유한 경우
                CatagoryData catagoryData = new CatagoryData(PARENT_TYPE, name, 0, null, 0);
                mArrayList.add(catagoryData);
                sortCategory = new SortCategory(mArrayList);
                mArrayList = sortCategory.rectifyArrayList();

                mAdapter.notifyDataSetChanged();
                dismiss();
            } else {
                // 중복값이 존재하는 경우
                Toast.makeText(getContext(), "중복된 항목이 있습니다. 이름을 다시 설정해주세요.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
