package com.example.visibletime;

import android.util.Log;

import com.example.visibletime.Data.CatagoryData;
import com.example.visibletime.Data.PlanData;
import com.example.visibletime.Data.ReportData;
import com.example.visibletime.Data.StatisticsData;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;
import static com.example.visibletime.Data.CatagoryData.PARENT_TYPE;

/**클래스 목적
 *      - 통계화면에 쓰일 데이터 List를 생성, 정렬 역할을 담당하는 클래스
 *      - 하루, 주간, 월간 통계에서 반복되는 코드를 재사용하기 위함
 * */
public class Statistics {
    private final String tag = "로그";

    /**setStatisticsArrayList()
     * ■ 목표
     *      What: arrayListSelected를 가공하여 statisticsDataArrayList를 만드는 것
     *      Why: statisticsDataArrayList, 즉 가공된 데이터가 있어야 그래프, 리사이클러뷰를 통해 보여줄 수 있기 때문
     *      How:
     * */
    public void setStatisticsArrayList(ArrayList<ReportData> arrayListSelected, ArrayList<StatisticsData> statisticsDataArrayList){
        for(int i=0; i<arrayListSelected.size(); i++){
            if(statisticsDataArrayList.size() == 0){
                // statisticsDataArrayList.size() == 0, Data가 처음으로 들어가는 경우
                if(arrayListSelected.get(i).getCategoryParent().equals("기타")){
                    Log.d(tag,"Statistics - setStatisticsArrayList() | 부모생성 기타 / 자식생성 O ");
                    // 부모 카테고리가 없는 경우

                    // 부모 데이터
                    StatisticsData statisticsParentData = new StatisticsData();
                    statisticsParentData.setType(PARENT_TYPE);
                    statisticsParentData.setColor(0);
                    statisticsParentData.setCategory("기타");
                    statisticsParentData.setCategoryParent(null);
                    statisticsParentData.setTargetTime(0);
                    statisticsParentData.setActionTime(arrayListSelected.get(i).getTimeDuration());
                    statisticsDataArrayList.add(statisticsParentData);
                    
                    // 자식 데이터
                    StatisticsData statisticsChildData = new StatisticsData();
                    statisticsChildData.setColor(arrayListSelected.get(i).getColor());
                    statisticsChildData.setCategory(arrayListSelected.get(i).getCategory());
                    statisticsChildData.setCategoryParent(arrayListSelected.get(i).getCategoryParent());
                    statisticsChildData.setType(CHILD_TYPE);
                    statisticsChildData.setActionTime(arrayListSelected.get(i).getTimeDuration());

                    statisticsDataArrayList.add(statisticsChildData);
                } else {
                    Log.d(tag,"Statistics - setStatisticsArrayList() | 부모생성 O / 자식생성 O ");
                    // 부모 카테고리가 있는 경우

                    // 부모 데이터
                    StatisticsData statisticsParentData = new StatisticsData();
                    statisticsParentData.setType(PARENT_TYPE);
                    statisticsParentData.setColor(0);
                    statisticsParentData.setCategory(arrayListSelected.get(i).getCategoryParent());
                    statisticsParentData.setCategoryParent(null);
                    statisticsParentData.setTargetTime(0);
                    statisticsParentData.setActionTime(arrayListSelected.get(i).getTimeDuration());
                    statisticsDataArrayList.add(statisticsParentData);

                    // 자식 데이터
                    StatisticsData statisticsChildData = new StatisticsData();
                    statisticsChildData.setType(CHILD_TYPE);
                    statisticsChildData.setColor(arrayListSelected.get(i).getColor());
                    statisticsChildData.setCategory(arrayListSelected.get(i).getCategory());
                    statisticsChildData.setCategoryParent(arrayListSelected.get(i).getCategoryParent());
                    statisticsChildData.setTargetTime(0);
                    statisticsChildData.setActionTime(arrayListSelected.get(i).getTimeDuration());
                    statisticsDataArrayList.add(statisticsChildData);
                }
            }
            // statisticsDataArrayList.size() != 0, Data가 존재하는 경우
            else {
                int indexOfChildArrayList = -1;
                int indexOfParentArrayList = -1;
                for(int j= 0; j<statisticsDataArrayList.size(); j++){
                    if(arrayListSelected.get(i).getCategory().equals(statisticsDataArrayList.get(j).getCategory())){
                        // 해당 값이 존재하는 경우
                        indexOfChildArrayList= j;
                    } else if(arrayListSelected.get(i).getCategoryParent().equals(statisticsDataArrayList.get(j).getCategory())){
                        indexOfParentArrayList = j;
                    } else if(statisticsDataArrayList.get(j).getCategory().equals("기타")){
                        if(arrayListSelected.get(i).getCategoryParent().equals("기타")){
                            indexOfParentArrayList = j;
                        }
                    }
                }
                
                // indexOfChildArrayList == -1 이라는게, 겹치는게 없어 새로 추가해야 하는 경우를 의미한다.
                
                // 자식과 부모가 이미 다 존재하는 경우
                if(indexOfChildArrayList != -1 && indexOfParentArrayList != -1) {
                    Log.d(tag,"Statistics - setStatisticsArrayList() | 부모 시간 + / 자식 시간 + ");

                    // 부모 데이터에 시간 추가
                    StatisticsData statisticsParentData = statisticsDataArrayList.get(indexOfParentArrayList);
                    int sumParentTime = statisticsDataArrayList.get(indexOfParentArrayList).getActionTime() + arrayListSelected.get(i).getTimeDuration();
                    statisticsParentData.setActionTime(sumParentTime);

                    // 자식 데이터에 시간 추가
                    StatisticsData statisticsChildData = statisticsDataArrayList.get(indexOfChildArrayList);
                    int sumChildTime = statisticsDataArrayList.get(indexOfChildArrayList).getActionTime() + arrayListSelected.get(i).getTimeDuration();
                    statisticsChildData.setActionTime(sumChildTime);
                }
                // 자식은 겹치는게 있고, 부모가 겹체는게 없는 경우
                else if (indexOfChildArrayList != -1 && indexOfParentArrayList == -1) {
                    Log.d(tag, "Statistics - setStatisticsArrayList() | 부모 추가 / 자식 시간+ ");
                    if (arrayListSelected.get(i).getCategoryParent().equals("기타")) {
                        // 부모 데이터
                        StatisticsData statisticsParentData = new StatisticsData();
                        statisticsParentData.setType(PARENT_TYPE);
                        statisticsParentData.setColor(0);
                        statisticsParentData.setCategory("기타");
                        statisticsParentData.setCategoryParent(null);
                        statisticsParentData.setTargetTime(0);
                        statisticsParentData.setActionTime(arrayListSelected.get(i).getTimeDuration());
                        statisticsDataArrayList.add(statisticsParentData);

                        // 자식 데이터에 시간 추가
                        StatisticsData statisticsChildData = statisticsDataArrayList.get(indexOfChildArrayList);
                        int sumChildTime = statisticsDataArrayList.get(indexOfChildArrayList).getActionTime() + arrayListSelected.get(i).getTimeDuration();
                        statisticsChildData.setActionTime(sumChildTime);
                    } else {
                        // 부모 데이터 생성
                        StatisticsData statisticsParentData = new StatisticsData();
                        statisticsParentData.setType(PARENT_TYPE);
                        statisticsParentData.setColor(0);
                        statisticsParentData.setCategory(arrayListSelected.get(i).getCategoryParent());
                        statisticsParentData.setCategoryParent(null);
                        statisticsParentData.setTargetTime(0);
                        statisticsParentData.setActionTime(arrayListSelected.get(i).getTimeDuration());
                        statisticsDataArrayList.add(statisticsParentData);

                        // 자식 데이터에 시간 추가
                        StatisticsData statisticsChildData = statisticsDataArrayList.get(indexOfChildArrayList);
                        int sumChildTime = statisticsDataArrayList.get(indexOfChildArrayList).getActionTime() + arrayListSelected.get(i).getTimeDuration();
                        statisticsChildData.setActionTime(sumChildTime);
                    }

                }
                // 자식은 겹치는게 없고, 부모가 겹체는게 있는 경우
                else if(indexOfChildArrayList == -1 && indexOfParentArrayList != -1){
                    Log.d(tag,"Statistics - setStatisticsArrayList() | 부모 시간+ / 자식 추가 ");

                    // 부모 데이터에 시간 추가
                    StatisticsData statisticsParentData = statisticsDataArrayList.get(indexOfParentArrayList);
                    int sumParentTime = statisticsDataArrayList.get(indexOfParentArrayList).getActionTime() + arrayListSelected.get(i).getTimeDuration();
                    statisticsParentData.setActionTime(sumParentTime);

                    // 자식 데이터 생성
                    StatisticsData statisticsChildData = new StatisticsData();
                    statisticsChildData.setType(CHILD_TYPE);
                    statisticsChildData.setColor(arrayListSelected.get(i).getColor());
                    statisticsChildData.setCategory(arrayListSelected.get(i).getCategory());
                    statisticsChildData.setCategoryParent(arrayListSelected.get(i).getCategoryParent());
                    statisticsChildData.setTargetTime(0);
                    statisticsChildData.setActionTime(arrayListSelected.get(i).getTimeDuration());
                    statisticsDataArrayList.add(statisticsChildData);
                }

                // Report 데이터가 Statistics 데이터에 존재하지 않은 경우
                else{
                    if(arrayListSelected.get(i).getCategoryParent().equals("기타")){
                        Log.d(tag,"Statistics - setStatisticsArrayList() | 부모생성 기타 생성 / 자식생성 O ");
                        // 부모 카테고리가 없는 경우

                        // 부모 데이터
                        StatisticsData statisticsParentData = new StatisticsData();
                        statisticsParentData.setType(PARENT_TYPE);
                        statisticsParentData.setColor(0);
                        statisticsParentData.setCategory("기타");
                        statisticsParentData.setCategoryParent(null);
                        statisticsParentData.setTargetTime(0);
                        statisticsParentData.setActionTime(arrayListSelected.get(i).getTimeDuration());
                        statisticsDataArrayList.add(statisticsParentData);

                        // 자식만 데이터 생성
                        StatisticsData statisticsChildData = new StatisticsData();
                        statisticsChildData.setType(CHILD_TYPE);
                        statisticsChildData.setColor(arrayListSelected.get(i).getColor());
                        statisticsChildData.setCategory(arrayListSelected.get(i).getCategory());
                        statisticsChildData.setCategoryParent(arrayListSelected.get(i).getCategoryParent());
                        statisticsChildData.setTargetTime(0);
                        statisticsChildData.setActionTime(arrayListSelected.get(i).getTimeDuration());
                        statisticsDataArrayList.add(statisticsChildData);
                    } else {
                        Log.d(tag,"Statistics - setStatisticsArrayList() | 부모생성 O / 자식생성 O ");
                        // 부모 카테고리가 있는 경우

                        // 부모 데이터 생성
                        StatisticsData statisticsParentData = new StatisticsData();
                        statisticsParentData.setType(PARENT_TYPE);
                        statisticsParentData.setColor(0);
                        statisticsParentData.setCategory(arrayListSelected.get(i).getCategoryParent());
                        statisticsParentData.setCategoryParent(null);
                        statisticsParentData.setTargetTime(0);
                        statisticsParentData.setActionTime(arrayListSelected.get(i).getTimeDuration());
                        statisticsDataArrayList.add(statisticsParentData);

                        // 자식 데이터 생성
                        StatisticsData statisticsChildData = new StatisticsData();
                        statisticsChildData.setType(CHILD_TYPE);
                        statisticsChildData.setColor(arrayListSelected.get(i).getColor());
                        statisticsChildData.setCategory(arrayListSelected.get(i).getCategory());
                        statisticsChildData.setCategoryParent(arrayListSelected.get(i).getCategoryParent());
                        statisticsChildData.setTargetTime(0);
                        statisticsChildData.setActionTime(arrayListSelected.get(i).getTimeDuration());
                        statisticsDataArrayList.add(statisticsChildData);
                    }
                }
            }
        }
    }

