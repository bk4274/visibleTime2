package com.example.visibletime.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visibletime.Data.CatagoryData;
import com.example.visibletime.R;
import com.example.visibletime.Ui.Dialog.DeleteCategoryParentDialog;
import com.example.visibletime.Ui.Dialog.EditCategoryChildDialog;
import com.example.visibletime.Ui.Dialog.EditCategoryParentDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.visibletime.Data.CatagoryData.CHILD_TYPE;
import static com.example.visibletime.Data.CatagoryData.PARENT_TYPE;


public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String tag = "로그";

    Context context;
    ArrayList<CatagoryData> mArrayList;

//    private int positionClicked;

    public CategoryAdapter(Context context, ArrayList<CatagoryData> mArrayList) {
        this.context = context;
        this.mArrayList = mArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case PARENT_TYPE:
                Log.d(tag,"CategoryAdapter - onCreateViewHolder() | PARENT_TYPE ");
                view = inflater.inflate(R.layout.item_category_general, parent, false);
                return new ParentViewHolder(view);
            case CHILD_TYPE:
                Log.d(tag,"CategoryAdapter - onCreateViewHolder() | CHILD_TYPE ");
                view = inflater.inflate(R.layout.item_cateagory_specific, parent, false);
                return new ChildViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CatagoryData catagoryData = mArrayList.get(position);
        Log.d(tag,"CategoryAdapter - onBindViewHolder() | 이름: "+catagoryData.getName());
        String nameParent = catagoryData.getName();

        // CatagoryData 변수
        // (int type, String name, String color, String parentName, int count)
        if (catagoryData != null) {
            switch (catagoryData.getType()) {
                case PARENT_TYPE:
                    Log.d(tag,"CategoryAdapter - onBindViewHolder() | PARENT_TYPE ");
                    ((ParentViewHolder) holder).nameTextView.setText(catagoryData.getName());
                    ((ParentViewHolder) holder).numberTextView.setText("("+catagoryData.getCount()+")");

                    /** ■ Parent 의 Child 개수 구하기
                     *      ● 프로세스
                    *           1. 내부 ArrayList 존재하지 않는 경우 (= 즉 현재 확장 상태인 경우)
                     *              반복문 & 조건문
                     *              1_1) 부모밑의 아이템의 위치가 전체 Data List인 mArrayList 안에 있는가?
                     *              1_2) 해당 아이템의 Type인 CHILD 인가?
                     *              1_3) 아이템의 속성중 상위 카테고리 이름과 현재 아이템의 이름과 일치하는가?
                     *              이 경우를 만족할 결과 Parent 카테고리의 속성중 'int count(포함하는 자식의 개수)' 하나씩 올리고 이를 반복한다.
                     *          2. 내부 ArrayList 존재하는 경우 (= 즉 현재 축소 상태인 경우)
                     *              내부 ArrayList의 개수를 반환한다.
                    */
                    int childShown = 0;     // 부모 밑에 보여주는 자식 카테고리의 수
                    int childUnshown = 0;   // 부모 안에 감쳐줘서 안보이는 자식 카테고리의 수

                    int positionParentForCount = mArrayList.indexOf(catagoryData); // Parent Category의 위치
                    while (mArrayList.size() > positionParentForCount + 1 +childShown) {
                        if(mArrayList.get(positionParentForCount + 1+childShown).getType() == CHILD_TYPE
                                && nameParent.equals(mArrayList.get(positionParentForCount + 1+childShown).getParentName())){
                            childShown++;
                        } else {
                            break;
                        }
                    }

                    if(catagoryData.invisibleChildren != null){
                        // 내부 ArrayList<Category> 가 있는 경우
                        childUnshown = catagoryData.invisibleChildren.size();
                    }
                    catagoryData.setCount(childShown+childUnshown);      // 최종 Child 개수 저장 (보여주고 있는 Child + 감춰진 Child)


                    // 개수가 0이면 (+)버튼과 숫자가 보이지 않게 한다.
                    if(catagoryData.getCount() == 0) {
                        ((ParentViewHolder) holder).expendBtn.setVisibility(View.GONE);
                        ((ParentViewHolder) holder).numberTextView.setVisibility(View.GONE);
                    }
                    // Parent Category가 내부  ArrayList<Category> 를 가지는지 여부를 가지고 Btn 표시를 다르게 해준다.
                    else {
                        ((ParentViewHolder) holder).numberTextView.setVisibility(View.VISIBLE);
                        ((ParentViewHolder) holder).numberTextView.setText("("+catagoryData.getCount()+")");
                        if(catagoryData.invisibleChildren == null){
                            // 내부 ArrayList<Category> 가 없는 경우
                            ((ParentViewHolder) holder).expendBtn.setVisibility(View.VISIBLE);
                            ((ParentViewHolder) holder).expendBtn.setImageResource(R.drawable.circle_minus);
                        } else{
                            // 내부 ArrayList<Category> 가 있는 경우
                            ((ParentViewHolder) holder).expendBtn.setVisibility(View.VISIBLE);
                            ((ParentViewHolder) holder).expendBtn.setImageResource(R.drawable.circle_plus);
                        }
                    }
                    /** 닫힌 상태, 열린 상태 기억에 따른 Child 카테고리 보이는 여부 다르게 하기
                     *
                     * ■ CategoryData에 bolean 형 isClose라는 변수를 추가한다.
                     *
                     * ■ 데이터 보여주는 것을 다르게 하자.
                     *      1) 닫힘 상태 (부모의 속성중 isClose = true 인 경우)
                     *          - 부모와
                     *      2) 열림 상태 (부모의 속성중 isClose = false 인 경우)
                     * */

                    /** ■ 생각해 볼만 한 것
                     *      클릭 리스너를 왜 onBindViewHolder에 묶어야 하나?
                     *      ViewHolder에 달면, ViewHolder가 재사용 될때 문제가 된다.
                     *
                     *  ■ Expandable RecyclerView
                     *      ● 프로세스
                     *         내부 ArrayList<Category> 의 존재 여부에 따라 나뉘어 진행
                     *         1. 내부 ArrayList 존재하지 않는 경우 (= 즉 현재 확장 상태인 경우)
                     *                - 버튼 클릭시 Child 카테고리는 mArrayList에서 삭제하고
                     *                - 내부 ArrayList에 추가해야 한다.
                     *         2. 내부 ArrayList 존재하는 경우 (= 즉 현재 축소 상태인 경우)
                     *              - 버튼 클릭시 내부 ArrayList에 있는 값을 mArrayList에 추가해야 함.
                     *  */
                    ((ParentViewHolder) holder).expendBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (catagoryData.invisibleChildren == null) {
                                // 1. 내부 ArrayList 존재하지 않는 경우 (= 즉 현재 확장 상태인 경우)
                                catagoryData.invisibleChildren = new ArrayList<CatagoryData>();  // 임시로 보관할 ArrayList<Item> 생성.
                                int parentPosition = mArrayList.indexOf(catagoryData); // Parent Category의 위치
                                int count = 0;                                         // 최종적으로 Child Category의 개수

                                // mArrayList.size() > parentPosition + 1 : 현재 mArrayList의 개수보다 작은 경우에
                                // mArrayList.get(parentPosition + 1).getType() == CHILD_TYPE : Child 타입 인 경우에 한해
                                // nameParent.equals(mArrayList.get(positionParentForCount + 1).getParentName()) : Parent 이름과 해당 타입의 부모와 이름이 같은 경우
                                while (mArrayList.size() > parentPosition + 1 && mArrayList.get(parentPosition + 1).getType() == CHILD_TYPE
                                        && nameParent.equals(mArrayList.get(parentPosition + 1).getParentName())) {
                                    catagoryData.invisibleChildren.add(mArrayList.remove(parentPosition + 1));        // 해당 포지션을 삭제한다.
                                    count++;
                                }
                                notifyItemRangeRemoved(parentPosition + 1, count);
                                ((ParentViewHolder) holder).expendBtn.setImageResource(R.drawable.circle_plus);
                            } else {
                                // 2. 내부 ArrayList 존재하는 경우 (= 즉 현재 축소 상태인 경우)
                                int position = mArrayList.indexOf(catagoryData);        // Parent Category의 위치
                                int index = position + 1;                               // mArrayList에 들어갈 끝 위치 +1을 해주는 이유는 mArrayList에 집어 넣은 때 부모보다 한 칸 아래여야 하기 때문

//                                < for each 문의 형식 >
//                                for (변수타입 변수이름 : 배열이름)
//                                    실행부분;
                                for (CatagoryData data : catagoryData.invisibleChildren) {
                                    mArrayList.add(index, data);
                                    index++;
                                }
                                notifyItemRangeInserted(position + 1, index - position - 1);
                                // mArrayList에 추가할 시작점과 끝점(-1을 해주는 이유는 초기 보정값으로 +1을 해줬지만, 최종 범위에서는 다시 빼야함.)
                                ((ParentViewHolder) holder).expendBtn.setImageResource(R.drawable.circle_minus);
                                catagoryData.invisibleChildren = null;
                            }
                        }
                    });


                    break;
                case CHILD_TYPE:
                    Log.d(tag,"CategoryAdapter - onBindViewHolder() | CHILD_TYPE ");
                    ((ChildViewHolder) holder).nameTextView.setText(catagoryData.getName());
                    ((ChildViewHolder) holder).bgShape.setColor(catagoryData.getColor());
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
//        Log.d(tag,"CategoryAdapter - getItemCount() |  ");
        if (mArrayList == null)
            return 0;
        return mArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(tag,"CategoryAdapter - getItemViewType() | position: "+position);
        if (mArrayList != null) {
            CatagoryData catagoryData = mArrayList.get(position);
            if (catagoryData != null) {
                Log.d(tag,"CategoryAdapter - getItemViewType() | catagoryData.getType(): "+catagoryData.getType());
                return catagoryData.getType();
            }
        }
        return 0;
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener{

        @BindView(R.id.nameTextView_ItemCategoryParent)
        TextView nameTextView;
        @BindView(R.id.numberTextView_ItemCategoryParent)
        TextView numberTextView;
        @BindView(R.id.expendBtn_ItemCategoryParent)
        ImageView expendBtn;

        public ParentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // OnCreateContextMenuListener를 현재 클래스에서 구현한다고 설정해둔 것.
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            // 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록
            // ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 됩니다.
            MenuItem Edit = contextMenu.add(Menu.NONE, 1001, 1, "수정");
            MenuItem Delete = contextMenu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:
                        // 편집 항목을 선택시
                        // 자식 카테고리 보여주는 Custom 카테고리 보여주기
                        EditCategoryParentDialog editCategoryParentDialog = new EditCategoryParentDialog(context, mArrayList, getAdapterPosition());
                        editCategoryParentDialog.show();
                        editCategoryParentDialog.setOnDismissListener(
                                new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        notifyDataSetChanged();
                                    }
                                });
                        break;

                    case 1002:
                        // 삭제 항목 선택
                        DeleteCategoryParentDialog deleteCategoryParentDialog = new DeleteCategoryParentDialog(context, mArrayList, getAdapterPosition());
                        deleteCategoryParentDialog.show();
                        deleteCategoryParentDialog.setOnDismissListener(
                                new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        notifyDataSetChanged();
                                    }
                                });
                        break;
                }
                return true;
            }
        };
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener{

        @BindView(R.id.circleImageView_ItemCategoryChild)
        ImageView circleImageView;
        @BindView(R.id.nameTextView_ItemCategoryChild)
        TextView nameTextView;
        GradientDrawable bgShape;       // Shape의 안 색상을 변경하기 위함.

        public ChildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            bgShape = (GradientDrawable) circleImageView.getBackground();
            // OnCreateContextMenuListener를 현재 클래스에서 구현한다고 설정해둔 것.
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            // 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록
            // ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 됩니다.
            MenuItem Edit = contextMenu.add(Menu.NONE, 1001, 1, "수정");
            MenuItem Delete = contextMenu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:
                        //  편집 항목을 선택시
                        // 자식 카테고리 보여주는 Custom 카테고리 보여주기
                        EditCategoryChildDialog editCategoryChildDialog = new EditCategoryChildDialog(context, mArrayList, getAdapterPosition());
                        editCategoryChildDialog.show();
                        editCategoryChildDialog.setOnDismissListener(
                                new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        notifyDataSetChanged();
                                    }
                                });
                        break;

                    case 1002:

                        String categoryName = mArrayList.get(getAdapterPosition()).getName();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("삭제 확인");
                        builder.setMessage("'"+categoryName +"'"+" 하위 카테고리를 정말 삭제하시겠습니까?");
                        builder.setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, "삭제 완료", Toast.LENGTH_LONG).show();
                                        mArrayList.remove(getAdapterPosition());
                                        notifyItemRemoved(getAdapterPosition());
                                        notifyItemRangeChanged(0, mArrayList.size());
                                    }
                                });
                        builder.setNegativeButton("아니오",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, "삭제 취소", Toast.LENGTH_LONG).show();
                                    }
                                });
                        // 다이얼로그 생성
                        AlertDialog alertDialog = builder.create();
                        // 다이얼로그 보여주기
                        alertDialog.show();
                        break;
                }
                return true;
            }
        };
    }
}
