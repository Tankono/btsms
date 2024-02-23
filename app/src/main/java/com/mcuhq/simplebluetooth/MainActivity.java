package com.mcuhq.simplebluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.mcuhq.simplebluetooth.bluetooth.BTController;
import com.mcuhq.simplebluetooth.ui.ClientFragment;
import com.mcuhq.simplebluetooth.ui.HostFragment;

public class MainActivity extends AppCompatActivity {
    int PERMISSION_CODE = 1010;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppPref.init(this);

        setContentView(R.layout.activity_main);
        findViewById(R.id.btHost).setOnClickListener(view -> setupHost());
        findViewById(R.id.btClient).setOnClickListener(view -> setupClient());

        BTController.init(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    PERMISSION_CODE);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        PERMISSION_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupHost(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new HostFragment())
                .commit();

    }

    private void setupClient(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new ClientFragment())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTController.getInstance().onDesTroy();
    }
}