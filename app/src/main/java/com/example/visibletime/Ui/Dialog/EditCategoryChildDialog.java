package com.example.visibletime.Ui.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.visibletime.Adapter.CategoryAdapter;
import com.example.visibletime.Data.CatagoryData;
import com.example.visibletime.R;
import com.example.visibletime.SortCategory;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;
import static com.example.visibletime.Data.CatagoryData.PARENT_TYPE;

public class EditCategoryChildDialog extends Dialog implements AdapterView.OnItemSelectedListener{

    private final String tag = "로그";

    private Context context;
    ArrayList<CatagoryData> mArrayList;
    SortCategory sortCategory;          // 데이터를 정렬하는 클래스
    int position;

    /**
     * Dialog View
     */
    // Button
    @BindView(R.id.cancelBtn_CategoryChildEdit)
    Button cancelBtn;
    @BindView(R.id.enterBtn_CategoryChildEdit)
    Button enterBtn;
    @BindView(R.id.nameEditTextView_CategoryChildEdit)
    EditText nameEditTextView;

    /**Color Picker*/
    @BindView(R.id.colorPicker_CategoryChildEdit)
    com.madrapps.pikolo.HSLColorPicker colorPicker;
    @BindView(R.id.circleImageView_CategoryChildEdit)
    ImageView circleImageView;      // 원형
    GradientDrawable bgShape;       // Shape의 안 색상을 변경하기 위함.

    int colorValue = 0;     //사용자가 ColorPicker을 하면 해당 값 저장.
    // 초기값이 0인 상태로 저장을 방지하기 위함.

    /**Spiner*/
    @BindView(R.id.spinner_CategoryChildEdit)
    Spinner spinner;
    String parentCategoryChoiced;       // Spinner에서 선택한 값을 받아 Data에 저장하기 위함.
    ArrayList<String> arrayListSpinner;         // Spinner에 보여줄 ArrayList.

    public EditCategoryChildDialog(@NonNull Context context, ArrayList<CatagoryData> arrayList, int position) {
        super(context);
        this.mArrayList =arrayList;
        this.position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag,"EditCategoryChildDialog - onCreate() |  ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_category_child);
        ButterKnife.bind(this);
        // 이름 설정
        nameEditTextView.setText(mArrayList.get(position).getName());

        setColorPicker();
        setSpinner();
    }



