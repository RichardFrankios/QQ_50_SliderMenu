package com.wsj.www.parallax;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * 头部视差效果.
 */
public class MainActivity extends AppCompatActivity {



    private ParallaxListView plv_data;


    private static String[] sLetters = { "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();

        initData();

    }

    private void initData() {

        // 设置不显示上面的波纹, 永远不显示.
        plv_data.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // 设置HEADERView
        View headerView = View.inflate(this, R.layout.parallax_header_view, null);
        ImageView imageView = (ImageView) headerView.findViewById(R.id.iv_header_img);
        imageView.setImageResource(R.drawable.low);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // 设置中间放大.
        // 添加到ListView.
        plv_data.addHeaderView(headerView);
        plv_data.setParallaxImageView(imageView);
        // 设置适配器
        plv_data.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sLetters));
    }

    private void initView() {

        plv_data = (ParallaxListView) findViewById(R.id.plv_data);


    }
}
