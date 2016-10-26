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
import com.ditclear.datepicker.model.FilterType;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 通用日期选择控件
 */

public class DatePickerFragment extends DialogFragment implements FilterDoneListener {

    public static final String TAG = "DatePickerFragment";

    private static final String ARG_X = "x";
    private static final String ARG_Y = "y";
    private static final String ARG_W = "width";
    private static final String ARG_H = "height";

    private List<Fragment> mFragments = new ArrayList<>();
    private float showX, showY;
    private int width = -1;
    private int height = -1;

    private SimpleFragmentPagerAdapter pagerAdapter;
    private OnDateFilterListener mDateFilterListener;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private View rootView;
    private DateTime dt;
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
    public static DatePickerFragment newInstance(float x, float y, int width, int height) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_X, x);
        args.putFloat(ARG_Y, y);
        args.putInt(ARG_W, width);
        args.putInt(ARG_H, height);
        fragment.setArguments(args);
        return fragment;
    }

    public static DatePickerFragment newInstance() {

        Bundle args = new Bundle();

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
        }
        dt=new DateTime();
        //去掉顶部title
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        if (mDateFragment==null){
            mDateFragment=DateFragment.newInstance().setFilterDoneListener(this);
        }
        if (mWeekFilterFragment==null){
            mWeekFilterFragment=FilterFragment.newInstance(getWeek()).setFilterDoneListener(this);
        }
        if (mMonthFilterFragment==null){
            mMonthFilterFragment=FilterFragment.newInstance(getMonth()).setFilterDoneListener(this);
        }
        if (mYearFilterFragment==null){
            mYearFilterFragment=YearFilterFragment.newInstance(getYear()).setFilterDoneListener(this);
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

    /**
     * 按周分组
     *
     * @return 按周分组的集合
     */
    private ArrayList<FilterType> getWeek() {
        final ArrayList<FilterType> list = new ArrayList<>();
        int year = dt.getYear();
        Observable.just(year, year - 1).map(new Func1<Integer, FilterType>() {
            @Override
            public FilterType call(Integer year) {

                FilterType filterType = new FilterType();
                filterType.desc = year + "年";
                DateTime dt = new DateTime().withYear(year);
                GregorianCalendar calendar = dt.toGregorianCalendar();

                int weeks = year == DateTime.now().getYear() ? DateTime.now().getWeekOfWeekyear()
                        : calendar.getMaximum(Calendar.WEEK_OF_YEAR);
                Log.d(TAG, "call: " + weeks);

                filterType.child = new ArrayList<String>();
                for (int i = weeks; i > 0; i--) {
                    dt = dt.withWeekOfWeekyear(i).withDayOfWeek(1);
                    String week = String.format(getResources().getString(R.string.filter_week), i,
                            dt.toString("MM月dd日"), dt.plusDays(6).toString("MM月dd日"));
                    if (year == DateTime.now().getYear() && i == weeks) {
                        week += " 本周";
                    }
                    filterType.child.add(week);
                }
                return filterType;
            }
        }).toList().subscribe(new Action1<List<FilterType>>() {
            @Override
            public void call(List<FilterType> filterTypes) {
                list.addAll(filterTypes);
            }
        });
        return list;
    }

    /**
     * 按月分组
     *
     * @return 按月分组后的集合
     */
    private ArrayList<FilterType> getMonth() {
        final ArrayList<FilterType> list = new ArrayList<>();
        int year = dt.getYear();
        FilterType f = new FilterType();
        f.desc = year + "年";
        f.child = new ArrayList<>();
        for (int i = dt.getMonthOfYear(); i > 0; i--) {
            if (i == dt.getMonthOfYear()) {
                f.child.add(i + "月 本月");
            } else {
                f.child.add(i + "月");
            }
        }
        list.add(f);
        FilterType f1 = new FilterType();
        f1.desc = (year - 1) + "年";
        f1.child = new ArrayList<>();
        for (int i = 12; i > 0; i--) {
            f1.child.add(i + "月");
        }
        list.add(f1);
        return list;
    }

    /**
     * 按年分组
     *
     * @return 按年分组后的集合
     */
    private ArrayList<String> getYear() {

        ArrayList<String> list = new ArrayList<>();
        int year = dt.getYear();
        list.add(year + "年");
        list.add((year - 1) + "年");
        return list;
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
