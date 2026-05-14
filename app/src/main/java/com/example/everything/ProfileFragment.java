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
import android.widget.ImageView;
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

    private com.example.everything.views.CircularStatsView statsPosts, statsEvents, statsCommunities;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        sessionManager = new SessionManager(requireContext());
        apiService = ApiClient.getClient().create(ApiService.class);
        
        statsPosts = view.findViewById(R.id.statsPosts);
        statsEvents = view.findViewById(R.id.statsEvents);
        statsCommunities = view.findViewById(R.id.statsCommunities);
        
        // Customize progress colors
        if (statsPosts != null) statsPosts.setProgressColor(Color.parseColor("#8B5CF6"));
        if (statsEvents != null) statsEvents.setProgressColor(Color.parseColor("#6366F1"));
        if (statsCommunities != null) statsCommunities.setProgressColor(Color.parseColor("#EC4899"));
        
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
                    setPostCount(0);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.example.everything.models.api.CommunityDto>> call, Throwable t) {
                setPostCount(0);
            }
        });
    }

    private void countUserPostsFromCommunities(View view, int userId, List<com.example.everything.models.api.CommunityDto> communities) {
        if (communities.isEmpty()) {
            setPostCount(0);
            return;
        }

        final int[] totalPostCount = {0};
        final int[] completedCalls = {0};
        
        for (com.example.everything.models.api.CommunityDto community : communities) {
            apiService.getCommunityPosts(community.getId()).enqueue(new retrofit2.Callback<List<com.example.everything.models.api.PostDto>>() {
                @Override
                public void onResponse(retrofit2.Call<List<com.example.everything.models.api.PostDto>> call, retrofit2.Response<List<com.example.everything.models.api.PostDto>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<com.example.everything.models.api.PostDto> posts = response.body();
                        for (com.example.everything.models.api.PostDto post : posts) {
                            if (post.getAuthorId() == userId) {
                                totalPostCount[0]++;
                            }
                        }
                    }
                    completedCalls[0]++;
                    if (completedCalls[0] == communities.size()) {
                        setPostCount(totalPostCount[0]);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<List<com.example.everything.models.api.PostDto>> call, Throwable t) {
                    completedCalls[0]++;
                    if (completedCalls[0] == communities.size()) {
                        setPostCount(totalPostCount[0]);
                    }
                }
            });
        }
    }

    private void setPostCount(int count) {
        if (statsPosts != null) {
            float progress = count / 10f; // Assume 10 is goal
            statsPosts.setProgress(progress, String.valueOf(count), "Posts");
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
                    setEventCount(0);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.example.everything.models.api.CommunityDto>> call, Throwable t) {
                setEventCount(0);
            }
        });
    }

    private void countUserEventsFromCommunities(View view, int userId, List<com.example.everything.models.api.CommunityDto> communities) {
        if (communities.isEmpty()) {
            setEventCount(0);
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
                        for (com.example.everything.models.api.EventDto event : events) {
                            if (event.getCreatedByUsername().equals(sessionManager.getUsername())) {
                                totalEventCount[0]++;
                            }
                        }
                    }
                    completedCalls[0]++;
                    if (completedCalls[0] == communities.size()) {
                        setEventCount(totalEventCount[0]);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<List<com.example.everything.models.api.EventDto>> call, Throwable t) {
                    completedCalls[0]++;
                    if (completedCalls[0] == communities.size()) {
                        setEventCount(totalEventCount[0]);
                    }
                }
            });
        }
    }

    private void setEventCount(int count) {
        if (statsEvents != null) {
            float progress = count / 5f; // Assume 5 is goal
            statsEvents.setProgress(progress, String.valueOf(count), "Events");
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
        
        if (statsCommunities != null) {
            float progress = communities.size() / 10f; // Assume 10 is goal
            statsCommunities.setProgress(progress, String.valueOf(communities.size()), "Communities");
        }
        
        llCommunities.removeAllViews();
        for (CommunityDto dto : communities) {
            Community community = new Community(dto);
            View row = buildCommunityRow(community);
            llCommunities.addView(row);
        }
    }

    private View buildCommunityRow(Community community) {
        View row = LayoutInflater.from(getContext()).inflate(R.layout.item_profile_community, null);
        
        TextView tvName = row.findViewById(R.id.tvProfileCommunityName);
        TextView tvCategory = row.findViewById(R.id.tvProfileCommunityCategory);
        ImageView ivIcon = row.findViewById(R.id.ivProfileCommunityIcon);

        tvName.setText(community.getName());
        tvCategory.setText(community.getCategory());
        
        // Apply theme icon
        CommunityTheme theme = CommunityVisualStore.getThemeForCommunity(community.getId());
        ivIcon.setImageResource(theme.getIconResId());

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
                        sessionManager.logout();
                        Intent intent = new Intent(getActivity(), AuthActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}