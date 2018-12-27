package com.example.imtiaz.lab_tasks.myadapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.imtiaz.lab_tasks.fragments.UserHome;
import com.example.imtiaz.lab_tasks.fragments.Camera_Frag;
import com.example.imtiaz.lab_tasks.fragments.Timeline_Frag;

public class MySectionsPagerAdapter extends FragmentPagerAdapter {

    public MySectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                Camera_Frag camera_frag = new Camera_Frag();
                return camera_frag;
            case 1:
                Timeline_Frag timeline_frag = new Timeline_Frag();
                return timeline_frag;
            case 2:
                UserHome userHome = new UserHome();
                return userHome;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
