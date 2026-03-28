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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvCommunities = view.findViewById(R.id.rvHomeCommunities);
        fabCreateCommunity = view.findViewById(R.id.fabCreateCommunity);
        
        setupRecyclerView();
        setupFab();
        
        loadJoinedCommunities();
        
        return view;
    }
    
    private void setupRecyclerView() {
        rvCommunities.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommunityAdapter(getContext(), communityList, new CommunityAdapter.OnCommunityClickListener() {
            @Override
            public void onCommunityClick(Community community) {
                // Navigate to community details
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
    
    private void loadJoinedCommunities() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getJoinedCommunities().enqueue(new Callback<List<CommunityDto>>() {
            @Override
            public void onResponse(Call<List<CommunityDto>> call, Response<List<CommunityDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    communityList.clear();
                    for (int i = 0; i < response.body().size(); i++) {
                        CommunityDto dto = response.body().get(i);
                        Community community = new Community(dto);
                        community.setJoined(true); // These are joined communities
                        communityList.add(community);
                        
                        getCommunityMemberCount(community, i);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<CommunityDto>> call, Throwable t) {
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
        adapter.notifyDataSetChanged();
        
        if (getContext() != null) {
            Toast.makeText(getContext(), "No joined communities found", Toast.LENGTH_SHORT).show();
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
                    adapter.notifyItemRemoved(position);
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