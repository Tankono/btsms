package com.example.btsms;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.btsms.bluetooth.BTConnectListener;
import com.example.btsms.bluetooth.BTController;
import com.example.btsms.bluetooth.LogUtils;
import com.example.btsms.ui.ClientFragment;
import com.example.btsms.ui.HostFragment;

public class MainActivity extends AppCompatActivity {
    int PERMISSION_CODE = 1010;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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