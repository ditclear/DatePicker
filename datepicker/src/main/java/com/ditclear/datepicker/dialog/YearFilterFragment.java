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

import org.joda.time.DateTime;

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
    private int offset=2;
    private SingleListView<String> mSingleListView;

    public YearFilterFragment setFilterDoneListener(FilterDoneListener doneListener) {
        mDoneListener = doneListener;
        return  this;
    }

    public static YearFilterFragment newInstance(int offset) {

        Bundle args = new Bundle();
        YearFilterFragment fragment = new YearFilterFragment();
        args.putInt("offset",offset);
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
        if (getArguments()!=null) {
            offset = getArguments().getInt("offset",2);
        }
        if (offset<0){
            throw new IllegalStateException(
                    "offset can not less than zero");
        }
        list=getYear();
    }

    /**
     * 按年分组
     *
     * @return 按年分组后的集合
     */
    private ArrayList<String> getYear() {
        ArrayList<String> list = new ArrayList<>();
        int year = DateTime.now().getYear();
        while (offset>0){
            list.add(year + "年");
            year--;
            offset--;
        }
        return list;
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
