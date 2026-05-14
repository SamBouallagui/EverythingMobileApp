package com.example.everything;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.everything.models.api.CreateCommunityRequest;
import com.example.everything.models.api.CommunityDto;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCommunityActivity extends AppCompatActivity {
    private TextInputEditText etCommunityName;
    private TextInputEditText etCommunityDescription;
    private Button btnCreateCommunity;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);

        setupToolbar();
        setupViews();
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create Community");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViews() {
        etCommunityName = findViewById(R.id.etCommunityName);
        etCommunityDescription = findViewById(R.id.etCommunityDescription);
        btnCreateCommunity = findViewById(R.id.btnCreateCommunity);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnCreateCommunity.setOnClickListener(v -> createCommunity());
    }

    private void createCommunity() {
        String name = etCommunityName.getText().toString().trim();
        String description = etCommunityDescription.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            etCommunityName.setError("Community name is required");
            return;
        }

        if (description.isEmpty()) {
            etCommunityDescription.setError("Community description is required");
            return;
        }

        btnCreateCommunity.setEnabled(false);
        btnCreateCommunity.setText("Creating...");
        progressBar.setVisibility(View.VISIBLE);

        // Create request
        CreateCommunityRequest request = new CreateCommunityRequest(name, description);

        // Make API call
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.createCommunity(request).enqueue(new Callback<CommunityDto>() {
            @Override
            public void onResponse(Call<CommunityDto> call, Response<CommunityDto> response) {
                // Hide loading state
                btnCreateCommunity.setEnabled(true);
                btnCreateCommunity.setText("Create Community");
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateCommunityActivity.this, 
                        "Community created successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to community details
                    CommunityDto communityDto = response.body();
                    Community community = new Community(communityDto);
                    community.setJoined(true); // Creator is automatically joined
                    
                    Intent intent = new Intent(CreateCommunityActivity.this, CommunityDetailActivity.class);
                    intent.putExtra("community", community);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateCommunityActivity.this, 
                        "Failed to create community", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommunityDto> call, Throwable t) {
                // Hide loading state
                btnCreateCommunity.setEnabled(true);
                btnCreateCommunity.setText("Create Community");
                progressBar.setVisibility(View.GONE);

                Toast.makeText(CreateCommunityActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
