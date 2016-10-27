package com.ditclear.datepicker.dialog;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ditclear.datepicker.R;
import com.ditclear.datepicker.calendarlistview.DatePickerController;
import com.ditclear.datepicker.calendarlistview.DayPickerView;
import com.ditclear.datepicker.calendarlistview.SimpleMonthAdapter;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * 按日筛选
 */
public class DateFragment extends Fragment  implements DatePickerController{

    private DayPickerView pickerView;

    private FilterDoneListener mDoneListener;

    private Calendar mCalendar;

    private boolean showDefault=true;

    private int offset;

    public DateFragment setFilterDoneListener(FilterDoneListener doneListener) {
        mDoneListener = doneListener;
        return this;
    }


    public static DateFragment newInstance(@IntRange(from = 0) int offset) {

        Bundle args = new Bundle();
        args.putInt("offset",offset);
        DateFragment fragment = new DateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            offset=getArguments().getInt("offset",2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pickerView = (DayPickerView) view.findViewById(R.id.pickerView);
        pickerView.setController(this);
        pickerView.setSelected(true);
        if (mCalendar==null){
            mCalendar=new GregorianCalendar();
        }
        if (showDefault) {
            pickerView.setSelectedDay(mCalendar);
        }
    }

    public void setShowDefault(boolean showDefault) {
        this.showDefault = showDefault;
    }

    @Override
    public int getMaxYear() {
        return DateTime.now().getYear();
    }

    @Override
    public int getMinYear() {
        return DateTime.now().getYear()-offset+1;
    }

    @Override
    public int getCurrentYear() {
        return DateTime.now().getYear()-offset+1;
    }

    /**
     * 选择日期后回调
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDayOfMonthSelected(final int year,  int month, final int day) {
        Log.d("Day Selected", day + " / " + month + " / " + year);
        //下次打开默认选中
        mCalendar.set(year,month,day);
        month++;
        final int finalMonth = month;
        pickerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDoneListener != null) {
                    mDoneListener.onFilterDone(year+"年"+ finalMonth +"月"+day+"日");
                }
            }
        },500);

    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {

    }

    public void clear() {
        showDefault=false;
        pickerView.clear();
    }
}
