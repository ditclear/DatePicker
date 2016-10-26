package com.ditclear.datepicker.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 页面描述：筛选类
 * <p>
 * Created by ditclear on 16/10/24.
 */
public class FilterType implements Parcelable {
    //左侧index
    public String desc;

    //右侧内容集合
    public List<String> child;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.desc);
        dest.writeStringList(this.child);
    }

    public FilterType() {
    }

    protected FilterType(Parcel in) {
        this.desc = in.readString();
        this.child = in.createStringArrayList();
    }

    public static final Creator<FilterType> CREATOR = new Creator<FilterType>() {
        @Override
        public FilterType createFromParcel(Parcel source) {
            return new FilterType(source);
        }

        @Override
        public FilterType[] newArray(int size) {
            return new FilterType[size];
        }
    };

    @Override
    public String toString() {
        return "FilterType{" +
                "desc='" + desc + '\'' +
                ", child=" + child +
                '}';
    }
}