    private void setColorPicker() {
        Log.d(tag,"EditCategoryChildDialog - setColorPicker() |  ");
        /**ColorPicker
         *참조
         *  ColorPicker
         *      https://github.com/JoelSalzesson/Pikolo
         *  Circle ImageView 만들기 & 원 안의 색상 바꾸는 방법
         *      https://stack07142.tistory.com/246
         *
         * ■ Gradle 추가
         *
         * ■ ColorPicker에서 뽑은 색상 ImageView에 보여주는 방법
         *
         *      - Drawer Shape:oval 파일 생성
         *      - ImageView Background: 해당 Drawer파일로 바꿈
         *      - GradientDrawable 인스턴스 생성
         *          - What: Shape 안의 색상을 바꿔준다.
         *          - How: ImageView 객체를 통해 생성
         *      - ColorPicer의 메서드를 통해 선택시 GradientDrawable 인스턴스(bgShape)색상을 바꿔준다.
         *
         * */
        bgShape = (GradientDrawable) circleImageView.getBackground();
        bgShape.setColor(mArrayList.get(position).getColor());
        colorValue = mArrayList.get(position).getColor();
        colorPicker.setColor(mArrayList.get(position).getColor());      // 컬러 위치 정해주기

        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                bgShape.setColor(color);
                colorValue = color;
            }
        });
    }

    private void setSpinner() {
        Log.d(tag,"EditCategoryChildDialog - setSpinner() |  ");
        /**Spinner 등록
         *
         * 참조: https://kangchobo.tistory.com/45
         *
         * 프로세스
         *      - Spinner와 Adapter과 연결
         *      - Adapter에는 Spinner를 눌렀을때 사용자에게 보여줘야 하는 '상위 카테고리' 목록 리스트를 넣어줘야 한다.
         * */

        spinner.setOnItemSelectedListener(this);
        arrayListSpinner = new ArrayList<>();
        arrayListSpinner.add("상위 카테고리");
        int defaultPosition = 0;        // 수정이므로 이전에 저장했던 상위 카테고리를 보여주기 위함.
        parentCategoryChoiced = mArrayList.get(position).getParentName();
        Log.d(tag,"EditCategoryChildDialog - setSpinner() | parentCategoryChoiced: "+parentCategoryChoiced);

        /*Activity 에서 받아온 ArrayList<CatagoryData> mArrayList 에서 부모 타입인 것만 arrayListSpinner에 집어 넣는다.

        ■ Child 카테고리의 String parentName 에 해당하는 arrayListSpinner의 위치를 확인하는 방법
              ● 조건문
                    - arrayListSpinner의 값과 String parentName 값이 같은가?
                    - 참인 경우, 번호 저장
                    - 부모와 같은 값일 때 arrayListSpinner의 size를 defaultPosition에 저장한다.
        */
        for (int i = 0; i < mArrayList.size(); i++) {
            CatagoryData catagoryData = mArrayList.get(i);
            if (catagoryData.getType() == PARENT_TYPE) {
                arrayListSpinner.add(catagoryData.getName());
                if (mArrayList.get(i).getName().equals(parentCategoryChoiced)) {
                    defaultPosition = arrayListSpinner.size()-1;    // -1을 해주는 이유는 초기에 "상위 카테고리"라고 해줬기 때문.
                }
            }
        }
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,arrayListSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(defaultPosition);
    }


    @OnClick(R.id.cancelBtn_CategoryChildEdit)
    void cancleBtnClicked(){
        cancel();
    }

    /**확인 버튼 누르면 EditText에 있는 카테고리 이름으로 CatagoryData 데이터를 만들어 ArrayList에 넣는다.
     *
     * 조건문으로 이름이 없다면, Toast Message로 값을 받을 수 있도록 알려준다.
     * */
    @OnClick(R.id.enterBtn_CategoryChildEdit)
    void enterBtnClicked(){
        String name = nameEditTextView.getText().toString();
        if(name.length() == 0){
            Toast.makeText(getContext(), "카테고리 이름을 적어주세요.", Toast.LENGTH_SHORT).show();
        } else if(colorValue == 0){
            Toast.makeText(getContext(), "색상을 변경해주세요..", Toast.LENGTH_SHORT).show();
        }else{
//            Log.d(tag,"EditCategoryChildDialog - enterBtnClicked() | colorValue: "+colorValue);
//            Log.d(tag,"EditCategoryChildDialog - enterBtnClicked() | parentCategoryChoiced: "+parentCategoryChoiced);

            /* ■ 중복방지

               □ mArrayList에 저장되어 있는 값과 EditText에 있는 값이 같은지 검사한다.
                    ● 참: 같다면, EditCategoryChildDialog를 닫는다.
                    ● 거짓: 다르다면, 다른 값과 중복된 값이 있는지 확인후 중복된 값이 있다면 사용자에게 Toast Message로 알려준다.

            * */
            if(name.equals(mArrayList.get(position).getName())){
                CatagoryData catagoryData = new CatagoryData(CHILD_TYPE, name, colorValue, parentCategoryChoiced, 0);
                mArrayList.set(position,catagoryData);
                sortCategory = new SortCategory(mArrayList);
                mArrayList = sortCategory.rectifyArrayList();
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
                }
                //  mArrayList.size() == 0인 경우 처음 입력하는 것이니 값을 넣어준다.
                if(isDistinguishable){
                    // 중복이 없고 고유한 경우
                    // 이름이 설정된 경우 생성자에서 받은 데이터 List에 값을 넣어준다.
                    CatagoryData catagoryData = new CatagoryData(CHILD_TYPE, name, colorValue, parentCategoryChoiced, 0);
                    mArrayList.set(position,catagoryData);      // 기존에는 add.함수로 ArrayList에 추가해줬지만 이번에는 변경
                    sortCategory = new SortCategory(mArrayList);
                    mArrayList = sortCategory.rectifyArrayList();
                    dismiss();
                } else {
                    // 중복값이 존재하는 경우
                    Toast.makeText(getContext(), "중복된 항목이 있습니다. 이름을 다시 설정해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**Spinner 메서드*/
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        parentCategoryChoiced =arrayListSpinner.get(i);
        Log.d(tag,"EditCategoryChildDialog - onItemSelected() | parentCategoryChoiced: "+parentCategoryChoiced);

        // 아무것도 선택하지 않는 값은 null로 저장한다.
        if(parentCategoryChoiced.equals("상위 카테고리")){
            parentCategoryChoiced = "기타";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(tag,"EditCategoryChildDialog - onNothingSelected() |  ");
        parentCategoryChoiced = null;
        Log.d(tag,"EditCategoryChildDialog - onNothingSelected() | parentCategoryChoiced: "+parentCategoryChoiced);

    }
}