    /**inputTargetTimeStatisticsArrayList()
     * ■ 목표
     *      - selectedPlanDataArrayList() 데이터를 statisticsDataArrayList에 집어 넣기
     **/
    public void inputTargetTimeStatisticsArrayList(ArrayList<PlanData> selectedPlanDataArrayList, ArrayList<StatisticsData> statisticsDataArrayList){
        Log.d(tag,"Statistics - inputTargetTimeStatisticsArrayList() |  ");
        for(int i=0; i<selectedPlanDataArrayList.size(); i++){
            boolean isExsit = false;
            for(int j=0; j<statisticsDataArrayList.size(); j++){
                if(selectedPlanDataArrayList.get(i).getCategory().equals(statisticsDataArrayList.get(j).getCategory())){
                    isExsit = true;
                    statisticsDataArrayList.get(j).setTargetTime(selectedPlanDataArrayList.get(i).getTargetTime());
                }
            }
            // 해당하는 값이 없다면, 값을 추가하자.
            if(isExsit == false){
                if(selectedPlanDataArrayList.get(i).getType() == PARENT_TYPE){
                    // 부모 데이터 생성
                    Log.d(tag,"Statistics - inputTargetTimeStatisticsArrayList() | 부모 데이터 생성 ");
                    StatisticsData statisticsParentData = new StatisticsData();
                    statisticsParentData.setType(PARENT_TYPE);
                    statisticsParentData.setColor(0);
                    statisticsParentData.setCategory(selectedPlanDataArrayList.get(i).getCategory());
                    statisticsParentData.setCategoryParent(null);
                    statisticsParentData.setTargetTime(selectedPlanDataArrayList.get(i).getTargetTime());
                    statisticsParentData.setActionTime(0);
                    statisticsDataArrayList.add(statisticsParentData);
                } else {
                    // 자식 데이터 생성
                    Log.d(tag,"Statistics - inputTargetTimeStatisticsArrayList() | 자식 데이터 생성 ");
                    StatisticsData statisticsChildData = new StatisticsData();
                    statisticsChildData.setType(CHILD_TYPE);
                    statisticsChildData.setColor(selectedPlanDataArrayList.get(i).getColor());
                    statisticsChildData.setCategory(selectedPlanDataArrayList.get(i).getCategory());
                    statisticsChildData.setCategoryParent(selectedPlanDataArrayList.get(i).getCategoryParent());
                    statisticsChildData.setTargetTime(selectedPlanDataArrayList.get(i).getTargetTime());
                    statisticsChildData.setActionTime(0);
                    statisticsDataArrayList.add(statisticsChildData);
                }
            }
        }
    }

