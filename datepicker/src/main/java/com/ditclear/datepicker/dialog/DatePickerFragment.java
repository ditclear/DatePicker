package com.ditclear.datepicker.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ditclear.datepicker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用日期选择控件
 */

public class DatePickerFragment extends DialogFragment implements FilterDoneListener {

    public static final String TAG = "DatePickerFragment";

    private static final String ARG_X = "x";
    private static final String ARG_Y = "y";
    private static final String ARG_W = "width";
    private static final String ARG_H = "height";
    private static final String ARG_OFFSET = "offset";

    private List<Fragment> mFragments = new ArrayList<>();
    private float showX, showY;
    private int width = -1;
    private int height = -1;
    private int offset;

    private SimpleFragmentPagerAdapter pagerAdapter;
    private OnDateFilterListener mDateFilterListener;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private View rootView;

    private DateFragment mDateFragment;
    private FilterFragment mWeekFilterFragment;
    private FilterFragment mMonthFilterFragment;
    private YearFilterFragment mYearFilterFragment;

    public DatePickerFragment() {
        // Required empty public constructor
    }

    /**
     * 新建一个dialog
     *
     * @param x      x坐标
     * @param y      y坐标
     * @param width  dialog宽度  默认屏幕宽度的3/4
     * @param height dialog高度  默认屏幕高度的4/5
     * @return
     */
    public static DatePickerFragment newInstance(int yearOffset,float x, float y, int width, int height) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_X, x);
        args.putFloat(ARG_Y, y);
        args.putInt(ARG_W, width);
        args.putInt(ARG_H, height);
        args.putInt(ARG_OFFSET, yearOffset);
        fragment.setArguments(args);
        return fragment;
    }

    public static DatePickerFragment newInstance(int yearOffset) {

        Bundle args = new Bundle();
        args.putInt(ARG_OFFSET, yearOffset);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            showX = getArguments().getFloat(ARG_X);
            showY = getArguments().getFloat(ARG_Y);
            width = getArguments().getInt(ARG_W);
            height = getArguments().getInt(ARG_H);
            offset = getArguments().getInt(ARG_OFFSET);
        }
        //去掉顶部title
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        if (mDateFragment==null){
            mDateFragment=DateFragment.newInstance(offset).setFilterDoneListener(this);
        }
        if (mWeekFilterFragment==null){
            mWeekFilterFragment=FilterFragment.newInstance(true,offset).setFilterDoneListener(this);
        }
        if (mMonthFilterFragment==null){
            mMonthFilterFragment=FilterFragment.newInstance(false,offset).setFilterDoneListener(this);
        }
        if (mYearFilterFragment==null){
            mYearFilterFragment=YearFilterFragment.newInstance(offset).setFilterDoneListener(this);
        }
        mFragments.add(mDateFragment);
        mFragments.add(mWeekFilterFragment);
        mFragments.add(mMonthFilterFragment);
        mFragments.add(mYearFilterFragment);
        pagerAdapter = new SimpleFragmentPagerAdapter(getChildFragmentManager(), getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (getDialog() == null) {
            // Returns mDialog
            // Tells DialogFragment to not use the fragment as a dialog, and so won't try to use mDialog
            setShowsDialog(false);
        }
        super.onActivityCreated(savedInstanceState);
        final DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        Log.d(TAG, "onStart: " + showX + "  " + showY);
        if (width > 0) {
            layoutParams.width = width;
        } else {
            layoutParams.width = dm.widthPixels / 4 * 3;
        }
        if (height > 0) {
            layoutParams.height = height;
        } else {
            layoutParams.height = dm.heightPixels / 5 * 3;
        }
        layoutParams.x = (int) showX;
        layoutParams.y = (int) showY;
        layoutParams.gravity = Gravity.NO_GRAVITY;
        getDialog().onWindowAttributesChanged(layoutParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_dialog, null, false);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        initEvent();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return rootView;
    }

    /**
     * 添加内容
     */
    private void initEvent() {
        mViewPager.setOffscreenPageLimit(SimpleFragmentPagerAdapter.PAGE_COUNT);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * 选择日期后的回调，关闭dialog，将日期传给上级页面
     *
     * @param date
     */
    @Override
    public void onFilterDone(String date) {
        for (int i = 0; i < SimpleFragmentPagerAdapter.PAGE_COUNT; i++) {
            if (i==mViewPager.getCurrentItem()){
                if (i==0){
                    mDateFragment.setShowDefault(true);
                }
                continue;
            }
            switch (i){
                case 0:
                    mDateFragment.clear();
                    break;
                case 1:
                    mWeekFilterFragment.clear();
                    break;
                case 2:
                    mMonthFilterFragment.clear();
                    break;
                case 3:
                    mYearFilterFragment.clear();
                    break;
            }
        }
        if (mDateFilterListener != null) {
            mDateFilterListener.onDateFilter(date);
        }
        dismiss();
    }

    public DatePickerFragment setDateFilterListener(OnDateFilterListener dateFilterListener) {
        mDateFilterListener = dateFilterListener;
        return this;
    }

    public interface OnDateFilterListener {
        void onDateFilter(String date);
    }

    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        public static final int PAGE_COUNT = 4;
        private String tabTitles[] = new String[]{
                "按日", "按周", "按月", "按年"};
        private Context context;

        public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
