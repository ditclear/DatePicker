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

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 页面描述：按周或按月筛选
 * <p>
 * Created by ditclear on 16/10/24.
 */

public class FilterFragment extends Fragment {

    public static final String TAG = "FilterFragment";

    private Context mContext;
    private ArrayList<FilterType> list;
    private DateTime dt;
    private boolean isWeek;
    private int offset=2;

    private FilterDoneListener mDoneListener;
    private int leftPos=0;
    private int rightPos=-1;
    private DoubleListView<FilterType, String> mDoubleListView;

    public static FilterFragment newInstance(boolean isWeek,int offset) {

        Bundle args = new Bundle();
        args.putBoolean("isWeek", isWeek);
        args.putInt("offset",offset);
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return mDoubleListView == null ? createDoubleListView() : mDoubleListView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        isWeek = getArguments().getBoolean("isWeek", true);
        offset=getArguments().getInt("offset",2);
        dt = DateTime.now();

        list = isWeek ? getWeek() : getMonth();

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
                        checkedTextView.setPadding(UIUtil.dp(mContext, 44), UIUtil.dp(mContext, 15), 0,
                                UIUtil.dp(mContext, 15));
                    }
                }).rightAdapter(new SimpleTextAdapter<String>(null, mContext) {
                    @Override
                    public String provideText(String s) {
                        return s;
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(UIUtil.dp(mContext, 30), UIUtil.dp(mContext, 15), 0,
                                UIUtil.dp(mContext, 15));
                        checkedTextView.setBackgroundResource(android.R.color.white);
                    }
                })
                .onLeftItemClickListener(new DoubleListView.OnLeftItemClickListener<FilterType, String>() {
                    @Override
                    public List<String> provideRightList(FilterType item, int position) {
                        List<String> child = item.child;

                        return child;
                    }
                }).onRightItemClickListener(
                        new DoubleListView.OnRightItemClickListener<FilterType, String>() {
                            @Override
                            public void onRightItemClick(int leftLastCheckedPosition, int rightLastChecked,
                                                         FilterType item, String string) {
                                leftPos = leftLastCheckedPosition;
                                rightPos = rightLastChecked;
                                if (mDoneListener != null) {
                                    mDoneListener.onFilterDone(item.desc + string);
                                }
                            }
                        });

        // 初始化选中

        Log.d("left", "createDoubleListView: " + leftPos);
        Log.d("right", "createDoubleListView: " + rightPos);

        mDoubleListView.setLeftLastCheckedPosition(leftPos);
        mDoubleListView.setRightLastChecked(rightPos);

        mDoubleListView.setLeftList(list, leftPos);
        mDoubleListView.setRightList(list.get(leftPos).child, rightPos);
        mDoubleListView.getLeftListView()
                .setBackgroundColor(mContext.getResources().getColor(R.color.b_c_fafafa));

        return mDoubleListView;
    }

    /**
     * 按周分组
     *
     * @return 按周分组的集合
     */
    private ArrayList<FilterType> getWeek() {
        final ArrayList<FilterType> list = new ArrayList<>();
        int year = dt.getYear();
        Integer[] years=new Integer[offset];
        for (int i = 0; i <offset ; i++) {
            years[i]=year--;
        }
        Observable.from(years).map(new Func1<Integer, FilterType>() {
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
                    try {
                        dt = dt.withWeekOfWeekyear(i).withDayOfWeek(1);
                    }catch (Exception e){
                        i--;
                        if (i>0) {
                            Log.d(TAG, "call: i " + i);
                            dt = dt.withWeekOfWeekyear(i).withDayOfWeek(1);
                        }else {
                            continue;
                        }
                    }
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
        for (int i = 0; i < offset; i++) {
            FilterType f = new FilterType();
            f.desc = year + "年";
            f.child = new ArrayList<>();
            int maxMonth=year==dt.getYear()?dt.getMonthOfYear():12;
            for (int j = maxMonth; j > 0; j--) {
                if (j == dt.getMonthOfYear()&&i==0) {
                    f.child.add(j + "月 本月");
                } else {
                    f.child.add(j + "月");
                }
            }
            list.add(f);
            year--;
        }
        return list;
    }

    public void clear() {
        if (mDoubleListView != null) {
            mDoubleListView.clear();

        }
    }
}
