package pro.pfe.first;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Teacher extends AppCompatActivity implements Teacher_Tab1.OnFragmentInteractionListener,Teacher_Tab2.OnFragmentInteractionListener {


    public static DB db;
    public static Boolean VIEWHOSTEDEXAM=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        TabLayout tab=(TabLayout) findViewById(R.id.tabLayout);
        tab.addTab(tab.newTab().setText(R.string.Exams_manager));
        tab.addTab(tab.newTab().setText(R.string.History));
        tab.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager vp =(ViewPager) findViewById(R.id.pager);
        pagesAdapter pAdapter = new pagesAdapter(getSupportFragmentManager(),tab.getTabCount());
        vp.setAdapter(pAdapter);
        vp.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        db = new DB(getApplicationContext());


    }
    @Override
    public void onStart(){
        super.onStart();

    }
    @Override
    protected void onResume() {
        super.onResume();
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
