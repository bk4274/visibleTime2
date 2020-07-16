package com.example.visibletime.Ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visibletime.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private final String tag = "로그";
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //툴바를 액션바로 사용하기 위함.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavigationView();

    }

    /**Navigation Drawer Layout에 사용되는 View 초기화
     * ■ DrawerLayout
     *      □ What: Root Layout
     *      □ How: AppBarConfiguration 객체 생성하는데 사용
     * ■ AppBarConfiguration
     *      □ What: AppBar를 설정하는 클래스
     *      □ How: 각 화면에 따라 AppBar의 내용을 바꾸고 싶을 경우
     * ■ NavigationView
     *      □ What: 다른 Activity, Fragemnt로 이동할 수 있는 menu를 담고 있는 View
     * ■ NavController
     *      □ What: 사용자의 menu 선택에 따라 달라질 View
     * */
    private void initNavigationView(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_record, R.id.nav_calendar,
                R.id.nav_statistics, R.id.nav_setting)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /**Header의 Email 변경*/
        View navHeader = navigationView.inflateHeaderView(R.layout.nav_header_drawer);/**HeaderView inflate*/
        TextView headerName = (TextView) navHeader.findViewById(R.id.headerName);

        /**루트아이디*/
        SharedPreferences sharedPresesntID = getSharedPreferences("presentID", MODE_PRIVATE);
        String presentID = sharedPresesntID.getString("presentID",null);
        if(presentID != null){
            headerName.setText(presentID);
        } else{
            Toast.makeText(MainActivity.this, "회원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    /**AppBar 옆에 생기는 목록*/
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        Log.d(tag,"Drawer - onCreateOptionsMenu() |  ");
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.drawer_menu, menu);
//        return true;
//    }
//
   /**역할: 햄버거 버튼 클릭시 반응함.*/
    @Override
    public boolean onSupportNavigateUp() {
        Log.d(tag,"Drawer - onSupportNavigateUp() |  "+super.onSupportNavigateUp());
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}