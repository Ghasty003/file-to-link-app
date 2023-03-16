package com.ghasty.filetolink;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton uploadBtn;
    private ImageView preview;
    private View link, choose;
    private ProgressBar uploadProgressBar;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadBtn = findViewById(R.id.upload_btn);
        uploadProgressBar = findViewById(R.id.upload_progressbar);
        preview = findViewById(R.id.preview);
        link = findViewById(R.id.link_view);
        choose = findViewById(R.id.choose);

        choose.setOnClickListener(v -> chooseImage());
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            preview.setImageURI(imageUri);
        }
    }
}