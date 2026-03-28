package com.example.everything;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.everything.models.api.CommunityDto;
import com.example.everything.models.api.UserDto;
import java.util.List;

public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        sessionManager = new SessionManager(requireContext());
        apiService = ApiClient.getClient().create(ApiService.class);
        
        setupProfileInfo(view);
        loadProfileStats(view);
        loadJoinedCommunities(view);
        setupSettings(view);
        return view;
    }

    private void setupProfileInfo(View view) {
        TextView tvName = view.findViewById(R.id.tvProfileName);
        TextView tvEmail = view.findViewById(R.id.tvProfileEmail);
        TextView tvRole = view.findViewById(R.id.tvProfileRole);

        tvName.setText(sessionManager.getUsername());
        tvEmail.setText(sessionManager.getEmail());
        tvRole.setText(sessionManager.getRole());
    }

    private void loadProfileStats(View view) {
        int currentUserId = sessionManager.getUserId();
        
        apiService.getUser(currentUserId).enqueue(new retrofit2.Callback<UserDto>() {
            @Override
            public void onResponse(retrofit2.Call<UserDto> call, retrofit2.Response<UserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDto user = response.body();
                    loadUserPostCount(view, currentUserId);
                    loadUserEventCount(view, currentUserId);
                } else {
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UserDto> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserPostCount(View view, int userId) {
        
        apiService.getJoinedCommunities().enqueue(new retrofit2.Callback<List<com.example.everything.models.api.CommunityDto>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.example.everything.models.api.CommunityDto>> call, retrofit2.Response<List<com.example.everything.models.api.CommunityDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.everything.models.api.CommunityDto> communities = response.body();
                    countUserPostsFromCommunities(view, userId, communities);
                } else {
                    setPostCount(view, 0);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.example.everything.models.api.CommunityDto>> call, Throwable t) {
                setPostCount(view, 0);
            }
        });
    }

    private void countUserPostsFromCommunities(View view, int userId, List<com.example.everything.models.api.CommunityDto> communities) {
        if (communities.isEmpty()) {
            setPostCount(view, 0);
            return;
        }

        // Count posts across all communities
        final int[] totalPostCount = {0};
        final int[] completedCalls = {0};
        
        for (com.example.everything.models.api.CommunityDto community : communities) {
            apiService.getCommunityPosts(community.getId()).enqueue(new retrofit2.Callback<List<com.example.everything.models.api.PostDto>>() {
                @Override
                public void onResponse(retrofit2.Call<List<com.example.everything.models.api.PostDto>> call, retrofit2.Response<List<com.example.everything.models.api.PostDto>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<com.example.everything.models.api.PostDto> posts = response.body();
                        
                        // Count posts by current user
                        int userPostsInThisCommunity = 0;
                        for (com.example.everything.models.api.PostDto post : posts) {
                            if (post.getAuthorId() == userId) {
                                totalPostCount[0]++;
                                userPostsInThisCommunity++;
                            }
                        }
                    } else {
                    }
                    
                    completedCalls[0]++;
                    
                    if (completedCalls[0] == communities.size()) {
                        setPostCount(view, totalPostCount[0]);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<List<com.example.everything.models.api.PostDto>> call, Throwable t) {
                        
                    completedCalls[0]++;
                    if (completedCalls[0] == communities.size()) {
                        setPostCount(view, totalPostCount[0]);
                    }
                }
            });
        }
    }

    private void setPostCount(View view, int count) {
        TextView tvPostCount = view.findViewById(R.id.tvPostCount);
        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
            tvPostCount.setText(String.valueOf(count));
        } else {
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                tvPostCount.setText(String.valueOf(count));
            });
        }
    }

    private void loadUserEventCount(View view, int userId) {
        

        apiService.getJoinedCommunities().enqueue(new retrofit2.Callback<List<com.example.everything.models.api.CommunityDto>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.example.everything.models.api.CommunityDto>> call, retrofit2.Response<List<com.example.everything.models.api.CommunityDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.everything.models.api.CommunityDto> communities = response.body();
                    countUserEventsFromCommunities(view, userId, communities);
                } else {
                    setEventCount(view, 0);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.example.everything.models.api.CommunityDto>> call, Throwable t) {
                setEventCount(view, 0);
            }
        });
    }

    private void countUserEventsFromCommunities(View view, int userId, List<com.example.everything.models.api.CommunityDto> communities) {
        if (communities.isEmpty()) {
            setEventCount(view, 0);
            return;
        }

        final int[] totalEventCount = {0};
        final int[] completedCalls = {0};
        
        
        for (com.example.everything.models.api.CommunityDto community : communities) {
            
            apiService.getCommunityEvents(community.getId()).enqueue(new retrofit2.Callback<List<com.example.everything.models.api.EventDto>>() {
                @Override
                public void onResponse(retrofit2.Call<List<com.example.everything.models.api.EventDto>> call, retrofit2.Response<List<com.example.everything.models.api.EventDto>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<com.example.everything.models.api.EventDto> events = response.body();
                        
                        // Count events created by current user
                        int userEventsInThisCommunity = 0;
                        for (com.example.everything.models.api.EventDto event : events) {
                            if (event.getCreatedByUsername().equals(sessionManager.getUsername())) {
                                totalEventCount[0]++;
                                userEventsInThisCommunity++;
                            }
                        }
                    } else {
                        }
                    
                    completedCalls[0]++;
                    
                    if (completedCalls[0] == communities.size()) {
                        setEventCount(view, totalEventCount[0]);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<List<com.example.everything.models.api.EventDto>> call, Throwable t) {
                        
                    completedCalls[0]++;
                    if (completedCalls[0] == communities.size()) {
                        setEventCount(view, totalEventCount[0]);
                    }
                }
            });
        }
    }

    private void setEventCount(View view, int count) {
        TextView tvEventCount = view.findViewById(R.id.tvEventCount);
        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
            tvEventCount.setText(String.valueOf(count));
        } else {
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                tvEventCount.setText(String.valueOf(count));
            });
        }
    }

    private void loadJoinedCommunities(View view) {
        apiService.getJoinedCommunities().enqueue(new retrofit2.Callback<List<CommunityDto>>() {
            @Override
            public void onResponse(retrofit2.Call<List<CommunityDto>> call, retrofit2.Response<List<CommunityDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CommunityDto> communities = response.body();
                    displayCommunities(view, communities);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<CommunityDto>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load communities", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCommunities(View view, List<CommunityDto> communities) {
        LinearLayout llCommunities = view.findViewById(R.id.llProfileCommunities);
        
        // Update community count
        TextView tvCommunityCount = view.findViewById(R.id.tvCommunityCount);
        tvCommunityCount.setText(String.valueOf(communities.size()));
        
        llCommunities.removeAllViews();
        
        for (CommunityDto dto : communities) {
            Community community = new Community(dto);
            
            View row = buildCommunityRow(community);
            llCommunities.addView(row);
        }
    }

    private View buildCommunityRow(Community community) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(32, 24, 32, 24);
        row.setBackgroundColor(Color.parseColor("#16213E"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 4);
        row.setLayoutParams(params);

        TextView tvName = new TextView(getContext());
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvName.setLayoutParams(nameParams);
        tvName.setText(community.getName());
        tvName.setTextColor(Color.WHITE);
        tvName.setTextSize(15);

        TextView tvCategory = new TextView(getContext());
        tvCategory.setText(community.getCategory());
        tvCategory.setTextColor(Color.parseColor("#7B61FF"));
        tvCategory.setTextSize(13);

        row.addView(tvName);
        row.addView(tvCategory);

        row.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CommunityDetailActivity.class);
            intent.putExtra("community", community);
            startActivity(intent);
        });

        return row;
    }

    private void setupSettings(View view) {

        view.findViewById(R.id.llLogout).setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Log Out", (dialog, which) -> {
                        // clear saved session
                        sessionManager.logout();

                        Intent intent = new Intent(getActivity(), AuthActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}