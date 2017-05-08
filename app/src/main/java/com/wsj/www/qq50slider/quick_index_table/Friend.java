package com.wsj.www.qq50slider.quick_index_table;

import android.text.TextUtils;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/8
 * 作用 : Bean 数据.
 */

public class Friend implements Comparable<Friend> {

    private String mName;
    private String mPinYinName;

    public Friend(String name) {
        mName = name;
        // 设置拼音
        setPinYinName(PinYinUtil.getPinYin(name));
    }

    /**
     * 获取姓名索引字母
     */
    public String getNameIndex() {
        if (TextUtils.isEmpty(mPinYinName)) return "";
        return mPinYinName.charAt(0) + "";
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPinYinName() {
        return mPinYinName;
    }

    public void setPinYinName(String pinYinName) {
        mPinYinName = pinYinName;
    }

    @Override
    public int compareTo(Friend o) {
        return getPinYinName().compareTo(o.getPinYinName());
    }
}
