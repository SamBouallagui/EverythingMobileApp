package com.example.everything;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.everything.models.api.CommunityDto;
import com.example.everything.models.api.MemberDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView rvCommunities;
    private FloatingActionButton fabCreateCommunity;
    private CommunityAdapter adapter;
    private List<Community> communityList = new ArrayList<>();
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefresh;
    private View shimmerView;
    private View emptyStateView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvCommunities = view.findViewById(R.id.rvHomeCommunities);
        fabCreateCommunity = view.findViewById(R.id.fabCreateCommunity);
        swipeRefresh = view.findViewById(R.id.swipeRefreshHome);
        shimmerView = view.findViewById(R.id.shimmer_home);
        emptyStateView = view.findViewById(R.id.empty_state_home);
        
        setupRecyclerView();
        setupFab();
        setupSwipeRefresh();
        
        loadJoinedCommunities(true);
        
        return view;
    }
    
    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> loadJoinedCommunities(false));
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.accent_primary));
    }
    
    private void setupRecyclerView() {
        rvCommunities.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommunityAdapter(getContext(), communityList, new CommunityAdapter.OnCommunityClickListener() {
            @Override
            public void onCommunityClick(Community community) {
                Intent intent = new Intent(getActivity(), CommunityDetailActivity.class);
                intent.putExtra("community", community);
                startActivity(intent);
            }

            @Override
            public void onJoinClick(Community community, int position) {
                if (community.isJoined()) {
                    leaveCommunity(community, position);
                }
            }
        });
        rvCommunities.setAdapter(adapter);
    }
    
    private void setupFab() {
        fabCreateCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateCommunityActivity.class);
            startActivity(intent);
        });
    }
    
    private void loadJoinedCommunities(boolean showShimmer) {
        if (showShimmer && shimmerView != null) {
            shimmerView.setVisibility(View.VISIBLE);
            com.example.everything.utils.AnimationUtils.startShimmer(shimmerView);
            rvCommunities.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.GONE);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getJoinedCommunities().enqueue(new Callback<List<CommunityDto>>() {
            @Override
            public void onResponse(Call<List<CommunityDto>> call, Response<List<CommunityDto>> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (shimmerView != null) shimmerView.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    rvCommunities.setVisibility(View.VISIBLE);
                    emptyStateView.setVisibility(View.GONE);
                    
                    communityList.clear();
                    for (int i = 0; i < response.body().size(); i++) {
                        CommunityDto dto = response.body().get(i);
                        Community community = new Community(dto);
                        community.setJoined(true);
                        communityList.add(community);
                        getCommunityMemberCount(community, i);
                    }
                    adapter.updateList(communityList);
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<CommunityDto>> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (shimmerView != null) shimmerView.setVisibility(View.GONE);
                showEmptyState();
            }
        });
    }
    
    private void getCommunityMemberCount(Community community, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityId = Integer.parseInt(community.getId());
        
        apiService.getCommunityMembers(communityId).enqueue(new Callback<List<MemberDto>>() {
            @Override
            public void onResponse(Call<List<MemberDto>> call, Response<List<MemberDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int memberCount = response.body().size();
                    community.setMemberCount(memberCount);
                    adapter.updateItem(position);
                } else {
                    community.setMemberCount(0);
                    adapter.updateItem(position);
                }
            }

            @Override
            public void onFailure(Call<List<MemberDto>> call, Throwable t) {
                community.setMemberCount(0);
                adapter.updateItem(position);
            }
        });
    }
    
    private void showEmptyState() {
        communityList.clear();
        adapter.updateList(communityList);
        
        rvCommunities.setVisibility(View.GONE);
        if (emptyStateView != null) {
            emptyStateView.setVisibility(View.VISIBLE);
            
            // Setup Create Community button in empty state
            View btnExplore = emptyStateView.findViewById(R.id.btnExplore);
            if (btnExplore != null) {
                btnExplore.setOnClickListener(v -> {
                    // Navigate to Explore
                    com.google.android.material.bottomnavigation.BottomNavigationView nav = getActivity().findViewById(R.id.bottomNav);
                    if (nav != null) {
                        nav.setSelectedItemId(R.id.nav_explore);
                    }
                });
            }
        }
    }
    
    private void leaveCommunity(Community community, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityId = Integer.parseInt(community.getId());
        
        apiService.leaveCommunity(communityId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    communityList.remove(position);
                    adapter.updateList(new ArrayList<>(communityList));
                    Toast.makeText(getContext(), "Left " + community.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Please login to leave communities", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to leave community", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}