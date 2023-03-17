package com.ghasty.filetolink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class ShowImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private RequestQueue requestQueue;
    private String imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        imageView = findViewById(R.id.view);

        requestQueue = Volley.newRequestQueue(this);
        imageId = getIntent().getStringExtra("imageId");

        fetchImage();
    }

    private void fetchImage() {
        String url = "https://file-to-link-e6pc.onrender.com/api/image/1678990180038";
        Log.d("Ghastyy", "starting");

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            Log.d("Ghastyy", response.toString());
        }, error -> {
            Log.d("Ghastyy", error.getLocalizedMessage());
            error.printStackTrace();
        });

        requestQueue.add(request);
    }
}