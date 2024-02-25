package com.mcuhq.simplebluetooth.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcuhq.simplebluetooth.AppPref;
import com.mcuhq.simplebluetooth.MessagEntity;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.helper.FilePickerHelper;

public class FilePickerFragment extends Fragment {
    RecyclerView recyclerView;
    SmsAdapter adapter = new SmsAdapter();
    private String fileURI = "";

    MessagEntity sms = new MessagEntity();
    public FilePickerFragment(){}
    public FilePickerFragment(MessagEntity sms){
        this.sms = sms;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_picker, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.fileSelectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePicker();
            }
        });
        view.findViewById(R.id.fileSelectorSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    private void filePicker(){
        String[] mimeTypes = {"image/*", "video/*", "application/pdf", "audio/*"};

        Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Choose a file"), 111);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == Activity.RESULT_OK) {
//                selectedFile = data?.data;
                if(data != null){
                    Uri selectedFile = data.getData();
                    String selectedFilePath = FilePickerHelper.getPath(getActivity(), selectedFile);
                    TextView tv = getView().findViewById(R.id.deviceInfoNameValue);
                    tv.setText(selectedFilePath);
                    fileURI = selectedFilePath;
                    AppPref.fileSelectPath = fileURI;
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(this, "File choosing cancelled", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(this, "Error while choosing this file", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
