package com.example.visibletime.Ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Adapter.CategoryAdapter;
import com.example.visibletime.Data.AlarmData;
import com.example.visibletime.Data.CatagoryData;
import com.example.visibletime.R;
import com.example.visibletime.SortCategory;
import com.example.visibletime.Ui.Dialog.CreateCategoryChildDialog;
import com.example.visibletime.Ui.Dialog.CreateCategoryParentDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryActivity extends AppCompatActivity {

    private final String tag = "로그";

//    @BindView(R.id.showLayout_Category)
//    LinearLayout showLayout;

    /**
     * RecyclerView
     */
    RecyclerView mRecyclerView;
    CategoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ArrayList<CatagoryData> mArrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);

        setRecyclerView();
        initFloatingActionButtonMenu();

        /**기존에 잘못 입력된 Shared 값 지우기*/
//        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
//        String presentID = sharedPresesntID.getString("presentID",null);
//        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
//        SharedPreferences.Editor editorCategory = sharedPreferences.edit();
//        editorCategory.remove("category");
//        editorCategory.commit();

        getListOfDataFromSharedPreferences();       // 데이터 불러오기
        mAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCategoryDataList();
    }

    /**FloatingAcitionButton
     * 목적:
     *      Floating Action Button 옆에 텍스트를 붙이기 위함.
     *      app:fab_title="" 이게 Gradle 추가해도 안되는 문제가 발생
     *
     * ■ Gradle 추가
     *      implementation 'com.getbase:floatingactionbutton:1.10.1'
     *
     * ■ import (자동 import 하면 CastException 발생함.)
     *      import com.getbase.floatingactionbutton.FloatingActionButton;
     *      import com.getbase.floatingactionbutton.FloatingActionsMenu;
     *
     * ■ floating_menu.xml 파일 추가
     *      ● View
     *         - Floating Button 선택시 배경화면 불투명하기 위함
     *      ● FloatingActionsMenu
     *         - 메뉴 버튼 열고 닫게 하기 위함
     *         - 백그라운드 동적 변경을 하는 용도
     *      ● FloatingActionButton
     *         - 각 Button 클릭시 적절한 액션 취하게 하기 위함.
     * */
    private void initFloatingActionButtonMenu() {
        final FloatingActionsMenu floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions_menu);

        // FloatingActionButton의 부모 객체
        final FloatingActionButton actionAddToQueue = (FloatingActionButton) findViewById(R.id.addCategoryParent);
        actionAddToQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionsMenu.collapse();

                // 부모 카테고리 보여주는 Custom 카테고리 보여주기
                CreateCategoryParentDialog createCategoryParentDialog = new CreateCategoryParentDialog(CategoryActivity.this, mArrayList, mAdapter);
                createCategoryParentDialog.show();
            }
        });

        // FloatingActionButton 자식 객체
        final FloatingActionButton actionAddToQueueAndPlay = (FloatingActionButton) findViewById(R.id.addCategoryChild);
        actionAddToQueueAndPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                floatingActionsMenu.collapse();

                // 자식 카테고리 보여주는 Custom 카테고리 보여주기
                CreateCategoryChildDialog createCategoryChildDialog = new CreateCategoryChildDialog(CategoryActivity.this, mArrayList, mAdapter);
                createCategoryChildDialog.show();
            }
        });

        // 배경화면
        final View backgroundOpac = findViewById(R.id.floating_menu_background);
        backgroundOpac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionsMenu.collapse();
            }
        });

        // Floating 메뉴버튼
        // 버튼에 따라 배경화면 달라지게 하기 위함.
        floatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {

            @Override
            public void onMenuExpanded() {
                backgroundOpac.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                backgroundOpac.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 《리사이클러뷰 프로세스》
     * ■ setRecyclerView()
     *      □ 목적: 리사이클러뷰 생성하여 View단에 보여주기
     *          1. RecyclerView, Adapter,(Linear)LayoutManager 생성
     *          2. RecyclerView 객체와 Adapter, (Linear)LayoutManager 객체 연결 : set()
     */
    private void setRecyclerView() {
        Log.d(tag,"CategoryActivity - setRecyclerView() |  ");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_Category);  //RecyclerView 객체 초기화(initiate)
        mArrayList = new ArrayList<>();         // RecycelerView에 들어갈 Data묶음 List
        mAdapter = new CategoryAdapter(this, mArrayList);    // Custom Adater 객체 생성 & Adapter와 mArrayList(Data List) 연결.
        mRecyclerView.setAdapter(mAdapter);             // RecyclerView와 Adater와 연결
        mLayoutManager = new LinearLayoutManager(this);     // RecyclerView를 어떻게 보여줄지 결정
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // 데이터 갱신
//        mAdapter.notifyDataSetChanged();


//        //child View들을 구분선을 만들어 주는 메서드
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    /**saveCategoryDataList()
     * ■ 메서드 목적:
     *      - mArrayList에 있는 값을 SharedPreference에 저장하기 위함.
     * ■ 프로세스
     *      ● 해당 아이디를 불러온다.
     *      ● 우선 기존에 있는 SharedPreference에 있는 값을 지운다.
     *      ● Parent Item가 닫힌 상태로, 내부 가지고 있는 CategoryData를 mArrayList에 저장한다. (정렬을 해야 하나?)
     *      ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
     *
     * ■ 호출
     *      - onActivityResult()
     *      - 사용자가 item View를 클릭하고 나서 나왔을 때 데이터가 수정 될 수 있기 때문에 이때 호출한다.
     * */
    private void saveCategoryDataList(){
        Log.d(tag,"CategoryActivity - saveCategoryDataList() |  ");

        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ● ID를 파일명으로 하는 Shared.xml 파일 생성 및 기존 값 지우기
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);
        SharedPreferences.Editor editorCategory = sharedPreferences.edit();
        editorCategory.remove("category");
        editorCategory.commit();

        /*
        ● Parent Item가 닫힌 상태로, 내부 가지고 있는 CategoryData를 mArrayList에 저장 및 정렬한다.
         정렬까지 해주는 이유는 '닫힘, 열림' 상태에 따라 기록 측정에서 카테고리를 선택하는 곳에서 Child Item이 보여주는
         위치가 변동되면 안되기 때문이다.

        ○ 프로세스
                1. for문 (int i =0; i<mArrayList.size(); i++)
                    - CategoryData 객체 중에서 ArrayList<CatagoryData> invisibleChildren를 가지고 있는 객체가 있다면
                        ⇒ if(mArrayList.get(i).invisibleChildren != null)
                    - invisibleChildren에 있는 CategoryData를 mArrayList에 저장하라.
                        ⇒ for(int j=0; j<mArrayList.get(i).invisibleChildren.size; j++)
                        ⇒ mArrayList.add(mArrayList.get(i).invisibleChildren.get(j));
                2. 데이터 정렬
                    - SortCategory의 인스턴스 생성
                        ⇒ SortCategory sortCategory = new SortCategory(mArrayList);
                    - 데이터를 정렬하는 메서드 실행
                        ⇒ mArrayList = sortCategory.rectifyArrayList();
*/
        for(int i =0; i<mArrayList.size(); i++){
            if(mArrayList.get(i).invisibleChildren != null){
                for(int j=0; j<mArrayList.get(i).invisibleChildren.size(); j++){
                    mArrayList.add(mArrayList.get(i).invisibleChildren.get(j));
                }
            }
        }
        SortCategory sortCategory = new SortCategory(mArrayList);
        mArrayList = sortCategory.rectifyArrayList();

        // ● 현재 ArrayList에 있는 값을 for문을 통해 차례대로 JSON 문자열 형태로 저장한다.
        if(mArrayList.size() != 0){
            for(int i=0; i<mArrayList.size(); i++){
                /**JSON 형태로 저장*/
                String strDataOfJSON;
                strDataOfJSON = "{ \"type\"" + ":" + "\"" + mArrayList.get(i).getType() + "\"" + ","
                        + "\"name\"" + ":" + "\"" + mArrayList.get(i).getName() + "\""+ ","
                        + "\"color\"" + ":" + "\"" + mArrayList.get(i).getColor() + "\""+ ","
                        + "\"parentName\"" + ":" + "\"" + mArrayList.get(i).getParentName() + "\""+ ","
                        + "\"count\"" + ":" + "\"" + mArrayList.get(i).getCount() + "\""
                        + "}";

                // □ 값을 처음으로 저장하는 확인
                String category = sharedPreferences.getString("category", null);

                if (category == null) {
                    // 첫 카테고리 정보를 저장하는 경우
                    editorCategory.putString("category", strDataOfJSON);
                    editorCategory.commit();
                } else {
                    // 카테고리 정보가 처음이 아닌 경우
                    editorCategory.putString("category", sharedPreferences.getString("category", "") + "," + strDataOfJSON);
                    editorCategory.commit();
                }
            }
        }
    }

    /**getListOfDataFromSharedPreferences
     *
     * ■ 메서드 목적
     *      - Shared에 저장된 값을 리사이클러뷰에 보이기.
     *
     * ■ 데이터 있는지 여부 확인
     *      - 없다면, 현재 값이 없다는 내용을 가진 Layout(showLayout) 띄우기
     *      - 있다면, Layout(showLayout) 사라지게 하기
     *
     * ■ Shared
     *      ● Shared에 저장된 현재 아이디를 불러온다.
     *      ● 아이디에 해당하는 Shared파일을 불러온다.
     *
     * ■ JSONArray
     *      ● JSONArray 생성
     *      ● 반복문(JSONArray.length())
     *          ○ JSON 객체 생성
     *          ○ String으로 저장된 값 변환
     *                  - String > boolean
     *                  - String > int
     *          ○ ArrayList에 값 넣기
     *
     * */
    private void getListOfDataFromSharedPreferences(){
        // 가려져 있는 값도 불러와서 지운다.
        for(int i =0; i<mArrayList.size(); i++){
            if(mArrayList.get(i).invisibleChildren != null){
                for(int j=0; j<mArrayList.get(i).invisibleChildren.size(); j++){
                    mArrayList.add(mArrayList.get(i).invisibleChildren.get(j));
                }
            }
        }
        SortCategory sortCategory = new SortCategory(mArrayList);
        mArrayList = sortCategory.rectifyArrayList();

        mArrayList.clear();

        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);

        // ■ ID를 파일명으로 하는 Shared.xml 파일 생성
        SharedPreferences sharedPreferences = getSharedPreferences(presentID, MODE_PRIVATE);

        // 값이 있는지 여부 확인하기
        if(sharedPreferences.getString("category",null) == null){
//            showLayout.setVisibility(View.VISIBLE);
        } else {
//            showLayout.setVisibility(View.GONE);
            try {
                // ● JSONArray 생성
                JSONArray jsonArray = new JSONArray("[" + sharedPreferences.getString("category", null) + "]");
                // ● 반복문(JSONArray.length())
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // ○ JSON에 String으로 저장된 값을 AlarmData 데이터 타입에 맞게 변환을 위한 변수 선언
                    int type = 0;
                    int color = 0;
                    int count = 0;

                    // int type, color, count 을 얻기 위한 데이 변환
                    type = Integer.parseInt(jsonObject.getString("type"));
                    color = Integer.parseInt(jsonObject.getString("color"));
                    count = Integer.parseInt(jsonObject.getString("count"));

                    // ○ ArrayList에 값 넣기
                    // Data 객체 생성 & 값 넣어주기
                    CatagoryData catagoryData = new CatagoryData();
                    catagoryData.setType(type);
                    catagoryData.setColor(color);
                    catagoryData.setCount(count);
                    catagoryData.setName(jsonObject.getString("name"));
                    catagoryData.setParentName(jsonObject.getString("parentName"));
                    // mArrayList에 값 넣어주기
                    mArrayList.add(catagoryData);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(tag, "CategoryActivity - onLoginBtnClicked() | Exception " + e);
            }
        }
    }
}
