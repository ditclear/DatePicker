package com.ditclear.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ditclear.datepicker.dialog.DatePickerFragment;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.OnDateFilterListener {

    private DatePickerFragment mPickerFragment;
    private TextView dateTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateTv= (TextView) findViewById(R.id.tv_date);
        mPickerFragment=DatePickerFragment.newInstance(4).setDateFilterListener(this);
    }

    public void onDatePick(View v){
        mPickerFragment.show(getSupportFragmentManager(),DatePickerFragment.TAG);
    }

    @Override
    public void onDateFilter(String date) {
        dateTv.setText(date);
    }
}
