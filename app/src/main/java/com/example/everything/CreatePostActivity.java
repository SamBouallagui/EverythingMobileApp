package com.example.everything;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.everything.models.api.CreatePostRequest;
import com.example.everything.models.api.PostDto;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {
    private static final int MAX_CHARS=500;
    private TextInputEditText etPostContent;
    private TextView tvCharCount;
    private String communityId;
    private String communityName;
    private Button btnSubmit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        communityId = getIntent().getStringExtra("communityId");
        communityName = getIntent().getStringExtra("communityName");
        setupToolbar();
        setupViews();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("New Post");
        }
    }
    
    private void setupViews(){
        etPostContent=findViewById(R.id.etPostContent);
        tvCharCount=findViewById(R.id.CharCount);
        btnSubmit=findViewById(R.id.SubmitPost);
        TextView tvPostingIn=findViewById(R.id.tvPostingIn);
        
        if (communityName!=null){
            tvPostingIn.setText("Posting in: " + communityName);
        }
        
        etPostContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int remaining =s.length();
                tvCharCount.setText(remaining+ " / "+MAX_CHARS);
                if (remaining >MAX_CHARS*0.9){
                    tvCharCount.setTextColor(android.graphics.Color.parseColor("#FF6B6B"));
                }else{
                    tvCharCount.setTextColor(android.graphics.Color.parseColor("#AAAAAA"));
                }
            }
        });
        
        btnSubmit.setOnClickListener(v -> submitPost());
    }
    
    private void submitPost(){
        String content =etPostContent.getText().toString().trim();
        if (content.isEmpty()){
            Toast.makeText(this,"Write Something First", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.length()>MAX_CHARS){
            Toast.makeText(this,"Post is too long",Toast.LENGTH_SHORT).show();
            return;
        }
        
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Posting...");
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityIdInt = Integer.parseInt(communityId);
        CreatePostRequest request = new CreatePostRequest(content);
        
        apiService.createCommunityPost(communityIdInt, request).enqueue(new Callback<PostDto>() {
            @Override
            public void onResponse(Call<PostDto> call, Response<PostDto> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Post");
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreatePostActivity.this, "Post submitted!", Toast.LENGTH_SHORT).show();
                    
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("postCreated", true);
                    setResult(RESULT_OK, resultIntent);
                    
                    finish();
                } else {
                    Toast.makeText(CreatePostActivity.this, "Failed to create post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostDto> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Post");
                Toast.makeText(CreatePostActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}