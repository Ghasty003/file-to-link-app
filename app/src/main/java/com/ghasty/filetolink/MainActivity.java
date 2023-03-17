package com.ghasty.filetolink;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private MaterialButton uploadBtn, viewImage;
    private ImageView preview;
    private TextView uploadLink;
    private View link, choose;
    private ProgressBar uploadProgressBar;
    private RequestQueue requestQueue;
    private Uri imageUri;

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
        uploadLink = findViewById(R.id.upload_link);
        viewImage = findViewById(R.id.view_image);

        uploadUrl = String.valueOf(System.currentTimeMillis());

        requestQueue = Volley.newRequestQueue(this);

        choose.setOnClickListener(v -> chooseImage());

        uploadLink.setOnClickListener(v -> copyToClipBoard());

        uploadBtn.setOnClickListener(v -> {
            toggleProgressVisibility(true);
            preview.setVisibility(View.INVISIBLE);
            try {
                uploadImage();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        viewImage.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ShowImageActivity.class);
            intent.putExtra("imageId", uploadUrl);
            startActivity(intent);
        });
    }

    private void copyToClipBoard() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied", uploadUrl);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "Text copied!", Toast.LENGTH_SHORT).show();
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
            preview.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);
            link.setVisibility(View.INVISIBLE);
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
        Log.d("Ghastyy", "starting..");


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("image", covertToBase64(imageUri));
        jsonObject.put("urlId", uploadUrl);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            Log.d("Ghastyy", response.toString());
            if (!response.equals(null) ) {
                toggleProgressVisibility(false);
                uploadBtn.setVisibility(View.INVISIBLE);
                link.setVisibility(View.VISIBLE);
                uploadLink.setText(uploadUrl);
                uploadUrl = String.valueOf(System.currentTimeMillis());
                Log.d("Ghastyy", "bye pro");
            }
        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };


        requestQueue.add(request);
    }

    private void toggleProgressVisibility(boolean show) {
        if (show) {
            uploadProgressBar.setVisibility(View.VISIBLE);
            return;
        }

        uploadProgressBar.setVisibility(View.INVISIBLE);
    }
}