package com.mcuhq.simplebluetooth.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ActivitySingleFragment extends AppCompatActivity {
    private int fragmentContainerId = 1000000001;
    private static Fragment rootFg;
    private FrameLayout contentView;

    public static void show(Activity act,Fragment fg){
        Intent intent = new Intent(act, ActivitySingleFragment.class);
        rootFg = fg;
        act.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = new FrameLayout(this);
        contentView.setId(fragmentContainerId);

        setContentView(contentView);
        setRootFragment(rootFg);
    }
    public void setRootFragment(Fragment fg){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainerId, fg)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rootFg = null;
    }
}
