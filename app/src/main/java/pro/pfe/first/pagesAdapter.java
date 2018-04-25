package pro.pfe.first;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class pagesAdapter extends FragmentStatePagerAdapter {
        int numTabs;

    public pagesAdapter(FragmentManager fm,int nTabs) {
        super(fm);
        this.numTabs=nTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                Teacher_Tab1 tab1 = new Teacher_Tab1();
                return tab1;
            case 1 :
                Teacher_Tab2 tab2 = new Teacher_Tab2();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}
