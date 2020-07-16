package com.example.visibletime.Ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.visibletime.Data.TutorialData;
import com.example.visibletime.R;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class TutorialActivity extends AppCompatActivity {

    private final String TAG = "로그";
    private ViewPager viewPager ;
    private TextViewPagerAdapter pagerAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"TutorialActivity - onCreate() |  ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager = (ViewPager) findViewById(R.id.viewPager) ;
        pagerAdapter = new TextViewPagerAdapter(this) ;
        pagerAdapter.addContext(new TutorialData(R.drawable.home, "메인화면","측정한 기록들이 원형 차트에 반영되며, 클릭 시 기록 정보를 확인 할 수 있습니다."));
        pagerAdapter.addContext(new TutorialData(R.drawable.actioin, "활동 목록","자신이 측정하고자 하는 활동을 계층별로 정할 수 있습니다."));
        pagerAdapter.addContext(new TutorialData(R.drawable.plan, "활동 목표", "활동 목표 시간을 세우고 '루틴'처럼 관리하실 수 있습니다."));
        pagerAdapter.addContext(new TutorialData(R.drawable.statistics, "통계", "원형 그래프에서는 자신이 활동에 대한 비율을 나타냅니다."));

        viewPager.setAdapter(pagerAdapter) ;

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);


    }

    public class TextViewPagerAdapter extends PagerAdapter {

        private List<TutorialData> tutorialDataArrayList = new ArrayList<>();

        // LayoutInflater 서비스 사용을 위한 Context 참조 저장.
        private Context mContext = null ;
        public TextViewPagerAdapter() {
        }
        // Context를 전달받아 mContext에 저장하는 생성자 추가.
        public TextViewPagerAdapter(Context context) {
            Log.d(TAG,"TextViewPagerAdapter - TextViewPagerAdapter() |  ");
            mContext = context ;
        }

        public void addContext(TutorialData tutorialData) {
            tutorialDataArrayList.add(tutorialData);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.d(TAG,"TextViewPagerAdapter - instantiateItem() |  ");
            Log.d(TAG,"TextViewPagerAdapter - instantiateItem() | container: "+container);
            Log.d(TAG,"TextViewPagerAdapter - instantiateItem() | position: "+position);

            View view = null ;

            if (mContext != null) {
                // LayoutInflater를 통해 "/res/layout/tutorial_page.xml_page.xml"을 뷰로 생성.
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.tutorial_page, container, false);

                // 나가기 버튼
                ImageView quitImageView = (ImageView) view.findViewById(R.id.quit_ViewPager);
                quitImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                TextView name = (TextView) view.findViewById(R.id.name_ViewPager) ;
                name.setText(tutorialDataArrayList.get(position).getName());
                ImageView imageView = (ImageView) view.findViewById(R.id.image_ViewPager);
//                switch (position){
//                    case 0:
//                        Drawable drawable = mContext.getResources().getDrawable(R.drawable.home);
//                        imageView.setImageDrawable(drawable);
//                        break;
//                    case 1:
//                        imageView.setImageResource(R.drawable.active);
//                        break;
//                    case 2:
//                        imageView.setImageResource(R.drawable.plan);
//                        break;
//                }
                imageView.setImageResource(tutorialDataArrayList.get(position).getImage());
                TextView context = (TextView) view.findViewById(R.id.context_ViewPager) ;
                context.setText(tutorialDataArrayList.get(position).getContext());
            }

            // 뷰페이저에 추가.
            container.addView(view) ;

            return view ;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.d(TAG,"TextViewPagerAdapter - destroyItem() |  ");
            Log.d(TAG,"TextViewPagerAdapter - destroyItem() | container: "+container);
            Log.d(TAG,"TextViewPagerAdapter - destroyItem() | position: "+position);
            Log.d(TAG,"TextViewPagerAdapter - destroyItem() | object: "+object);
            // 뷰페이저에서 삭제.
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            Log.d(TAG,"TextViewPagerAdapter - getCount() |  ");
            // 전체 페이지 수는 10개로 고정.
            return tutorialDataArrayList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            Log.d(TAG,"TextViewPagerAdapter - isViewFromObject() |  ");
            Log.d(TAG,"TextViewPagerAdapter - isViewFromObject() | view: "+view);
            Log.d(TAG,"TextViewPagerAdapter - isViewFromObject() | objext: "+object);
            return (view == (View)object);
        }
    }
}
