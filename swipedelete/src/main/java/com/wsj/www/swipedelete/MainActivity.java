package com.wsj.www.swipedelete;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements SwiperDeleteLayout.OnSwipeStateChangedListener{

    private static final String TAG = "MainActivity";

    private ListView lv_show;

    private ArrayList<String> mData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();

        initData();

    }

    private void initData() {
        for (int i = 0; i < 30; i++) {
            mData.add("content --- " + i);
        }

        lv_show.setAdapter(new MyAdapter());

//        lv_show.setOnScrollListener(new AbsListView.OnScrollListener() {
//            /**
//             * 状态变化
//             */
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                    // 如果垂直滑动则需要关闭已经代开的View
//                    SwipeDeleteLayoutManager.getInstance().closeSwipe();
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });

        lv_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: ======");
            }
        });
    }

    private void initView() {
        lv_show = (ListView) findViewById(R.id.lv_show);
    }

    @Override
    public void onOpened(Object tag) {
        Log.d(TAG, "onOpened: " + (Integer)tag);
        Toast.makeText(this, "Opened Index = " + tag, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClosed(Object tag) {
        Log.d(TAG, "onClosed: " + (Integer)tag);
        Toast.makeText(this, "Closed Index = " + tag, Toast.LENGTH_SHORT).show();
    }

    class MyAdapter extends BaseAdapter {
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

            ViewHolder holder ;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.layout_lv_item, null);
                holder = new ViewHolder();
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_name.setText(mData.get(position));
            SwiperDeleteLayout l = (SwiperDeleteLayout) convertView.findViewById(R.id.sdl_item);
            l.setTag(position);
            l.setListener(MainActivity.this);


            return convertView;
        }

        class ViewHolder {
            TextView tv_name;
        }

    }








}