    /**sortStatisticsArrayList()
     * ■ 목표
     *      1. 활동량이 많은 순서대로 정렬
     *      2. 부모-자식 대로 보이기 위한 정렬
     **/
    public void sortStatisticsArrayList(ArrayList<StatisticsData> statisticsDataArrayList){
        Collections.sort(statisticsDataArrayList);

        // 부모 Category만 따로 ArrayList<StatisticsData> arrayListParent에 담는다.
        ArrayList<StatisticsData> arrayListParent = new ArrayList<>();
        ArrayList<StatisticsData> arrayListTemp = new ArrayList<>();

        for(int i = 0; i<statisticsDataArrayList.size(); i++){
            if(statisticsDataArrayList.get(i).getType() == PARENT_TYPE){
                arrayListParent.add(statisticsDataArrayList.get(i));
            }
        }
        Log.d(tag,"Statistics - sortStatisticsArrayList() | arrayListParent.size(): "+arrayListParent.size());

        for(int i = 0; i<arrayListParent.size(); i++){
            arrayListTemp.add(arrayListParent.get(i));
            for(int j = 0; j<statisticsDataArrayList.size(); j++){
                // 현재 부모 카테고리의 이름과 자식 카테고리 속성의 부모와 이름이 같은 경우
                if(arrayListParent.get(i).getCategory().equals(statisticsDataArrayList.get(j).getCategoryParent())){
                    arrayListTemp.add(statisticsDataArrayList.get(j));
                }
            }
        }
        Log.d(tag,"Statistics - sortStatisticsArrayList() | arrayListTemp.size(): "+arrayListTemp.size());



//        for(int i=0; i<arrayListTemp.size(); i++){
//            Log.d(tag," ");
//            Log.d(tag,"Statistics - sortStatisticsArrayList() | 타입: "+arrayListTemp.get(i).getType());
//            Log.d(tag,"Statistics - sortStatisticsArrayList() | 부모 "+arrayListTemp.get(i).getCategoryParent());
//            Log.d(tag,"Statistics - sortStatisticsArrayList() | 이름: "+arrayListTemp.get(i).getCategory());
//            Log.d(tag,"Statistics - sortStatisticsArrayList() | 색상: "+arrayListTemp.get(i).getColor());
//            Log.d(tag,"Statistics - sortStatisticsArrayList() | 시간: "+arrayListTemp.get(i).getActionTime());
//        }

        statisticsDataArrayList.clear();
        for(int i=0; i<arrayListTemp.size(); i++){
            statisticsDataArrayList.add(arrayListTemp.get(i));
        }
    }
    /** setStatisticsChildDataArrayList()
     * ■ 목표
     *      - statisticsDataArrayList에서 자식만 선별하기 위함.
     * */
    public void setStatisticsChildDataArrayList(ArrayList<StatisticsData> statisticsDataArrayList, ArrayList<StatisticsData> statisticsChildDataArrayList){

        for(int i = 0; i<statisticsDataArrayList.size(); i++){
            if(statisticsDataArrayList.get(i).getType() == CHILD_TYPE){
                statisticsChildDataArrayList.add(statisticsDataArrayList.get(i));
            }
        }
    }
}
