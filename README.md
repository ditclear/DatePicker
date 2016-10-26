# DatePicker
通用的日期筛选控件

![](https://github.com/vienan/DatePicker/blob/master/screenshot.gif)

###usage:
```java

	public class MainActivity extends AppCompatActivity implements 	DatePickerFragment.OnDateFilterListener {

    private DatePickerFragment mPickerFragment;
    private TextView dateTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateTv= (TextView) findViewById(R.id.tv_date);
        mPickerFragment=DatePickerFragment.newInstance().setDateFilterListener(this);
    }

    public void onDatePick(View v){
        mPickerFragment.show(getSupportFragmentManager(),DatePickerFragment.TAG);
    }

    @Override
    public void onDateFilter(String date) {
        dateTv.setText(date);
    }
    
	}

```

###thanks to：
>https://github.com/baiiu/DropDownMenu

>https://github.com/traex/CalendarListview
