package com.grapevine.grapevine.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.grapevine.grapevine.AboutFragment;
import com.grapevine.grapevine.AnnotationFragment;
import com.grapevine.grapevine.MenuFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    Bundle bundle;

    public PageAdapter(FragmentManager fm, int numOfTabs,int userid) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.bundle = new Bundle();
        bundle.putInt("userid",userid);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0 : AnnotationFragment annotationFragment = new AnnotationFragment();
                annotationFragment.setArguments(bundle);
                return annotationFragment;
            case 1 : MenuFragment menuFragment = new MenuFragment();
                menuFragment.setArguments(bundle);
                return menuFragment;
            case 2 : AboutFragment aboutFragment = new AboutFragment();
                aboutFragment.setArguments(bundle);
                return aboutFragment;
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}
