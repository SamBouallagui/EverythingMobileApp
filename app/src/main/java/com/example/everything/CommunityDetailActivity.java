package com.example.everything;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.example.everything.models.api.MemberDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class CommunityDetailActivity extends AppCompatActivity {
    private Community community;
    private Button btnJoin;
    private FloatingActionButton fabCreatePost;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);
        //gets the object passed from ExploreFragment
        community=(Community) getIntent().getSerializableExtra("community");
        if (community ==null){
            //prevent crashing
            finish();
            return;
        }
        setupToolbar();
        setupCommunityInfo();
        setupTabs();
        setupFab();
        checkAdminRole();
    }

    private void setupToolbar(){
        Toolbar toolbar=findViewById(R.id.toolbar);
        //overwrite default action bar with our custom toolbar
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(community.getName());
            //arrow back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    private void setupCommunityInfo() {
        TextView tvCategory = findViewById(R.id.tvDetailCategory);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        TextView tvMemberCount = findViewById(R.id.tvDetailMemberCount);
        btnJoin = findViewById(R.id.btnDetailJoin);

        tvCategory.setText(community.getCategory());
        tvDescription.setText(community.getDescription());
        tvMemberCount.setText(community.getMemberCount() + " members");

        updateJoinButton();

        btnJoin.setOnClickListener(v -> {
            toggleJoinStatus();
        });
    }

    private void toggleJoinStatus() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityId = Integer.parseInt(community.getId());

        if (community.isJoined()) {
            apiService.leaveCommunity(communityId).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        community.setJoined(false);
                        int count = community.getMemberCount();
                        community.setMemberCount(Math.max(0, count - 1));
                        updateCommunityInfo();
                        updateJoinButton();
                        checkAdminRole();
                        notifyFragmentsStatusChanged();
                        Toast.makeText(CommunityDetailActivity.this, "Left " + community.getName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CommunityDetailActivity.this, "Failed to leave community", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(CommunityDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            apiService.joinCommunity(communityId).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        community.setJoined(true);
                        int count = community.getMemberCount();
                        community.setMemberCount(count + 1);
                        updateCommunityInfo();
                        updateJoinButton();
                        checkAdminRole();
                        notifyFragmentsStatusChanged();
                        Toast.makeText(CommunityDetailActivity.this, "Joined " + community.getName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CommunityDetailActivity.this, "Failed to join community", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(CommunityDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateCommunityInfo() {
        TextView tvMemberCount = findViewById(R.id.tvDetailMemberCount);
        tvMemberCount.setText(community.getMemberCount() + " members");
    }

    private void updateJoinButton() {
        if (btnJoin == null) return;

        if (community.isJoined()) {
            btnJoin.setText("Joined");
            btnJoin.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#1E1B4B")));
            btnJoin.setTextColor(android.graphics.Color.parseColor("#A78BFA"));
        } else {
            btnJoin.setText("Join");
            btnJoin.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#7C3AED")));
            btnJoin.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
        }
        
        updateFabVisibility();
    }

    private void updateFabVisibility() {
        if (fabCreatePost == null) return;
        
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        int currentTab = viewPager != null ? viewPager.getCurrentItem() : 0;
        
        // Only show post FAB on the Posts tab (index 0) and if the user is joined
        if (currentTab == 0 && community.isJoined()) {
            fabCreatePost.setVisibility(View.VISIBLE);
        } else {
            fabCreatePost.setVisibility(View.GONE);
        }
    }

    private void setupTabs() {
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        CommunityPagerAdapter adapter = new CommunityPagerAdapter(this, community.getId());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Posts");
            else if (position == 1) tab.setText("Events");
            else tab.setText("Members");
        }).attach();

        // hide the post FAB when on Events or Members fragment
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateFabVisibility();
            }
        });
    }

    private void setupFab() {
        fabCreatePost = findViewById(R.id.fabCreatePost);

        fabCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreatePostActivity.class);
            intent.putExtra("communityId", community.getId());
            intent.putExtra("communityName", community.getName());
            startActivityForResult(intent, 100);
        });


        updateJoinButton();
    }

    private void checkAdminRole() {
        if (!community.isJoined()) {
            isAdmin = false;
            invalidateOptionsMenu();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityId = Integer.parseInt(community.getId());
        
        apiService.getCurrentUserMember(communityId).enqueue(new Callback<MemberDto>() {
            @Override
            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isAdmin = "Admin".equals(response.body().getRole());
                } else {
                    isAdmin = false;
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onFailure(Call<MemberDto> call, Throwable t) {
                isAdmin = false;
                invalidateOptionsMenu();
            }
        });
    }
    
    private String getCurrentUserId() {
        // Get current user ID from SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("EverythingSession", MODE_PRIVATE);
        int userId = prefs.getInt("userId", 0);
        return String.valueOf(userId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isAdmin) {
            getMenuInflater().inflate(R.menu.menu_community_detail, menu);
        } else {
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_community) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Community")
                .setMessage("Are you sure you want to delete \"" + community.getName() + "\"? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteCommunity())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCommunity() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityId = Integer.parseInt(community.getId());
        
        apiService.deleteCommunity(communityId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CommunityDetailActivity.this, 
                        "Community deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CommunityDetailActivity.this, 
                        "Failed to delete community", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CommunityDetailActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 100 && resultCode == RESULT_OK) {
            boolean postCreated = data != null && data.getBooleanExtra("postCreated", false);
            if (postCreated) {
                notifyFragmentsStatusChanged();
            }
        }
        
        if (requestCode == 101 && resultCode == RESULT_OK) {
            boolean eventCreated = data != null && data.getBooleanExtra("eventCreated", false);
            if (eventCreated) {
                notifyFragmentsStatusChanged();
            }
        }
    }

    private void notifyFragmentsStatusChanged() {
        List<androidx.fragment.app.Fragment> fragments = getSupportFragmentManager().getFragments();
        for (androidx.fragment.app.Fragment fragment : fragments) {
            if (fragment instanceof CommunityPostsFragment) {
                ((CommunityPostsFragment) fragment).refreshPosts();
                ((CommunityPostsFragment) fragment).refreshUserRole();
            } else if (fragment instanceof CommunityMembersFragment) {
                ((CommunityMembersFragment) fragment).refreshMembers();
            } else if (fragment instanceof CommunityEventsFragment) {
                ((CommunityEventsFragment) fragment).refreshEvents();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}