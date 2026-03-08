package com.example.everything;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CommunityDetailActivity extends AppCompatActivity {
    private Community community;
    private Button btnJoin;
    private FloatingActionButton fabCreatePost;

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
            community.setJoined(!community.isJoined());
            updateJoinButton();

            String msg = community.isJoined()
                    ? "Joined " + community.getName()
                    : "Left " + community.getName();
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateJoinButton() {
        if (btnJoin == null) return;

        if (community.isJoined()) {
            btnJoin.setText("Joined");
            btnJoin.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#2A2A4A")));
            if (fabCreatePost != null) {
                fabCreatePost.setVisibility(View.VISIBLE);
            }
        } else {
            btnJoin.setText("Join");
            btnJoin.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#7B61FF")));
            if (fabCreatePost != null) {
                fabCreatePost.setVisibility(View.GONE);
            }
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
    }

    private void setupFab() {
        fabCreatePost = findViewById(R.id.fabCreatePost);

        fabCreatePost.setOnClickListener(v -> {
            // TODO: open CreatePostActivity
            Toast.makeText(this, "Create a post", Toast.LENGTH_SHORT).show();
        });


        updateJoinButton();
    }


    @Override
    public boolean onSupportNavigateUp() {
        //closes activity if back arrow button presssed
        finish();
        return true;
    }
}