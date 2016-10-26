package com.ditclear.datepicker.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baiiu.filter.adapter.SimpleTextAdapter;
import com.baiiu.filter.interfaces.OnFilterItemClickListener;
import com.baiiu.filter.typeview.SingleListView;
import com.baiiu.filter.util.UIUtil;
import com.baiiu.filter.view.FilterCheckedTextView;

import java.util.ArrayList;

/**
 * 页面描述：按年筛选
 * <p>
 * Created by ditclear on 16/10/24.
 */

public class YearFilterFragment extends Fragment {

    private Context mContext;

    private ArrayList<String > list;

    private FilterDoneListener mDoneListener;

    private int selectedPos=-1;
    private SingleListView<String> mSingleListView;

    public YearFilterFragment setFilterDoneListener(FilterDoneListener doneListener) {
        mDoneListener = doneListener;
        return  this;
    }

    public static YearFilterFragment newInstance(ArrayList<String  > list) {
        
        Bundle args = new Bundle();
        args.putStringArrayList("list",list);
        YearFilterFragment fragment = new YearFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return mSingleListView==null?createSingleListView():mSingleListView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        mContext=getActivity();
        list=getArguments().getStringArrayList("list");

    }

    private View createSingleListView() {
        mSingleListView=new SingleListView<String>(getActivity())
                .adapter(new SimpleTextAdapter<String>(null,mContext) {
                    @Override
                    public String provideText(String s) {
                        return s;
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(UIUtil.dp(mContext, 30), UIUtil.dp(mContext, 15), 0, UIUtil.dp(mContext, 15));
                        checkedTextView.setBackgroundResource(android.R.color.white);
                    }
                }).onItemClick(new OnFilterItemClickListener<String>() {
                    @Override
                    public void onItemClick(String year) {
                        selectedPos=list.indexOf(year);
                        if (mDoneListener != null) {
                            mDoneListener.onFilterDone(year);
                        }
                    }
                });
        //初始化选中.
        mSingleListView.setList(list, selectedPos);

        return mSingleListView;
    }

    public void clear() {
        if (mSingleListView!=null){
            mSingleListView.clear(selectedPos);
        }
        selectedPos=-1;
    }
}
