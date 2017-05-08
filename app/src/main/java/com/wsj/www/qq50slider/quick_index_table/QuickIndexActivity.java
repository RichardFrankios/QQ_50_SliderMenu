package com.wsj.www.qq50slider.quick_index_table;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.OvershootInterpolator;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.wsj.www.qq50slider.R;

import java.util.ArrayList;
import java.util.Collections;

public class QuickIndexActivity extends AppCompatActivity {

    private static final String TAG = "QuickIndexActivity";

    private QuickIndexBar qid_bar;
    private ListView lv_friend;
    private TextView tv_index_view;

    private boolean mIsIndexViewShowed = false;

    private ArrayList<Friend> mFriends = new ArrayList<Friend>();

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_index);


        initView();

        initData();



    }

    private void initData() {
        qid_bar.setOnTouchLetterListener(new QuickIndexBar.OnTouchLetterListener() {
            @Override
            public void onSelectLetter(String letter) {

                showCurrentWord(letter);


                // 滑动ListView
                for (int i = 0; i < mFriends.size(); i++) {
                    if (letter.equals(mFriends.get(i).getNameIndex())) {
                        // 滚动到制定行
                        lv_friend.setSelection(i);
                        break;
                    }
                }
            }
        });


        makeManyFriendDatas();

        // 排序数据
        Collections.sort(mFriends);
        // 设置Adapter
        lv_friend.setAdapter(new FriendAdapter(mFriends, this));

        ViewHelper.setScaleX(tv_index_view, 0f);
        ViewHelper.setScaleY(tv_index_view, 0f);

    }

    /**
     * 显示当前字母
     */
    private void showCurrentWord(String letter) {
        tv_index_view.setText(letter);
        if (!mIsIndexViewShowed) {
            mIsIndexViewShowed = true;
            // 显示
            ViewPropertyAnimator.animate(tv_index_view).scaleX(1f)
                    .setInterpolator(new OvershootInterpolator())
                    .setDuration(500)
                    .start();
            ViewPropertyAnimator.animate(tv_index_view).scaleY(1f)
                    .setInterpolator(new OvershootInterpolator())
                    .setDuration(500)
                    .start();
        }

        // 移除所有的消息
        mHandler.removeCallbacksAndMessages(null);

        // 1.5 s 后执行销毁动作.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsIndexViewShowed = false;
                // 主线程
                ViewPropertyAnimator.animate(tv_index_view).scaleX(0f).setDuration(500).start();
                ViewPropertyAnimator.animate(tv_index_view).scaleY(0f).setDuration(500).start();
            }
        }, 1500);





    }

    private void initView() {
        qid_bar = (QuickIndexBar) findViewById(R.id.qid_bar);
        lv_friend = (ListView) findViewById(R.id.lv_friend);
        tv_index_view = (TextView) findViewById(R.id.tv_index_view);
    }




    /**
     * 构造虚拟数据.
     */
    private void makeManyFriendDatas() {
        // 虚拟数据
        mFriends.add(new Friend("李伟"));
        mFriends.add(new Friend("张三"));
        mFriends.add(new Friend("阿三"));
        mFriends.add(new Friend("阿四"));
        mFriends.add(new Friend("段誉"));
        mFriends.add(new Friend("段正淳"));
        mFriends.add(new Friend("张三丰"));
        mFriends.add(new Friend("陈坤"));
        mFriends.add(new Friend("林俊杰1"));
        mFriends.add(new Friend("陈坤2"));
        mFriends.add(new Friend("王二a"));
        mFriends.add(new Friend("林俊杰a"));
        mFriends.add(new Friend("张四"));
        mFriends.add(new Friend("林俊杰"));
        mFriends.add(new Friend("王二"));
        mFriends.add(new Friend("王二b"));
        mFriends.add(new Friend("赵四"));
        mFriends.add(new Friend("杨坤"));
        mFriends.add(new Friend("赵子龙"));
        mFriends.add(new Friend("杨坤1"));
        mFriends.add(new Friend("李伟1"));
        mFriends.add(new Friend("宋江"));
        mFriends.add(new Friend("宋江1"));
        mFriends.add(new Friend("李伟3"));
    }
}
