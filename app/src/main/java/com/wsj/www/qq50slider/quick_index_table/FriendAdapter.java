package com.wsj.www.qq50slider.quick_index_table;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wsj.www.qq50slider.R;

import java.util.ArrayList;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/8
 * 作用 :
 */

public class FriendAdapter extends BaseAdapter {

    private ArrayList<Friend> mData;

    private Context mContext;

    public FriendAdapter(ArrayList<Friend> data, Context context) {
        mData = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(mContext,R.layout.adapter_item , null);
        }

        ViewHolder viewHolder = ViewHolder.getViewHolder(convertView);

        viewHolder.tv_name.setText(mData.get(position).getName());


        // 设置索引字符
        String currentIndex = mData.get(position).getNameIndex();
        viewHolder.tv_name_index.setText(currentIndex);
        // 判断是否显示索引View, 和上一个数据进行比较
        boolean hiddenIndex = false;
        if (position != 0) {
            String preIndex = mData.get(position - 1).getNameIndex();
            hiddenIndex = preIndex.equals(currentIndex);
        }
        viewHolder.tv_name_index.setVisibility(hiddenIndex ? View.GONE : View.VISIBLE);
        return convertView;
    }

    static class ViewHolder {
        TextView tv_name_index, tv_name;

        public ViewHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_name_index = (TextView) view.findViewById(R.id.tv_name_index);
        }

        public static ViewHolder getViewHolder(View view) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }
            return viewHolder;
        }
    }
}
