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
public class Plan {
    private final String tag = "로그";

    /**setPlanDataArrayList()
     * ■ 목표
     *      What: 추가하고자 하는 catagoryData에서 planDataArrayList를 만드는 것
     *      Why: planDataArrayList, 즉 가공된 데이터가 있어야 그래프, 리사이클러뷰를 통해 보여줄 수 있기 때문
     *      How:
     * */
    public void setPlanDataArrayList(CatagoryData catagoryData, ArrayList<PlanData> planDataArrayList) {
        if (planDataArrayList.size() == 0) {
            Log.d(tag, "Plan - setPlanDataArrayList() | 부모생성 O / 자식생성 O ");
            // 부모 데이터
            PlanData planDataParent = new PlanData(catagoryData.getParentName(), null, 0, PARENT_TYPE, 0,false);
            planDataArrayList.add(planDataParent);
            // 자식 데이터
            PlanData planDataChild = new PlanData(catagoryData.getName(), catagoryData.getParentName(), catagoryData.getColor(), CHILD_TYPE, 0, false);
            planDataArrayList.add(planDataChild);
        }
        // planDataArrayList.size() != 0, Data가 존재하는 경우
        else {
            int indexOfChildArrayList = -1;
            int indexOfParentArrayList = -1;
            for (int j = 0; j < planDataArrayList.size(); j++) {
                if (catagoryData.getName().equals(planDataArrayList.get(j).getCategory())) {
                    // 해당 값이 존재하는 경우
                    indexOfChildArrayList = j;
                } else if (catagoryData.getParentName().equals(planDataArrayList.get(j).getCategory())) {
                    indexOfParentArrayList = j;
                }
            }

            // indexOfChildArrayList == -1 이라는게, 겹치는게 없어 새로 추가해야 하는 경우를 의미한다.

            // 자식과 부모가 이미 다 존재하는 경우
            if (indexOfChildArrayList != -1 && indexOfParentArrayList != -1) {
                Log.d(tag, "Plan - setPlanDataArrayList() | 부모 시간 + / 자식 시간 + ");
            }
            // 자식은 겹치는게 있고, 부모가 겹체는게 없는 경우
            else if (indexOfChildArrayList != -1 && indexOfParentArrayList == -1) {
                Log.d(tag, "Plan - setPlanDataArrayList() | 부모 추가 / 자식 시간+ ");

                // 부모 데이터
                PlanData planDataParent = new PlanData(catagoryData.getParentName(), null, 0, PARENT_TYPE, 0, false);
                planDataArrayList.add(planDataParent);

            }
            // 자식은 겹치는게 없고, 부모가 겹체는게 있는 경우
            else if (indexOfChildArrayList == -1 && indexOfParentArrayList != -1) {
                Log.d(tag, "Plan - setPlanDataArrayList() | 부모 시간+ / 자식 추가 ");

                // 자식 데이터 생성
                PlanData planDataChild = new PlanData(catagoryData.getName(), catagoryData.getParentName(), catagoryData.getColor(), CHILD_TYPE, 0, false);
                planDataArrayList.add(planDataChild);
            }
            // Report 데이터가 Statistics 데이터에 존재하지 않은 경우
            else {
                // 부모 데이터
                PlanData planDataParent = new PlanData(catagoryData.getParentName(), null, 0, PARENT_TYPE, 0, false);
                planDataArrayList.add(planDataParent);
                // 자식 데이터
                PlanData planDataChild = new PlanData(catagoryData.getName(), catagoryData.getParentName(), catagoryData.getColor(), CHILD_TYPE, 0, false);
                planDataArrayList.add(planDataChild);
            }
        }
    }

    /**sortPlanDataArrayList()
     * ■ 목표
     *      1. 활동량이 많은 순서대로 정렬
     *      2. 부모-자식 대로 보이기 위한 정렬
     **/
    public void sortPlanDataArrayList(ArrayList<PlanData> planDataArrayList){

        // 부모 Category만 따로 ArrayList<StatisticsData> arrayListParent에 담는다.
        ArrayList<PlanData> arrayListParent = new ArrayList<>();
        ArrayList<PlanData> arrayListTemp = new ArrayList<>();

        for(int i = 0; i<planDataArrayList.size(); i++){
            if(planDataArrayList.get(i).getType() == PARENT_TYPE){
                arrayListParent.add(planDataArrayList.get(i));
            }
        }
        Log.d(tag,"Statistics - sortPlanDataArrayList() | arrayListParent.size(): "+arrayListParent.size());

        for(int i = 0; i<arrayListParent.size(); i++){
            arrayListTemp.add(arrayListParent.get(i));
            for(int j = 0; j<planDataArrayList.size(); j++){
                // 현재 부모 카테고리의 이름과 자식 카테고리 속성의 부모와 이름이 같은 경우
                if(arrayListParent.get(i).getCategory().equals(planDataArrayList.get(j).getCategoryParent())){
                    arrayListTemp.add(planDataArrayList.get(j));
                }
            }
        }
        Log.d(tag,"Statistics - sortPlanDataArrayList() | arrayListTemp.size(): "+arrayListTemp.size());

//        for(int i=0; i<arrayListTemp.size(); i++){
//            Log.d(tag," ");
//            Log.d(tag,"Statistics - sortPlanDataArrayList() | 타입: "+arrayListTemp.get(i).getType());
//            Log.d(tag,"Statistics - sortPlanDataArrayList() | 부모 "+arrayListTemp.get(i).getCategoryParent());
//            Log.d(tag,"Statistics - sortPlanDataArrayList() | 이름: "+arrayListTemp.get(i).getCategory());
//            Log.d(tag,"Statistics - sortPlanDataArrayList() | 색상: "+arrayListTemp.get(i).getColor());
//            Log.d(tag,"Statistics - sortPlanDataArrayList() | 시간: "+arrayListTemp.get(i).getActionTime());
//        }
        planDataArrayList.clear();
        for(int i=0; i<arrayListTemp.size(); i++){
            planDataArrayList.add(arrayListTemp.get(i));
        }
    }
    /** setStatisticsChildDataArrayList()
     * ■ 목표
     *      - statisticsDataArrayList에서 자식만 선별하기 위함.
     * */
    public void setPlanChildDataArrayList(ArrayList<PlanData> planDataArrayList, ArrayList<PlanData> planChildDataArrayList){

        for(int i = 0; i<planDataArrayList.size(); i++){
            if(planDataArrayList.get(i).getType() == CHILD_TYPE){
                planChildDataArrayList.add(planDataArrayList.get(i));
            }
        }
    }

