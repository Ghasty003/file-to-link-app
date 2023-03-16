package com.ghasty.filetolink;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private MaterialButton uploadBtn;
    private ImageView preview;
    private View link, choose;
    private ProgressBar uploadProgressBar;
    private RequestQueue requestQueue;
    private Uri imageUri;
    private String image;
    private long url;
    private String uploadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadBtn = findViewById(R.id.upload_btn);
        uploadProgressBar = findViewById(R.id.upload_progressbar);
        preview = findViewById(R.id.preview);
        link = findViewById(R.id.link_view);
        choose = findViewById(R.id.choose);


        image = covertToBase64(imageUri);
        url = System.currentTimeMillis();

        choose.setOnClickListener(v -> chooseImage());

        uploadBtn.setOnClickListener(v -> {
            try {
                uploadImage();
            } catch (JSONException e) {
                Log.d("Ghasty", e.getLocalizedMessage());
            }
        });
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
            uploadBtn.setVisibility(View.VISIBLE);
            Log.d("Ghastyy", covertToBase64(imageUri));
        }
    }

    private String covertToBase64(Uri file) {
        String base64 = null;

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            base64 = Base64.encodeToString(b, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return base64;
    }

    private void uploadImage() throws JSONException {
        String url = "https://file-to-link-e6pc.onrender.com/api/image";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("image", image);
        jsonObject.put("url", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            Log.d("Ghastyy", response.toString());
        }, error -> {
            error.printStackTrace();
        });

        requestQueue.add(request);
    }
}