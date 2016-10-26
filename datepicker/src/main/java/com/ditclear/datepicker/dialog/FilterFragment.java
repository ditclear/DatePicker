package com.ditclear.datepicker.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baiiu.filter.adapter.SimpleTextAdapter;
import com.baiiu.filter.typeview.DoubleListView;
import com.baiiu.filter.util.UIUtil;
import com.baiiu.filter.view.FilterCheckedTextView;
import com.ditclear.datepicker.R;
import com.ditclear.datepicker.model.FilterType;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面描述：按周或按月筛选
 * <p>
 * Created by ditclear on 16/10/24.
 */

public class FilterFragment extends Fragment {

    private Context mContext;
    private ArrayList<FilterType> list;

    private FilterDoneListener mDoneListener;
    private int leftPos;
    private int rightPos=-1;
    private DoubleListView<FilterType, String> mDoubleListView;

    public static FilterFragment newInstance(ArrayList<FilterType> list) {

        Bundle args = new Bundle();
        args.putParcelableArrayList("list", list);
        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public FilterFragment setFilterDoneListener(FilterDoneListener doneListener) {
        mDoneListener = doneListener;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return mDoubleListView==null?createDoubleListView():mDoubleListView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        list = getArguments().getParcelableArrayList("list");

    }

    private View createDoubleListView() {

        mDoubleListView = new DoubleListView<FilterType, String>(mContext)
                .leftAdapter(new SimpleTextAdapter<FilterType>(null, mContext) {
                    @Override
                    public String provideText(FilterType filterType) {
                        return filterType.desc;
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(UIUtil.dp(mContext, 44), UIUtil.dp(mContext, 15), 0, UIUtil.dp(mContext, 15));
                    }
                })
                .rightAdapter(new SimpleTextAdapter<String>(null, mContext) {
                    @Override
                    public String provideText(String s) {
                        return s;
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(UIUtil.dp(mContext, 30), UIUtil.dp(mContext, 15), 0, UIUtil.dp(mContext, 15));
                        checkedTextView.setBackgroundResource(android.R.color.white);
                    }
                })
                .onLeftItemClickListener(new DoubleListView.OnLeftItemClickListener<FilterType, String>() {
                    @Override
                    public List<String> provideRightList(FilterType item, int position) {
                        List<String> child = item.child;

                        return child;
                    }
                })
                .onRightItemClickListener(new DoubleListView.OnRightItemClickListener<FilterType, String>() {
                    @Override
                    public void onRightItemClick(int leftLastCheckedPosition, int rightLastChecked, FilterType item, String string) {
                        leftPos = leftLastCheckedPosition;
                        rightPos = rightLastChecked;
                        if (mDoneListener != null) {
                            mDoneListener.onFilterDone(item.desc + string);
                        }
                    }
                });

        //初始化选中

        Log.d("left", "createDoubleListView: " + leftPos);
        Log.d("right", "createDoubleListView: " + rightPos);

        mDoubleListView.setLeftLastCheckedPosition(leftPos);
        mDoubleListView.setRightLastChecked(rightPos);

        mDoubleListView.setLeftList(list,leftPos);
        mDoubleListView.setRightList(list.get(leftPos).child, rightPos);
        mDoubleListView.getLeftListView().setBackgroundColor(mContext.getResources().getColor(R.color.b_c_fafafa));

        return mDoubleListView;
    }

    public void clear() {
        if (mDoubleListView!=null){
            mDoubleListView.clear();

        }
    }
}
