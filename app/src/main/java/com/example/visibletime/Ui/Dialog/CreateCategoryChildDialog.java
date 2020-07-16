package com.example.visibletime.Ui.Dialog;

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
import static com.example.visibletime.Data.CatagoryData.PARENT_TYPE;
import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;

public class CreateCategoryChildDialog extends Dialog implements AdapterView.OnItemSelectedListener{

    private final String tag = "로그";

    private Context context;
    ArrayList<CatagoryData> mArrayList;
    SortCategory sortCategory;          // 데이터를 정렬하는 클래스
    CategoryAdapter mAdapter;


    /**
     * Dialog View
     */
    // Button
    @BindView(R.id.cancelBtn_CategoryChild)
    Button cancelBtn;
    @BindView(R.id.enterBtn_CategoryChild)
    Button enterBtn;
    @BindView(R.id.nameEditTextView_CategoryChild)
    EditText nameEditTextView;

    /**Color Picker*/
    @BindView(R.id.colorPicker_CategoryChild)
    com.madrapps.pikolo.HSLColorPicker colorPicker;
    @BindView(R.id.circleImageView_CategoryChild)
    ImageView circleImageView;      // 원형
    GradientDrawable bgShape;       // Shape의 안 색상을 변경하기 위함.
    int colorValue = 0;     //사용자가 ColorPicker을 하면 해당 값 저장.
                            // 초기값이 0인 상태로 저장을 방지하기 위함.

    /**Spiner*/
    @BindView(R.id.spinner_CategoryChild)
    Spinner spinner;
    String parentCategoryChoiced;       // Spinner에서 선택한 값을 받아 Data에 저장하기 위함.
    ArrayList<String> arrayListSpinner;         // Spinner에 보여줄 ArrayList.

    public CreateCategoryChildDialog(@NonNull Context context, ArrayList<CatagoryData> arrayList, CategoryAdapter mAdapter) {
        super(context);
        this.mArrayList =arrayList;
        this.mAdapter = mAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag,"CreateCategoryChildDialog - onCreate() |  ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_category_child);
        ButterKnife.bind(this);
        setColorPicker();
        setSpinner();

    }


    private void setColorPicker() {
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

        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                bgShape.setColor(color);
                colorValue = color;
            }
        });
    }

    private void setSpinner() {
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

        // Activity 에서 받아온 ArrayList<CatagoryData> mArrayList 에서 부모 타입인 것만 arrayListSpinner에 집어 넣는다.
        for(int i =0; i<mArrayList.size(); i++){
            CatagoryData catagoryData = mArrayList.get(i);
            if(catagoryData.getType() == PARENT_TYPE){
                arrayListSpinner.add(catagoryData.getName());
            }
        }
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,arrayListSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    @OnClick(R.id.cancelBtn_CategoryChild)
    void cancleBtnClicked(){
        cancel();
    }

    /**확인 버튼 누르면 EditText에 있는 카테고리 이름으로 CatagoryData 데이터를 만들어 ArrayList에 넣는다.
     *
     * 조건문으로 이름이 없다면, Toast Message로 값을 받을 수 있도록 알려준다.
     * */
    @OnClick(R.id.enterBtn_CategoryChild)
    void enterBtnClicked(){
        String name = nameEditTextView.getText().toString();
        if(name.length() == 0){
            Toast.makeText(getContext(), "카테고리 이름을 적어주세요.", Toast.LENGTH_SHORT).show();
        } else if(colorValue == 0){
            Toast.makeText(getContext(), "색상을 변경해주세요..", Toast.LENGTH_SHORT).show();
        }else{
//            Log.d(tag,"CreateCategoryChildDialog - enterBtnClicked() | colorValue: "+colorValue);
//            Log.d(tag,"CreateCategoryChildDialog - enterBtnClicked() | parentCategoryChoiced: "+parentCategoryChoiced);

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
                // 이름이 설정된 경우 생성자에서 받은 데이터 List에 값을 넣어준다.
                CatagoryData catagoryData = new CatagoryData(CHILD_TYPE, name, colorValue, parentCategoryChoiced, 0);
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

    /**Spinner 메서드*/
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        parentCategoryChoiced =arrayListSpinner.get(i);
        Log.d(tag,"CreateCategoryChildDialog - onItemSelected() | parentCategoryChoiced: "+parentCategoryChoiced);

        // 아무것도 선택하지 않는 값은 null로 저장한다.
        if(parentCategoryChoiced.equals("상위 카테고리")){
            parentCategoryChoiced = "기타";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(tag,"CreateCategoryChildDialog - onNothingSelected() |  ");
        parentCategoryChoiced = null;
        Log.d(tag,"CreateCategoryChildDialog - onNothingSelected() | parentCategoryChoiced: "+parentCategoryChoiced);

    }
}
