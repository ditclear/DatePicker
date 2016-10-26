package com.baiiu.filter.typeview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.baiiu.filter.R;
import com.baiiu.filter.adapter.BaseBaseAdapter;
import com.baiiu.filter.adapter.SimpleTextAdapter;
import com.baiiu.filter.util.CommonUtil;

import java.util.List;

/**
 * Created by baiiu on 15/12/17.
 * 双列ListView
 */
public class DoubleListView<LEFTD, RIGHTD> extends LinearLayout
        implements AdapterView.OnItemClickListener {

    private BaseBaseAdapter<LEFTD> mLeftAdapter;
    private BaseBaseAdapter<RIGHTD> mRightAdapter;
    private ListView lv_left;
    private ListView lv_right;

    public DoubleListView(Context context) {
        this(context, null);
    }

    public DoubleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DoubleListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        inflate(context, R.layout.merge_filter_list, this);

        lv_left = (ListView) findViewById(R.id.lv_left);
        lv_right = (ListView) findViewById(R.id.lv_right);
        lv_left.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv_right.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lv_left.setOnItemClickListener(this);
        lv_right.setOnItemClickListener(this);
        lv_right.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem==0){

                }
            }
        });
    }


    public DoubleListView<LEFTD, RIGHTD> leftAdapter(SimpleTextAdapter<LEFTD> leftAdapter) {
        mLeftAdapter = leftAdapter;
        lv_left.setAdapter(leftAdapter);
        return this;
    }

    public DoubleListView<LEFTD, RIGHTD> rightAdapter(SimpleTextAdapter<RIGHTD> rightAdapter) {
        mRightAdapter = rightAdapter;
        lv_right.setAdapter(rightAdapter);
        return this;
    }

    public void setLeftList(List<LEFTD> list, int checkedPosition) {
        mLeftAdapter.setList(list);

        if (checkedPosition != -1) {
//            lv_left.performItemClick(mLeftAdapter.getView(checkedPositoin, null, null), checkedPositoin, mLeftAdapter.getItemId(checkedPositoin));//调用此方法相当于点击.第一次进来时会触发重复加载.
            lv_left.setItemChecked(checkedPosition, true);
        }
    }

    public void setRightList(List<RIGHTD> list, int checkedPosition) {
        mRightAdapter.setList(list);
        if (checkedPosition != -1) {
            lv_right.setItemChecked(checkedPosition, true);
        }
    }

    private OnLeftItemClickListener<LEFTD, RIGHTD> mOnLeftItemClickListener;
    private OnRightItemClickListener<LEFTD, RIGHTD> mOnRightItemClickListener;

    public void clear() {
        lv_right.setItemChecked(mRightLastChecked,false);
    }

    public interface OnLeftItemClickListener<LEFTD, RIGHTD> {
        List<RIGHTD> provideRightList(LEFTD leftAdapter, int position);
    }

    public interface OnRightItemClickListener<LEFTD, RIGHTD> {
        void onRightItemClick(int leftLastCheckedPosition, int rightLastChecked, LEFTD item, RIGHTD childItem);
    }

    public DoubleListView<LEFTD, RIGHTD> onLeftItemClickListener(OnLeftItemClickListener<LEFTD, RIGHTD> onLeftItemClickListener) {
        this.mOnLeftItemClickListener = onLeftItemClickListener;
        return this;
    }

    public DoubleListView<LEFTD, RIGHTD> onRightItemClickListener(OnRightItemClickListener<LEFTD, RIGHTD> onRightItemClickListener) {
        this.mOnRightItemClickListener = onRightItemClickListener;
        return this;
    }

    public ListView getLeftListView() {
        return lv_left;
    }

    public ListView getRightListView() {
        return lv_right;
    }

    //========================点击事件===================================
    private int mRightLastChecked;
    private int mLeftLastPosition;
    private int mLeftLastCheckedPosition;


    public int getRightLastChecked() {
        return mRightLastChecked;
    }

    public void setRightLastChecked(int rightLastChecked) {
        mRightLastChecked = rightLastChecked;
    }

    public int getLeftLastCheckedPosition() {
        return mLeftLastCheckedPosition;
    }

    public void setLeftLastCheckedPosition(int leftLastCheckedPosition) {
        mLeftLastCheckedPosition = leftLastCheckedPosition;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (CommonUtil.isFastDoubleClick()) {
            return;
        }

        if (mLeftAdapter == null || mRightAdapter == null) {
            return;
        }

        if (parent == lv_left) {
            mLeftLastPosition = position;

            if (mOnLeftItemClickListener != null) {
                LEFTD item = mLeftAdapter.getItem(position);

                List<RIGHTD> rightds = mOnLeftItemClickListener.provideRightList(item, position);
                setRightList(rightds,-1);

                if (CommonUtil.isEmpty(rightds)) {
                    //当前点的就是这个条目
                    mLeftLastCheckedPosition = -1;
                }
            }
            lv_right.setItemChecked(mRightLastChecked, mLeftLastCheckedPosition == position);
        } else {
            mLeftLastCheckedPosition = mLeftLastPosition;
            mRightLastChecked = position;

            if (mOnRightItemClickListener != null) {
                mOnRightItemClickListener.onRightItemClick(mLeftLastCheckedPosition,mRightLastChecked,mLeftAdapter.getItem(mLeftLastCheckedPosition), mRightAdapter.getItem(mRightLastChecked));
            }
        }
    }



}
