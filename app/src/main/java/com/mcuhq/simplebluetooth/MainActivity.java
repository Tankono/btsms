package com.mcuhq.simplebluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import com.mcuhq.simplebluetooth.base.ActivitySingleFragment;
import com.mcuhq.simplebluetooth.bluetooth.BTController;
import com.mcuhq.simplebluetooth.bluetooth.Logger;
import com.mcuhq.simplebluetooth.helper.SmsHepler;
import com.mcuhq.simplebluetooth.helper.PermissionHelper;
import com.mcuhq.simplebluetooth.ui.HostFragment;
import com.mcuhq.simplebluetooth.ui.ScanFragment;

public class MainActivity extends AppCompatActivity {
    int PERMISSION_CODE = 1010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppPref.init(this);
        SmsHepler.init(this);
        BTController.init(this);

        setContentView(R.layout.activity_main);
        findViewById(R.id.btHost).setOnClickListener(view -> setupHost());
        findViewById(R.id.btClient).setOnClickListener(view -> setupClient());

        PermissionHelper.requestPermissions(this, 1,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                new PermissionHelper.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                    }

                    @Override
                    public void onPermissionDenied() {
                    }
                });

//        Testing.test(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupHost() {
        ActivitySingleFragment.show(this, new HostFragment());
    }

    private void setupClient() {
        ActivitySingleFragment.show(this, new ScanFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTController.getInstance().onDesTroy();
    }
}