    /**setChildTargetTime()
     * 목적: 자식 category의 targetTime 구하기
     * 문제: 데이터가 중복으로 쌓이는 문제
     * 해결방법:
     *      자식인 경우 targetTime을 0으로 초기화
     *      부모인 경우도 찾아서 0으로 해줘야 한다.
     * */
    public void setChildTargetTime(int targetTime, int position, ArrayList<PlanData> planDataArrayList){
        Log.d(tag,"Plan - setTargetTime() |  ");
        planDataArrayList.get(position).setTargetTime(0);       //자식중복으로 더해짐을 방지하기 위함
        planDataArrayList.get(position).setTargetTime(targetTime);
        // 해당 부모 category도 찾아서 0으로 만들기 위함
//        for(int i=0; i<planDataArrayList.size(); i++){
//           if(planDataArrayList.get(i).getCategory().equals(planDataArrayList.get(position).getCategoryParent())){
//               planDataArrayList.get(i).setTargetTime(0);
//           }
//        }
    }

    /**setParentTargetTime()
     * 목적: 부모 Category의 targetTime 구하기
     * 방법:
     *      1. 부모 리스트를 따로 만든다.
     *      2. 부모 리스트의 자식 리스트 targetTime을 부모에 더해준다.
     *      3. 부모 리스트에 있는 값을 다시 전체 리스트인 planDataArrayList에 넣어준다.
     * */
    public void setParentTargetTime(ArrayList<PlanData> planDataArrayList){
        Log.d(tag,"Plan - setTargetTime() |  ");


        // 1. 부모 Category만 따로 ArrayList<StatisticsData> arrayListParent에 담는다.
        ArrayList<PlanData> arrayListParent = new ArrayList<>();
        for(int i = 0; i<planDataArrayList.size(); i++){
            if(planDataArrayList.get(i).getType() == PARENT_TYPE){
                arrayListParent.add(planDataArrayList.get(i));
            }
        }
        // 부모가 중복으로 데이터 쌓이는 것을 방지하기 위한 초기화 필요
        // 초기화 작업
        for(int i=0; i<arrayListParent.size(); i++){
            arrayListParent.get(i).setTargetTime(0);
        }
        // 2. 자식이 가지고 있는 targetTime 만큼 부모에게 더해준다.
        for(int i = 0; i<arrayListParent.size(); i++){
            for(int j = 0; j<planDataArrayList.size(); j++){
                // 현재 부모 카테고리의 이름과 자식 카테고리 속성의 부모와 이름이 같은 경우
                // 부모 카테고리인 arrayListParent의 TargetTime만큼 값을 더해준다.
                if(arrayListParent.get(i).getCategory().equals(planDataArrayList.get(j).getCategoryParent())){
                    arrayListParent.get(i).setTargetTime(arrayListParent.get(i).getTargetTime()+planDataArrayList.get(j).getTargetTime());
                }
            }
        }
        // 3. 다시 planDataArrayList 데이터 옮기기
        for(int i=0; i<arrayListParent.size(); i++){
            for(int j=0; j<planDataArrayList.size(); j++){
                if(arrayListParent.get(i).getCategory().equals(planDataArrayList.get(j).getCategory())){
                    planDataArrayList.get(j).setTargetTime(arrayListParent.get(i).getTargetTime());
                }
            }
        }
    }
    /**deleteParentCategory()
     * ■ 목적: 자식이 삭제될 때, 남아있는 자식 카테고리가 없는 부모 카테고리 경우 같이 삭제한다.
     * */
    public void deleteParentCategory(ArrayList<PlanData> planDataArrayList){
        for(int i=planDataArrayList.size()-1; i>=0; i--){
            if(planDataArrayList.get(i).getType() == PARENT_TYPE){
                boolean isExist = false;
                for(int j=0; j<planDataArrayList.size(); j++){
                    // 부모 카테고리의 해당하는 자식 카테고리가 있는지 여부 확인
                    if(planDataArrayList.get(i).getCategory().equals(planDataArrayList.get(j).getCategoryParent())){
                        isExist = true;
                        break;
                    }
                }
                // 값이 존재하지 않는 경우 해당 부모 삭제
                if(isExist == false){
                    planDataArrayList.remove(i);
                }
            }
        }
    }
    /**setExtraData()
     * 목적: Plan Data 가운데, routine을 설정
     * */
    public void setExtraData(String routine, ArrayList<PlanData> planDataArrayList){
        for(int i=0; i<planDataArrayList.size(); i++){
            planDataArrayList.get(i).setRoutine(routine);
        }
    }
}
