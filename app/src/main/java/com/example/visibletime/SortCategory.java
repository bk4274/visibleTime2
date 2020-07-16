package com.example.visibletime;

import android.util.Log;

import com.example.visibletime.Data.CatagoryData;

import java.util.ArrayList;
import static com.example.visibletime.Data.CatagoryData.PARENT_TYPE;
import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;

/**클래스 목적
 * - Category가 추가, 수정, 삭제 되었을때, mArrayList의 순서를 바꿔주기 위함.
 *
 * */
public class SortCategory {
    private final String tag = "로그";

    ArrayList<CatagoryData> arrayList;        // 받은 값
    ArrayList<CatagoryData> arrayListTemp;


    public SortCategory(ArrayList<CatagoryData> arrayList) {
        this.arrayList = arrayList;
    }

    /** Category가 추가 되었을 경우
     * 
     * ★★★★★ 
     * 문제 : 데이터를 수정해도 최종적으로 RecyclerView에 들어가는 데이터 순서는 내가 Category 만든 순서로 들어가게 됨.
     * 원인 : Adapter에 연결되어 있는 참조(주소값)은 
     *        CategoryActivity에서 생성한  ArrayList<CatagoryData> mArrayList; 이다.
     *        근데 나는 여기서 새로 만든 ArrayList<CatagoryData> 값을 Return 했기 때문에, 애초에 변경된 값이 안들어가게 된 것이다.
     *        그래서 내가 인자로 받은 mArrayList 값을 그대로 반환하니 내가 원하는 순서대로 나오게 되었음.
     * ★★★★★
     *
     * ■ 프로세스
     *
     *      ● 부모 Category만 따로 ArrayList<CatagoryData>에 담는다.
     *          - 부모의 개수 + 1 만큼 반복하기 위함
     *          - +1을 한 이유는 부모가 없는 자식 카테고리를 최후에 넣기 위함.
     *
     *      ● 반복문 실행
     *          ○ 조건절: 현재 부모가 존재하는 상태인가?
     *              - 참:
     *                  1. 부모 카테고리를 저장한다.
     *                  2. 반복문으로 부모 하위 카테고리를 저장한다.
     *
     *              - 거짓:
     *                  1. 전체 리스트에서 "기타"에 해당하는 자식 카테고리를 저장한다.
     *
     *      ● 인자로 받은 ArrayList에 있는 값을 지우고, 새로 정렬한 값을 집어 넣는다.
     * */
    public ArrayList<CatagoryData> rectifyArrayList(){
        Log.d(tag,"SortCategory - rectifyArrayList() |  ");

        // ● 부모 Category만 따로 ArrayList<CatagoryData>에 담는다.
        arrayListTemp = new ArrayList<>();
        ArrayList<CatagoryData> arrayListParent = new ArrayList<>();
        for(int i = 0; i<arrayList.size(); i++){
            CatagoryData catagoryData = arrayList.get(i);
            if(catagoryData.getType() == PARENT_TYPE){
                arrayListParent.add(catagoryData);
            }
        }
        Log.d(tag,"SortCategory - rectifyArrayList() | 부모 어레이 리스트 완성 ");
        Log.d(tag,"SortCategory - rectifyArrayList() | arrayListParent.size(): "+arrayListParent.size());

//          수정전

//        for(int i = 0; i<=arrayListParent.size(); i++){
//            if(i != arrayListParent.size()){
//                // 부모가 있는 경우
//                CatagoryData catagoryData = arrayListParent.get(i);
//                String parentName = catagoryData.getParentName();
//                int type = catagoryData.getType();
//                arrayListAfter.add(catagoryData);
//
//                for(int j = 0; j<arrayList.size(); j++){
//                    if(type == CHILD_TYPE){
//                        if (parentName.equals(arrayList.get(j).getParentName())){
//                            // 부모와 같은 경우 arrayListAfter 값을 저장한다.
//                            arrayListAfter.add(arrayList.get(j));
//                        }
//                    }
//                }
//            } else{
//                // 이 경우는 부모 값이 없는 자식 카테고리를 담기 위함
//                for(int j = 0; j<arrayList.size(); j++){
//                    if(type == CHILD_TYPE){
//                        if (arrayList.get(j).getParentName().equals(null)){
//                            arrayListAfter.add(arrayList.get(j));
//                        }
//                    }
//                }
//            }
//        }

// 수정 후
        for(int i = 0; i<=arrayListParent.size(); i++){
            if(i != arrayListParent.size()){
                // 부모가 있는 경우
                Log.d(tag,"SortCategory - rectifyArrayList() | i: "+i);

                CatagoryData catagoryData = arrayListParent.get(i);
//                catagoryData.setCount(0);                   // 중첩해서 쌓이는 문제를 해결하기 위해 0으로 초기화함
                String name = catagoryData.getName();       // 자식 Category의 상위 카테고리가 부모인 경우

                //1. 부모 카테고리를 저장한다.
                arrayListTemp.add(catagoryData);
                int parentCategoryPosition = arrayListTemp.size()-1;
                Log.d(tag,"SortCategory - rectifyArrayList() | Parent Name: "+name);

                //  2. 반복문으로 부모 하위 카테고리를 저장한다.
                for(int j = 0; j<arrayList.size(); j++){
                    if(arrayList.get(j).getType() == CHILD_TYPE){
                        if (name.equals(arrayList.get(j).getParentName())){
                            arrayListTemp.add(arrayList.get(j));
                            Log.d(tag,"SortCategory - rectifyArrayList() | Child Name "+arrayList.get(j).getName());

                            // ■ 주석 처리한 이유
                            // 부모의 숫자를 여기서 처리하게 되면 중첩문제로 초기화를 해야 하는데,
                            // 초기화를 하게 되면 다른 Parent 카테고리를 '접기'기능을 했을 때 가지고 있는 기존 숫자가 날라가 버리는 문제가 발생함.
                            // 부모에 들어갔으면 부모의 Count 개수를 늘려준다.
//                            int count = arrayListTemp.get(parentCategoryPosition).getCount();
//                            arrayListTemp.get(parentCategoryPosition).setCount(count+1);
                        }
                    }
                }
            } else{
                // 이 경우는 부모 값이 없는 자식 카테고리를 담기 위함
                Log.d(tag,"SortCategory - rectifyArrayList() | Parent Name: 기타 ");
                for(int j = 0; j<arrayList.size(); j++){
                    if(arrayList.get(j).getType() == CHILD_TYPE){
                        if (arrayList.get(j).getParentName().equals("기타")){
                            Log.d(tag,"SortCategory - rectifyArrayList() | 부모가 없어 최 하단에 저장한다. ");
                            arrayListTemp.add(arrayList.get(j));
                            Log.d(tag,"SortCategory - rectifyArrayList() | Child Name: "+arrayList.get(j).getName());
                        }
                    }
                }
            }
        }
        //● 인자로 받은 ArrayList에 있는 값을 지우고, 새로 정렬한 값을 집어 넣는다.
        arrayList.clear();
        Log.d(tag,"SortCategory - rectifyArrayList() | arrayList.size() "+arrayList.size());
        for(int i=0; i<arrayListTemp.size(); i++){
            arrayList.add(arrayListTemp.get(i));
            Log.d(tag,"SortCategory - rectifyArrayList() | 이름: "+arrayListTemp.get(i).getName());
        }
        return arrayList;
    }


}
