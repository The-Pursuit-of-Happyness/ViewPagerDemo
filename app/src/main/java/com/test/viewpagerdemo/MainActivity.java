package com.test.viewpagerdemo;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ViewPager vp_ad;
    private TextView tv_imageinfo;
    private LinearLayout ll_dots;
    private int[] imageids = new int[] { R.drawable.a, R.drawable.b,
            R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f,
            R.drawable.g, R.drawable.h };
    private String[] imageinfos = new String[] { "让我们开始新的一天", "艺术神奇",
            "美食诱惑，难以抵挡", "有你的夜，月色格外的美", "萌萌哒", "有你在身边，终觉得暖暖的", "乡村之恋，还是那片土地",
            "路在脚下，就得一直走下去" };
    private List<ImageView> imagelist;
    // 记录上一个点
    private int lastShowDot;
    // 判断是否继续
    private boolean isRunning;
    private boolean isScrolling = false;

    int count = 0;

    private final int CHANGE_PAGER = 0x0015;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp_ad = (ViewPager) findViewById(R.id.vp_ad);
        tv_imageinfo = (TextView) findViewById(R.id.tv_imageinfo);
        ll_dots = (LinearLayout) findViewById(R.id.ll_dots);
        init();
        addListener();
    }

    private void init() {
        imagelist = new ArrayList<ImageView>();
        lastShowDot = 0;
        for (int i = 0; i < imageids.length; i++) {
            // 初始化图片资源
            ImageView imageview = new ImageView(this);
            imageview.setBackgroundResource(imageids[i]);
            imagelist.add(imageview);
            // 初始化切换点
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.dot);
            // 布局要采用具体的布局方式
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            dot.setLayoutParams(params);
            if (i == 0) {
                dot.setEnabled(true);
            } else {
                dot.setEnabled(false);
            }
            ll_dots.addView(dot);
        }
        tv_imageinfo.setText(imageinfos[0]);
        vp_ad.setAdapter(new myAdapter());
        // 实现两边循环滑动
        vp_ad.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2
                % imagelist.size());
        isRunning = true;
        new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    count++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (isScrolling || vp_ad.isPressed() || vp_ad.isSelected()) {
                        count = 0;
                    } else {
                        if ((count % 5) == 0) {
                            Message msg = new Message();
                            msg.what = CHANGE_PAGER;
                            handler.sendMessage(msg);
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * 添加ViewPage的状态监听
     */
    private void addListener() {
        vp_ad.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            // 当页面状态发生变化时调用
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            // 当页面正在滑动时调用
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                //设置是否滑动为滑动
                if(arg1>0.0000001)
                    isScrolling = true;
                else
                    //设置是否滑动为滑动
                    isScrolling = false;
            }

            // 当页面切换后调用
            @Override
            public void onPageSelected(int position) {
                position = position%imagelist.size();

                //设置文字描述内容
                tv_imageinfo.setText(imageinfos[position]);

                //改变指示点的状态
                //把当前点enbale 为true
                ll_dots.getChildAt(position).setEnabled(true);
                //把上一个点设为false
                ll_dots.getChildAt(lastShowDot).setEnabled(false);
                lastShowDot = position;
            }
        });

        vp_ad.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isScrolling = true;
                return false;
            }
        });
    }

    // 定义实现循环的handler
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_PAGER:
                    vp_ad.setCurrentItem(vp_ad.getCurrentItem() + 1);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        isRunning = false;
        super.onDestroy();

    }

    /**
     * 定义适配器
     *
     */
    class myAdapter extends PagerAdapter {

        @Override
        /**
         * 销毁对应位置上的object
         */
        public void destroyItem(ViewGroup container, int position, Object object) {
            // super.destroyItem(container, position, object);
            container.removeView((View) object);
        }

        @Override
        /**
         * 获得相应位置上的view，
         * container view的容器，就是viewpager本身
         * position 相应的位置
         */
        public Object instantiateItem(ViewGroup container, int position) {
            // 实现循环滑动
            position = position % imagelist.size();
            // 添加内容
            container.addView(imagelist.get(position));
            return imagelist.get(position);
        }

        @Override
        /**
         * 获得页面的总数
         */
        public int getCount() {
            // return imagelist.size();
            // 实现循环滑动
            return Integer.MAX_VALUE;
        }

        /**
         * 判断页面上的view和object是否有关联
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
