package com.grapevine.grapevine;

import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.grapevine.grapevine.Adapters.PageAdapter;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    TabItem tabanno,tabme,tababo;
    int userID;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tablayout);
        tabanno = findViewById(R.id.tabannotate);
        tabme = findViewById(R.id.tabmenu);
        tababo = findViewById(R.id.tababout);
        viewPager = findViewById(R.id.viewpager);

        Intent intent = getIntent();
        userID = intent.getIntExtra("userid",0);

        pageAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),userID);
        viewPager.setAdapter(pageAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==1){
                    tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.black));
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.black));
                    }
                }
                else if(tab.getPosition()==2){
                    tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.black));
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.black));
                    }
                }
                else{
                    tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.black));
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.black));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}
